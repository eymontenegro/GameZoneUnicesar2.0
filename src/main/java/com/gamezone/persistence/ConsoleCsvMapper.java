package com.gamezone.persistence;

import com.gamezone.model.Console;
import com.gamezone.model.Product;

/**
 * CSV mapper for {@link Console}: brand, model and generation.
 */
public class ConsoleCsvMapper implements ProductCsvMapper {

    @Override
    public String typeCode() {
        return "CONSOLE";
    }

    @Override
    public boolean supports(Product product) {
        return product instanceof Console;
    }

    @Override
    public String[] specificColumnNames() {
        return new String[] {"brand", "model", "generation"};
    }

    @Override
    public String[] toSpecificColumns(Product product) {
        Console c = (Console) product;
        return new String[] {c.getBrand(), c.getModel(), c.getGeneration()};
    }

    @Override
    public Product fromColumns(String id, String title, double price, int stock, String[] specific) {
        return new Console(specific[0], specific[1], specific[2], id, title, price, stock);
    }
}
