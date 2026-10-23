package com.gamezone.model;

import java.util.List;

/**
 * Concrete class representing a video game controller.
 * Inherits console compatibility from CompatibleAccessory.
 */
public class Controller extends CompatibleAccessory {

    private String connectionType;

    /**
     * Constructs a new Controller product with the specified details.
     *
     * @param id the unique product identifier
     * @param title the product title or name
     * @param price the price of the controller
     * @param stock the initial available stock quantity
     * @param connectionType the type of connection (e.g., "Wireless", "Wired")
     * @param compatibleConsoleIds list of console IDs compatible with this controller
     */
    public Controller(String id, String title, double price, int stock, String connectionType, List<String> compatibleConsoleIds) {
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

    /**
     * Provides a text description of the controller accessory.
     *
     * @return a formatted description string
     */
    @Override
    public String describe() {
        return String.format("%s es un control con conexión %s, precio: $%.2f, stock: %d",
                getTitle(), connectionType, getPrice(), getStock());
    }
}