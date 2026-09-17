
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
        this.compatibleConsoleIds = (compatibleConsoleIds != null) ? compatibleConsoleIds : new ArrayList<>();
    }

    public String getConnectionType() {
        return connectionType;
    }

    public void setConnectionType(String connectionType) {
        this.connectionType = connectionType;
    }

    @Override
    public List<String> getCompatibleConsoleIds() {
        return new ArrayList<>(this.compatibleConsoleIds);
    }

    @Override
    public void addCompatibleConsole(String consoleId) {
        if (this.compatibleConsoleIds == null) {
            this.compatibleConsoleIds = new ArrayList<>();
        }
        if (consoleId != null && !this.compatibleConsoleIds.contains(consoleId)) {
            this.compatibleConsoleIds.add(consoleId);
        }
    }

    @Override
    public boolean isCompatibleWith(String consoleId) {
        return this.compatibleConsoleIds != null && this.compatibleConsoleIds.contains(consoleId);
    }

    @Override
    public String describe() {
        return getTitle() + " es un control con conexion " + connectionType + ", precio: $" + getPrice() + ", stock: " + getStock();
    }
}