package com.gamezone.service;

import com.gamezone.model.BulkPurchaseDiscount;
import com.gamezone.model.CategoryDiscount;
import com.gamezone.model.PercentageDiscount;
import com.gamezone.model.Promotion;
import com.gamezone.model.Sale;
import com.gamezone.persistence.PromotionRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Applies the business rules for managing promotions: registering new
 * percentage, category and bulk-purchase promotions, listing them,
 * and selecting the best applicable promotion for a given sale.
 * Uses PromotionRepository to keep the file in sync with every change.
 */
public class PromotionService {

    private List<Promotion> promotions;
    private PromotionRepository repository;

    /**
     * Creates the service using the given repository and loads any
     * previously saved promotions from the file, so the catalog is
     * available as soon as the application starts.
     *
     * @param repository the repository used to persist promotions
     */
    public PromotionService(PromotionRepository repository) {
        this.repository = repository;
        this.promotions = repository.loadAll();
    }

    /**
     * Registers a new percentage promotion and immediately saves the
     * updated catalog to the file.
     *
     * @param id the promotion identifier
     * @param name the promotion name
     * @param startDate the date the promotion becomes active
     * @param endDate the date the promotion stops being active
     * @param percentage the discount percentage applied to the sale total
     */
    public void registerPercentageDiscount(String id, String name, LocalDate startDate, LocalDate endDate,
                                            double percentage) {
        PercentageDiscount newPromotion = new PercentageDiscount(id, name, startDate, endDate, percentage);
        promotions.add(newPromotion);
        repository.saveAll(promotions);
    }

    /**
     * Registers a new category promotion and immediately saves the
     * updated catalog to the file.
     *
     * @param id the promotion identifier
     * @param name the promotion name
     * @param startDate the date the promotion becomes active
     * @param endDate the date the promotion stops being active
     * @param percentage the discount percentage applied to the target category
     * @param targetCategory the category this promotion applies to ("VIDEOGAME" or "CONSOLE")
     */
    public void registerCategoryDiscount(String id, String name, LocalDate startDate, LocalDate endDate,
                                          double percentage, String targetCategory) {
        CategoryDiscount newPromotion = new CategoryDiscount(id, name, startDate, endDate, percentage, targetCategory);
        promotions.add(newPromotion);
        repository.saveAll(promotions);
    }

    /**
     * Registers a new bulk-purchase promotion and immediately saves the
     * updated catalog to the file.
     *
     * @param id the promotion identifier
     * @param name the promotion name
     * @param startDate the date the promotion becomes active
     * @param endDate the date the promotion stops being active
     * @param minimumQuantity the minimum number of products required in the sale
     * @param percentage the discount percentage applied to the sale total
     */
    public void registerBulkPurchaseDiscount(String id, String name, LocalDate startDate, LocalDate endDate,
                                              int minimumQuantity, double percentage) {
        BulkPurchaseDiscount newPromotion = new BulkPurchaseDiscount(id, name, startDate, endDate, minimumQuantity, percentage);
        promotions.add(newPromotion);
        repository.saveAll(promotions);
    }

    /**
     * Returns the full list of currently registered promotions.
     *
     * @return the list of all promotions
     */
    public List<Promotion> listAllPromotions() {
        // TODO: implemented in a follow-up commit
        return null;
    }

    /**
     * Returns the promotions that are active on the current date.
     *
     * @return the list of currently active promotions
     */
    public List<Promotion> listActivePromotions() {
        // TODO: implemented in a follow-up commit
        return null;
    }

    /**
     * Finds a single promotion by its id.
     *
     * @param id the id of the promotion to find
     * @return the matching promotion
     */
    public Promotion findById(String id) {
        // TODO: implemented in a follow-up commit
        return null;
    }

    /**
     * Among the active promotions, finds the one that would grant the
     * highest monetary discount to the given sale.
     *
     * @param sale the sale to evaluate
     * @return the best applicable promotion, or null if none applies
     */
    public Promotion findBestPromotionFor(Sale sale) {
        // TODO: implemented in a follow-up commit
        return null;
    }
}