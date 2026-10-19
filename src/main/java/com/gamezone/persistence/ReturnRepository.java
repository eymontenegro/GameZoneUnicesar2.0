package com.gamezone.persistence;

import com.gamezone.exceptions.PersistenceException;
import com.gamezone.model.Product;
import com.gamezone.model.Return;
import com.gamezone.model.Sale;
import com.gamezone.model.SaleDetail;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handles saving and loading Return objects to and from a CSV file,
 * so the return history persists between application runs.
 * Reference resolution (Sale and Product lookups) is done using the lists
 * passed in by the caller.
 *
 * <p>Every low-level {@link IOException} is wrapped in a
 * {@link PersistenceException}; corrupt file content, or a reference to a
 * sale or product that does not exist, is reported with the file name and
 * line number instead of being skipped silently.</p>
 */
public class ReturnRepository {

    private static final String DEFAULT_FILE = "data/returns.csv";
    private static final int COLUMNS = 6;

    private final String filePath;

    /**
     * Creates a repository that uses the default file {@code data/returns.csv}.
     */
    public ReturnRepository() {
        this(DEFAULT_FILE);
    }

    /**
     * Creates a repository that uses the given file (useful for tests).
     *
     * @param filePath path of the CSV file
     * @throws IllegalArgumentException if the path is null or blank
     */
    public ReturnRepository(String filePath) {
        if (filePath == null || filePath.isBlank()) {
            throw new IllegalArgumentException("La ruta del archivo de devoluciones no puede estar vacia");
        }
        this.filePath = filePath;
    }

    /**
     * Converts a single Return into one CSV-formatted line.
     *
     * @param returnObj the return to convert
     * @return a comma-separated line representing the return
     * @throws IllegalArgumentException if any value cannot be stored safely
     */
    private String returnToCsvLine(Return returnObj) {
        String id = CsvSupport.safe(returnObj.getId(), "id");
        if (returnObj.getOriginalSale() == null) {
            throw new IllegalArgumentException("La devolucion '" + id + "' no tiene venta original");
        }
        if (returnObj.getDate() == null) {
            throw new IllegalArgumentException("La devolucion '" + id + "' no tiene fecha");
        }
        CsvSupport.requireValidPrice(returnObj.getRefundAmount(), "refundAmount");

        List<Product> returnedProducts = returnObj.getReturnedProducts();
        List<String> productIds = new ArrayList<>();
        if (returnedProducts != null) {
            for (Product product : returnedProducts) {
                if (product == null) {
                    throw new IllegalArgumentException("La devolucion '" + id + "' contiene un producto nulo");
                }
                productIds.add(CsvSupport.safe(product.getId(), "productId", ";"));
            }
        }

        return id + ","
                + CsvSupport.safe(returnObj.getOriginalSale().getId(), "saleId") + ","
                + returnObj.getDate() + ","
                + CsvSupport.safe(returnObj.getReason(), "reason") + ","
                + returnObj.getRefundAmount() + ","
                + String.join(";", productIds);
    }

