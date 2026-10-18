package com.gamezone.persistence;

import com.gamezone.exceptions.PersistenceException;
import com.gamezone.model.Accessory;
import com.gamezone.model.BasicWarranty;
import com.gamezone.model.Client;
import com.gamezone.model.ExtendedWarranty;
import com.gamezone.model.Product;
import com.gamezone.model.Promotion;
import com.gamezone.model.Sale;
import com.gamezone.model.Seller;
import com.gamezone.model.Warranty;

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
 * Handles saving and loading Warranty objects (BasicWarranty and
 * ExtendedWarranty) to and from a CSV file, so the warranty catalog
 * persists between application runs. Reference resolution (Sale and
 * Product lookups) is done using SaleRepository, ProductRepository,
 * PersonRepository, AccessoryRepository and PromotionRepository, all
 * injected through the constructor.
 *
 * <p>Every low-level {@link IOException} is wrapped in a
 * {@link PersistenceException}; corrupt file content, or a reference to a
 * sale or product that does not exist, is reported with the file name and
 * line number instead of being skipped silently.</p>
 */
public class WarrantyRepository {

    private static final String DEFAULT_FILE = "data/warranties.csv";
    private static final int COLUMNS = 6;

    private final String filePath;
    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;
    private final PersonRepository personRepository;
    private final AccessoryRepository accessoryRepository;
    private final PromotionRepository promotionRepository;

    /**
     * Creates the repository injecting the dependencies needed to
     * resolve Sale and Product references while loading warranties
     * from the file. Uses the default file {@code data/warranties.csv}.
     *
     * @param saleRepository      the repository used to resolve sale references
     * @param productRepository   the repository used to resolve product references
     * @param personRepository    the repository needed to resolve clients and sellers
     * @param accessoryRepository the repository needed to resolve accessories
     * @param promotionRepository the repository needed to resolve promotions
     * @throws IllegalArgumentException if any repository is null
     */
    public WarrantyRepository(SaleRepository saleRepository,
                              ProductRepository productRepository,
                              PersonRepository personRepository,
                              AccessoryRepository accessoryRepository,
                              PromotionRepository promotionRepository) {
        this(saleRepository, productRepository, personRepository, accessoryRepository,
                promotionRepository, DEFAULT_FILE);
    }

    /**
     * Same as the main constructor, but with a custom file (useful for tests).
     *
     * @param saleRepository      the repository used to resolve sale references
     * @param productRepository   the repository used to resolve product references
     * @param personRepository    the repository needed to resolve clients and sellers
     * @param accessoryRepository the repository needed to resolve accessories
     * @param promotionRepository the repository needed to resolve promotions
     * @param filePath            path of the CSV file
     * @throws IllegalArgumentException if any repository is null or the path is null or blank
     */
    public WarrantyRepository(SaleRepository saleRepository,
                              ProductRepository productRepository,
                              PersonRepository personRepository,
                              AccessoryRepository accessoryRepository,
                              PromotionRepository promotionRepository,
                              String filePath) {
        if (saleRepository == null || productRepository == null || personRepository == null
                || accessoryRepository == null || promotionRepository == null) {
            throw new IllegalArgumentException("Los repositorios requeridos por WarrantyRepository no pueden ser nulos");
        }
        if (filePath == null || filePath.isBlank()) {
            throw new IllegalArgumentException("La ruta del archivo de garantias no puede estar vacia");
        }
        this.saleRepository = saleRepository;
        this.productRepository = productRepository;
        this.personRepository = personRepository;
        this.accessoryRepository = accessoryRepository;
        this.promotionRepository = promotionRepository;
        this.filePath = filePath;
    }

    /**
     * Converts a single Warranty into one CSV-formatted line.
     *
     * @param warranty the warranty to convert
     * @return a comma-separated line representing the warranty
     * @throws IllegalArgumentException if any value cannot be stored safely
     */
    private String warrantyToCsvLine(Warranty warranty) {
        String id = CsvSupport.safe(warranty.getId(), "id");
        if (warranty.getProduct() == null || warranty.getSale() == null) {
            throw new IllegalArgumentException("La garantia '" + id + "' debe tener producto y venta");
        }
        if (warranty.getStartDate() == null || warranty.getEndDate() == null) {
            throw new IllegalArgumentException("La garantia '" + id + "' debe tener fechas de inicio y fin");
        }
        String type = (warranty instanceof BasicWarranty) ? "BASIC" : "EXTENDED";
        return type + ","
                + id + ","
                + CsvSupport.safe(warranty.getProduct().getId(), "productId") + ","
                + CsvSupport.safe(warranty.getSale().getId(), "saleId") + ","
                + warranty.getStartDate() + ","
                + warranty.getEndDate();
    }

