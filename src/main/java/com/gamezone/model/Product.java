package com.gamezone.model;

/**
 * Represents a generic product sold by the store.
 * This is the base class for all product types (video games, consoles, accessories),
 * and holds the attributes and behavior common to any product.
 */
public abstract class Product {

    private String id;
    private String title;
    private double price;
    private int stock;

    /**
     * Constructs a new Product with base attributes.
     *
     * @param id the unique product identifier
     * @param title the title or commercial name of the product
     * @param price the unit selling price
     * @param stock the available inventory quantity
     */
    public Product(String id, String title, double price, int stock) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.stock = stock;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public double getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    /**
     * Builds a description of the product, combining common and
     * particular attributes. Each subclass provides its own version.
     *
     * @return a text description of the product
     */
    public abstract String describe();
    
    /**
     * Returns the commercial category this product belongs to.
     *
     * @return the product category
     */
    public abstract ProductCategory getCategory();
    
    /**
     * Tells whether there is enough stock to serve the given quantity.
     *
     * @param quantity the number of units requested
     * @return true if the available stock covers the quantity
     */
    public boolean hasStockFor(int quantity) {
        return stock >= quantity;
    }
    /**
     * Reduces the available stock by the given quantity.
     *
     * @param quantity the number of units to subtract from stock
     * @throws IllegalArgumentException if quantity is less than or equal to zero,
     *                                  or greater than available stock
     */
    public void reduceStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("La cantidad a descontar debe ser mayor que cero.");
        }
         if (!hasStockFor(quantity)){
            throw new IllegalArgumentException("Stock insuficiente: disponible " + stock + ", solicitado " + quantity);
        }
        this.stock -= quantity;
    }

    /**
     * Increases the available stock by the given quantity. Used when a
     * previously sold product is returned and becomes available for
     * sale again.
     *
     * @param quantity the number of units to add to stock
     * @throws IllegalArgumentException if quantity is negative or zero
     */
    public void increaseStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("La cantidad a restaurar debe ser mayor que cero.");
        }
        this.stock += quantity;
    }
}