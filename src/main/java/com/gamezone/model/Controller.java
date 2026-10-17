package com.gamezone.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Concrete class representing a video game controller.
 * Implements ConsoleCompatible to manage compatibility with consoles.
 */
public class Controller extends Accessory implements ConsoleCompatible {

    private String connectionType;
    private List<String> compatibleConsoleIds;

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
        super(id, title, price, stock);
        this.connectionType = connectionType;
        this.compatibleConsoleIds = (compatibleConsoleIds != null) ? new ArrayList<>(compatibleConsoleIds) : new ArrayList<>();
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
     * Gets a copy of the list of compatible console identifiers.
     *
     * @return a list containing the compatible console IDs
     */
    @Override
    public List<String> getCompatibleConsoleIds() {
        return new ArrayList<>(this.compatibleConsoleIds);
    }

    /**
     * Adds a new console identifier to the compatibility list if not already present.
     *
     * @param consoleId the console ID to add
     */
    @Override
    public void addCompatibleConsole(String consoleId) {
        if (this.compatibleConsoleIds == null) {
            this.compatibleConsoleIds = new ArrayList<>();
        }
        if (consoleId != null && !consoleId.trim().isEmpty() && !this.compatibleConsoleIds.contains(consoleId)) {
            this.compatibleConsoleIds.add(consoleId);
        }
    }

    /**
     * Checks if this controller is compatible with the given console ID.
     *
     * @param consoleId the console ID to verify
     * @return true if compatible, false otherwise
     */
    @Override
    public boolean isCompatibleWith(String consoleId) {
        return this.compatibleConsoleIds != null && this.compatibleConsoleIds.contains(consoleId);
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