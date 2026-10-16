package com.gamezone.persistence;

import com.gamezone.model.Product;
import com.gamezone.model.Return;
import com.gamezone.model.Sale;
import com.gamezone.model.SaleDetail;

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
 * Handles saving and loading Return objects to and from a CSV file,
 * so the return history persists between application runs.
 * Reference resolution (Sale and Product lookups) is done using the lists
 * passed in by the caller.
 */
public class ReturnRepository {

    private static final String FILE_PATH = "data/returns.csv";

    /**
     * Converts a single Return into one CSV-formatted line.
     *
     * @param returnObj the return to convert
     * @return a comma-separated line representing the return
     */
    private String returnToCsvLine(Return returnObj) {
        StringBuilder productIdsBuilder = new StringBuilder();
        List<Product> returnedProducts = returnObj.getReturnedProducts();

        if (returnedProducts != null) {
            for (int i = 0; i < returnedProducts.size(); i++) {
                Product product = returnedProducts.get(i);
                if (product != null) {
                    productIdsBuilder.append(product.getId());
                    if (i < returnedProducts.size() - 1) {
                        productIdsBuilder.append(";");
                    }
                }
            }
        }

        String saleId = (returnObj.getOriginalSale() != null) ? returnObj.getOriginalSale().getId() : "";

        return returnObj.getId() + ","
                + saleId + ","
                + returnObj.getDate() + ","
                + returnObj.getReason() + ","
                + returnObj.getRefundAmount() + ","
                + productIdsBuilder.toString();
    }

    /**
     * Saves the full list of returns to the CSV file, overwriting any previous content.
     *
     * @param returns the list of returns to persist
     */
    public void saveAll(List<Return> returns) {
        File file = new File(FILE_PATH);
        if (file.getParentFile() != null) {
            file.getParentFile().mkdirs();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            if (returns != null) {
                for (Return returnObj : returns) {
                    if (returnObj != null && returnObj.getOriginalSale() != null) {
                        writer.write(returnToCsvLine(returnObj));
                        writer.newLine();
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error saving returns: " + e.getMessage(), e);
        }
    }

    /**
     * Finds a product by id among the details of the given sale.
     *
     * @param sale      the sale whose details are searched
     * @param productId the id of the product to find
     * @return the matching product, or null if not found in the sale
     */
    private Product findProductInSale(Sale sale, String productId) {
        if (sale == null || sale.getDetails() == null) {
            return null;
        }

        for (SaleDetail detail : sale.getDetails()) {
            if (detail != null && detail.getProduct() != null && detail.getProduct().getId().equals(productId)) {
                return detail.getProduct();
            }
        }
        return null;
    }

    /**
     * Reconstructs a single Return from one CSV line.
     *
     * @param line    a single line read from the CSV file
     * @param saleMap map of existing sales indexed by ID
     * @return the reconstructed Return object, or null if invalid or missing sale
     */
    private Return csvLineToReturn(String line, Map<String, Sale> saleMap) {
        try {
            String[] parts = line.split(",", -1);
            if (parts.length < 6) {
                return null;
            }

            String id = parts[0].trim();
            String saleId = parts[1].trim();
            LocalDate date = LocalDate.parse(parts[2].trim());
            String reason = parts[3].trim();
            double refundAmount = Double.parseDouble(parts[4].trim());
            String productIdsStr = parts[5].trim();

            Sale sale = saleMap.get(saleId);
            if (sale == null) {
                return null;
            }

            List<Product> returnedProducts = new ArrayList<>();
            if (!productIdsStr.isEmpty()) {
                String[] productIds = productIdsStr.split(";");
                for (String productId : productIds) {
                    Product product = findProductInSale(sale, productId.trim());
                    if (product != null) {
                        returnedProducts.add(product);
                    }
                }
            }

            return new Return(id, date, sale, returnedProducts, reason, refundAmount);
        } catch (NumberFormatException | DateTimeParseException e) {
            return null; // Skip malformed lines defensively
        }
    }

    /**
     * Loads the full list of returns from the CSV file.
     *
     * @param sales the list of existing sales, used to resolve each return's original sale
     * @return the list of returns loaded from the file
     */
    public List<Return> loadAll(List<Sale> sales) {
        List<Return> returns = new ArrayList<>();
        File file = new File(FILE_PATH);

        if (!file.exists()) {
            return returns;
        }

        // Map for fast O(1) sale lookup
        Map<String, Sale> saleMap = new HashMap<>();
        if (sales != null) {
            for (Sale sale : sales) {
                if (sale != null) {
                    saleMap.put(sale.getId(), sale);
                }
            }
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                Return returnObj = csvLineToReturn(line, saleMap);
                if (returnObj != null) {
                    returns.add(returnObj);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error loading returns: " + e.getMessage(), e);
        }

        return returns;
    }
}