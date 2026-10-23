package com.gamezone.persistence;

import com.gamezone.exceptions.PersistenceException;
import com.gamezone.model.Return;
import com.gamezone.model.Sale;

import java.util.List;

/**
 * Storage contract for returns. The service layer depends on this
 * interface instead of a concrete storage mechanism, so returns can be
 * kept in a CSV file, a database or an in-memory fake without changing
 * the business logic.
 */
public interface ReturnStorage {

    /**
     * Loads every stored return, linking each one to its original sale.
     *
     * @param sales the sales already loaded, used to resolve each return's original sale
     * @return the stored returns, or an empty list when there is nothing stored
     * @throws PersistenceException if the returns cannot be read
     */
    List<Return> loadAll(List<Sale> sales);

    /**
     * Replaces the stored returns with the given ones.
     *
     * @param returns the returns to store
     * @throws PersistenceException if the returns cannot be written
     */
    void saveAll(List<Return> returns);
}