package com.gamezone.model;
/**
 * Represents a console product, with attributes specific to consoles
 * such as brand, model, and generation.
 */

public class Console extends Product {
    private String brand;
    private String model;
    private String generation;

    public Console(String brand, String model, String generation, String id, String title, double price, int stock) {
        super(id, title, price, stock);
        this.brand = brand;
        this.model = model;
        this.generation = generation;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public String getGeneration() {
        return generation;
    }

    @Override
    public String describe() {
        return   getTitle()+ " es una consola de " + brand + " , modelo " + model + " , generacion " + generation ;   }
 
    
    
    
}
