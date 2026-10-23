package com.gamezone.service;

import com.gamezone.model.Return;
import com.gamezone.model.Sale;

import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

/**
 * Computes the monthly balance of the store: the revenue of the sales of a
 * month minus the refunds paid in that same month. It combines data from
 * {@link SaleService} and {@link ReturnService} so that neither of them has
 * to take care of financial reporting.
 */
public class MonthlyBalanceService {

    private final SaleService saleService;
    private final ReturnService returnService;

    /**
     * Creates the service with the services that hold the sales and returns.
     *
     * @param saleService   the service used to read the sales
     * @param returnService the service used to read the returns
     */
    public MonthlyBalanceService(SaleService saleService, ReturnService returnService) {
        this.saleService = Objects.requireNonNull(saleService, "SaleService cannot be null.");
        this.returnService = Objects.requireNonNull(returnService, "ReturnService cannot be null.");
    }

    /**
     * Calculates the balance of a month: final total of the sales made in the
     * month minus the refund amount of the returns made in the month.
     *
     * @param month the month number (1 to 12)
     * @param year  the year
     * @return the sales revenue minus the refunds of the given month
     */
    public double generateMonthlyBalance(int month, int year) {
        double totalSales = Optional.ofNullable(saleService.listAllSales())
                .orElse(Collections.emptyList())
                .stream()
                .filter(sale -> sale != null && sale.getDate() != null)
                .filter(sale -> sale.getDate().getMonthValue() == month && sale.getDate().getYear() == year)
                .mapToDouble(Sale::calculateFinalTotal)
                .sum();

        double totalReturns = returnService.viewAllReturns().stream()
                .filter(r -> r != null && r.getDate() != null)
                .filter(r -> r.getDate().getMonthValue() == month && r.getDate().getYear() == year)
                .mapToDouble(Return::getRefundAmount)
                .sum();

        return totalSales - totalReturns;
    }
}