package com.gamezone.persistence;

import com.gamezone.exceptions.PersistenceException;
import com.gamezone.model.Client;
import com.gamezone.model.Product;
import com.gamezone.model.Promotion;
import com.gamezone.model.Sale;
import com.gamezone.model.SaleDetail;
import com.gamezone.model.Seller;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handles persistence operations for {@link Sale} objects to and from a CSV file.
 *
 * <p>Every low-level {@link IOException} is wrapped in a
 * {@link PersistenceException}; corrupt file content (bad columns, numbers,
 * dates, or references to a client, seller or product that no longer exists)
 * is reported with the file name and line number instead of being skipped,
 * so a damaged line is never silently lost when the file is saved again.</p>
 */
public class SaleRepository implements SaleStorage {

    private static final String DEFAULT_FILE = "data/sales.csv";
    private static final int COLUMNS = 9;

    private final String filePath;

    /**
     * Creates a repository that uses the default file {@code data/sales.csv}.
     */
    public SaleRepository() {
        this(DEFAULT_FILE);
    }

    /**
     * Creates a repository that uses the given file (useful for tests).
     *
     * @param filePath path of the CSV file
     * @throws IllegalArgumentException if the path is null or blank
     */
    public SaleRepository(String filePath) {
        if (filePath == null || filePath.isBlank()) {
            throw new IllegalArgumentException("La ruta del archivo de ventas no puede estar vacia");
        }
        this.filePath = filePath;
    }

    /**
     * Converts a single {@link Sale} object into a CSV-formatted string line.
     *
     * @param sale the sale instance to convert
     * @return a CSV-formatted representation of the sale
     * @throws IllegalArgumentException if any value cannot be stored safely
     */
    private String saleToCsvLine(Sale sale) {
        String saleId = CsvSupport.safe(sale.getId(), "id");
        if (sale.getDate() == null) {
            throw new IllegalArgumentException("La venta '" + saleId + "' no tiene fecha");
        }
        if (sale.getSeller() == null || sale.getClient() == null) {
            throw new IllegalArgumentException("La venta '" + saleId + "' debe tener cliente y vendedor");
        }
        String sellerId = CsvSupport.safe(sale.getSeller().getId(), "sellerId");
        String clientId = CsvSupport.safe(sale.getClient().getId(), "clientId");
        String promotion = CsvSupport.safe(sale.getAppliedPromotionName(), "appliedPromotionName");
        CsvSupport.requireValidPrice(sale.getDiscountAmount(), "discountAmount");
        CsvSupport.requireValidPrice(sale.getAdditionalCosts(), "additionalCosts");
        CsvSupport.requireValidPrice(sale.getTotal(), "total");

        List<SaleDetail> details = sale.getDetails();
        if (details == null) {
            throw new IllegalArgumentException("La venta '" + saleId + "' tiene una lista de detalles nula");
        }
        List<String> detailsFormatted = new ArrayList<>();
        for (SaleDetail detail : details) {
            if (detail == null || detail.getProduct() == null) {
                throw new IllegalArgumentException("La venta '" + saleId + "' contiene un detalle sin producto");
            }
            CsvSupport.requireValidPrice(detail.getUnitPrice(), "unitPrice");
            detailsFormatted.add(CsvSupport.safe(detail.getProduct().getId(), "productId", ":;") + ":"
                    + detail.getQuantity() + ":"
                    + detail.getUnitPrice());
        }

        return saleId + ","
                + sale.getDate() + ","
                + sellerId + ","
                + clientId + ","
                + promotion + ","
                + sale.getDiscountAmount() + ","
                + sale.getAdditionalCosts() + ","
                + sale.getTotal() + ","
                + String.join(";", detailsFormatted);
    }

