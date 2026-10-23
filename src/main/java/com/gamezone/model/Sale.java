package com.gamezone.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a sale transaction in the GameZone store.
 * Aggregates client, seller, items (SaleDetail), applied promotion name,
 * discount amount, additional costs (warranties), and total final.
 */
public class Sale {

    private String id;
    private LocalDate date;
    private Client client;
    private Seller seller;
    private List<SaleDetail> details;
    private String appliedPromotionName;
    private double discountAmount;
    private double additionalCosts;
    private double total;

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
        this.appliedPromotionName = "Ninguna";
        this.discountAmount = 0.0;
        this.additionalCosts = 0.0;
        this.total = 0.0;
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

    public String getAppliedPromotionName() {
        return appliedPromotionName;
    }

    public void setAppliedPromotionName(String appliedPromotionName) {
        this.appliedPromotionName = (appliedPromotionName != null && !appliedPromotionName.trim().isEmpty())
                ? appliedPromotionName : "Ninguna";
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = Math.max(0.0, discountAmount);
    }

    public double getAdditionalCosts() {
        return additionalCosts;
    }

    public void setAdditionalCosts(double additionalCosts) {
        this.additionalCosts = Math.max(0.0, additionalCosts);
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = Math.max(0.0, total);
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
     * Adds an additional cost to the sale (e.g. extended warranty cost).
     *
     * @param amount the additional cost to add
     */
    public void addExtraCost(double amount) {
        if (amount > 0) {
            this.additionalCosts += amount;
        }
    }

    /**
     * Calculates the gross subtotal of all items in the sale before discounts or extra costs.
     *
     * @return subtotal amount of all line items
     */
    public double calculateSubtotal() {
        double subtotal = 0.0;
        for (SaleDetail detail : details) {
            subtotal += detail.getSubtotal();
        }
        return subtotal;
    }

    /**
     * Legacy support method for subtotal calculation.
     *
     * @return subtotal amount of all line items
     */
    public double calculateTotal() {
        return calculateSubtotal();
    }

    /**
     * Returns the total number of units sold in this sale, that is, the sum of
     * the quantity of every line item.
     *
     * @return total units of the sale, or 0 when the sale has no details
     */
    public int getTotalUnits() {
        int totalUnits = 0;
        for (SaleDetail detail : details) {
            if (detail != null) {
                totalUnits += detail.getQuantity();
            }
        }
        return totalUnits;
    }

    /**
     * Returns the proportion of the gross subtotal that was discounted
     * (discount amount divided by subtotal).
     *
     * @return the discount ratio, or 0.0 when the subtotal is not positive
     */
    public double getDiscountRatio() {
        double subtotal = calculateSubtotal();
        return (subtotal > 0) ? (discountAmount / subtotal) : 0.0;
    }

    /**
     * Looks up a product among the lines of this sale.
     *
     * @param productId the identifier of the product to look for
     * @return the product with that identifier, or null if the sale does not contain it
     */
    public Product findProductById(String productId) {
        if (productId == null) {
            return null;
        }
        for (SaleDetail detail : details) {
            if (detail != null && detail.getProduct() != null
                    && productId.equals(detail.getProduct().getId())) {
                return detail.getProduct();
            }
        }
        return null;
    }

    /**
     * Recalculates and updates the final total of the sale based on subtotal,
     * applied discount, and additional costs (A3).
     * Final Total = (Subtotal - Discount) + AdditionalCosts.
     *
     * @return the calculated final total
     */
    public double calculateFinalTotal() {
        double subtotal = calculateSubtotal();
        double netSubtotal = Math.max(0.0, subtotal - discountAmount);
        this.total = netSubtotal + additionalCosts;
        return this.total;
    }

    /**
     * Checks if the sale is eligible for a return on a specific date (within 30 calendar days).
     *
     * @param returnDate the date when the return is requested
     * @return true if returnDate is within 30 days of the sale date
     */
    public boolean canBeReturned(LocalDate returnDate) {
        if (returnDate == null) {
            returnDate = LocalDate.now();
        }
        LocalDate limitDate = this.date.plusDays(30);
        return !returnDate.isBefore(this.date) && !returnDate.isAfter(limitDate);
    }

    /**
     * Checks if the sale is eligible for a return on the current date.
     *
     * @return true if current date is within 30 days of the sale date
     */
    public boolean canBeReturned() {
        return canBeReturned(LocalDate.now());
    }
}