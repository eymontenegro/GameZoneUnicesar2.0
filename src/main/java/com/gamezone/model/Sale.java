package com.gamezone.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a sale transaction made by a Client and handled by a Seller.
 * Contains a collection of SaleDetail items representing individual products sold.
 */
public class Sale {

    private String id;
    private LocalDate date;
    private Client client;
    private Seller seller;
    private List<SaleDetail> details;

    /**
     * Creates a new Sale with the given date, client, seller, and details list.
     * A unique identifier is generated automatically for this sale.
     *
     * @param date the date when the sale took place
     * @param client the client who made the purchase
     * @param seller the seller who processed the sale
     * @param details the list of sale details (items purchased)
     */
    public Sale(LocalDate date, Client client, Seller seller, List<SaleDetail> details) {
        this.id = UUID.randomUUID().toString();
        this.date = Objects.requireNonNull(date, "La fecha no puede ser nula.");
        this.client = Objects.requireNonNull(client, "El cliente no puede ser nulo.");
        this.seller = Objects.requireNonNull(seller, "El vendedor no puede ser nulo.");
        Objects.requireNonNull(details, "La lista de detalles no puede ser nula.");
        this.details = new ArrayList<>(details);
    }

    /**
     * Returns the unique identifier of this sale.
     *
     * @return the sale id
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the date of the sale.
     *
     * @return the sale date
     */
    public LocalDate getDate() {
        return date;
    }

     /**
     * Returns the client involved in the sale.
     *
     * @return the client
     */
    public Client getClient() {
        return client;
    }

   /**
     * Returns the seller who handled the sale.
     *
     * @return the seller
     */
    public Seller getSeller() {
        return seller;
    }

     /**
     * Returns an unmodifiable list of sale details.
     *
     * @return the list of sale details
     */
    public List<SaleDetail> getDetails() {
        return Collections.unmodifiableList(details);
    }

   /**
     * Calculates the total amount of the sale by summing up
     * the subtotals of all detail lines.
     *
     * @return the total price of the sale
     */
    public double calculateTotal() {
        double total = 0.0;
        for (SaleDetail detail : details) {
            total += detail.getSubtotal();
        }
        return total;
    }

       /**
     * Confirms the sale.
     * Validates that the sale contains at least one product detail
     * and adds this sale to the client's purchase history.
     */
    public void confirm() {
        if (details.isEmpty()) {
            throw new IllegalStateException("No se puede confirmar una venta sin al menos un producto.");
        }
        client.addPurchase(this.getId());
    }
}
