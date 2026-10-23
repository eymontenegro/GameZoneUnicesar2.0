package com.gamezone.model;

/**
 * Commercial categories a product can belong to. Each product reports its
 * own category through {@link Product#getCategory()}.
 */
public enum ProductCategory {
    VIDEOGAME,
    CONSOLE,
    ACCESSORY
}
