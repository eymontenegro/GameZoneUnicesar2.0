package com.gamezone.service;

import com.gamezone.model.BasicWarranty;
import com.gamezone.model.ExtendedWarranty;
import com.gamezone.model.Product;
import com.gamezone.model.Sale;
import com.gamezone.model.Warranty;
import com.gamezone.persistence.WarrantyRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Applies the business rules for managing warranties: assigning basic
 * and extended warranties, finding a warranty by product and sale,
 * and listing active or soon-to-expire warranties. Uses
 * WarrantyRepository to keep the file in sync with every change.
 */
public class WarrantyService {

    private List<Warranty> warranties;
    private WarrantyRepository repository;
    private SaleService saleService;
    private ProductService productService;

    /**
     * Creates the service injecting the repository and the services
     * needed to resolve sale and product references while loading
     * previously saved warranties from the file.
     *
     * @param repository the repository used to persist warranties
     * @param saleService the service used to resolve sale references on load
     * @param productService the service used to resolve product references on load
     */
    public WarrantyService(WarrantyRepository repository, SaleService saleService, ProductService productService) {
        this.repository = repository;
        this.saleService = saleService;
        this.productService = productService;
        this.warranties = repository.loadAll(saleService.listAllSales(), productService.listAll());
    }

    /**
     * Creates and persists an automatic basic warranty for the given
     * product and sale. Basic warranties have no additional cost.
     *
     * @param product the covered product
     * @param sale the sale that generated the warranty
     * @param startDate the warranty's start date (the sale's date)
     * @return the newly created and persisted BasicWarranty
     */
    public BasicWarranty assignBasicWarranty(Product product, Sale sale, LocalDate startDate) {
        String id = UUID.randomUUID().toString();
        BasicWarranty warranty = new BasicWarranty(id, product, sale, startDate);
        warranties.add(warranty);
        repository.saveAll(warranties);
        return warranty;
    }

    /**
     * Creates and persists an extended warranty for the given product
     * and sale, requested by the seller at the time of the sale.
     *
     * @param product the covered product
     * @param sale the sale that generated the warranty
     * @param startDate the warranty's start date (the sale's date)
     * @return the newly created and persisted ExtendedWarranty
     */
    public ExtendedWarranty assignExtendedWarranty(Product product, Sale sale, LocalDate startDate) {
        String id = UUID.randomUUID().toString();
        ExtendedWarranty warranty = new ExtendedWarranty(id, product, sale, startDate);
        warranties.add(warranty);
        repository.saveAll(warranties);
        return warranty;
    }

    /**
     * Finds the warranty associated with a specific product within a
     * specific sale.
     *
     * @param productId the id of the product
     * @param saleId the id of the sale
     * @return the matching warranty, or null if none exists
     */
    public Warranty findWarrantyByProduct(String productId, String saleId) {
        for (Warranty warranty : warranties) {
            if (warranty.getProduct().getId().equals(productId) && warranty.getSale().getId().equals(saleId)) {
                return warranty;
            }
        }
        return null;
    }

    /**
     * Returns the full list of currently registered warranties.
     *
     * @return the list of all warranties
     */
    public List<Warranty> listAllWarranties() {
        return warranties;
    }

    /**
     * Returns the warranties that are active on the current date.
     *
     * @return the list of currently active warranties
     */
    public List<Warranty> listActiveWarranties() {
        List<Warranty> activeWarranties = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (Warranty warranty : warranties) {
            if (warranty.isActive(today)) {
                activeWarranties.add(warranty);
            }
        }
        return activeWarranties;
    }

    /**
     * Returns the warranties whose end date falls within the given
     * number of days from today.
     *
     * @param daysAhead the number of days to look ahead
     * @return the list of warranties expiring within that window
     */
    public List<Warranty> listWarrantiesExpiringSoon(int daysAhead) {
        // TODO: implemented in a follow-up commit
        return null;
    }
}