package com.gamezone.persistence;

import com.gamezone.exceptions.PersistenceException;
import com.gamezone.model.Promotion;

import java.util.List;

/**
 * Storage contract for promotions. The service layer depends on this
 * interface instead of a concrete storage mechanism, so promotions can be
 * kept in a CSV file, a database or an in-memory fake without changing
 * the business logic.
 */
public interface PromotionStorage {

    /**
     * Loads every stored promotion.
     *
     * @return the stored promotions, or an empty list when there is nothing stored
     * @throws PersistenceException if the promotions cannot be read
     */
    List<Promotion> loadAll();

    /**
     * Replaces the stored promotions with the given ones.
     *
     * @param promotions the promotions to store
     * @throws PersistenceException if the promotions cannot be written
     */
    void saveAll(List<Promotion> promotions);
}