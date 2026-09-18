package com.gamezone.model;

import java.time.LocalDate;

/**
 * Concrete promotion that applies a percentage discount only to items matching a target category.
 */
public class CategoryDiscount extends Promotion {
    private double percentage;
    private String targetCategory;

    /**
     * Constructs a new CategoryDiscount promotion.
     *
     * @param id the promotion identifier
     * @param name the promotion name
     * @param startDate validity start date
     * @param endDate validity end date
     * @param percentage discount percentage
     * @param targetCategory target product category (e.g., "VIDEOGAME", "CONSOLE")
     */
    public CategoryDiscount(String id, String name, LocalDate startDate, LocalDate endDate, double percentage, String targetCategory) {
        super(id, name, startDate, endDate);
        this.percentage = percentage;
        this.targetCategory = targetCategory;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public String getTargetCategory() {
        return targetCategory;
    }

    public void setTargetCategory(String targetCategory) {
        this.targetCategory = targetCategory;
    }

    @Override
    public double calculateDiscount(Sale sale) {
        double applicableSubtotal = 0.0;
        for (SaleDetail detail : sale.getDetails()) {
            Product product = detail.getProduct();
            if (matchesCategory(product, targetCategory)) {
                applicableSubtotal += detail.getSubtotal();
            }
        }
        return applicableSubtotal * (percentage / 100.0);
    }

    /**
     * Checks whether the given product belongs to the target category.
     *
     * @param product the product to check
     * @param category the target category ("VIDEOGAME" or "CONSOLE")
     * @return true if the product matches the category
     */
    private boolean matchesCategory(Product product, String category) {
        if (category.equalsIgnoreCase("VIDEOGAME")) {
            return product instanceof VideoGame;
        } else if (category.equalsIgnoreCase("CONSOLE")) {
            return product instanceof Console;
        }
        return false;
    }
}
