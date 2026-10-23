package com.gamezone.persistence;

import com.gamezone.exceptions.PersistenceException;
import com.gamezone.model.Product;

import java.util.List;

/**
 * Storage contract for the product catalog. The service layer depends on
 * this interface instead of a concrete storage mechanism, so the catalog
 * can be kept in a CSV file, a database or an in-memory fake without
 * changing the business logic.
 */
public interface ProductStorage {

    /**
     * Loads every stored product.
     *
     * @return the stored products, or an empty list when there is nothing stored
     * @throws PersistenceException if the products cannot be read
     */
    List<Product> load();

    /**
     * Replaces the stored catalog with the given products.
     *
     * @param products the products to store
     * @throws PersistenceException if the products cannot be written
     */
    void saveAll(List<Product> products);
}
