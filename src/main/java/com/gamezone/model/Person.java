package com.gamezone.model;

/**
 * Represents a generic person interacting with the store.
 * This is the base class for all person types (clients, sellers),
 * and holds the attributes and behavior common to any person.
 */
public abstract class Person {
    private String id;
    private String name;
    private String phone;

    public Person(String id, String name, String phone) {
        this.id = id;
        this.name = name;
        this.phone = phone;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    /**
     * Builds a description of the person's role, combining common and
     * particular attributes. Each subclass provides its own version.
     *
     * @return a text description of the person's role
     */
    public abstract String getRoleDescription();
}