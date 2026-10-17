package com.gamezone.validation;

import com.gamezone.exceptions.InvalidDataException;
import com.gamezone.model.Product;
import com.gamezone.model.Sale;

import java.time.LocalDate;

/**
 * Centralizes the validation rules for the warranties module.
 * <p>
 * This class only inspects the values it receives: it never accesses
 * files, services or repositories.
 */
public final class WarrantyValidator {

    private WarrantyValidator() {
        // Utility class: no instances.
    }

    /**
     * Validates the data required to create a warranty.
     *
     * @param product   the product covered by the warranty
     * @param sale      the sale the warranty belongs to
     * @param startDate the first day the warranty is valid
     * @throws InvalidDataException if the product or the sale is null or has no id,
     *                              or the start date is null
     */
    public static void validateWarrantyData(Product product, Sale sale, LocalDate startDate) {
        if (product == null || product.getId() == null) {
            throw new InvalidDataException("producto cubierto", "no puede ser nulo ni carecer de identificador");
        }
        if (sale == null || sale.getId() == null) {
            throw new InvalidDataException("venta asociada", "no puede ser nula ni carecer de identificador");
        }
        if (startDate == null) {
            throw new InvalidDataException("fecha de inicio de la garantía", "no puede ser nula");
        }
    }

    /**
     * Validates the number of days used to look for warranties about to expire.
     *
     * @param daysAhead the number of days to look ahead
     * @throws InvalidDataException if the number of days is negative
     */
    public static void validateDaysAhead(int daysAhead) {
        if (daysAhead < 0) {
            throw new InvalidDataException("días de anticipación", "no pueden ser negativos");
        }
    }
}