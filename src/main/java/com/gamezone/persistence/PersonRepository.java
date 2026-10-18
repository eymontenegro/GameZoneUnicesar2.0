package com.gamezone.persistence;

import com.gamezone.exceptions.PersistenceException;
import com.gamezone.model.Client;
import com.gamezone.model.Seller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles saving and loading Person objects (Client and Seller)
 * to and from CSV files, so the people registered in the store
 * persist between application runs.
 *
 * <p>Every low-level {@link IOException} is wrapped in a
 * {@link PersistenceException}; corrupt file content is reported with the
 * file name and line number.</p>
 */
public class PersonRepository {

    private static final String DEFAULT_CLIENTS_FILE = "data/clients.csv";
    private static final String DEFAULT_SELLERS_FILE = "data/sellers.csv";
    private static final int SELLER_COLUMNS = 5;

    private final String clientsFile;
    private final String sellersFile;

    /**
     * Creates a repository that uses the default files
     * {@code data/clients.csv} and {@code data/sellers.csv}.
     */
    public PersonRepository() {
        this(DEFAULT_CLIENTS_FILE, DEFAULT_SELLERS_FILE);
    }

    /**
     * Creates a repository that uses the given files (useful for tests).
     *
     * @param clientsFile path of the clients CSV file
     * @param sellersFile path of the sellers CSV file
     * @throws IllegalArgumentException if any path is null or blank
     */
    public PersonRepository(String clientsFile, String sellersFile) {
        if (clientsFile == null || clientsFile.isBlank() || sellersFile == null || sellersFile.isBlank()) {
            throw new IllegalArgumentException("Las rutas de los archivos de personas no pueden estar vacias");
        }
        this.clientsFile = clientsFile;
        this.sellersFile = sellersFile;
    }

    /**
     * Converts a single client into one CSV-formatted line.
     *
     * @param client the client to convert
     * @return a comma-separated line representing the client
     * @throws IllegalArgumentException if any value cannot be stored safely
     */
    private String clientToCsvLine(Client client) {
        List<String> history = client.getPurchaseHistory();
        if (history == null) {
            throw new IllegalArgumentException("El historial de compras del cliente no puede ser nulo");
        }
        List<String> safeHistory = new ArrayList<>();
        for (String saleId : history) {
            safeHistory.add(CsvSupport.safe(saleId, "purchaseHistory", ";"));
        }
        return CsvSupport.safe(client.getId(), "id") + ","
                + CsvSupport.safe(client.getName(), "name") + ","
                + CsvSupport.safe(client.getPhone(), "phone") + ","
                + CsvSupport.safe(client.getEmail(), "email") + ","
                + String.join(";", safeHistory);
    }

    /**
     * Converts a single seller into one CSV-formatted line.
     *
     * @param seller the seller to convert
     * @return a comma-separated line representing the seller
     * @throws IllegalArgumentException if any value cannot be stored safely
     */
    private String sellerToCsvLine(Seller seller) {
        return CsvSupport.safe(seller.getId(), "id") + ","
                + CsvSupport.safe(seller.getName(), "name") + ","
                + CsvSupport.safe(seller.getPhone(), "phone") + ","
                + CsvSupport.safe(seller.getEmployeeCode(), "employeeCode") + ","
                + CsvSupport.safe(seller.getShift(), "shift");
    }

    /**
     * Saves the full list of clients to the CSV file, overwriting
     * any previous content. All clients are validated before the file is
     * opened, so an invalid client never leaves a truncated file behind.
     *
     * @param clients the list of clients to persist
     * @throws IllegalArgumentException if the list or any client is null or has
     *         values that cannot be stored
     * @throws PersistenceException if an I/O error occurs while writing
     */
    public void saveClients(List<Client> clients) {
        if (clients == null) {
            throw new IllegalArgumentException("La lista de clientes no puede ser nula");
        }
        List<String> lines = new ArrayList<>();
        for (Client client : clients) {
            if (client == null) {
                throw new IllegalArgumentException("La lista de clientes contiene un elemento nulo");
            }
            lines.add(clientToCsvLine(client));
        }
        writeLines(clientsFile, lines, "cliente(s)");
    }

