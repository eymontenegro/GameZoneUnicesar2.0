package com.gamezone.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a client of the store, with attributes specific to clients
 * such as email and purchase history.
 */
public class Client extends Person {
    private String email;
    private final List<String> purchaseHistory;

    public Client(String email, String id, String name, String phone) {
        super(id, name, phone);
        this.email = email;
        this.purchaseHistory = new ArrayList<>();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Returns an unmodifiable view of the purchase history to prevent direct modification.
     */
    public List<String> getPurchaseHistory() {
        return Collections.unmodifiableList(purchaseHistory);
    }

    /**
     * Adds a sale id to this client's purchase history.
     *
     * @param saleId the id of the sale to register
     */
    public void addPurchase(String saleId) {
        if (saleId != null && !saleId.isBlank()) {
            purchaseHistory.add(saleId);
        }
    }

    @Override
    public String getRoleDescription() {
        return getName() + " es un cliente registrado, correo " + email;
    }
}