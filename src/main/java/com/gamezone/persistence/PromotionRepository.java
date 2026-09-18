package com.gamezone.persistence;

import com.gamezone.model.BulkPurchaseDiscount;
import com.gamezone.model.CategoryDiscount;
import com.gamezone.model.PercentageDiscount;
import com.gamezone.model.Promotion;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Handles saving and loading Promotion objects (PercentageDiscount,
 * CategoryDiscount and BulkPurchaseDiscount) to and from a CSV file,
 * so the promotion catalog persists between application runs.
 */
public class PromotionRepository {

    private static final String FILE_PATH = "data/promotions.csv";

    /**
     * Converts a single promotion into one CSV-formatted line, choosing
     * which extra columns to include based on the promotion's real type.
     *
     * @param promotion the promotion to convert
     * @return a comma-separated line representing the promotion
     * @throws IllegalArgumentException if the promotion type is not recognized
     */
    private String promotionToCsvLine(Promotion promotion) {
        String commonData = promotion.getId() + "," + promotion.getName() + ","
                + promotion.getStartDate() + "," + promotion.getEndDate();

        String type;
        String specificData;

        if (promotion instanceof PercentageDiscount percentageDiscount) {
            type = "PERCENTAGE";
            specificData = String.valueOf(percentageDiscount.getPercentage());

        } else if (promotion instanceof CategoryDiscount categoryDiscount) {
            type = "CATEGORY";
            specificData = categoryDiscount.getPercentage() + "," + categoryDiscount.getTargetCategory();

        } else if (promotion instanceof BulkPurchaseDiscount bulkPurchaseDiscount) {
            type = "BULK";
            specificData = bulkPurchaseDiscount.getMinimumQuantity() + "," + bulkPurchaseDiscount.getPercentage();

        } else {
            throw new IllegalArgumentException("Unknown promotion type");
        }

        return type + "," + commonData + "," + specificData;
    }

    /**
     * Saves the full list of promotions to the CSV file, overwriting
     * any previous content.
     *
     * @param promotions the list of promotions to persist
     * @throws RuntimeException if an I/O error occurs while writing
     */
    public void saveAll(List<Promotion> promotions) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Promotion promotion : promotions) {
                writer.write(promotionToCsvLine(promotion));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error saving promotions: " + e.getMessage(), e);
        }
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