package com.gamezone.persistence;

import com.gamezone.model.Product;
import com.gamezone.model.Return;
import com.gamezone.service.ProductService;
import com.gamezone.service.SaleService;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
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
     * Loads the full list of returns from the CSV file. If the file
     * does not exist yet (first run), returns an empty list instead
     * of failing.
     *
     * @return the list of returns loaded from the file
     */
    public List<Return> loadAll() {
        // TODO: implemented in a follow-up commit
        return null;
    }
}