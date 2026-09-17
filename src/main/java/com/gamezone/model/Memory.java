package com.gamezone.model;

import java.util.List;

/**
 * Concrete class representing a memory expansion product.
 * Extends the abstract Accessory class.
 */
public class Memory extends Accessory {
    private int capacity;
    private String type;

    /**
     * Constructs a new Memory accessory with the specified details.
     *
     * @param id the unique product identifier
     * @param title the product title or name
     * @param price the price of the memory card
     * @param stock the initial available stock quantity
     * @param compatibleConsoleIds list of console IDs compatible with this memory
     * @param capacity the storage capacity in GB
     * @param type the type of memory (e.g., "MicroSD", "SSD", "Proprietary Card")
     */
    public Memory(String id, String title, double price, int stock, List<String> compatibleConsoleIds, int capacity, String type) {
        super(id, title, price, stock, compatibleConsoleIds);
        this.capacity = capacity;
        this.type = type;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String describe() {
        return getTitle() + " es una memoria tipo " + type + " de " + capacity + "GB, precio: $" + getPrice() + ", stock: " + getStock();
    }
}
