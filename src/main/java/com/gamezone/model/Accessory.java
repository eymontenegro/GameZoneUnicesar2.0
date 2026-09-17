
package com.gamezone.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class representing a general game accessory.
 * Extends the base Product class.
 */
public abstract class Accessory extends Product {
     private List<String> compatibleConsoleIds;
 
 /**
     * Creates a new accessory with common product attributes and compatible console IDs.
     *
     * @param id the product identifier
     * @param title the product title
     * @param price the product price
     * @param stock the initial available stock
     * @param compatibleConsoleIds list of console IDs this accessory is compatible with
     */
    public Accessory(String id, String title, double price, int stock) {
        super(id, title, price, stock);
       this.compatibleConsoleIds = (compatibleConsoleIds != null) ? compatibleConsoleIds : new ArrayList<>(); 
    }
    /**
 * Returns the list of console ids this accessory is compatible with.
 *
 * @return the list of compatible console ids
 */
    public List<String> getCompatibleConsoleIds() {
        return compatibleConsoleIds;
    }
    /**
     * Sets the list of compatible console ids.
     *
     * @param compatibleConsoleIds list of console ids
     */
    public void setCompatibleConsoleIds(List<String> compatibleConsoleIds) {
        this.compatibleConsoleIds = compatibleConsoleIds;
    }
 /**
 * Registers a console as compatible with this accessory.
 *
 * @param consoleId the id of the console to add
 */   
    public void addCompatibleConsole(String consoleId) {
        if (this.compatibleConsoleIds == null) {
            this.compatibleConsoleIds = new ArrayList<>();
        }
        this.compatibleConsoleIds.add(consoleId);
    }
  /**
 * Checks whether this accessory is compatible with the given console.
 *
 * @param consoleId the id of the console to check
 * @return true if the console id is in the compatible list
 */  
  public boolean isCompatibleWith(String consoleId) {
        return this.compatibleConsoleIds != null && this.compatibleConsoleIds.contains(consoleId);
    }
} 
    
