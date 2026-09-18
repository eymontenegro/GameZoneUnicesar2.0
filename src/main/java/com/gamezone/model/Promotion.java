
package com.gamezone.model;

import java.time.LocalDate;

/**
 * Abstract base class representing a commercial promotion.
 */
public abstract class Promotion {
    private String id;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;

    /**
     * Constructs a new Promotion with common attributes.
     *
     * @param id the unique promotion identifier
     * @param name the name of the promotion
     * @param startDate the validity start date
     * @param endDate the validity end date
     */
    public Promotion(String id, String name, LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    /**
     * Checks if the promotion is active on the given date.
     *
     * @param date the date to check against validity range
     * @return true if date is within range (inclusive), false otherwise
     */
    public boolean isActive(LocalDate date) {
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    /**
     * Calculates the discount amount in money for a given sale.
     *
     * @param sale the sale to calculate the discount for
     * @return discount amount in monetary terms
     */
    public abstract double calculateDiscount(Sale sale);
}
