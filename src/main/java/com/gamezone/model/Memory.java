package com.gamezone.model;

import java.util.List;

/**
 * Concrete class representing a memory expansion product.
 * Inherits console compatibility from CompatibleAccessory.
 */
public class Memory extends CompatibleAccessory {

    private int capacity;
    private String type;

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
        super(id, title, price, stock, compatibleConsoleIds);
        this.capacity = capacity;
        this.type = type;
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
     * Returns the kind of this accessory.
     *
     * @return {@code "MEMORY"}
     */
    @Override
    public String getAccessoryKind() {
        return "MEMORY";
    }

    /**
     * Provides a text description of the memory accessory.
     *
     * @return a formatted description string
     */
    @Override
    public String describe() {
        return String.format("%s es una memoria tipo %s de %dGB, precio: $%.2f, stock: %d",
                getTitle(), type, capacity, getPrice(), getStock());
    }
}