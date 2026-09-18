package com.gamezone.model;

import java.time.LocalDate;

/**
 * Concrete promotion that applies a percentage discount if total items reach or exceed a threshold.
 */
public class BulkPurchaseDiscount extends Promotion {
    private int minimumQuantity;
    private double percentage;

    /**
     * Constructs a new BulkPurchaseDiscount promotion.
     *
     * @param id the promotion identifier
     * @param name the promotion name
     * @param startDate validity start date
     * @param endDate validity end date
     * @param minimumQuantity minimum total items required
     * @param percentage discount percentage
     */
    public BulkPurchaseDiscount(String id, String name, LocalDate startDate, LocalDate endDate, int minimumQuantity, double percentage) {
        super(id, name, startDate, endDate);
        this.minimumQuantity = minimumQuantity;
        this.percentage = percentage;
    }

    public int getMinimumQuantity() {
        return minimumQuantity;
    }

    public void setMinimumQuantity(int minimumQuantity) {
        this.minimumQuantity = minimumQuantity;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    @Override
    public double calculateDiscount(Sale sale) {
        int totalUnits = 0;
        for (SaleDetail detail : sale.getDetails()) {
            totalUnits += detail.getQuantity();
        }

        if (totalUnits >= minimumQuantity) {
            return sale.calculateTotal() * (percentage / 100.0);
        }

        return 0.0;
    }
}
