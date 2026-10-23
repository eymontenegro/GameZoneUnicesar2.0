package com.gamezone.model;

import java.time.LocalDate;

/**
 * Concrete promotion that applies a percentage discount only to items matching a target category.
 */
public class CategoryDiscount extends PercentageBasedPromotion {

    private String targetCategory;

    /**
     * Constructs a new CategoryDiscount promotion.
     *
     * @param id             the promotion identifier
     * @param name           the promotion name
     * @param startDate      validity start date
     * @param endDate        validity end date
     * @param percentage     discount percentage between 0 and 100
     * @param targetCategory target product category (e.g., "VIDEOGAME", "CONSOLE", "ACCESSORY")
     */
    public CategoryDiscount(String id, String name, LocalDate startDate, LocalDate endDate, double percentage, String targetCategory) {
        super(id, name, startDate, endDate);
        setPercentage(percentage);
        setTargetCategory(targetCategory);
    }

    public String getTargetCategory() {
        return targetCategory;
    }

    public void setTargetCategory(String targetCategory) {
        if (targetCategory == null || targetCategory.trim().isEmpty()) {
            throw new IllegalArgumentException("La categoría objetivo no puede ser nula ni estar vacía.");
        }
        this.targetCategory = targetCategory;
    }

    @Override
    public double calculateDiscount(Sale sale) {
        if (sale == null || sale.getDetails() == null) {
            return 0.0;
        }

        double applicableSubtotal = 0.0;
        for (SaleDetail detail : sale.getDetails()) {
            if (detail != null && detail.getProduct() != null) {
                Product product = detail.getProduct();
                if (matchesCategory(product, targetCategory)) {
                    applicableSubtotal += detail.getSubtotal();
                }
            }
        }
        return applyPercentage(applicableSubtotal);
    }

    /**
     * Checks whether the given product belongs to the target category.
     *
     * @param product  the product to check
     * @param category the target category ("VIDEOGAME", "CONSOLE", "ACCESSORY")
     * @return true if the product matches the category
     */
    private boolean matchesCategory(Product product, String category) {
        if (product == null || category == null) {
            return false;
        }

                String cat = category.trim().toUpperCase();
        return product.getCategory().name().equals(cat);
    }
}