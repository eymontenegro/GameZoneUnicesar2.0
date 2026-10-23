package com.gamezone.model;

import java.time.LocalDate;

/**
 * Base class for promotions whose discount is a percentage of some amount
 * of the sale. It owns the percentage, its valid range and the way it is
 * applied, so that rule is defined in a single place.
 */
public abstract class PercentageBasedPromotion extends Promotion {

    private double percentage;

    /**
     * Constructs the common promotion data. Subclasses set the percentage
     * with {@link #setPercentage(double)} so its range is validated.
     *
     * @param id        the promotion identifier
     * @param name      the promotion name
     * @param startDate validity start date
     * @param endDate   validity end date
     */
    protected PercentageBasedPromotion(String id, String name, LocalDate startDate, LocalDate endDate) {
        super(id, name, startDate, endDate);
    }

    /**
     * Gets the discount percentage.
     *
     * @return the percentage between 0 and 100
     */
    public double getPercentage() {
        return percentage;
    }

    /**
     * Sets the discount percentage.
     *
     * @param percentage discount percentage between 0 and 100
     * @throws IllegalArgumentException if the percentage is out of range
     */
    public void setPercentage(double percentage) {
        if (percentage < 0.0 || percentage > 100.0) {
            throw new IllegalArgumentException("El porcentaje de descuento debe estar entre 0 y 100.");
        }
        this.percentage = percentage;
    }

    /**
     * Applies the percentage to the given base amount.
     *
     * @param base the amount the discount is calculated on
     * @return the discount amount
     */
    protected double applyPercentage(double base) {
        return base * (percentage / 100.0);
    }
}
