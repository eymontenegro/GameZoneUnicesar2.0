package com.gamezone.persistence;

import com.gamezone.model.Promotion;

import java.util.List;

/**
 * Handles saving and loading Promotion objects (PercentageDiscount,
 * CategoryDiscount and BulkPurchaseDiscount) to and from a CSV file,
 * so the promotion catalog persists between application runs.
 */
public class PromotionRepository {

    private static final String FILE_PATH = "data/promotions.csv";

    /**
     * Saves the full list of promotions to the CSV file, overwriting
     * any previous content.
     *
     * @param promotions the list of promotions to persist
     */
    public void saveAll(List<Promotion> promotions) {
        // TODO: implemented in a follow-up commit
    }

    /**
     * Loads the full list of promotions from the CSV file. If the file
     * does not exist yet (first run), returns an empty list instead
     * of failing.
     *
     * @return the list of promotions loaded from the file
     */
    public List<Promotion> loadAll() {
        // TODO: implemented in a follow-up commit
        return null;
    }
}