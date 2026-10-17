package com.gamezone.service;

import com.gamezone.exceptions.BusinessRuleException;
import com.gamezone.exceptions.InvalidDataException;
import com.gamezone.persistence.ProductRepository;
import com.gamezone.validation.ProductValidator;
import com.gamezone.model.Product;
import com.gamezone.model.VideoGame;
import com.gamezone.model.Console;

import java.util.ArrayList;
import java.util.List;

/**
 * Applies the business rules for managing products: registering new
 * video games and consoles, listing the inventory, consulting individual
 * products, and updating stock. Uses ProductRepository to keep the file in sync.
 */
public class ProductService {

    private List<Product> products;
    private ProductRepository repository;

    /**
     * Creates the service with a default ProductRepository instance and loads
     * any previously saved products from the file.
     */
    public ProductService() {
        this(new ProductRepository());
    }

    /**
     * Creates the service using the provided ProductRepository instance.
     *
     * @param repository the repository used to persist products
     */
    public ProductService(ProductRepository repository) {
        if (repository == null) {
            throw new InvalidDataException("repositorio", "no puede ser nulo");
        }
        this.repository = repository;
        List<Product> loadedProducts = repository.load();
        this.products = (loadedProducts != null) ? loadedProducts : new ArrayList<>();
    }

    /**
     * Validates basic product properties and ensures ID uniqueness.
     *
     * @param id    the product identifier
     * @param title the product title
     * @param price the product price
     * @param stock the initial stock
     * @throws InvalidDataException  if any field is invalid
     * @throws BusinessRuleException if a product with the same ID already exists
     */
    private void validateBasicData(String id, String title, double price, int stock) {
        ProductValidator.validateProductData(id, title, price, stock);
        if (existsId(id)) {
            throw new BusinessRuleException("Ya existe un producto con el ID: " + id);
        }
    }

    /**
     * Checks if a product with the given ID already exists.
     *
     * @param id the product ID to check
     * @return true if found, false otherwise
     */
    public boolean existsId(String id) {
        if (id == null) return false;
        for (Product product : products) {
            if (product != null && id.equalsIgnoreCase(product.getId())) {
                return true;
            }
        }
        return false;
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
    public void registerVideoGame(String id, String title, double price, int stock,
                                   String platform, String genre, String ageRating) {
        validateBasicData(id, title, price, stock);
        if (platform == null || platform.trim().isEmpty()) {
            throw new InvalidDataException("plataforma", "no puede estar vacía");
        }
        VideoGame newVideoGame = new VideoGame(platform, genre, ageRating, id, title, price, stock);
        products.add(newVideoGame);
        repository.saveAll(products);
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
    public void registerConsole(String id, String title, double price, int stock,
                                String brand, String model, String generation) {
        validateBasicData(id, title, price, stock);
        if (brand == null || brand.trim().isEmpty()) {
            throw new InvalidDataException("marca", "no puede estar vacía");
        }
        Console newConsole = new Console(brand, model, generation, id, title, price, stock);
        products.add(newConsole);
        repository.saveAll(products);
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
     * Returns a list containing only VideoGame products.
     *
     * @return list of video games
     */
    public List<VideoGame> listVideoGames() {
        List<VideoGame> result = new ArrayList<>();
        for (Product product : products) {
            if (product instanceof VideoGame) {
                result.add((VideoGame) product);
            }
        }
        return result;
    }

    /**
     * Returns a list containing only Console products.
     *
     * @return list of consoles
     */
    public List<Console> listConsoles() {
        List<Console> result = new ArrayList<>();
        for (Product product : products) {
            if (product instanceof Console) {
                result.add((Console) product);
            }
        }
        return result;
    }

    /**
     * Finds a single product by its ID.
     *
     * @param id the id of the product to find
     * @return the matching product
     * @throws IllegalArgumentException if no product with that id exists or if id is null
     */
    public Product findById(String id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID del producto no puede ser nulo.");
        }
        for (Product product : products) {
            if (product != null && id.equalsIgnoreCase(product.getId())) {
                return product;
            }
        }
        throw new IllegalArgumentException("Unknown product id : " + id);
    }

    /**
     * Reduces the stock of a specific product, identified by its id,
     * and saves the updated inventory to the file.
     *
     * @param productId the id of the product to update
     * @param quantity the quantity to subtract from the product's stock
     * @throws IllegalArgumentException if no product with that id exists or quantity is negative
     */
    public void updateStock(String productId, int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        Product product = findById(productId);
        product.reduceStock(quantity);
        repository.saveAll(products);
    }

    /**
     * Restores (increases) the stock of a specific product, identified by
     * its id, and saves the updated inventory to the file. Used when a
     * returned product becomes available for sale again.
     *
     * @param productId the id of the product to update
     * @param quantity the quantity to add back to the product's stock
     * @throws IllegalArgumentException if no product with that id exists, or if quantity is negative
     */
    public void restoreStock(String productId, int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        Product product = findById(productId);
        product.increaseStock(quantity);
        repository.saveAll(products);
    }
}