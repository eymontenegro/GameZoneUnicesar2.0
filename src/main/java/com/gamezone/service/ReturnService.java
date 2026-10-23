package com.gamezone.service;

import com.gamezone.exceptions.ResourceNotFoundException;
import com.gamezone.model.Product;
import com.gamezone.model.Return;
import com.gamezone.model.Sale;
import com.gamezone.model.SaleDetail;
import com.gamezone.persistence.ReturnStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Applies the business rules for processing product returns: validating
 * the 30-day return window, validating that requested products belong to
 * the original sale and haven't already been returned, restoring stock,
 * and persisting returns. Also generates monthly net balance reports.
 */
public class ReturnService {

    private final List<Return> returns;
    private final ReturnStorage repository;
    private final SaleService saleService;

    /**
     * Constructs ReturnService injecting required dependencies.
     *
     * @param repository  the repository used to persist returns
     * @param saleService the service used to look up original sales and restore stock
     */
    public ReturnService(ReturnStorage repository, SaleService saleService) {
        this.repository = Objects.requireNonNull(repository, "ReturnRepository cannot be null.");
        this.saleService = Objects.requireNonNull(saleService, "SaleService cannot be null.");

        List<Return> loadedReturns = repository.loadAll(saleService.listAllSales());
        this.returns = Collections.synchronizedList(loadedReturns != null ? new ArrayList<>(loadedReturns) : new ArrayList<>());
    }

    private Sale findSaleById(String saleId) {
        if (saleId == null || saleId.trim().isEmpty()) {
            throw new IllegalArgumentException("Sale ID cannot be null or empty.");
        }
        try {
            return saleService.findById(saleId);
        } catch (ResourceNotFoundException e) {
            throw new IllegalArgumentException("The specified sale does not exist: " + saleId, e);
        }
    }

    private Product findProductInSale(Sale sale, String productId) {
        if (sale != null && sale.getDetails() != null && productId != null) {
            for (SaleDetail detail : sale.getDetails()) {
                if (detail != null && detail.getProduct() != null && productId.equals(detail.getProduct().getId())) {
                    return detail.getProduct();
                }
            }
        }
        throw new IllegalArgumentException("Product ID " + productId + " does not belong to sale " + (sale != null ? sale.getId() : "unknown") + ".");
    }

    /**
     * Registers a new return for one or more products from an existing sale.
     *
     * @param saleId     the id of the original sale
     * @param productIds the ids of the products being returned
     * @param reason     the reason for the return
     * @return the newly created and persisted Return
     */
    public Return registerReturn(String saleId, List<String> productIds, String reason) {
        Sale sale = findSaleById(saleId);

        if (!sale.canBeReturned()) {
            throw new IllegalArgumentException("Return cannot be processed: more than 30 days have passed since purchase.");
        }

        if (productIds == null || productIds.isEmpty()) {
            throw new IllegalArgumentException("At least one product must be specified for return.");
        }

        // Check for already returned products under this sale to prevent double-refunding
        List<Return> previousReturns = viewReturnsBySale(saleId);
        List<String> alreadyReturnedProductIds = previousReturns.stream()
                .filter(r -> r.getReturnedProducts() != null)
                .flatMap(r -> r.getReturnedProducts().stream())
                .map(Product::getId)
                .collect(Collectors.toList());

        List<Product> returnedProducts = new ArrayList<>();
        for (String productId : productIds) {
            if (productId != null) {
                if (alreadyReturnedProductIds.contains(productId)) {
                    throw new IllegalArgumentException("Product ID " + productId + " has already been returned for this sale.");
                }
                returnedProducts.add(findProductInSale(sale, productId));
            }
        }

        if (returnedProducts.isEmpty()) {
            throw new IllegalArgumentException("No valid products were found to process the return.");
        }

        String returnId = UUID.randomUUID().toString();
        Return newReturn = new Return(returnId, LocalDate.now(), sale, returnedProducts, reason, 0.0);
        newReturn.calculateRefundAmount();

        // Restore 1 unit per item (or adapt to quantity if SaleDetail supports multi-quantity returns)
        for (Product product : returnedProducts) {
            saleService.restoreStock(product, 1);
        }

        synchronized (returns) {
            returns.add(newReturn);
            repository.saveAll(new ArrayList<>(returns));
        }

        return newReturn;
    }

    public List<Return> viewAllReturns() {
        synchronized (returns) {
            return new ArrayList<>(returns);
        }
    }

    public List<Return> viewReturnsByCustomer(String customerId) {
        if (customerId == null) {
            return Collections.emptyList();
        }

        synchronized (returns) {
            return returns.stream()
                    .filter(r -> r != null && r.getOriginalSale() != null && r.getOriginalSale().getClient() != null)
                    .filter(r -> customerId.equals(r.getOriginalSale().getClient().getId()))
                    .collect(Collectors.toList());
        }
    }

    public List<Return> viewReturnsBySale(String saleId) {
        if (saleId == null) {
            return Collections.emptyList();
        }

        synchronized (returns) {
            return returns.stream()
                    .filter(r -> r != null && r.getOriginalSale() != null)
                    .filter(r -> saleId.equals(r.getOriginalSale().getId()))
                    .collect(Collectors.toList());
        }
    }

    public double generateMonthlyBalance(int month, int year) {
        double totalSales = Optional.ofNullable(saleService.listAllSales())
                .orElse(Collections.emptyList())
                .stream()
                .filter(sale -> sale != null && sale.getDate() != null)
                .filter(sale -> sale.getDate().getMonthValue() == month && sale.getDate().getYear() == year)
                .mapToDouble(Sale::calculateFinalTotal)
                .sum();

        double totalReturns;
        synchronized (returns) {
            totalReturns = returns.stream()
                    .filter(r -> r != null && r.getDate() != null)
                    .filter(r -> r.getDate().getMonthValue() == month && r.getDate().getYear() == year)
                    .mapToDouble(Return::getRefundAmount)
                    .sum();
        }

        return totalSales - totalReturns;
    }
}