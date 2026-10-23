package com.gamezone.persistence;

import com.gamezone.exceptions.PersistenceException;
import com.gamezone.model.Client;
import com.gamezone.model.Seller;

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
public class PersonRepository implements PersonStorage {

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
    @Override
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
        CsvSupport.writeLines(clientsFile, lines, "cliente(s)");
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
        CsvSupport.writeLines(sellersFile, lines, "vendedor(es)");
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
    @Override
    public List<Client> loadClients() {
        return CsvSupport.readLines(clientsFile, "los clientes", this::csvLineToClient);
    }

    /**
     * Reconstructs a single Client from one CSV line.
     *
     * @param line a single line read from the CSV file
     * @param lineNumber the 1-based number of the line, used in error messages
     * @return the reconstructed Client object
     * @throws PersistenceException if the line is malformed
     */
    private Client csvLineToClient(String line, int lineNumber) {
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
        return client;
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
    @Override
    public List<Seller> loadSellers() {
        return CsvSupport.readLines(sellersFile, "los vendedores", this::csvLineToSeller);
    }

    /**
     * Reconstructs a single Seller from one CSV line.
     *
     * @param line a single line read from the CSV file
     * @param lineNumber the 1-based number of the line, used in error messages
     * @return the reconstructed Seller object
     * @throws PersistenceException if the line is malformed
     */
    private Seller csvLineToSeller(String line, int lineNumber) {
        String[] parts = line.split(",", -1);
        CsvSupport.requireColumns(parts, SELLER_COLUMNS, sellersFile, lineNumber);
        CsvSupport.requireNotBlank(parts[0], "id", sellersFile, lineNumber);
        // id,name,phone,employeeCode,shift
        return new Seller(parts[3], parts[4], parts[0], parts[1], parts[2]);
    }
}