package com.gamezone.service;

import com.gamezone.model.Product;
import com.gamezone.model.Return;
import com.gamezone.model.Sale;
import com.gamezone.model.SaleDetail;
import com.gamezone.persistence.ReturnRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Applies the business rules for processing product returns: validating
 * the 30-day return window, validating that the requested products
 * belong to the original sale, restoring stock, and persisting the
 * return using ReturnRepository. Also generates the monthly balance
 * report combining sales and returns data.
 */
public class ReturnService {

    private List<Return> returns;
    private ReturnRepository repository;
    private SaleService saleService;
    private ProductService productService;

    /**
     * Creates the service injecting all required dependencies and loads
     * any previously saved returns from the file.
     *
     * @param repository the repository used to persist returns
     * @param saleService the service used to look up the original sale
     * @param productService the service used to restore stock on returned products
     */
    public ReturnService(ReturnRepository repository, SaleService saleService, ProductService productService) {
        this.repository = repository;
        this.saleService = saleService;
        this.productService = productService;
        this.returns = repository.loadAll();
    }

    /**
     * Checks whether a product with the given id is part of the given sale.
     *
     * @param sale the sale to search
     * @param productId the id of the product to look for
     * @return the matching product from the sale's details
     * @throws IllegalArgumentException if the product does not belong to the sale
     */
    private Product findProductInSale(Sale sale, String productId) {
        for (SaleDetail detail : sale.getDetails()) {
            if (detail.getProduct().getId().equals(productId)) {
                return detail.getProduct();
            }
        }
        throw new IllegalArgumentException("El producto con ID " + productId
                + " no pertenece a la venta " + sale.getId() + ".");
    }

    /**
     * Registers a new return for one or more products from an existing sale.
     * Validates that the sale exists, that it is still within the 30-day
     * return window, and that every requested product actually belongs to
     * that sale. On success, restores the stock of each returned product
     * and persists the return.
     *
     * @param saleId the id of the original sale
     * @param productIds the ids of the products being returned
     * @param reason the reason for the return
     * @return the newly created and persisted Return
     * @throws IllegalArgumentException if the sale does not exist, is outside
     *         the 30-day window, or references a product not in the sale
     */
    public Return registerReturn(String saleId, List<String> productIds, String reason) {
        Sale sale;
        try {
            sale = saleService.findById(saleId);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("La venta indicada no existe: " + saleId);
        }

        if (!sale.canBeReturned()) {
            throw new IllegalArgumentException(
                    "La devolución no puede procesarse: han pasado más de 30 días desde la venta.");
        }

        if (productIds == null || productIds.isEmpty()) {
            throw new IllegalArgumentException("Debe indicar al menos un producto a devolver.");
        }

        List<Product> returnedProducts = new ArrayList<>();
        for (String productId : productIds) {
            returnedProducts.add(findProductInSale(sale, productId));
        }

        String returnId = UUID.randomUUID().toString();
        Return newReturn = new Return(returnId, LocalDate.now(), sale, returnedProducts, reason, 0.0);
        newReturn.calculateRefundAmount();

        for (String productId : productIds) {
            productService.restoreStock(productId, 1);
        }

        returns.add(newReturn);
        repository.saveAll(returns);

        return newReturn;
    }

    /**
     * Returns the full list of registered returns.
     *
     * @return the list of all returns
     */
    public List<Return> viewAllReturns() {
        // TODO: implemented in a follow-up commit
        return null;
    }

    /**
     * Returns the list of returns whose original sale belongs to the given customer.
     *
     * @param customerId the id of the client
     * @return the list of returns made by that customer
     */
    public List<Return> viewReturnsByCustomer(String customerId) {
        // TODO: implemented in a follow-up commit
        return null;
    }

    /**
     * Returns the list of returns associated with the given sale.
     *
     * @param saleId the id of the sale
     * @return the list of returns for that sale
     */
    public List<Return> viewReturnsBySale(String saleId) {
        // TODO: implemented in a follow-up commit
        return null;
    }

    /**
     * Generates the net balance for a given month and year: total sales
     * minus total returns for that period.
     *
     * @param month the month to evaluate (1-12)
     * @param year the year to evaluate
     * @return the net balance (sales minus returns) for that period
     */
    public double generateMonthlyBalance(int month, int year) {
        // TODO: implemented in a follow-up commit
        return 0.0;
    }
}