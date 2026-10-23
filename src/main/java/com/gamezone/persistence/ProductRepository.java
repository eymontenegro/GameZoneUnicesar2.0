package com.gamezone.persistence;

import com.gamezone.exceptions.PersistenceException;
import com.gamezone.model.Console;
import com.gamezone.model.Product;
import com.gamezone.model.VideoGame;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles saving and loading Product objects (VideoGame and Console)
 * to and from a CSV file, so the product catalog persists between
 * application runs.
 *
 * <p>Every low-level {@link IOException} is wrapped in a
 * {@link PersistenceException}; corrupt file content is reported with the
 * file name and line number.</p>
 */
 public class ProductRepository implements ProductStorage{

    private static final String DEFAULT_FILE = "data/products.csv";
    private static final int COLUMNS = 8;

    private final String filePath;

    /**
     * Creates a repository that uses the default file {@code data/products.csv}.
     */
    public ProductRepository() {
        this(DEFAULT_FILE);
    }

    /**
     * Creates a repository that uses the given file (useful for tests).
     *
     * @param filePath path of the CSV file
     * @throws IllegalArgumentException if the path is null or blank
     */
    public ProductRepository(String filePath) {
        if (filePath == null || filePath.isBlank()) {
            throw new IllegalArgumentException("La ruta del archivo de productos no puede estar vacia");
        }
        this.filePath = filePath;
    }

    /**
     * Converts a single product into one CSV-formatted line, choosing
     * which extra columns to include based on the product's real type.
     *
     * @param product the product to convert (VideoGame or Console)
     * @return a comma-separated line representing the product
     * @throws IllegalArgumentException if the product type is not recognized
     *         or any of its values cannot be stored safely
     */
    private String productToCsvLine(Product product) {
        CsvSupport.requireValidPrice(product.getPrice(), "price");
        if (product.getStock() < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo: " + product.getStock());
        }
        String commonData = CsvSupport.safe(product.getId(), "id") + ","
                + CsvSupport.safe(product.getTitle(), "title") + ","
                + product.getPrice() + ","
                + product.getStock();

        String type;
        String specificData;

        if (product instanceof VideoGame) {
            VideoGame vg = (VideoGame) product;
            type = "VIDEOGAME";
            specificData = CsvSupport.safe(vg.getPlatform(), "platform") + ","
                    + CsvSupport.safe(vg.getGenre(), "genre") + ","
                    + CsvSupport.safe(vg.getAgeRating(), "ageRating");
        } else if (product instanceof Console) {
            Console c = (Console) product;
            type = "CONSOLE";
            specificData = CsvSupport.safe(c.getBrand(), "brand") + ","
                    + CsvSupport.safe(c.getModel(), "model") + ","
                    + CsvSupport.safe(c.getGeneration(), "generation");
        } else {
            throw new IllegalArgumentException("Unknown product type");
        }

        return type + "," + commonData + "," + specificData;
    }

    /**
     * Saves the full list of products to the CSV file, overwriting
     * any previous content. All products are validated and converted
     * before the file is opened, so an invalid product never leaves a
     * truncated file behind.
     *
     * @param products the list of products to persist
     * @throws IllegalArgumentException if the list or any product is null, of an
     *         unknown type, or has values that cannot be stored
     * @throws PersistenceException if an I/O error occurs while writing
     */
   @Override
   public void saveAll(List<Product> products){
        if (products == null) {
            throw new IllegalArgumentException("La lista de productos no puede ser nula");
        }
        List<String> lines = new ArrayList<>();
        for (Product product : products) {
            if (product == null) {
                throw new IllegalArgumentException("La lista de productos contiene un elemento nulo");
            }
            lines.add(productToCsvLine(product));
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            throw new PersistenceException("No se pudieron guardar " + lines.size()
                    + " producto(s) en '" + filePath + "'", e);
        }
    }

    /**
     * Reconstructs a single Product (VideoGame or Console) from one
     * CSV-formatted line, based on the type indicated in the first column.
     *
     * @param line a single line read from the CSV file
     * @param lineNumber the 1-based number of the line, used in error messages
     * @return the reconstructed Product object
     * @throws PersistenceException if the line is malformed
     */
    private Product csvLineToProduct(String line, int lineNumber) {
        String[] parts = line.split(",", -1);
        CsvSupport.requireColumns(parts, COLUMNS, filePath, lineNumber);

        String type = parts[0];
        String id = parts[1];
        String title = parts[2];
        CsvSupport.requireNotBlank(id, "id", filePath, lineNumber);
        CsvSupport.requireNotBlank(title, "title", filePath, lineNumber);

        double price;
        int stock;
        try {
            price = Double.parseDouble(parts[3]);
        } catch (NumberFormatException e) {
            throw CsvSupport.corruptLine(filePath, lineNumber, "el precio no es un numero valido: '" + parts[3] + "'", e);
        }
        try {
            stock = Integer.parseInt(parts[4]);
        } catch (NumberFormatException e) {
            throw CsvSupport.corruptLine(filePath, lineNumber, "el stock no es un entero valido: '" + parts[4] + "'", e);
        }
        if (Double.isNaN(price) || Double.isInfinite(price) || price < 0) {
            throw CsvSupport.corruptLine(filePath, lineNumber, "el precio no puede ser negativo ni infinito: " + price, null);
        }
        if (stock < 0) {
            throw CsvSupport.corruptLine(filePath, lineNumber, "el stock no puede ser negativo: " + stock, null);
        }

        if (type.equals("VIDEOGAME")) {
            return new VideoGame(parts[5], parts[6], parts[7], id, title, price, stock);
        } else if (type.equals("CONSOLE")) {
            return new Console(parts[5], parts[6], parts[7], id, title, price, stock);
        } else {
            throw CsvSupport.corruptLine(filePath, lineNumber, "tipo de producto desconocido: '" + type + "'", null);
        }
    }

    /**
     * Loads the full list of products from the CSV file. If the file
     * does not exist yet (first run), returns an empty list instead
     * of failing. Blank lines are ignored.
     *
     * @return the list of products loaded from the file
     * @throws PersistenceException if an I/O error occurs while reading, or
     *         the file contains a malformed line
     */
    @Override
    public List<Product> load() {
        List<Product> products = new ArrayList<>();
        File file = new File(filePath);

        if (!file.exists()) {
            return products;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.isBlank()) {
                    continue;
                }
                products.add(csvLineToProduct(line, lineNumber));
            }
        } catch (IOException e) {
            throw new PersistenceException("No se pudieron leer los productos desde '" + filePath + "'", e);
        }

        return products;
    }
}