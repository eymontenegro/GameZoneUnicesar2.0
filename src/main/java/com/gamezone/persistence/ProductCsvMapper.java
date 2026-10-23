package com.gamezone.persistence;

import com.gamezone.model.Product;

/**
 * Maps one concrete product type to and from the type-specific columns of
 * the products CSV file. {@link ProductRepository} keeps the common columns
 * and delegates the rest to the mapper that supports each product, so a new
 * product type only needs a new mapper instead of new conditionals.
 */
public interface ProductCsvMapper {

    /**
     * Returns the code written in the first column for this product type.
     *
     * @return the type code (for example {@code "VIDEOGAME"})
     */
    String typeCode();

    /**
     * Tells whether this mapper handles the given product.
     *
     * @param product the product to check
     * @return true if this mapper can convert the product
     */
    boolean supports(Product product);

    /**
     * Returns the names of the type-specific columns, in file order. They
     * are used in the error messages when a value cannot be stored.
     *
     * @return the column names
     */
    String[] specificColumnNames();

    /**
     * Extracts the type-specific values of a supported product, in the same
     * order as {@link #specificColumnNames()}.
     *
     * @param product a product accepted by {@link #supports(Product)}
     * @return the raw values of the specific columns
     */
    String[] toSpecificColumns(Product product);

    /**
     * Builds a product of this type from already validated common data and
     * its type-specific columns.
     *
     * @param id       the product identifier
     * @param title    the product title
     * @param price    the product price
     * @param stock    the available stock
     * @param specific the type-specific column values, in file order
     * @return the rebuilt product
     */
    Product fromColumns(String id, String title, double price, int stock, String[] specific);
}
