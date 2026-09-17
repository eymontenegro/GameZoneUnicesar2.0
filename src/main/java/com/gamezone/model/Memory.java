package com.gamezone.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Concrete class representing a memory expansion product.
 * Extends the abstract Accessory class and implements ConsoleCompatible.
 */
public class Memory extends Accessory implements ConsoleCompatible {
    private int capacity;
    private String type;
    private List<String> compatibleConsoleIds;

    /**
     * Constructs a new Memory accessory with the specified details.
     *
     * @param id the unique product identifier
     * @param title the product title or name
     * @param price the price of the memory card
     * @param stock the initial available stock quantity
     * @param capacity the storage capacity in GB
     * @param type the type of memory (e.g., "MicroSD", "SSD", "Proprietary Card")
     * @param compatibleConsoleIds list of console IDs compatible with this memory
     */
    public Memory(String id, String title, double price, int stock, int capacity, String type, List<String> compatibleConsoleIds) {
        super(id, title, price, stock);
        this.capacity = capacity;
        this.type = type;
        this.compatibleConsoleIds = (compatibleConsoleIds != null) ? compatibleConsoleIds : new ArrayList<>();
    }

    /**
     * Gets the storage capacity in GB.
     *
     * @return the capacity in GB
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * Sets the storage capacity in GB.
     *
     * @param capacity the capacity to set
     */
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    /**
     * Gets the memory type.
     *
     * @return the type of memory
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the memory type.
     *
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
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
        if (consoleId != null && !this.compatibleConsoleIds.contains(consoleId)) {
            this.compatibleConsoleIds.add(consoleId);
        }
    }

    /**
     * Checks if this memory is compatible with the given console ID.
     *
     * @param consoleId the console ID to verify
     * @return true if compatible, false otherwise
     */
    @Override
    public boolean isCompatibleWith(String consoleId) {
        return this.compatibleConsoleIds != null && this.compatibleConsoleIds.contains(consoleId);
    }

    /**
     * Provides a text description of the memory accessory.
     *
     * @return a formatted description string
     */
    @Override
    public String describe() {
        return getTitle() + " es una memoria tipo " + type + " de " + capacity + "GB, precio: $" + getPrice() + ", stock: " + getStock();
    }
}
