package com.gamezone.persistence;

import com.gamezone.model.BasicWarranty;
import com.gamezone.model.ExtendedWarranty;
import com.gamezone.model.Product;
import com.gamezone.model.Sale;
import com.gamezone.model.Warranty;

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
 * Handles saving and loading Warranty objects (BasicWarranty and
 * ExtendedWarranty) to and from a CSV file, so the warranty catalog
 * persists between application runs. Reference resolution (Sale and
 * Product lookups) is done using the lists passed in by the caller,
 * keeping this class free of any dependency on the service layer.
 */
public class WarrantyRepository {

    private static final String FILE_PATH = "data/warranties.csv";

    /**
     * Converts a single Warranty into one CSV-formatted line, using a
     * type discriminator to distinguish basic from extended warranties.
     *
     * @param warranty the warranty to convert
     * @return a comma-separated line representing the warranty
     */
    private String warrantyToCsvLine(Warranty warranty) {
        String type = (warranty instanceof BasicWarranty) ? "BASIC" : "EXTENDED";
        return type + ","
                + warranty.getId() + ","
                + warranty.getProduct().getId() + ","
                + warranty.getSale().getId() + ","
                + warranty.getStartDate() + ","
                + warranty.getEndDate();
    }

    /**
     * Saves the full list of warranties to the CSV file, overwriting
     * any previous content.
     *
     * @param warranties the list of warranties to persist
     * @throws RuntimeException if an I/O error occurs while writing
     */
    public void saveAll(List<Warranty> warranties) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Warranty warranty : warranties) {
                writer.write(warrantyToCsvLine(warranty));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error saving warranties: " + e.getMessage(), e);
        }
    }

    /**
     * Reconstructs a single Warranty (BasicWarranty or ExtendedWarranty)
     * from one CSV-formatted line, resolving the product and sale
     * against the provided lookup maps. The end date is not read from
     * the file since each subclass recalculates it automatically from
     * the start date in its constructor.
     *
     * @param line the CSV line to parse
     * @param saleMap a lookup of sales by id
     * @param productMap a lookup of products by id
     * @return the reconstructed Warranty, or null if the product or
     *         sale could not be resolved, or the type is unrecognized
     */
    private Warranty csvLineToWarranty(String line, Map<String, Sale> saleMap, Map<String, Product> productMap) {
        String[] parts = line.split(",", -1);
        if (parts.length < 5) {
            return null;
        }

        String type = parts[0];
        String id = parts[1];
        String productId = parts[2];
        String saleId = parts[3];
        LocalDate startDate = LocalDate.parse(parts[4]);

        Product product = productMap.get(productId);
        Sale sale = saleMap.get(saleId);

        if (product == null || sale == null) {
            return null;
        }

        if (type.equals("BASIC")) {
            return new BasicWarranty(id, product, sale, startDate);
        } else if (type.equals("EXTENDED")) {
            return new ExtendedWarranty(id, product, sale, startDate);
        }

        return null;
    }

    /**
     * Loads the full list of warranties from the CSV file, resolving
     * each warranty's product and sale against the given lists. If the
     * file does not exist yet (first run), returns an empty list
     * instead of failing.
     *
     * @param sales the list of existing sales, used to resolve each warranty's sale
     * @param products the list of existing products, used to resolve each warranty's product
     * @return the list of warranties loaded from the file
     * @throws RuntimeException if an I/O error occurs while reading
     */
    public List<Warranty> loadAll(List<Sale> sales, List<Product> products) {
        List<Warranty> warranties = new ArrayList<>();
        File file = new File(FILE_PATH);

        if (!file.exists()) {
            return warranties;
        }

        Map<String, Sale> saleMap = new HashMap<>();
        for (Sale sale : sales) {
            saleMap.put(sale.getId(), sale);
        }

        Map<String, Product> productMap = new HashMap<>();
        for (Product product : products) {
            productMap.put(product.getId(), product);
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                Warranty warranty = csvLineToWarranty(line, saleMap, productMap);
                if (warranty != null) {
                    warranties.add(warranty);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error loading warranties: " + e.getMessage(), e);
        }

        return warranties;
    }
}