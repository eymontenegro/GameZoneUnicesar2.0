package com.gamezone.persistence;

import com.gamezone.model.Accessory;
import com.gamezone.model.Client;
import com.gamezone.model.Product;
import com.gamezone.model.Promotion;
import com.gamezone.model.Sale;
import com.gamezone.model.SaleDetail;
import com.gamezone.model.Seller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handles saving and loading Sale objects to and from a CSV file.
 * Integrates references to Client, Seller, Product/Accessory, and Promotion entities.
 */
public class SaleRepository {

    private static final String FILE_PATH = "data/sales.csv";

    /**
     * Converts a Sale object to a single CSV line format:
     * id,clientId,sellerId,date,promotionId,productId1:quantity1:unitPrice1;productId2:quantity2...
     * 
     * @param sale the sale to convert
     * @return the formatted CSV line string
     */
    private String saleToCsvLine(Sale sale) {
        String promoId = (sale.getPromotion() != null) ? sale.getPromotion().getId() : "NONE";
        StringBuilder detailsBuilder = new StringBuilder();

        List<SaleDetail> details = sale.getDetails();
        for (int i = 0; i < details.size(); i++) {
            SaleDetail detail = details.get(i);
            detailsBuilder.append(detail.getProduct().getId())
                    .append(":")
                    .append(detail.getQuantity())
                    .append(":")
                    .append(detail.getUnitPrice());
            if (i < details.size() - 1) {
                detailsBuilder.append(";");
            }
        }

        return sale.getId() + ","
                + sale.getClient().getId() + ","
                + sale.getSeller().getId() + ","
                + sale.getDate() + ","
                + promoId + ","
                + detailsBuilder.toString();
    }

    /**
     * Saves the list of sales to the CSV file.
     *
     * @param sales list of sales to persist
     * @throws RuntimeException if an I/O error occurs during saving
     */
    public void saveAll(List<Sale> sales) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Sale sale : sales) {
                writer.write(saleToCsvLine(sale));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error saving sales: " + e.getMessage(), e);
        }
    }

    /**
     * Loads all sales from the CSV file using existing repositories to resolve dependencies.
     *
     * @param clients list of registered clients
     * @param sellers list of registered sellers
     * @param products list of registered products (consoles and video games)
     * @param accessories list of registered accessories
     * @param promotions list of registered promotions
     * @return list of reconstructed Sale objects
     * @throws RuntimeException if an I/O error occurs during loading
     */
    public List<Sale> loadAll(List<Client> clients,
                              List<Seller> sellers,
                              List<Product> products,
                              List<Accessory> accessories,
                              List<Promotion> promotions) {

        List<Sale> sales = new ArrayList<>();
        File file = new File(FILE_PATH);

        if (!file.exists()) {
            return sales;
        }

        // Quick lookup maps
        Map<String, Client> clientMap = new HashMap<>();
        for (Client c : clients) clientMap.put(c.getId(), c);

        Map<String, Seller> sellerMap = new HashMap<>();
        for (Seller s : sellers) sellerMap.put(s.getId(), s);

        Map<String, Product> productMap = new HashMap<>();
        for (Product p : products) productMap.put(p.getId(), p);
        for (Accessory a : accessories) productMap.put(a.getId(), a);

        Map<String, Promotion> promoMap = new HashMap<>();
        for (Promotion pr : promotions) promoMap.put(pr.getId(), pr);

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] parts = line.split(",", -1);
                if (parts.length < 6) continue;

                String saleId = parts[0];
                String clientId = parts[1];
                String sellerId = parts[2];
                LocalDate date = LocalDate.parse(parts[3]);
                String promoId = parts[4];
                String detailsStr = parts[5];

                Client client = clientMap.get(clientId);
                Seller seller = sellerMap.get(sellerId);

                if (client != null && seller != null) {
                    Sale sale = new Sale(saleId, client, seller, date);

                    if (!"NONE".equalsIgnoreCase(promoId) && promoMap.containsKey(promoId)) {
                        sale.setPromotion(promoMap.get(promoId));
                    }

                    if (!detailsStr.isEmpty()) {
                        String[] detailItems = detailsStr.split(";");
                        for (String item : detailItems) {
                            String[] itemParts = item.split(":");
                            if (itemParts.length == 3) {
                                String prodId = itemParts[0];
                                int quantity = Integer.parseInt(itemParts[1]);
                                double unitPrice = Double.parseDouble(itemParts[2]);

                                Product product = productMap.get(prodId);
                                if (product != null) {
                                    sale.addDetail(new SaleDetail(product, quantity, unitPrice));
                                }
                            }
                        }
                    }
                    sales.add(sale);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error loading sales: " + e.getMessage(), e);
        }

        return sales;
    }
}
