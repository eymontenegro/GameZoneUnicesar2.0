package com.gamezone.service;
 
import com.gamezone.persistence.ProductRepository;
import com.gamezone.model.Product;
import java.util.List;
import com.gamezone.model.VideoGame;
import com.gamezone.model.Console;


/**
 * Applies the business rules for managing products: registering new
 * video games and consoles, listing the inventory, and updating stock.
 * Uses ProductRepository to keep the file in sync with every change.
 */
public class ProductService {
     private List<Product> products;
     private ProductRepository repository;

    /**
    * Creates the service and loads any previously saved products
    * from the file, so the inventory is available as soon as the
    * application starts.
    */
    public ProductService() {
        this.repository = new ProductRepository();
        this.products = repository.load();
    }
    
   /**
     * Registers a new video game and immediately saves the updated
     * inventory to the file.
     *
     * @param id the product identifier
     * @param title the product title
     * @param price the product price
     * @param stock the initial available stock
     * @param platform the platform the video game runs on
     * @param genre the video game's genre
     * @param ageRating the recommended age rating
     */
    
    public void registerVideoGame(String id, String title, double price, int stock, String platform, String genre, String ageRating) {
    VideoGame newVideoGame = new VideoGame( platform, genre, ageRating, id, title, price, stock);
    products.add(newVideoGame);
    repository.save(products); 
}
    
     /**
     * Registers a new console and immediately saves the updated
     * inventory to the file.
     *
     * @param id the product identifier
     * @param title the product title
     * @param price the product price
     * @param stock the initial available stock
     * @param brand the console's brand
     * @param model the console's model
     * @param generation the console's generation
     */

public void registerConsole(String id, String title, double price, int stock, String brand, String model, String generation) {
    Console newConsole = new Console( brand, model, generation, id, title, price, stock);
    products.add(newConsole);
    repository.save(products);
 }
}
