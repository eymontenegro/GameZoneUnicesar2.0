package com.gamezone.model;

import java.time.LocalDate;

/**
 * Represents a basic factory warranty covering manufacturing defects for 6 months.
 */
public class BasicWarranty extends Warranty {

    /**
     * Constructs a new BasicWarranty instance.
     *
     * @param id        the unique identifier
     * @param product   the covered product
     * @param sale      the associated sale
     * @param startDate the start date
     */
    public BasicWarranty(String id, Product product, Sale sale, LocalDate startDate) {
        super(id, product, sale, startDate);
    }

    @Override
    public int getDurationInMonths() {
        return 6;
    }

    @Override
    public String getWarrantyType() {
        return "Garantía Básica";
    }

    @Override
    public double getAdditionalCost() {
        return 0.0;
    }
}
