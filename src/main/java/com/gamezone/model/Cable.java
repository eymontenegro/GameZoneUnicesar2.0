
package com.gamezone.model;

import java.util.List;

/**
 * Concrete class representing a connection cable accessory.
 * Extends the abstract Accessory class.
 */
public class Cable extends Accessory {
    private double length;
    private String connectorType;
    
    /**
     * Constructs a new Cable accessory with the specified details.
     *
     * @param id the unique product identifier
     * @param title the product title or name
     * @param price the price of the cable
     * @param stock the initial available stock quantity
     * @param compatibleConsoleIds list of console IDs compatible with this cable
     * @param length the length of the cable in meters
     * @param connectorType the type of connector (e.g., "HDMI", "USB-C")
     */
    public Cable(double length, String connectorType, String id, String title, double price, int stock, List<String> compatibleConsoleIds) {
        super(id, title, price, stock, compatibleConsoleIds);
        this.length = length;
        this.connectorType = connectorType;
    }
    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public String getConnectorType() {
        return connectorType;
    }

    public void setConnectorType(String connectorType) {
        this.connectorType = connectorType;
    }

    @Override
    public String describe() {
        return getTitle() + " es un cable de conector " + connectorType + " con longitud de " + length + "m, precio: $" + getPrice() + ", stock: " + getStock();
    }

}
