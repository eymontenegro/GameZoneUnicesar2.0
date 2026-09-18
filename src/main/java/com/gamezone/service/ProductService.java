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
 /**
     * Returns the full list of currently available products.
     *
     * @return the list of products in inventory
     */

public List<Product> listAll() {
    return products;
}
/**
     * Reduces the stock of a specific product, identified by its id,
     * and saves the updated inventory to the file.
     *
     * @param productId the id of the product to update
     * @param quantity the quantity to subtract from the product's stock
     * @throws IllegalArgumentException if no product with that id exists
     */
public void updateStock(String productId, int quantity) {
    for (Product product : products) {
        if (product.getId().equals(productId)) {
            product.reduceStock(quantity);
            repository.save(products);
            return; 
        }
         } 
        throw new IllegalArgumentException("Unknown product id : " + productId);
    }

/**
     * Restores (increases) the stock of a specific product, identified by
     * its id, and saves the updated inventory to the file. Used when a
     * returned product becomes available for sale again.
     *
     * @param productId the id of the product to update
     * @param quantity the quantity to add back to the product's stock
     * @throws IllegalArgumentException if no product with that id exists,
     *                                  or if the quantity is negative
     */
public void restoreStock(String productId, int quantity) {
    if (quantity < 0) {
        throw new IllegalArgumentException("Quantity cannot be negative");
    }
    for (Product product : products) {
        if (product.getId().equals(productId)) {
            product.increaseStock(quantity);
            repository.save(products);
            return;
        }
    }
    throw new IllegalArgumentException("Unknown product id: " + productId);
}
}
