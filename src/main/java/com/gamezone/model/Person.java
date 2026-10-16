package com.gamezone.model;

/**
 * Abstract class representing a person in the system.
 * Serves as a base class for specific roles like Client.
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

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Abstract method to be implemented by subclasses to define role details.
     *
     * @return A description of the person's role in the store.
     */
    public abstract String getRoleDescription();
}