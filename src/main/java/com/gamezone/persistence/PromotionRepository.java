package com.gamezone.persistence;

import com.gamezone.exceptions.PersistenceException;
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
 *
 * <p>Every low-level {@link IOException} is wrapped in a
 * {@link PersistenceException}; corrupt file content is reported with the
 * file name and line number instead of being skipped silently.</p>
 */
public class PromotionRepository implements PromotionStorage {

    private static final String DEFAULT_FILE = "data/promotions.csv";

    private final String filePath;

    /**
     * Creates a repository that uses the default file {@code data/promotions.csv}.
     */
    public PromotionRepository() {
        this(DEFAULT_FILE);
    }

    /**
     * Creates a repository that uses the given file (useful for tests).
     *
     * @param filePath path of the CSV file
     * @throws IllegalArgumentException if the path is null or blank
     */
    public PromotionRepository(String filePath) {
        if (filePath == null || filePath.isBlank()) {
            throw new IllegalArgumentException("La ruta del archivo de promociones no puede estar vacia");
        }
        this.filePath = filePath;
    }

    /**
     * Converts a single promotion into one CSV-formatted line, choosing
     * which extra columns to include based on the promotion's real type.
     *
     * @param promotion the promotion to convert
     * @return a comma-separated line representing the promotion
     * @throws IllegalArgumentException if the promotion type is not recognized
     *         or any of its values cannot be stored safely
     */
    private String promotionToCsvLine(Promotion promotion) {
        if (promotion.getEndDate().isBefore(promotion.getStartDate())) {
            throw new IllegalArgumentException("La fecha de fin no puede ser anterior a la de inicio en la promocion '"
                    + promotion.getId() + "'");
        }
        String commonData = CsvSupport.safe(promotion.getId(), "id") + ","
                + CsvSupport.safe(promotion.getName(), "name") + ","
                + promotion.getStartDate() + "," + promotion.getEndDate();

        String type;
        String specificData;

        if (promotion instanceof PercentageDiscount percentageDiscount) {
            type = "PERCENTAGE";
            specificData = String.valueOf(percentageDiscount.getPercentage());

        } else if (promotion instanceof CategoryDiscount categoryDiscount) {
            type = "CATEGORY";
            specificData = categoryDiscount.getPercentage() + ","
                    + CsvSupport.safe(categoryDiscount.getTargetCategory(), "targetCategory");

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
     * any previous content. All promotions are validated and converted
     * before the file is opened, so an invalid promotion never leaves a
     * truncated file behind.
     *
     * @param promotions the list of promotions to persist
     * @throws IllegalArgumentException if the list or any promotion is null, of an
     *         unknown type, or has values that cannot be stored
     * @throws PersistenceException if an I/O error occurs while writing
     */
    @Override
    public void saveAll(List<Promotion> promotions) {
        if (promotions == null) {
            throw new IllegalArgumentException("La lista de promociones no puede ser nula");
        }
        List<String> lines = new ArrayList<>();
        for (Promotion promotion : promotions) {
            if (promotion == null) {
                throw new IllegalArgumentException("La lista de promociones contiene un elemento nulo");
            }
            lines.add(promotionToCsvLine(promotion));
        }

        File file = new File(filePath);
        CsvSupport.ensureParentDirectory(file, "las promociones");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            throw new PersistenceException("No se pudieron guardar " + lines.size()
                    + " promocion(es) en '" + filePath + "'", e);
        }
    }

    /**
     * Reconstructs a single Promotion (PercentageDiscount, CategoryDiscount
     * or BulkPurchaseDiscount) from one CSV-formatted line.
     *
     * @param line a single line read from the CSV file
     * @param lineNumber the 1-based number of the line, used in error messages
     * @return the reconstructed Promotion object
     * @throws PersistenceException if the line is malformed
     */
    private Promotion csvLineToPromotion(String line, int lineNumber) {
        String[] parts = line.split(",", -1);
        if (parts.length < 6) {
            throw CsvSupport.corruptLine(filePath, lineNumber,
                    "se esperaban al menos 6 columnas pero hay " + parts.length, null);
        }

        String type = parts[0].trim();
        String id = parts[1].trim();
        String name = parts[2].trim();
        CsvSupport.requireNotBlank(id, "id", filePath, lineNumber);
        CsvSupport.requireNotBlank(name, "name", filePath, lineNumber);
        LocalDate startDate = CsvSupport.parseDate(parts[3], "startDate", filePath, lineNumber);
        LocalDate endDate = CsvSupport.parseDate(parts[4], "endDate", filePath, lineNumber);
        if (endDate.isBefore(startDate)) {
            throw CsvSupport.corruptLine(filePath, lineNumber,
                    "la fecha de fin (" + endDate + ") es anterior a la de inicio (" + startDate + ")", null);
        }

        try {
            if ("PERCENTAGE".equalsIgnoreCase(type)) {
                CsvSupport.requireColumns(parts, 6, filePath, lineNumber);
                double percentage = CsvSupport.parseDouble(parts[5], "percentage", filePath, lineNumber);
                return new PercentageDiscount(id, name, startDate, endDate, percentage);

            } else if ("CATEGORY".equalsIgnoreCase(type)) {
                CsvSupport.requireColumns(parts, 7, filePath, lineNumber);
                double percentage = CsvSupport.parseDouble(parts[5], "percentage", filePath, lineNumber);
                String targetCategory = parts[6].trim();
                CsvSupport.requireNotBlank(targetCategory, "targetCategory", filePath, lineNumber);
                return new CategoryDiscount(id, name, startDate, endDate, percentage, targetCategory);

            } else if ("BULK".equalsIgnoreCase(type)) {
                CsvSupport.requireColumns(parts, 7, filePath, lineNumber);
                int minimumQuantity = CsvSupport.parseInt(parts[5], "minimumQuantity", filePath, lineNumber);
                double percentage = CsvSupport.parseDouble(parts[6], "percentage", filePath, lineNumber);
                return new BulkPurchaseDiscount(id, name, startDate, endDate, minimumQuantity, percentage);
            }
        } catch (IllegalArgumentException | NullPointerException e) {
            throw CsvSupport.corruptLine(filePath, lineNumber, "valores invalidos: " + e.getMessage(), e);
        }

        throw CsvSupport.corruptLine(filePath, lineNumber, "tipo de promocion desconocido: '" + type + "'", null);
    }

    /**
     * Loads the full list of promotions from the CSV file. If the file
     * does not exist yet (first run), returns an empty list instead
     * of failing. Blank lines are ignored.
     *
     * @return the list of promotions loaded from the file
     * @throws PersistenceException if an I/O error occurs while reading, or
     *         the file contains a malformed line
     */
    @Override
    public List<Promotion> loadAll() {
        List<Promotion> promotions = new ArrayList<>();
        File file = new File(filePath);

        if (!file.exists()) {
            return promotions;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.isBlank()) {
                    continue;
                }
                promotions.add(csvLineToPromotion(line, lineNumber));
            }
        } catch (IOException e) {
            throw new PersistenceException("No se pudieron leer las promociones desde '" + filePath + "'", e);
        }

        return promotions;
    }
}