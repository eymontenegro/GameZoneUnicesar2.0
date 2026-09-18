package com.gamezone.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a sale transaction in the GameZone store.
 * Aggregates client, seller, items (SaleDetail), applied promotion, date,
 * and any additional costs not tied to a product line item (e.g. extended warranties).
 */
public class Sale {

    private String id;
    private LocalDate date;
    private Client client;
    private Seller seller;
    private List<SaleDetail> details;
    private Promotion promotion;
    private double additionalCosts;

    /**
     * Constructs a new Sale with the specified ID, client, seller, and date.
     *
     * @param id the unique sale identifier
     * @param client the client who made the purchase
     * @param seller the seller who processed the sale
     * @param date the date of the sale
     */
    public Sale(String id, Client client, Seller seller, LocalDate date) {
        this.id = Objects.requireNonNull(id, "Sale ID cannot be null.");
        this.client = Objects.requireNonNull(client, "Client cannot be null.");
        this.seller = Objects.requireNonNull(seller, "Seller cannot be null.");
        this.date = (date != null) ? date : LocalDate.now();
        this.details = new ArrayList<>();
        this.promotion = null;
        this.additionalCosts = 0.0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Seller getSeller() {
        return seller;
    }

    public void setSeller(Seller seller) {
        this.seller = seller;
    }

    public List<SaleDetail> getDetails() {
        return new ArrayList<>(details);
    }

    public Promotion getPromotion() {
        return promotion;
    }

    public void setPromotion(Promotion promotion) {
        this.promotion = promotion;
    }

    /**
     * Adds a sale detail line to this sale.
     *
     * @param detail the sale detail to add
     */
    public void addDetail(SaleDetail detail) {
        if (detail != null) {
            this.details.add(detail);
        }
    }

    /**
     * Adds an additional cost to the sale that is not tied to a product line
     * item, such as the extra cost of an extended warranty. This cost is
     * added to the final total after the promotional discount is applied,
     * since it is not part of the discountable product subtotal.
     *
     * @param amount the additional cost to add; ignored if negative
     */
    public void addExtraCost(double amount) {
        if (amount > 0) {
            this.additionalCosts += amount;
        }
    }

    /**
     * Returns the accumulated additional costs (e.g. extended warranties)
     * applied to this sale.
     *
     * @return the total additional costs
     */
    public double getAdditionalCosts() {
        return additionalCosts;
    }

    /**
     * Calculates the gross total (subtotal) of the sale before applying any discount.
     *
     * @return subtotal amount of all item details
     */
    public double calculateTotal() {
        double total = 0.0;
        for (SaleDetail detail : details) {
            total += detail.getSubtotal();
        }
        return total;
    }

    /**
     * Calculates the monetary discount amount based on the assigned promotion.
     *
     * @return discount amount, or 0.0 if no promotion is active/applied
     */
    public double calculateDiscount() {
        if (promotion != null && promotion.isActive(date)) {
            return promotion.calculateDiscount(this);
        }
        return 0.0;
    }

    /**
     * Calculates the final net total: the discounted product subtotal plus
     * any additional costs (e.g. extended warranties), which are not subject
     * to promotional discounts.
     *
     * @return final total amount
     */
    public double calculateFinalTotal() {
        double grossTotal = calculateTotal();
        double discount = calculateDiscount();
        double netProductTotal = Math.max(0.0, grossTotal - discount);
        return netProductTotal + additionalCosts;
    }

    /**
     * Checks if the sale is eligible for a return within 30 calendar days.
     *
     * @return true if the current date is within 30 days of the sale date, false otherwise
     */
    public boolean canBeReturned() {
        LocalDate limitDate = this.date.plusDays(30);
        return !LocalDate.now().isAfter(limitDate);
    }
}
