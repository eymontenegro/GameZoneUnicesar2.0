package com.gamezone.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Abstract base class representing a commercial promotion in the GameZone system.
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
        this.id = Objects.requireNonNull(id, "Promotion ID cannot be null.");
        this.name = Objects.requireNonNull(name, "Promotion name cannot be null.");
        this.startDate = Objects.requireNonNull(startDate, "Start date cannot be null.");
        this.endDate = Objects.requireNonNull(endDate, "End date cannot be null.");
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
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
        if (date == null) {
            date = LocalDate.now();
        }
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    /**
     * Calculates the monetary discount amount for a given sale.
     *
     * @param sale the sale transaction to calculate the discount for
     * @return discount amount in pesos
     */
    public abstract double calculateDiscount(Sale sale);
}