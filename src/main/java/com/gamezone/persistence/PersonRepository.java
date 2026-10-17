package com.gamezone.persistence;

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
        String purchases = (client.getPurchaseHistory() != null) 
                ? String.join(";", client.getPurchaseHistory()) 
                : "";
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
     */
    public void saveClients(List<Client> clients) {
        File file = new File(CLIENTS_FILE);
        if (file.getParentFile() != null) {
            file.getParentFile().mkdirs();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            if (clients != null) {
                for (Client client : clients) {
                    if (client != null) {
                        writer.write(clientToCsvLine(client));
                        writer.newLine();
                    }
                }
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
     */
    public void saveSellers(List<Seller> sellers) {
        File file = new File(SELLERS_FILE);
        if (file.getParentFile() != null) {
            file.getParentFile().mkdirs();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            if (sellers != null) {
                for (Seller seller : sellers) {
                    if (seller != null) {
                        writer.write(sellerToCsvLine(seller));
                        writer.newLine();
                    }
                }
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
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] parts = line.split(",", -1);
                if (parts.length >= 4) {
                    String id = parts[0].trim();
                    String name = parts[1].trim();
                    String phone = parts[2].trim();
                    String email = parts[3].trim();
                    Client client = new Client(email, id, name, phone);

                    if (parts.length > 4 && !parts[4].trim().isEmpty()) {
                        for (String saleId : parts[4].trim().split(";")) {
                            if (!saleId.trim().isEmpty()) {
                                client.addPurchase(saleId.trim());
                            }
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
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] parts = line.split(",", -1);
                if (parts.length >= 5) {
                    String id = parts[0].trim();
                    String name = parts[1].trim();
                    String phone = parts[2].trim();
                    String employeeCode = parts[3].trim();
                    String shift = parts[4].trim();
                    sellers.add(new Seller(employeeCode, shift, id, name, phone));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error cargando los vendedores: " + e.getMessage(), e);
        }

        return sellers;
    }
}