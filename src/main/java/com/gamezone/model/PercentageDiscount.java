package com.gamezone.model;

import java.time.LocalDate;

/**
 * Concrete promotion that applies a global percentage discount on the sale subtotal.
 */
public class PercentageDiscount extends PercentageBasedPromotion {

    /**
     * Constructs a new PercentageDiscount promotion.
     *
     * @param id the promotion identifier
     * @param name the promotion name
     * @param startDate validity start date
     * @param endDate validity end date
     * @param percentage discount percentage between 0 and 100 (e.g., 15.0 for 15%)
     */
    public PercentageDiscount(String id, String name, LocalDate startDate, LocalDate endDate, double percentage) {
        super(id, name, startDate, endDate);
        setPercentage(percentage);
    }

    @Override
    public double calculateDiscount(Sale sale) {
        if (sale == null) {
            return 0.0;
        }
        return applyPercentage(sale.calculateTotal());
    }
}