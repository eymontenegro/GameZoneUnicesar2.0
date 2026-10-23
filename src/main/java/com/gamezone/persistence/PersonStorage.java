package com.gamezone.persistence;

import com.gamezone.exceptions.PersistenceException;
import com.gamezone.model.Client;
import com.gamezone.model.Seller;

import java.util.List;

/**
 * Storage contract for the people of the store. The service layer depends on
 * this interface instead of a concrete storage mechanism, so clients and
 * sellers can be kept in CSV files, a database or an in-memory fake without
 * changing the business logic.
 */
public interface PersonStorage {

    /**
     * Loads every stored client.
     *
     * @return the stored clients, or an empty list when there is nothing stored
     * @throws PersistenceException if the clients cannot be read
     */
    List<Client> loadClients();

    /**
     * Loads every stored seller.
     *
     * @return the stored sellers, or an empty list when there is nothing stored
     * @throws PersistenceException if the sellers cannot be read
     */
    List<Seller> loadSellers();

    /**
     * Replaces the stored clients with the given list.
     *
     * @param clients the clients to store
     * @throws PersistenceException if the clients cannot be written
     */
    void saveClients(List<Client> clients);
}