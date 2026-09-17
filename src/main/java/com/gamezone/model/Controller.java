
package com.gamezone.model;

import java.util.List;

/**
 * Concrete class representing a video game controller.
 */
public class Controller extends Accessory {
    private String connectionType;
    
    /**
     * Constructs a new Controller product with the specified details.
     *
     * @param id the unique product identifier
     * @param title the product title or name
     * @param price the price of the controller
     * @param stock the initial available stock quantity
     * @param compatibleConsoleIds list of console IDs compatible with this controller
     * @param connectionType the type of connection (e.g., "Wireless", "USB-C")
     */
    public Controller(String connectionType, String id, String title, double price, int stock, List<String> compatibleConsoleIds) {
        super(id, title, price, stock, compatibleConsoleIds);
        this.connectionType = connectionType;
    }
    /**
     * Gets the connection type of the controller.
     *
     * @return the connection type
     */
    public String getConnectionType() {
        return connectionType;
    }
    /**
     * Sets the connection type of the controller.
     *
     * @param connectionType the connection type to set
     */
    public void setConnectionType(String connectionType) {
        this.connectionType = connectionType;
    }   
    @Override
    public String describe() {
        return getTitle() + " es un control con conexion " + connectionType + ", precio: $" + getPrice() + ", stock: " + getStock();
    }
    
}
