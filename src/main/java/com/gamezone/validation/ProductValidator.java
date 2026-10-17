/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gamezone.validation;


import com.gamezone.exceptions.BusinessRuleException;
import com.gamezone.exceptions.InvalidDataException;
import com.gamezone.exceptions.ResourceNotFoundException;
import com.gamezone.model.Product;

/**
 * Static validation rules for the product module.
 *
 * <p>This class only depends on the model and the exceptions layer. It never
 * touches repositories, services or files.</p>
 */
public final class ProductValidator {

    private ProductValidator() {
        // Utility class: no instances.
    }

    /**
     * Validates the basic data of a product.
     *
     * @param id    product identifier, must not be null or blank
     * @param title product title, must not be null or blank
     * @param price product price, must not be negative
     * @param stock product stock, must not be negative
     * @throws InvalidDataException if any value breaks its rule
     */
    public static void validateProductData(String id, String title, double price, int stock) {
        if (id == null || id.trim().isEmpty()) {
            throw new InvalidDataException("identificador", "no puede ser nulo ni vacío");
        }
        if (title == null || title.trim().isEmpty()) {
            throw new InvalidDataException("título", "no puede ser nulo ni vacío");
        }
         if (Double.isNaN(price) || price < 0) {
            throw new InvalidDataException("precio", "no puede ser negativo");
        }
        if (stock < 0) {
            throw new InvalidDataException("stock", "no puede ser negativo");
        }
    }

    /**
     * Validates that a product has enough stock for the requested quantity.
     *
     * @param product           product to check
     * @param requestedQuantity quantity requested
     * @throws BusinessRuleException if the available stock is lower than requested
     */
    public static void validateStockAvailability(Product product, int requestedQuantity) {
        if (product.getStock() < requestedQuantity) {
            throw new BusinessRuleException("Stock insuficiente para el producto '"
                    + product.getTitle() + "': disponible " + product.getStock()
                    + ", solicitado " + requestedQuantity);
        }
    }

    /**
     * Validates that a product was found.
     *
     * @param product   product returned by a search, may be null
     * @param productId identifier that was searched
     * @throws ResourceNotFoundException if the product is null
     */
    public static void validateProductExists(Product product, String productId) {
        if (product == null) {
            throw new ResourceNotFoundException("producto", productId);
        }
    }
}
