package com.gamezone.service;

import com.gamezone.model.BulkPurchaseDiscount;
import com.gamezone.model.CategoryDiscount;
import com.gamezone.model.PercentageDiscount;
import com.gamezone.model.Promotion;
import com.gamezone.model.Sale;
import com.gamezone.persistence.PromotionRepository;

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
 * Uses PromotionRepository to keep persistence synchronized.
 */
public class PromotionService {

    private final List<Promotion> promotions;
    private final PromotionRepository repository;

    /**
     * Constructs the service using the given repository and loads any
     * previously saved promotions from persistence.
     *
     * @param repository the repository used to persist promotions
     */
    public PromotionService(PromotionRepository repository) {
        this.repository = Objects.requireNonNull(repository, "PromotionRepository cannot be null");
        List<Promotion> loadedPromotions = repository.loadAll();
        this.promotions = Collections.synchronizedList(loadedPromotions != null ? new ArrayList<>(loadedPromotions) : new ArrayList<>());
    }

    private void validateCommonPromotionData(String id, String name, LocalDate startDate, LocalDate endDate, double percentage) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Promotion ID cannot be empty.");
        }
        if (existsId(id)) {
            throw new IllegalArgumentException("A promotion already exists with ID: " + id);
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Promotion name cannot be empty.");
        }
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Start date and end date cannot be null.");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date.");
        }
        if (percentage <= 0 || percentage > 100) {
            throw new IllegalArgumentException("Discount percentage must be greater than 0 and less than or equal to 100.");
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
        if (targetCategory == null || targetCategory.trim().isEmpty()) {
            throw new IllegalArgumentException("Target category cannot be empty.");
        }
        CategoryDiscount newPromotion = new CategoryDiscount(id, name, startDate, endDate, percentage, targetCategory);
        savePromotion(newPromotion);
    }

    public void registerBulkPurchaseDiscount(String id, String name, LocalDate startDate, LocalDate endDate,
                                             int minimumQuantity, double percentage) {
        validateCommonPromotionData(id, name, startDate, endDate, percentage);
        if (minimumQuantity <= 0) {
            throw new IllegalArgumentException("Minimum quantity for bulk purchase must be greater than 0.");
        }
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

    public Promotion findById(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Promotion ID cannot be null or empty.");
        }
        synchronized (promotions) {
            return promotions.stream()
                    .filter(Objects::nonNull)
                    .filter(p -> p.getId() != null && p.getId().equalsIgnoreCase(id.trim()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Unknown promotion ID: " + id));
        }
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