    /**
     * Saves all sales from the provided list to the CSV file, overwriting existing content.
     * All sales are validated and converted before the file is opened, so an
     * invalid sale never leaves a truncated file behind.
     *
     * @param sales the list of {@link Sale} objects to persist
     * @throws IllegalArgumentException if the list or any sale is null, or has
     *         values that cannot be stored
     * @throws PersistenceException if an I/O error occurs while writing
     */
    @Override
    public void saveAll(List<Sale> sales) {
        if (sales == null) {
            throw new IllegalArgumentException("La lista de ventas no puede ser nula");
        }
        List<String> lines = new ArrayList<>();
        for (Sale sale : sales) {
            if (sale == null) {
                throw new IllegalArgumentException("La lista de ventas contiene un elemento nulo");
            }
            lines.add(saleToCsvLine(sale));
        }

        CsvSupport.ensureParentDirectory(new File(filePath), "las ventas");
        CsvSupport.writeLines(filePath, lines, "venta(s)");
    }

    /**
     * Loads and reconstructs all sales from the CSV file. If the file does not
     * exist yet (first run), returns an empty list. Blank lines are ignored.
     * <p>
     * Converts the provided domain lists into internal lookup maps to quickly resolve
     * and bind associated {@link Client}, {@link Seller}, and {@link Product} references.
     * </p>
     *
     * @param clients     list of system clients
     * @param sellers     list of system sellers
     * @param products    list of main products (video games and consoles)
     * @param accessories list of accessories
     * @return a reconstructed list of {@link Sale} objects
     * @throws PersistenceException if an I/O error occurs while reading, or the
     *         file contains a malformed line or a reference that cannot be resolved
     */
    @Override
    public List<Sale> loadAll(List<Client> clients,
                              List<Seller> sellers,
                              List<? extends Product> products,
                              List<? extends Product> accessories) {

        List<Sale> sales = new ArrayList<>();
        File file = new File(filePath);

        if (!file.exists()) {
            return sales;
        }

        Map<String, Client> clientMap = new HashMap<>();
        if (clients != null) {
            for (Client c : clients) {
                if (c != null && c.getId() != null) {
                    clientMap.put(c.getId(), c);
                }
            }
        }

        Map<String, Seller> sellerMap = new HashMap<>();
        if (sellers != null) {
            for (Seller s : sellers) {
                if (s != null && s.getId() != null) {
                    sellerMap.put(s.getId(), s);
                }
            }
        }

        Map<String, Product> productMap = new HashMap<>();
        if (products != null) {
            for (Product p : products) {
                if (p != null && p.getId() != null) {
                    productMap.put(p.getId(), p);
                }
            }
        }
        if (accessories != null) {
            for (Product a : accessories) {
                if (a != null && a.getId() != null) {
                    productMap.put(a.getId(), a);
                }
            }
        }

        return CsvSupport.readLines(filePath, "las ventas",
                (line, lineNumber) -> csvLineToSale(line, lineNumber, clientMap, sellerMap, productMap));
    }

    /**
     * Same as {@link #loadAll(List, List, List, List)}; the {@code promotions}
     * list was never used to rebuild a sale and is ignored.
     *
     * @param clients     list of system clients
     * @param sellers     list of system sellers
     * @param products    list of main products (video games and consoles)
     * @param accessories list of accessories
     * @param promotions  ignored
     * @return a reconstructed list of {@link Sale} objects
     * @throws PersistenceException if an I/O error occurs while reading, or the
     *         file contains a malformed line or a reference that cannot be resolved
     * @deprecated use {@link #loadAll(List, List, List, List)}; this overload only
     *             exists so callers outside the persistence layer can migrate first
     */
    @Deprecated
    public List<Sale> loadAll(List<Client> clients,
                              List<Seller> sellers,
                              List<? extends Product> products,
                              List<? extends Product> accessories,
                              List<? extends Promotion> promotions) {
        return loadAll(clients, sellers, products, accessories);
    }