    /**
     * Saves the full list of sellers to the CSV file, overwriting
     * any previous content. All sellers are validated before the file is
     * opened, so an invalid seller never leaves a truncated file behind.
     *
     * @param sellers the list of sellers to persist
     * @throws IllegalArgumentException if the list or any seller is null or has
     *         values that cannot be stored
     * @throws PersistenceException if an I/O error occurs while writing
     */
    public void saveSellers(List<Seller> sellers) {
        if (sellers == null) {
            throw new IllegalArgumentException("La lista de vendedores no puede ser nula");
        }
        List<String> lines = new ArrayList<>();
        for (Seller seller : sellers) {
            if (seller == null) {
                throw new IllegalArgumentException("La lista de vendedores contiene un elemento nulo");
            }
            lines.add(sellerToCsvLine(seller));
        }
        writeLines(sellersFile, lines, "vendedor(es)");
    }

    /**
     * Writes the given lines to a file, wrapping any I/O error.
     *
     * @param path the file to write
     * @param lines the lines to write
     * @param what plural description of the saved entities, for the error message
     * @throws PersistenceException if an I/O error occurs
     */
    private void writeLines(String path, List<String> lines, String what) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            throw new PersistenceException("No se pudieron guardar " + lines.size() + " " + what
                    + " en '" + path + "'", e);
        }
    }

    /**
     * Loads the full list of clients from the CSV file. If the file
     * does not exist yet (first run), returns an empty list instead
     * of failing. Blank lines are ignored.
     *
     * @return the list of clients loaded from the file
     * @throws PersistenceException if an I/O error occurs while reading, or
     *         the file contains a malformed line
     */
    public List<Client> loadClients() {
        List<Client> clients = new ArrayList<>();
        File file = new File(clientsFile);

        if (!file.exists()) {
            return clients;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.isBlank()) {
                    continue;
                }
                String[] parts = line.split(",", -1);
                // id,name,phone,email[,purchases]
                if (parts.length != 4 && parts.length != 5) {
                    throw CsvSupport.corruptLine(clientsFile, lineNumber,
                            "se esperaban 4 o 5 columnas pero hay " + parts.length, null);
                }
                CsvSupport.requireNotBlank(parts[0], "id", clientsFile, lineNumber);
                Client client = new Client(parts[3], parts[0], parts[1], parts[2]);
                if (parts.length == 5 && !parts[4].isEmpty()) {
                    for (String saleId : parts[4].split(";")) {
                        client.addPurchase(saleId);
                    }
                }
                clients.add(client);
            }
        } catch (IOException e) {
            throw new PersistenceException("No se pudieron leer los clientes desde '" + clientsFile + "'", e);
        }

        return clients;
    }

    /**
     * Loads the full list of sellers from the CSV file. If the file
     * does not exist yet (first run), returns an empty list instead
     * of failing. Blank lines are ignored.
     *
     * @return the list of sellers loaded from the file
     * @throws PersistenceException if an I/O error occurs while reading, or
     *         the file contains a malformed line
     */
    public List<Seller> loadSellers() {
        List<Seller> sellers = new ArrayList<>();
        File file = new File(sellersFile);

        if (!file.exists()) {
            return sellers;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.isBlank()) {
                    continue;
                }
                String[] parts = line.split(",", -1);
                CsvSupport.requireColumns(parts, SELLER_COLUMNS, sellersFile, lineNumber);
                CsvSupport.requireNotBlank(parts[0], "id", sellersFile, lineNumber);
                // id,name,phone,employeeCode,shift
                sellers.add(new Seller(parts[3], parts[4], parts[0], parts[1], parts[2]));
            }
        } catch (IOException e) {
            throw new PersistenceException("No se pudieron leer los vendedores desde '" + sellersFile + "'", e);
        }

        return sellers;
    }
}