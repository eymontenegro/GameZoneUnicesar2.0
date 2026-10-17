package com.gamezone.model;

import java.time.LocalDate;

/**
 * Represents an extended warranty covering manufacturing defects and accidental damages
 * for 12 months, carrying an additional cost equivalent to 10% of the product price.
 */
public class ExtendedWarranty extends Warranty {

    /**
     * Constructs a new ExtendedWarranty instance.
     *
     * @param id        the unique identifier
     * @param product   the covered product
     * @param sale      the associated sale
     * @param startDate the start date
     */
    public ExtendedWarranty(String id, Product product, Sale sale, LocalDate startDate) {
        super(id, product, sale, startDate);
    }

    @Override
    public int getDurationInMonths() {
        return 12;
    }

    @Override
    public String getWarrantyType() {
        return "Garantía Extendida";
    }

    @Override
    public double getAdditionalCost() {
        return getProduct().getPrice() * 0.10;
    }
}