package com.gamezone.persistence;

import com.gamezone.model.Console;
import com.gamezone.model.Product;
import com.gamezone.model.VideoGame;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles saving and loading Product objects (VideoGame and Console)
 * to and from a CSV file, so the product catalog persists between
 * application runs.
 */
public class ProductRepository {

    private static final String FILE_PATH = "data/products.csv";

    /**
     * Converts a single product into one CSV-formatted line, choosing
     * which extra columns to include based on the product's real type.
     *
     * @param product the product to convert (VideoGame or Console)
     * @return a comma-separated line representing the product
     * @throws IllegalArgumentException if the product type is not recognized
     */
    private String productToCsvLine(Product product) {
        String commonData = product.getId() + "," + product.getTitle() + ","
                + product.getPrice() + "," + product.getStock();

        String type;
        String specificData;

        if (product instanceof VideoGame vg) {
            type = "VIDEOGAME";
            specificData = vg.getPlatform() + "," + vg.getGenre() + "," + vg.getAgeRating();

        } else if (product instanceof Console c) {
            type = "CONSOLE";
            specificData = c.getBrand() + "," + c.getModel() + "," + c.getGeneration();

        } else {
            throw new IllegalArgumentException("Unknown product type");
        }

        return type + "," + commonData + "," + specificData;
    }

    /**
     * Saves the full list of products to the CSV file, overwriting
     * any previous content.
     *
     * @param products the list of products to persist
     */
    public void saveAll(List<Product> products) {
        File file = new File(FILE_PATH);
        if (file.getParentFile() != null) {
            file.getParentFile().mkdirs();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            if (products != null) {
                for (Product product : products) {
                    if (product != null) {
                        writer.write(productToCsvLine(product));
                        writer.newLine();
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error saving products: " + e.getMessage(), e);
        }
    }

    /**
     * Reconstructs a single Product (VideoGame or Console) from one CSV line.
     *
     * @param line a single line read from the CSV file
     * @return the reconstructed Product object, or null if line is malformed/unrecognized
     */
    private Product csvLineToProduct(String line) {
        try {
            String[] parts = line.split(",", -1);
            if (parts.length < 8) {
                return null;
            }

            String type = parts[0].trim();
            String id = parts[1].trim();
            String title = parts[2].trim();
            double price = Double.parseDouble(parts[3].trim());
            int stock = Integer.parseInt(parts[4].trim());

            if ("VIDEOGAME".equalsIgnoreCase(type)) {
                String platform = parts[5].trim();
                String genre = parts[6].trim();
                String ageRating = parts[7].trim();
                return new VideoGame(platform, genre, ageRating, id, title, price, stock);

            } else if ("CONSOLE".equalsIgnoreCase(type)) {
                String brand = parts[5].trim();
                String model = parts[6].trim();
                String generation = parts[7].trim();
                return new Console(brand, model, generation, id, title, price, stock);
            }
        } catch (NumberFormatException e) {
            return null; // Skip malformed numerical values defensively
        }

        return null;
    }

    /**
     * Loads the full list of products from the CSV file. If the file
     * does not exist yet (first run), returns an empty list instead
     * of failing.
     *
     * @return the list of products loaded from the file
     */
    public List<Product> load() {
        List<Product> products = new ArrayList<>();
        File file = new File(FILE_PATH);

        if (!file.exists()) {
            return products;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                Product product = csvLineToProduct(line);
                if (product != null) {
                    products.add(product);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error loading products: " + e.getMessage(), e);
        }

        return products;
    }
}