package com.gamezone.persistence;

import com.gamezone.model.Client;
import com.gamezone.model.Product;
import com.gamezone.model.Sale;
import com.gamezone.model.Seller;

import java.util.List;

/**
 * Storage contract used by the sale use cases. It separates the business rules
 * of {@code SaleService} from the way sales are stored (CSV today, anything else
 * tomorrow) and lets the service be tested with an in-memory implementation.
 */
public interface SaleStorage {

    /**
     * Loads all stored sales, binding each one to the given clients, sellers,
     * products and accessories.
     *
     * @param clients     list of system clients
     * @param sellers     list of system sellers
     * @param products    list of main products (video games and consoles)
     * @param accessories list of accessories
     * @return the reconstructed sales, or an empty list if nothing is stored yet
     */
    List<Sale> loadAll(List<Client> clients,
                       List<Seller> sellers,
                       List<? extends Product> products,
                       List<? extends Product> accessories);

    /**
     * Stores all the given sales, replacing what was stored before.
     *
     * @param sales the sales to persist
     */
    void saveAll(List<Sale> sales);
}