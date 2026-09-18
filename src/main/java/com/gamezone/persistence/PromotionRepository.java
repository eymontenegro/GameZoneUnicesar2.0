package com.gamezone.persistence;

import com.gamezone.model.BulkPurchaseDiscount;
import com.gamezone.model.CategoryDiscount;
import com.gamezone.model.PercentageDiscount;
import com.gamezone.model.Promotion;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
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
     * Reconstructs a single Promotion (PercentageDiscount, CategoryDiscount
     * or BulkPurchaseDiscount) from one CSV-formatted line, based on the
     * type indicated in the first column.
     *
     * @param line a single line read from the CSV file
     * @return the reconstructed Promotion object
     * @throws IllegalArgumentException if the promotion type is not recognized
     */
    private Promotion csvLineToPromotion(String line) {
        String[] parts = line.split(",");
        String type = parts[0];
        String id = parts[1];
        String name = parts[2];
        LocalDate startDate = LocalDate.parse(parts[3]);
        LocalDate endDate = LocalDate.parse(parts[4]);

        if (type.equals("PERCENTAGE")) {
            double percentage = Double.parseDouble(parts[5]);
            return new PercentageDiscount(id, name, startDate, endDate, percentage);

        } else if (type.equals("CATEGORY")) {
            double percentage = Double.parseDouble(parts[5]);
            String targetCategory = parts[6];
            return new CategoryDiscount(id, name, startDate, endDate, percentage, targetCategory);

        } else if (type.equals("BULK")) {
            int minimumQuantity = Integer.parseInt(parts[5]);
            double percentage = Double.parseDouble(parts[6]);
            return new BulkPurchaseDiscount(id, name, startDate, endDate, minimumQuantity, percentage);

        } else {
            throw new IllegalArgumentException("Unknown promotion type: " + type);
        }
    }

    /**
     * Loads the full list of promotions from the CSV file. If the file
     * does not exist yet (first run), returns an empty list instead
     * of failing.
     *
     * @return the list of promotions loaded from the file
     * @throws RuntimeException if an I/O error occurs while reading
     */
    public List<Promotion> loadAll() {
        List<Promotion> promotions = new ArrayList<>();
        File file = new File(FILE_PATH);

        if (!file.exists()) {
            return promotions;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                promotions.add(csvLineToPromotion(line));
            }
        } catch (IOException e) {
            throw new RuntimeException("Error loading promotions: " + e.getMessage(), e);
        }

        return promotions;
    }
}