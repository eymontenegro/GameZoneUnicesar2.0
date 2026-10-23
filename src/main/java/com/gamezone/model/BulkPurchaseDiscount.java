package com.gamezone.model;

import java.time.LocalDate;

/**
 * Concrete promotion that applies a percentage discount if total items reach or exceed a threshold.
 */
public class BulkPurchaseDiscount extends PercentageBasedPromotion {

    private int minimumQuantity;

    /**
     * Constructs a new BulkPurchaseDiscount promotion.
     *
     * @param id the promotion identifier
     * @param name the promotion name
     * @param startDate validity start date
     * @param endDate validity end date
     * @param minimumQuantity minimum total items required
     * @param percentage discount percentage between 0 and 100
     */
    public BulkPurchaseDiscount(String id, String name, LocalDate startDate, LocalDate endDate, int minimumQuantity, double percentage) {
        super(id, name, startDate, endDate);
        setMinimumQuantity(minimumQuantity);
        setPercentage(percentage);
    }

    public int getMinimumQuantity() {
        return minimumQuantity;
    }

    public void setMinimumQuantity(int minimumQuantity) {
        if (minimumQuantity <= 0) {
            throw new IllegalArgumentException("La cantidad mínima debe ser un número positivo mayor que 0.");
        }
        this.minimumQuantity = minimumQuantity;
    }

    @Override
    public double calculateDiscount(Sale sale) {
        if (sale == null || sale.getDetails() == null) {
            return 0.0;
        }

               if (sale.getTotalUnits() >= minimumQuantity) {

        return 0.0;
    }
}