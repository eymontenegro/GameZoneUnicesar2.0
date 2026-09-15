package com.gamezone.persistence;

import com.gamezone.model.Product;
import com.gamezone.model.VideoGame;
import com.gamezone.model.Console;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.io.File;
/**
 * Handles saving and loading Product objects (VideoGame and Console)
 * to and from a CSV file, so the product catalog persists between
 * application runs.
 */  
public class ProductRepository {
     /**
     * Converts a single product into one CSV-formatted line, choosing
     * which extra columns to include based on the product's real type.
     *
     * @param product the product to convert (VideoGame or Console)
     * @return a comma-separated line representing the product
     * @throws IllegalArgumentException if the product type is not recognized
     */
    private String productToCsvLine(Product product){
        String commonData = product.getId() + "," + product.getTitle() + "," + product.getPrice() + "," + product.getStock();
        
        String type;
        String specificData;
        
        if (product instanceof VideoGame){
            VideoGame vg = (VideoGame) product;
            type = "VIDEOGAME";
            specificData = vg.getPlatform() + "," + vg.getGenre() + "," + vg.getAgeRating();
            
        }else if (product instanceof Console){
            Console c =(Console) product;
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
     * @throws RuntimeException if an I/O error occurs while writing
     */
    public void save(List<Product> products) {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter("data/products.csv"))) {
        for (Product product : products) {
            writer.write(productToCsvLine(product));
            writer.newLine();
        }
    } catch (IOException e) {
        
      throw new RuntimeException("Error saving products: " + e.getMessage(), e);
    }
}
    
    /**
     * Reconstructs a single Product (VideoGame or Console) from one
     * CSV-formatted line, based on the type indicated in the first column.
     *
     * @param line a single line read from the CSV file
     * @return the reconstructed Product object
     * @throws IllegalArgumentException if the product type is not recognized
     */
    private Product csvLineToProduct(String line) {
    String[] parts = line.split(",");
    String type = parts[0];
    String id = parts[1];
    String title = parts[2];
    double price = Double.parseDouble(parts[3]);
    int stock = Integer.parseInt(parts[4]);
    
    
        if (type.equals("VIDEOGAME")) {
        String platform = parts[5];
        String genre = parts[6];
        String ageRating = parts[7];
        return new VideoGame(platform, genre, ageRating, id, title, price, stock);

    } else if (type.equals("CONSOLE")) {
     String brand = parts[5];
        String model = parts[6];
        String generation = parts[7];
        return new Console(brand, model, generation, id, title, price, stock); 
        
    } else {
        throw new IllegalArgumentException("Unknown product type: " + type);
    }
 }
    
    /**
     * Loads the full list of products from the CSV file. If the file
     * does not exist yet (first run), returns an empty list instead
     * of failing.
     *
     * @return the list of products loaded from the file
     * @throws RuntimeException if an I/O error occurs while reading
     */
    public List<Product> load() {
    List<Product> products = new ArrayList<>();
    File file = new File("data/products.csv");

    if (!file.exists()) {
        return products; 
    }

    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
        
        String line;
        while ((line = reader.readLine()) != null) {
            products.add(csvLineToProduct(line));
        }
    } catch (IOException e) {
        throw new RuntimeException("Error loading products: " + e.getMessage(), e);
    }

    return products;
}
}

