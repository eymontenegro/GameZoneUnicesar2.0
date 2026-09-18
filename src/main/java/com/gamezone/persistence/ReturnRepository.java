package com.gamezone.persistence;

import com.gamezone.model.Product;
import com.gamezone.model.Return;
import com.gamezone.model.Sale;
import com.gamezone.model.SaleDetail;
import com.gamezone.service.ProductService;
import com.gamezone.service.SaleService;

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
 * Handles saving and loading Return objects to and from a CSV file,
 * so the return history persists between application runs.
 * Depends on SaleService and ProductService to resolve the original
 * sale and the returned products when reconstructing each Return.
 */
public class ReturnRepository {

    private static final String FILE_PATH = "data/returns.csv";

    private SaleService saleService;
    private ProductService productService;

    /**
     * Creates the repository, injecting the services needed to resolve
     * references to the original Sale and to the returned Products
     * while loading returns from the file.
     *
     * @param saleService the service used to look up the original sale by id
     * @param productService the service used to look up returned products by id
     */
    public ReturnRepository(SaleService saleService, ProductService productService) {
        this.saleService = saleService;
        this.productService = productService;
    }

    /**
     * Converts a single Return into one CSV-formatted line. The returned
     * products are stored as a semicolon-separated list of product ids.
     *
     * @param returnObj the return to convert
     * @return a comma-separated line representing the return
     */
    private String returnToCsvLine(Return returnObj) {
        StringBuilder productIdsBuilder = new StringBuilder();
        List<Product> returnedProducts = returnObj.getReturnedProducts();

        for (int i = 0; i < returnedProducts.size(); i++) {
            productIdsBuilder.append(returnedProducts.get(i).getId());
            if (i < returnedProducts.size() - 1) {
                productIdsBuilder.append(";");
            }
        }

        return returnObj.getId() + ","
                + returnObj.getOriginalSale().getId() + ","
                + returnObj.getDate() + ","
                + returnObj.getReason() + ","
                + returnObj.getRefundAmount() + ","
                + productIdsBuilder;
    }

    /**
     * Saves the full list of returns to the CSV file, overwriting
     * any previous content.
     *
     * @param returns the list of returns to persist
     * @throws RuntimeException if an I/O error occurs while writing
     */
    public void saveAll(List<Return> returns) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Return returnObj : returns) {
                writer.write(returnToCsvLine(returnObj));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error saving returns: " + e.getMessage(), e);
        }
    }

    /**
     * Finds a product by id among the details of the given sale. Checking
     * the sale's own resolved product list first ensures both regular
     * products and accessories are matched correctly.
     *
     * @param sale the sale whose details are searched
     * @param productId the id of the product to find
     * @return the matching product, or null if not found in the sale
     */
    private Product findProductInSale(Sale sale, String productId) {
        for (SaleDetail detail : sale.getDetails()) {
            if (detail.getProduct().getId().equals(productId)) {
                return detail.getProduct();
            }
        }
        return null;
    }

    /**
     * Reconstructs a single Return from one CSV-formatted line, resolving
     * the original sale through SaleService and the returned products
     * through the sale's own details (falling back to ProductService).
     *
     * @param line a single line read from the CSV file
     * @return the reconstructed Return object, or null if the referenced
     *         sale no longer exists
     */
    private Return csvLineToReturn(String line) {
        String[] parts = line.split(",", -1);
        String id = parts[0];
        String saleId = parts[1];
        LocalDate date = LocalDate.parse(parts[2]);
        String reason = parts[3];
        double refundAmount = Double.parseDouble(parts[4]);
        String productIdsStr = parts[5];

        Sale sale;
        try {
            sale = saleService.findById(saleId);
        } catch (IllegalArgumentException e) {
            return null;
        }

        List<Product> returnedProducts = new ArrayList<>();
        if (!productIdsStr.isEmpty()) {
            String[] productIds = productIdsStr.split(";");
            for (String productId : productIds) {
                Product product = findProductInSale(sale, productId);
                if (product == null) {
                    for (Product candidate : productService.listAll()) {
                        if (candidate.getId().equals(productId)) {
                            product = candidate;
                            break;
                        }
                    }
                }
                if (product != null) {
                    returnedProducts.add(product);
                }
            }
        }

        return new Return(id, date, sale, returnedProducts, reason, refundAmount);
    }

    /**
     * Loads the full list of returns from the CSV file. If the file
     * does not exist yet (first run), returns an empty list instead
     * of failing.
     *
     * @return the list of returns loaded from the file
     * @throws RuntimeException if an I/O error occurs while reading
     */
    public List<Return> loadAll() {
        List<Return> returns = new ArrayList<>();
        File file = new File(FILE_PATH);

        if (!file.exists()) {
            return returns;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                Return returnObj = csvLineToReturn(line);
                if (returnObj != null) {
                    returns.add(returnObj);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error loading returns: " + e.getMessage(), e);
        }

        return returns;
    }
}