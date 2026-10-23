package com.gamezone.service;

import com.gamezone.exceptions.BusinessRuleException;
import com.gamezone.exceptions.InvalidDataException;
import com.gamezone.exceptions.ResourceNotFoundException;
import com.gamezone.model.BulkPurchaseDiscount;
import com.gamezone.model.CategoryDiscount;
import com.gamezone.model.PercentageDiscount;
import com.gamezone.model.Promotion;
import com.gamezone.model.Sale;
import com.gamezone.persistence.PromotionStorage;
import com.gamezone.validation.PromotionValidator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Applies the business rules for managing promotions: registering new
 * percentage, category, and bulk-purchase promotions, listing them,
 * and selecting the best applicable promotion for a given sale.
 * Uses PromotionStorage to keep persistence synchronized.
 */
public class PromotionService {

    private final List<Promotion> promotions;
    private final PromotionStorage repository;

    /**
     * Constructs the service using the given repository and loads any
     * previously saved promotions from persistence.
     *
     * @param repository the repository used to persist promotions
     */
    public PromotionService(PromotionStorage repository) {
        this.repository = Objects.requireNonNull(repository, "PromotionRepository cannot be null");
        List<Promotion> loadedPromotions = repository.loadAll();
        this.promotions = Collections.synchronizedList(loadedPromotions != null ? new ArrayList<>(loadedPromotions) : new ArrayList<>());
    }

    /**
     * Validates the data shared by every promotion and checks for duplicates.
     *
     * @throws InvalidDataException  if any field is invalid
     * @throws BusinessRuleException if a promotion with the same ID already exists
     */
    private void validateCommonPromotionData(String id, String name, LocalDate startDate, LocalDate endDate, double percentage) {
        PromotionValidator.validatePromotionData(id, name, startDate, endDate, percentage);
        if (existsId(id)) {
            throw new BusinessRuleException("Ya existe una promoción con el ID: " + id);
        }
    }

    private boolean existsId(String id) {
        synchronized (promotions) {
            return promotions.stream()
                    .filter(Objects::nonNull)
                    .anyMatch(p -> p.getId() != null && p.getId().equalsIgnoreCase(id.trim()));
        }
    }

    public void registerPercentageDiscount(String id, String name, LocalDate startDate, LocalDate endDate, double percentage) {
        validateCommonPromotionData(id, name, startDate, endDate, percentage);
        PercentageDiscount newPromotion = new PercentageDiscount(id, name, startDate, endDate, percentage);
        savePromotion(newPromotion);
    }

    public void registerCategoryDiscount(String id, String name, LocalDate startDate, LocalDate endDate,
                                         double percentage, String targetCategory) {
        validateCommonPromotionData(id, name, startDate, endDate, percentage);
        PromotionValidator.validateCategoryData(targetCategory);
        CategoryDiscount newPromotion = new CategoryDiscount(id, name, startDate, endDate, percentage, targetCategory);
        savePromotion(newPromotion);
    }

    public void registerBulkPurchaseDiscount(String id, String name, LocalDate startDate, LocalDate endDate,
                                             int minimumQuantity, double percentage) {
        validateCommonPromotionData(id, name, startDate, endDate, percentage);
        PromotionValidator.validateBulkPurchaseData(minimumQuantity);
        BulkPurchaseDiscount newPromotion = new BulkPurchaseDiscount(id, name, startDate, endDate, minimumQuantity, percentage);
        savePromotion(newPromotion);
    }

    private void savePromotion(Promotion promotion) {
        synchronized (promotions) {
            promotions.add(promotion);
            repository.saveAll(new ArrayList<>(promotions));
        }
    }

    public List<Promotion> listAllPromotions() {
        synchronized (promotions) {
            return new ArrayList<>(promotions);
        }
    }

    public List<Promotion> listActivePromotions() {
        return listActivePromotionsOnDate(LocalDate.now());
    }

    public List<Promotion> listActivePromotionsOnDate(LocalDate date) {
        if (date == null) {
            return Collections.emptyList();
        }
        synchronized (promotions) {
            return promotions.stream()
                    .filter(Objects::nonNull)
                    .filter(promotion -> promotion.isActive(date))
                    .collect(Collectors.toList());
        }
    }

    /**
     * Finds a single promotion by its ID.
     *
     * @param id the id of the promotion to find
     * @return the matching promotion
     * @throws InvalidDataException      if id is null or empty
     * @throws ResourceNotFoundException if no promotion with that id exists
     */
    public Promotion findById(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new InvalidDataException("identificador de la promoción", "no puede ser nulo ni vacío");
        }
        Promotion found;
        synchronized (promotions) {
            found = promotions.stream()
                    .filter(Objects::nonNull)
                    .filter(p -> p.getId() != null && p.getId().equalsIgnoreCase(id.trim()))
                    .findFirst()
                    .orElse(null);
        }
        PromotionValidator.validatePromotionExists(found, id);
        return found;
    }

    public Promotion findBestPromotionFor(Sale sale) {
        if (sale == null) {
            return null;
        }

        LocalDate evaluationDate = Optional.ofNullable(sale.getDate()).orElseGet(LocalDate::now);
        List<Promotion> activePromotions = listActivePromotionsOnDate(evaluationDate);

        Promotion bestPromotion = null;
        double bestDiscount = 0.0;

        for (Promotion promotion : activePromotions) {
            double discount = promotion.calculateDiscount(sale);
            if (discount > bestDiscount) {
                bestDiscount = discount;
                bestPromotion = promotion;
            }
        }

        return bestPromotion;
    }
}