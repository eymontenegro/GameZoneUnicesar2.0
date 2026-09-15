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
}
