package com.gamezone.persistence;

import com.gamezone.model.Return;
import com.gamezone.service.ProductService;
import com.gamezone.service.SaleService;

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
     * Saves the full list of returns to the CSV file, overwriting
     * any previous content.
     *
     * @param returns the list of returns to persist
     */
    public void saveAll(List<Return> returns) {
        // TODO: implemented in a follow-up commit
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