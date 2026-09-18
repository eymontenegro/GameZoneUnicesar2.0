
package com.gamezone.model;

/**
 * Abstract class representing a general game accessory.
 * Extends the base Product class.
 */
public abstract class Accessory extends Product {
/**
     * Creates a new accessory with common product attributes and compatible console IDs.
     *
     * @param id the product identifier
     * @param title the product title
     * @param price the product price
     * @param stock the initial available stock
     */
    public Accessory(String id, String title, double price, int stock) {
        super(id, title, price, stock);
    }
  }
 
     
     
  
     
    
    