    /**
     * Reconstructs a single {@link Sale} from one CSV-formatted line.
     *
     * @param line the line read from the file
     * @param lineNumber the 1-based line number, used in error messages
     * @param clientMap clients by id
     * @param sellerMap sellers by id
     * @param productMap products and accessories by id
     * @return the reconstructed sale
     * @throws PersistenceException if the line is malformed or references unknown data
     */
    private Sale csvLineToSale(String line, int lineNumber,
                               Map<String, Client> clientMap,
                               Map<String, Seller> sellerMap,
                               Map<String, Product> productMap) {
        String[] parts = line.split(",", -1);
        CsvSupport.requireColumns(parts, COLUMNS, filePath, lineNumber);

        String id = parts[0].trim();
        CsvSupport.requireNotBlank(id, "id", filePath, lineNumber);

        LocalDate date = CsvSupport.parseDate(parts[1], "date", filePath, lineNumber);

        String sellerId = parts[2].trim();
        String clientId = parts[3].trim();
        Seller seller = sellerMap.get(sellerId);
        if (seller == null) {
            throw CsvSupport.corruptLine(filePath, lineNumber,
                    "el vendedor '" + sellerId + "' de la venta '" + id + "' no existe", null);
        }
        Client client = clientMap.get(clientId);
        if (client == null) {
            throw CsvSupport.corruptLine(filePath, lineNumber,
                    "el cliente '" + clientId + "' de la venta '" + id + "' no existe", null);
        }

        double discountAmount = parseAmount(parts[5], "descuento", lineNumber);
        double additionalCosts = parseAmount(parts[6], "costos adicionales", lineNumber);
        double total = parseAmount(parts[7], "total", lineNumber);

        Sale sale = new Sale(id, client, seller, date);
        sale.setAppliedPromotionName(parts[4].trim());
        sale.setDiscountAmount(discountAmount);
        sale.setAdditionalCosts(additionalCosts);
        sale.setTotal(total);

        String detailsData = parts[8].trim();
        if (!detailsData.isEmpty()) {
            for (String detailStr : detailsData.split(";", -1)) {
                sale.addDetail(parseDetail(detailStr, lineNumber, productMap));
            }
        }
        return sale;
    }

    /**
     * Parses a monetary column, rejecting non-numeric, infinite and negative values.
     *
     * @param text the raw column value
     * @param fieldName the field name, used in the error message
     * @param lineNumber the 1-based line number
     * @return the parsed amount
     * @throws PersistenceException if the value is not a valid non-negative number
     */
    private double parseAmount(String text, String fieldName, int lineNumber) {
        double value = CsvSupport.parseDouble(text, fieldName, filePath, lineNumber);
        if (value < 0) {
            throw CsvSupport.corruptLine(filePath, lineNumber,
                    "el campo '" + fieldName + "' no puede ser negativo: " + value, null);
        }
        return value;
    }

    /**
     * Parses one {@code productId:quantity:unitPrice} item of a sale.
     *
     * @param detailStr the raw item text
     * @param lineNumber the 1-based line number
     * @param productMap products and accessories by id
     * @return the reconstructed detail
     * @throws PersistenceException if the item is malformed or the product does not exist
     */
    private SaleDetail parseDetail(String detailStr, int lineNumber, Map<String, Product> productMap) {
        String[] itemParts = detailStr.split(":", -1);
        if (itemParts.length != 3) {
            throw CsvSupport.corruptLine(filePath, lineNumber,
                    "el detalle '" + detailStr + "' debe tener el formato producto:cantidad:precio", null);
        }
        String productId = itemParts[0].trim();
        Product product = productMap.get(productId);
        if (product == null) {
            throw CsvSupport.corruptLine(filePath, lineNumber,
                    "el producto '" + productId + "' del detalle no existe", null);
        }
        int quantity = CsvSupport.parseInt(itemParts[1], "cantidad", filePath, lineNumber);
        double unitPrice = CsvSupport.parseDouble(itemParts[2], "precio", filePath, lineNumber);
        try {
            return new SaleDetail(product, quantity, unitPrice);
        } catch (IllegalArgumentException e) {
            throw CsvSupport.corruptLine(filePath, lineNumber,
                    "detalle invalido '" + detailStr + "': " + e.getMessage(), e);
        }
    }
}