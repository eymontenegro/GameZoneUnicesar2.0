package com.gamezone.persistence;

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
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handles persistence operations for {@link Sale} objects to and from a CSV file.
 */
public class SaleRepository {

    private static final String FILE_PATH = "data/sales.csv";

    /**
     * Converts a single {@link Sale} object into a CSV-formatted string line.
     *
     * @param sale the sale instance to convert
     * @return a CSV-formatted representation of the sale
     */
    private String saleToCsvLine(Sale sale) {
        List<String> detailsFormatted = new ArrayList<>();
        if (sale.getDetails() != null) {
            for (SaleDetail detail : sale.getDetails()) {
                if (detail != null && detail.getProduct() != null) {
                    detailsFormatted.add(detail.getProduct().getId() + ":" 
                            + detail.getQuantity() + ":" 
                            + detail.getUnitPrice());
                }
            }
        }
        String detailsData = String.join(";", detailsFormatted);

        String sellerId = (sale.getSeller() != null) ? sale.getSeller().getId() : "";
        String clientId = (sale.getClient() != null) ? sale.getClient().getId() : "";

        return sale.getId() + "," 
                + sale.getDate() + "," 
                + sellerId + "," 
                + clientId + "," 
                + sale.getAppliedPromotionName() + "," 
                + sale.getDiscountAmount() + "," 
                + sale.getAdditionalCosts() + "," 
                + sale.getTotal() + "," 
                + detailsData;
    }

    /**
     * Saves all sales from the provided list to the CSV file, overwriting existing content.
     *
     * @param sales the list of {@link Sale} objects to persist
     * @throws RuntimeException if an I/O error occurs during the writing process
     */
    public void saveAll(List<Sale> sales) {
        File file = new File(FILE_PATH);
        if (file.getParentFile() != null) {
            file.getParentFile().mkdirs();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            if (sales != null) {
                for (Sale sale : sales) {
                    if (sale != null) {
                        writer.write(saleToCsvLine(sale));
                        writer.newLine();
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error saving sales: " + e.getMessage(), e);
        }
    }

    /**
     * Loads and reconstructs all sales from the CSV file.
     * <p>
     * Converts the provided list domain models into internal lookup maps to quickly resolve 
     * and bind associated {@link Client}, {@link Seller}, and {@link Product} references.
     * </p>
     *
     * @param clients     list of system clients
     * @param sellers     list of system sellers
     * @param products    list of main products (video games and consoles)
     * @param accessories list of accessories
     * @param promotions  list of active/registered promotions
     * @return a reconstructed list of {@link Sale} objects
     * @throws RuntimeException if an I/O error occurs during file reading
     */
    public List<Sale> loadAll(List<Client> clients,
                              List<Seller> sellers,
                              List<? extends Product> products,
                              List<? extends Product> accessories,
                              List<? extends Promotion> promotions) {

        List<Sale> sales = new ArrayList<>();
        File file = new File(FILE_PATH);

        if (!file.exists()) {
            return sales;
        }

        // 1. Map Clients by ID for O(1) lookup
        Map<String, Client> clientMap = new HashMap<>();
        if (clients != null) {
            for (Client c : clients) {
                if (c != null && c.getId() != null) {
                    clientMap.put(c.getId(), c);
                }
            }
        }

        // 2. Map Sellers by ID for O(1) lookup
        Map<String, Seller> sellerMap = new HashMap<>();
        if (sellers != null) {
            for (Seller s : sellers) {
                if (s != null && s.getId() != null) {
                    sellerMap.put(s.getId(), s);
                }
            }
        }

        // 3. Map full catalog (Products & Accessories) by ID
        Map<String, Product> productMap = new HashMap<>();
        if (products != null) {
            for (Product p : products) {
                if (p != null && p.getId() != null) {
                    productMap.put(p.getId(), p);
                }
            }
        }
        if (accessories != null) {
            for (Product a : accessories) {
                if (a != null && a.getId() != null) {
                    productMap.put(a.getId(), a);
                }
            }
        }

        // 4. Parse CSV file and reconstruct Sale instances
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] parts = line.split(",", -1);
                if (parts.length >= 9) {
                    try {
                        String id = parts[0].trim();
                        LocalDate date = LocalDate.parse(parts[1].trim());
                        String sellerId = parts[2].trim();
                        String clientId = parts[3].trim();
                        String promoName = parts[4].trim();
                        double discountAmount = Double.parseDouble(parts[5].trim());
                        double additionalCosts = Double.parseDouble(parts[6].trim());
                        double total = Double.parseDouble(parts[7].trim());
                        String detailsData = parts[8].trim();

                        Seller seller = sellerMap.get(sellerId);
                        Client client = clientMap.get(clientId);

                        if (seller != null && client != null) {
                            Sale sale = new Sale(id, client, seller, date);
                            
                            sale.setAppliedPromotionName(promoName);
                            sale.setDiscountAmount(discountAmount);
                            sale.setAdditionalCosts(additionalCosts);
                            sale.setTotal(total);

                            // Reconstruct sale items (SaleDetail)
                            if (!detailsData.isEmpty()) {
                                String[] detailsArr = detailsData.split(";");
                                for (String detailStr : detailsArr) {
                                    String[] itemParts = detailStr.split(":");
                                    if (itemParts.length >= 3) {
                                        String productId = itemParts[0].trim();
                                        int quantity = Integer.parseInt(itemParts[1].trim());
                                        double unitPrice = Double.parseDouble(itemParts[2].trim());

                                        Product product = productMap.get(productId);
                                        if (product != null) {
                                            sale.addDetail(new SaleDetail(product, quantity, unitPrice));
                                        }
                                    }
                                }
                            }

                            sales.add(sale);
                        }
                    } catch (NumberFormatException | DateTimeParseException e) {
                        // Skip corrupted or unparseable lines defensively
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error loading sales: " + e.getMessage(), e);
        }

        return sales;
    }
}