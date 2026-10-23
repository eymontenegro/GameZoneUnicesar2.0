package com.gamezone.validation;

import com.gamezone.exceptions.InvalidDataException;
import com.gamezone.exceptions.ResourceNotFoundException;
import com.gamezone.model.Promotion;

import java.time.LocalDate;

/**
 * Centralizes the validation rules for the promotions module.
 * <p>
 * This class only inspects the values it receives: it never accesses
 * files, services or repositories.
 */
public final class PromotionValidator {

    private PromotionValidator() {
        // Utility class: no instances.
    }

    /**
     * Validates the data shared by every kind of promotion.
     *
     * @param id         the promotion identifier
     * @param name       the promotion name
     * @param startDate  the first day the promotion is valid
     * @param endDate    the last day the promotion is valid
     * @param percentage the discount percentage
     * @throws InvalidDataException if the id or name is empty, a date is missing or inverted,
     *                              or the percentage is outside the range (0, 100]
     */
    public static void validatePromotionData(String id, String name, LocalDate startDate,
                                             LocalDate endDate, double percentage) {
        if (id == null || id.trim().isEmpty()) {
            throw new InvalidDataException("identificador", "no puede estar vacío");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidDataException("nombre", "no puede estar vacío");
        }
        if (startDate == null || endDate == null) {
            throw new InvalidDataException("fechas", "la fecha de inicio y la de fin no pueden ser nulas");
        }
        if (startDate.isAfter(endDate)) {
            throw new InvalidDataException("fechas", "la fecha de inicio no puede ser posterior a la fecha de fin");
        }
        if (percentage <= 0 || percentage > 100) {
            throw new InvalidDataException("porcentaje de descuento",
                    "debe ser mayor que 0 y menor o igual que 100");
        }
    }

    /**
     * Validates the data specific to a category discount.
     *
     * @param targetCategory the category the discount applies to
     * @throws InvalidDataException if the category is empty
     */
    public static void validateCategoryData(String targetCategory) {
        if (targetCategory == null || targetCategory.trim().isEmpty()) {
            throw new InvalidDataException("categoría", "no puede estar vacía");
        }
    }

    /**
     * Validates the data specific to a bulk purchase discount.
     *
     * @param minimumQuantity the minimum quantity required to get the discount
     * @throws InvalidDataException if the minimum quantity is not positive
     */
    public static void validateBulkPurchaseData(int minimumQuantity) {
        if (minimumQuantity <= 0) {
            throw new InvalidDataException("cantidad mínima", "debe ser mayor que 0");
        }
    }

    /**
     * Validates that a promotion was found.
     *
     * @param promotion   the promotion returned by a search (may be null)
     * @param promotionId the identifier that was searched
     * @throws ResourceNotFoundException if the promotion is null
     */
    public static void validatePromotionExists(Promotion promotion, String promotionId) {
        if (promotion == null) {
            throw new ResourceNotFoundException("promoción", promotionId);
        }
    }
}