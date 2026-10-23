package com.gamezone.model;

/**
 * Represents a console product, with attributes specific to consoles
 * such as brand, model, and generation.
 */
public class Console extends Product {

    private String brand;
    private String model;
    private String generation;

    /**
     * Constructs a new Console product.
     *
     * @param brand      the manufacturer brand (e.g., "Sony", "Nintendo", "Microsoft")
     * @param model      the specific model name (e.g., "PlayStation 5 Pro", "Switch OLED")
     * @param generation the console generation (e.g., "9th Gen")
     * @param id         the unique product identifier
     * @param title      the product title or name
     * @param price      the unit price
     * @param stock      the available stock quantity
     */
    public Console(String brand, String model, String generation, String id, String title, double price, int stock) {
        super(id, title, price, stock);
        this.brand = brand;
        this.model = model;
        this.generation = generation;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getGeneration() {
        return generation;
    }

    public void setGeneration(String generation) {
        this.generation = generation;
    }
        /**
     * Consoles belong to the {@link ProductCategory#CONSOLE} category.
     *
     * @return {@link ProductCategory#CONSOLE}
     */
    @Override
    public ProductCategory getCategory() {
        return ProductCategory.CONSOLE;
    }

    @Override
    public String describe() {
        return String.format("%s es una consola de %s, modelo %s, generación %s, precio: $%.2f, stock: %d",
                getTitle(), brand, model, generation, getPrice(), getStock());
    }
}