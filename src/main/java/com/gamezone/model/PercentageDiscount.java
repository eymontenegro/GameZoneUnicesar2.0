
package com.gamezone.model;

import java.time.LocalDate;

/**
 * Concrete promotion that applies a global percentage discount on the sale subtotal.
 */
public class PercentageDiscount extends Promotion {
    private double percentage;

    /**
     * Constructs a new PercentageDiscount promotion.
     *
     * @param id the promotion identifier
     * @param name the promotion name
     * @param startDate validity start date
     * @param endDate validity end date
     * @param percentage discount percentage (e.g., 15.0 for 15%)
     */
    public PercentageDiscount(String id, String name, LocalDate startDate, LocalDate endDate, double percentage) {
        super(id, name, startDate, endDate);
        this.percentage = percentage;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    @Override
    public double calculateDiscount(Sale sale) {
        return sale.calculateTotal() * (percentage / 100.0);
    }
}