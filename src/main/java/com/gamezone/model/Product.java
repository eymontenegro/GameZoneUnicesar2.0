package com.gamezone.model;
/**
 * Represents a generic product sold by the store.
 * This is the base class for all product types (video games, consoles),
 * and holds the attributes and behavior common to any product.
 */
public abstract class Product {
    private String id;
    private String title;
    private double price;
    private int stock;

    public Product(String id, String title, double price, int stock) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.stock = stock;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public double getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }
