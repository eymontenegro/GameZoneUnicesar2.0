package com.gamezone.model;
/**
 * Represents a generic product sold by the store.
 * This is the base class for all product types (video games, consoles),
 * and holds the attributes and behavior common to any product.
 */
public abstract class Product {
    private String id;
    private String title;
    private double price;
    private int stock;

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
     * Reduces the available stock by the given quantity.
     *
     * @param quantity the number of units to subtract from stock
     * @throws IllegalArgumentException if quantity is greater than the available stock
     */
   public void reduceStock (int quantity ){
       if(stock < quantity){
           throw new IllegalArgumentException(" Stock insuficiente: disponible " + stock + " solicitado " + quantity );
       }
       stock = stock - quantity;
   }

   /**
     * Increases the available stock by the given quantity. Used when a
     * previously sold product is returned and becomes available for
     * sale again.
     *
     * @param quantity the number of units to add to stock
     * @throws IllegalArgumentException if quantity is negative
     */
   public void increaseStock (int quantity ){
       if(quantity < 0){
           throw new IllegalArgumentException(" La cantidad a restaurar no puede ser negativa ");
       }
       stock = stock + quantity;
   }
     
}
