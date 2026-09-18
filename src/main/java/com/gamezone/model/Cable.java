
package com.gamezone.model;

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
     * @param length the length of the cable in meters
     * @param connectorType the type of connector (e.g., "HDMI", "USB-C")
     */
    public Cable(String id, String title, double price, int stock, double length, String connectorType) {
        super(id, title, price, stock);
        this.length = length;
        this.connectorType = connectorType;
    }

    /**
     * Gets the length of the cable in meters.
     *
     * @return the cable length
     */
    public double getLength() {
        return length;
    }

    /**
     * Sets the length of the cable in meters.
     *
     * @param length the cable length to set
     */
    public void setLength(double length) {
        this.length = length;
    }

    /**
     * Gets the connector type of the cable.
     *
     * @return the connector type
     */
    public String getConnectorType() {
        return connectorType;
    }

    /**
     * Sets the connector type of the cable.
     *
     * @param connectorType the connector type to set
     */
    public void setConnectorType(String connectorType) {
        this.connectorType = connectorType;
    }

    /**
     * Provides a text description of the cable.
     *
     * @return a formatted description string
     */
    @Override
    public String describe() {
        return getTitle() + " es un cable de conector " + connectorType + " con longitud de " + length + "m, precio: $" + getPrice() + ", stock: " + getStock();
    }
}