    /**
     * Saves the full list of warranties to the CSV file, overwriting
     * any previous content. All warranties are validated and converted
     * before the file is opened, so an invalid warranty never leaves a
     * truncated file behind.
     *
     * @param warranties the list of warranties to persist
     * @throws IllegalArgumentException if the list or any warranty is null, or has
     *         values that cannot be stored
     * @throws PersistenceException if an I/O error occurs while writing
     */
    public void saveAll(List<Warranty> warranties) {
        if (warranties == null) {
            throw new IllegalArgumentException("La lista de garantias no puede ser nula");
        }
        List<String> lines = new ArrayList<>();
        for (Warranty warranty : warranties) {
            if (warranty == null) {
                throw new IllegalArgumentException("La lista de garantias contiene un elemento nulo");
            }
            lines.add(warrantyToCsvLine(warranty));
        }

        File file = new File(filePath);
        CsvSupport.ensureParentDirectory(file, "las garantias");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            throw new PersistenceException("No se pudieron guardar " + lines.size()
                    + " garantia(s) en '" + filePath + "'", e);
        }
    }

    /**
     * Reconstructs a single Warranty from one CSV line.
     *
     * @param line       the CSV line to parse
     * @param lineNumber the 1-based number of the line, used in error messages
     * @param saleMap    a lookup of sales by id
     * @param productMap a lookup of products (including accessories) by id
     * @return the reconstructed Warranty
     * @throws PersistenceException if the line is malformed or references unknown data
     */
    private Warranty csvLineToWarranty(String line, int lineNumber,
                                       Map<String, Sale> saleMap, Map<String, Product> productMap) {
        String[] parts = line.split(",", -1);
        CsvSupport.requireColumns(parts, COLUMNS, filePath, lineNumber);

        String type = parts[0].trim();
        String id = parts[1].trim();
        String productId = parts[2].trim();
        String saleId = parts[3].trim();
        CsvSupport.requireNotBlank(id, "id", filePath, lineNumber);
        LocalDate startDate = CsvSupport.parseDate(parts[4], "startDate", filePath, lineNumber);
        CsvSupport.parseDate(parts[5], "endDate", filePath, lineNumber);

        Product product = productMap.get(productId);
        if (product == null) {
            throw CsvSupport.corruptLine(filePath, lineNumber,
                    "el producto '" + productId + "' de la garantia '" + id + "' no existe", null);
        }
        Sale sale = saleMap.get(saleId);
        if (sale == null) {
            throw CsvSupport.corruptLine(filePath, lineNumber,
                    "la venta '" + saleId + "' de la garantia '" + id + "' no existe", null);
        }

        try {
            if ("BASIC".equalsIgnoreCase(type)) {
                return new BasicWarranty(id, product, sale, startDate);
            } else if ("EXTENDED".equalsIgnoreCase(type)) {
                return new ExtendedWarranty(id, product, sale, startDate);
            }
        } catch (IllegalArgumentException | NullPointerException e) {
            throw CsvSupport.corruptLine(filePath, lineNumber, "valores invalidos: " + e.getMessage(), e);
        }

        throw CsvSupport.corruptLine(filePath, lineNumber, "tipo de garantia desconocido: '" + type + "'", null);
    }

    /**
     * Loads the full list of warranties from the CSV file. If the file does
     * not exist yet (first run), returns an empty list. Blank lines are ignored.
     *
     * @return the list of warranties loaded from the file
     * @throws PersistenceException if an I/O error occurs while reading, or the
     *         file (or any file it depends on) contains a malformed line or an
     *         unresolvable reference
     */
    public List<Warranty> loadAll() {
        List<Warranty> warranties = new ArrayList<>();
        File file = new File(filePath);

        if (!file.exists()) {
            return warranties;
        }

        List<Client> clients = personRepository.loadClients();
        List<Seller> sellers = personRepository.loadSellers();
        List<Product> products = productRepository.load();
        List<Accessory> accessories = accessoryRepository.loadAll();
        List<Promotion> promotions = promotionRepository.loadAll();

        List<Sale> sales = saleRepository.loadAll(clients, sellers, products, accessories, promotions);

        Map<String, Sale> saleMap = new HashMap<>();
        for (Sale sale : sales) {
            saleMap.put(sale.getId(), sale);
        }

        Map<String, Product> productMap = new HashMap<>();
        for (Product product : products) {
            productMap.put(product.getId(), product);
        }
        for (Accessory accessory : accessories) {
            productMap.put(accessory.getId(), accessory);
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.isBlank()) {
                    continue;
                }
                warranties.add(csvLineToWarranty(line, lineNumber, saleMap, productMap));
            }
        } catch (IOException e) {
            throw new PersistenceException("No se pudieron leer las garantias desde '" + filePath + "'", e);
        }

        return warranties;
    }
}