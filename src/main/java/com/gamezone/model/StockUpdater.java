package com.gamezone.model;

/**
 * Something that can change the stored stock of an item by its id.
 * It lets the model ask for a stock change without depending on the service layer.
 */
public interface StockUpdater {

    /**
     * Reduces the stock of the item with the given id.
     *
     * @param productId the item identifier
     * @param quantity the units to subtract
     */
    void updateStock(String productId, int quantity);

    /**
     * Restores (increases) the stock of the item with the given id.
     *
     * @param productId the item identifier
     * @param quantity the units to add back
     */
    void restoreStock(String productId, int quantity);
}