    /**
     * Saves the full list of returns to the CSV file, overwriting any previous content.
     * All returns are validated and converted before the file is opened, so an
     * invalid return never leaves a truncated file behind.
     *
     * @param returns the list of returns to persist
     * @throws IllegalArgumentException if the list or any return is null, or has
     *         values that cannot be stored
     * @throws PersistenceException if an I/O error occurs while writing
     */
    public void saveAll(List<Return> returns) {
        if (returns == null) {
            throw new IllegalArgumentException("La lista de devoluciones no puede ser nula");
        }
        List<String> lines = new ArrayList<>();
        for (Return returnObj : returns) {
            if (returnObj == null) {
                throw new IllegalArgumentException("La lista de devoluciones contiene un elemento nulo");
            }
            lines.add(returnToCsvLine(returnObj));
        }

        File file = new File(filePath);
        CsvSupport.ensureParentDirectory(file, "las devoluciones");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            throw new PersistenceException("No se pudieron guardar " + lines.size()
                    + " devolucion(es) en '" + filePath + "'", e);
        }
    }

    /**
     * Finds a product by id among the details of the given sale.
     *
     * @param sale      the sale whose details are searched
     * @param productId the id of the product to find
     * @return the matching product, or null if not found in the sale
     */
    private Product findProductInSale(Sale sale, String productId) {
        if (sale == null || sale.getDetails() == null) {
            return null;
        }

        for (SaleDetail detail : sale.getDetails()) {
            if (detail != null && detail.getProduct() != null && productId.equals(detail.getProduct().getId())) {
                return detail.getProduct();
            }
        }
        return null;
    }

    /**
     * Reconstructs a single Return from one CSV line.
     *
     * @param line    a single line read from the CSV file
     * @param lineNumber the 1-based number of the line, used in error messages
     * @param saleMap map of existing sales indexed by ID
     * @return the reconstructed Return object
     * @throws PersistenceException if the line is malformed or references unknown data
     */
    private Return csvLineToReturn(String line, int lineNumber, Map<String, Sale> saleMap) {
        String[] parts = line.split(",", -1);
        CsvSupport.requireColumns(parts, COLUMNS, filePath, lineNumber);

        String id = parts[0].trim();
        String saleId = parts[1].trim();
        CsvSupport.requireNotBlank(id, "id", filePath, lineNumber);
        LocalDate date = CsvSupport.parseDate(parts[2], "date", filePath, lineNumber);
        String reason = parts[3].trim();
        double storedRefund = CsvSupport.parseDouble(parts[4], "refundAmount", filePath, lineNumber);
        if (storedRefund < 0) {
            throw CsvSupport.corruptLine(filePath, lineNumber,
                    "el reembolso no puede ser negativo: " + storedRefund, null);
        }
        String productIdsStr = parts[5].trim();

        Sale sale = saleMap.get(saleId);
        if (sale == null) {
            throw CsvSupport.corruptLine(filePath, lineNumber,
                    "la venta '" + saleId + "' de la devolucion '" + id + "' no existe", null);
        }

        List<Product> returnedProducts = new ArrayList<>();
        if (!productIdsStr.isEmpty()) {
            for (String rawId : productIdsStr.split(";", -1)) {
                String productId = rawId.trim();
                CsvSupport.requireNotBlank(productId, "productId", filePath, lineNumber);
                Product product = findProductInSale(sale, productId);
                if (product == null) {
                    throw CsvSupport.corruptLine(filePath, lineNumber,
                            "el producto '" + productId + "' no pertenece a la venta '" + saleId + "'", null);
                }
                returnedProducts.add(product);
            }
        }

        try {
            Return result = new Return(id, date, sale, returnedProducts, reason, 0.0);
            // The file stores the TOTAL refund; the model only takes the warranty part
            // as input and adds the products refund by itself, so recover that part here.
            double warrantyPart = storedRefund - result.getRefundAmount();
            if (warrantyPart > 0.005) {
                result.setWarrantyRefundAmount(warrantyPart);
            }
            return result;
        } catch (IllegalArgumentException | NullPointerException e) {
            throw CsvSupport.corruptLine(filePath, lineNumber, "valores invalidos: " + e.getMessage(), e);
        }
    }

    /**
     * Loads the full list of returns from the CSV file. If the file does not
     * exist yet (first run), returns an empty list. Blank lines are ignored.
     *
     * @param sales the list of existing sales, used to resolve each return's original sale
     * @return the list of returns loaded from the file
     * @throws PersistenceException if an I/O error occurs while reading, or the
     *         file contains a malformed line or a reference that cannot be resolved
     */
    public List<Return> loadAll(List<Sale> sales) {
        List<Return> returns = new ArrayList<>();
        File file = new File(filePath);

        if (!file.exists()) {
            return returns;
        }

        Map<String, Sale> saleMap = new HashMap<>();
        if (sales != null) {
            for (Sale sale : sales) {
                if (sale != null) {
                    saleMap.put(sale.getId(), sale);
                }
            }
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.isBlank()) {
                    continue;
                }
                returns.add(csvLineToReturn(line, lineNumber, saleMap));
            }
        } catch (IOException e) {
            throw new PersistenceException("No se pudieron leer las devoluciones desde '" + filePath + "'", e);
        }

        return returns;
    }
}