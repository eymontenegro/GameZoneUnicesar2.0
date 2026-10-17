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
import java.time.format.DateTimeParseException;
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
     */
    public void saveAll(List<Promotion> promotions) {
        File file = new File(FILE_PATH);
        if (file.getParentFile() != null) {
            file.getParentFile().mkdirs();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            if (promotions != null) {
                for (Promotion promotion : promotions) {
                    if (promotion != null) {
                        writer.write(promotionToCsvLine(promotion));
                        writer.newLine();
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error saving promotions: " + e.getMessage(), e);
        }
    }

    /**
     * Reconstructs a single Promotion (PercentageDiscount, CategoryDiscount
     * or BulkPurchaseDiscount) from one CSV-formatted line.
     *
     * @param line a single line read from the CSV file
     * @return the reconstructed Promotion object, or null if line is invalid/malformed
     */
    private Promotion csvLineToPromotion(String line) {
        try {
            String[] parts = line.split(",", -1);
            if (parts.length < 6) {
                return null;
            }

            String type = parts[0].trim();
            String id = parts[1].trim();
            String name = parts[2].trim();
            LocalDate startDate = LocalDate.parse(parts[3].trim());
            LocalDate endDate = LocalDate.parse(parts[4].trim());

            if ("PERCENTAGE".equalsIgnoreCase(type)) {
                double percentage = Double.parseDouble(parts[5].trim());
                return new PercentageDiscount(id, name, startDate, endDate, percentage);

            } else if ("CATEGORY".equalsIgnoreCase(type)) {
                if (parts.length < 7) return null;
                double percentage = Double.parseDouble(parts[5].trim());
                String targetCategory = parts[6].trim();
                return new CategoryDiscount(id, name, startDate, endDate, percentage, targetCategory);

            } else if ("BULK".equalsIgnoreCase(type)) {
                if (parts.length < 7) return null;
                int minimumQuantity = Integer.parseInt(parts[5].trim());
                double percentage = Double.parseDouble(parts[6].trim());
                return new BulkPurchaseDiscount(id, name, startDate, endDate, minimumQuantity, percentage);
            }
        } catch (NumberFormatException | DateTimeParseException e) {
            return null; // Skip malformed lines defensively
        }

        return null;
    }

    /**
     * Loads the full list of promotions from the CSV file. If the file
     * does not exist yet (first run), returns an empty list instead
     * of failing.
     *
     * @return the list of promotions loaded from the file
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
                Promotion promotion = csvLineToPromotion(line);
                if (promotion != null) {
                    promotions.add(promotion);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error loading promotions: " + e.getMessage(), e);
        }

        return promotions;
    }
}