package com.gamezone.validation;

import com.gamezone.exceptions.BusinessRuleException;
import com.gamezone.exceptions.InvalidDataException;
import com.gamezone.exceptions.ResourceNotFoundException;
import com.gamezone.model.Client;
import com.gamezone.model.Sale;
import com.gamezone.model.SaleDetail;
import com.gamezone.model.Seller;

import java.util.List;

/**
 * Centralizes the validation rules for the sales module.
 * <p>
 * This class only inspects the objects it receives: it never accesses
 * files, services or repositories.
 */
public final class SaleValidator {

    private SaleValidator() {
        // Utility class: no instances.
    }

    /**
     * Validates the data required to register a sale.
     *
     * @param client  the client who makes the purchase
     * @param seller  the seller who registers the sale
     * @param details the lines of the sale
     * @throws InvalidDataException  if the client or the seller is null
     * @throws BusinessRuleException if the sale has no lines
     */
    public static void validateSaleData(Client client, Seller seller, List<SaleDetail> details) {
        if (client == null) {
            throw new InvalidDataException("cliente", "no puede ser nulo");
        }
        if (seller == null) {
            throw new InvalidDataException("vendedor", "no puede ser nulo");
        }
        if (details == null || details.isEmpty()) {
            throw new BusinessRuleException("La venta debe contener al menos un producto");
        }
    }

    /**
     * Validates that none of the lines of a sale is null or lacks a product.
     *
     * @param details the lines of the sale
     * @throws InvalidDataException if a line or its product is null
     */
    public static void validateSaleDetails(List<SaleDetail> details) {
        if (details == null) {
            return;
        }
        for (SaleDetail detail : details) {
            if (detail == null || detail.getProduct() == null) {
                throw new InvalidDataException("detalle de la venta",
                        "contiene entradas nulas o sin producto");
            }
        }
    }

    /**
     * Validates that a sale was found.
     *
     * @param sale   the sale returned by a search (may be null)
     * @param saleId the identifier that was searched
     * @throws ResourceNotFoundException if the sale is null
     */
    public static void validateSaleExists(Sale sale, String saleId) {
        if (sale == null) {
            throw new ResourceNotFoundException("venta", saleId);
        }
    }
}