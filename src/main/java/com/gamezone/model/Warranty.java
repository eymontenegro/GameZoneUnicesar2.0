
package com.gamezone.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Abstract base class representing a product warranty in the system.
 * Encapsulates common attributes and behavior for both basic and extended warranties.
 */
public abstract class Warranty {

    private String id;
    private Product product;
    private Sale sale;
    private LocalDate startDate;
    private LocalDate endDate;

    /**
     * Constructs a new Warranty and automatically computes its end date
     * based on the concrete subclass duration.
     *
     * @param id        the unique identifier of the warranty
     * @param product   the product covered by the warranty
     * @param sale      the sale transaction associated with the warranty
     * @param startDate the starting date of the warranty
     */
    public Warranty(String id, Product product, Sale sale, LocalDate startDate) {
        this.id = id;
        this.product = product;
        this.sale = sale;
        this.startDate = startDate;
        this.endDate = startDate.plusMonths(getDurationInMonths());
    }

    /**
     * Returns the duration of the warranty in months.
     *
     * @return duration in months
     */
    public abstract int getDurationInMonths();

    /**
     * Returns the string representation of the warranty type.
     *
     * @return the warranty type name
     */
    public abstract String getWarrantyType();

    /**
     * Returns any additional cost associated with this warranty type.
     *
     * @return additional cost amount
     */
    public abstract double getAdditionalCost();

    /**
     * Checks if the warranty is active on a given date.
     *
     * @param date the date to check against the warranty period
     * @return true if the date is on or between start and end dates, false otherwise
     */
    public boolean isActive(LocalDate date) {
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    /**
     * Generates a formatted certificate string in Spanish containing warranty details.
     *
     * @return formatted certificate text
     */
    public String generateWarrantyCertificate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return "=== CERTIFICADO DE GARANTÍA ===\n" +
                "ID Garantía: " + id + "\n" +
                "Tipo: " + getWarrantyType() + "\n" +
                "Producto: " + product.getTitle() + "\n" +
                "Fecha de Inicio: " + startDate.format(formatter) + "\n" +
                "Fecha de Vencimiento: " + endDate.format(formatter) + "\n" +
                "Costo Adicional: $" + getAdditionalCost() + "\n" +
                "===============================";
    }

    public String getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public Sale getSale() {
        return sale;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }
}
