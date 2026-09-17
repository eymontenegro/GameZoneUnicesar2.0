package com.gamezone.persistence;

import com.gamezone.model.Person;
import com.gamezone.model.Client;
import com.gamezone.model.Seller;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.util.List;
import java.util.ArrayList;

/**
 * Handles saving and loading Person objects (Client and Seller)
 * to and from a CSV file, so the people registered in the store
 * persist between application runs.
 */
public class PersonRepository {

    private static final String CLIENTS_FILE = "data/clients.csv";
    private static final String SELLERS_FILE = "data/sellers.csv";

    /**
     * Converts a single client into one CSV-formatted line.
     *
     * @param client the client to convert
     * @return a comma-separated line representing the client
     */
    private String clientToCsvLine(Client client) {
        String purchases = String.join(";", client.getPurchaseHistory());
        return client.getId() + "," + client.getName() + "," + client.getPhone()
                + "," + client.getEmail() + "," + purchases;
    }

    /**
     * Converts a single seller into one CSV-formatted line.
     *
     * @param seller the seller to convert
     * @return a comma-separated line representing the seller
     */
    private String sellerToCsvLine(Seller seller) {
        return seller.getId() + "," + seller.getName() + "," + seller.getPhone()
                + "," + seller.getEmployeeCode() + "," + seller.getShift();
    }

    /**
     * Saves the full list of clients to the CSV file, overwriting
     * any previous content.
     *
     * @param clients the list of clients to persist
     * @throws RuntimeException if an I/O error occurs while writing
     */
    public void saveClients(List<Client> clients) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CLIENTS_FILE))) {
            for (Client client : clients) {
                writer.write(clientToCsvLine(client));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error guardando los clientes: " + e.getMessage(), e);
        }
    }

    /**
     * Saves the full list of sellers to the CSV file, overwriting
     * any previous content.
     *
     * @param sellers the list of sellers to persist
     * @throws RuntimeException if an I/O error occurs while writing
     */
    public void saveSellers(List<Seller> sellers) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SELLERS_FILE))) {
            for (Seller seller : sellers) {
                writer.write(sellerToCsvLine(seller));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error guardando los vendedores: " + e.getMessage(), e);
        }
    }

    /**
     * Loads the full list of clients from the CSV file. If the file
     * does not exist yet (first run), returns an empty list instead
     * of failing.
     *
     * @return the list of clients loaded from the file
     * @throws RuntimeException if an I/O error occurs while reading
     */
    public List<Client> loadClients() {
        List<Client> clients = new ArrayList<>();
        File file = new File(CLIENTS_FILE);

        if (!file.exists()) {
            return clients;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isBlank()) {
                    String[] parts = line.split(",", -1);
                    String id = parts[0];
                    String name = parts[1];
                    String phone = parts[2];
                    String email = parts[3];
                    Client client = new Client(email, id, name, phone);
                    if (parts.length > 4 && !parts[4].isEmpty()) {
                        for (String saleId : parts[4].split(";")) {
                            client.addPurchase(saleId);
                        }
                    }
                    clients.add(client);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error cargando los clientes: " + e.getMessage(), e);
        }

        return clients;
    }

    /**
     * Loads the full list of sellers from the CSV file. If the file
     * does not exist yet (first run), returns an empty list instead
     * of failing.
     *
     * @return the list of sellers loaded from the file
     * @throws RuntimeException if an I/O error occurs while reading
     */
    public List<Seller> loadSellers() {
        List<Seller> sellers = new ArrayList<>();
        File file = new File(SELLERS_FILE);

        if (!file.exists()) {
            return sellers;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isBlank()) {
                    String[] parts = line.split(",", -1);
                    String id = parts[0];
                    String name = parts[1];
                    String phone = parts[2];
                    String employeeCode = parts[3];
                    String shift = parts[4];
                    sellers.add(new Seller(employeeCode, shift, id, name, phone));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error cargando los vendedores: " + e.getMessage(), e);
        }

        return sellers;
    }
}