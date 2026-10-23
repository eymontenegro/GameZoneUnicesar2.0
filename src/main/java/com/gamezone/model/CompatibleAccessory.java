package com.gamezone.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for accessories that declare which consoles they work with.
 * It owns the list of compatible console identifiers, so the compatibility
 * rules live in one place; the check itself is the default
 * {@link ConsoleCompatible#isCompatibleWith(String)}.
 */
public abstract class CompatibleAccessory extends Accessory implements ConsoleCompatible {

    private List<String> compatibleConsoleIds;

    /**
     * Creates an accessory with its common product data and its compatible consoles.
     *
     * @param id                   the product identifier
     * @param title                the product title
     * @param price                the product price
     * @param stock                the initial available stock
     * @param compatibleConsoleIds the compatible console IDs (copied; null means none)
     */
    public CompatibleAccessory(String id, String title, double price, int stock, List<String> compatibleConsoleIds) {
        super(id, title, price, stock);
        this.compatibleConsoleIds = (compatibleConsoleIds != null) ? new ArrayList<>(compatibleConsoleIds) : new ArrayList<>();
    }

    /**
     * Gets a copy of the list of compatible console identifiers.
     *
     * @return a list containing the compatible console IDs
     */
    @Override
    public List<String> getCompatibleConsoleIds() {
        return new ArrayList<>(this.compatibleConsoleIds);
    }

    /**
     * Adds a new console identifier to the compatibility list if not already present.
     *
     * @param consoleId the console ID to add
     */
    @Override
    public void addCompatibleConsole(String consoleId) {
        if (consoleId != null && !consoleId.trim().isEmpty() && !this.compatibleConsoleIds.contains(consoleId)) {
            this.compatibleConsoleIds.add(consoleId);
        }
    }
}
