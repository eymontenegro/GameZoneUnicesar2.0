package com.gamezone.persistence;

import com.gamezone.exceptions.PersistenceException;
import com.gamezone.model.Accessory;

import java.util.List;

/**
 * Storage contract for the accessory catalog. The service layer depends on
 * this interface instead of a concrete storage mechanism, so the catalog
 * can be kept in a CSV file, a database or an in-memory fake without
 * changing the business logic.
 */
public interface AccessoryStorage {

    /**
     * Loads every stored accessory.
     *
     * @return the stored accessories, or an empty list when there is nothing stored
     * @throws PersistenceException if the accessories cannot be read
     */
    List<Accessory> loadAll();

    /**
     * Replaces the stored catalog with the given accessories.
     *
     * @param accessories the accessories to store
     * @throws PersistenceException if the accessories cannot be written
     */
    void saveAll(List<Accessory> accessories);
}