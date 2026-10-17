package com.gamezone.model;

import java.util.Objects;

/**
 * Represents a detail line within a Sale.
 * It connects a Product with the purchased quantity and unit price.
 */
public class SaleDetail {

    private Product product;
    private int quantity;
    private double unitPrice;

   /**
     * Creates a new SaleDetail with the specified product, quantity, and unit price.
     *
     * @param product the product included in this sale detail
     * @param quantity the quantity of the product purchased
     * @param unitPrice the price per unit at the time of sale
     */
    public SaleDetail(Product product, int quantity, double unitPrice) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero.");
        }
        if (unitPrice < 0) {
            throw new IllegalArgumentException("El precio unitario no puede ser negativo.");
        }
        this.product = Objects.requireNonNull(product, "El producto no puede ser nulo.");
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

   /**
     * Returns the product associated with this sale detail.
     *
     * @return the product
     */
    public Product getProduct() {
        return product;
    }

    /**
     * Returns the quantity of the product purchased.
     *
     * @return the quantity
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Returns the unit price of the product at the time of sale.
     *
     * @return the unit price
     */
    public double getUnitPrice() {
        return unitPrice;
    }

    /**
     * Calculates and returns the subtotal for this sale detail.
     * Subtotal = quantity * unitPrice.
     *
     * @return the subtotal amount
     */
    public double getSubtotal() {
        return quantity * unitPrice;
    }
}
