package com.gamezone.service;

import com.gamezone.exceptions.InvalidDataException;
import com.gamezone.model.BasicWarranty;
import com.gamezone.model.ExtendedWarranty;
import com.gamezone.model.Product;
import com.gamezone.model.Sale;
import com.gamezone.model.Warranty;
import com.gamezone.persistence.WarrantyRepository;
import com.gamezone.validation.WarrantyValidator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Applies the business rules for managing warranties: assigning basic
 * and extended warranties, finding a warranty by ID or by product/sale,
 * and listing active or soon-to-expire warranties. Uses WarrantyRepository
 * to keep the file in sync with every change.
 */
public class WarrantyService {

    private final List<Warranty> warranties;
    private final WarrantyRepository repository;

    /**
     * Creates the service injecting the repository, loading previously
     * saved warranties from the file.
     *
     * @param repository the repository used to persist and load warranties
     */
    public WarrantyService(WarrantyRepository repository) {
        this.repository = Objects.requireNonNull(repository, "El repositorio de garantías no puede ser nulo.");
        List<Warranty> loaded = repository.loadAll();
        this.warranties = Collections.synchronizedList(loaded != null ? new ArrayList<>(loaded) : new ArrayList<>());
    }

    /**
     * Validates required entities before creating any warranty.
     *
     * @throws InvalidDataException if the product, the sale or the start date is invalid
     */
    private void validateWarrantyCreation(Product product, Sale sale, LocalDate startDate) {
        WarrantyValidator.validateWarrantyData(product, sale, startDate);
    }

    public BasicWarranty assignBasicWarranty(Product product, Sale sale, LocalDate startDate) {
        validateWarrantyCreation(product, sale, startDate);
        String id = UUID.randomUUID().toString();
        BasicWarranty warranty = new BasicWarranty(id, product, sale, startDate);
        saveWarranty(warranty);
        return warranty;
    }

    public ExtendedWarranty assignExtendedWarranty(Product product, Sale sale, LocalDate startDate) {
        validateWarrantyCreation(product, sale, startDate);
        String id = UUID.randomUUID().toString();
        ExtendedWarranty warranty = new ExtendedWarranty(id, product, sale, startDate);
        saveWarranty(warranty);
        return warranty;
    }

    private void saveWarranty(Warranty warranty) {
        synchronized (warranties) {
            warranties.add(warranty);
            repository.saveAll(new ArrayList<>(warranties));
        }
    }

    public Warranty findById(String warrantyId) {
        if (warrantyId == null || warrantyId.trim().isEmpty()) {
            return null;
        }
        synchronized (warranties) {
            return warranties.stream()
                    .filter(Objects::nonNull)
                    .filter(w -> warrantyId.equalsIgnoreCase(w.getId()))
                    .findFirst()
                    .orElse(null);
        }
    }

    public Warranty findWarrantyByProduct(String productId, String saleId) {
        if (productId == null || saleId == null) {
            return null;
        }
        synchronized (warranties) {
            return warranties.stream()
                    .filter(Objects::nonNull)
                    .filter(w -> w.getProduct() != null && w.getSale() != null)
                    .filter(w -> productId.equalsIgnoreCase(w.getProduct().getId()) && saleId.equalsIgnoreCase(w.getSale().getId()))
                    .findFirst()
                    .orElse(null);
        }
    }

    public List<Warranty> listAllWarranties() {
        synchronized (warranties) {
            return new ArrayList<>(warranties);
        }
    }

    public List<Warranty> listActiveWarranties() {
        LocalDate today = LocalDate.now();
        synchronized (warranties) {
            return warranties.stream()
                    .filter(Objects::nonNull)
                    .filter(w -> w.isActive(today))
                    .collect(Collectors.toList());
        }
    }

    public List<Warranty> listWarrantiesExpiringSoon(int daysAhead) {
        WarrantyValidator.validateDaysAhead(daysAhead);
        LocalDate today = LocalDate.now();
        LocalDate limit = today.plusDays(daysAhead);

        synchronized (warranties) {
            return warranties.stream()
                    .filter(Objects::nonNull)
                    .filter(w -> w.expiresBetween(today, limit))
                    .collect(Collectors.toList());
        }
    }
}