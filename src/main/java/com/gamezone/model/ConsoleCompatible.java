package com.gamezone.model;

import java.util.List;

/**
 * Interface representing products or elements compatible with video game consoles.
 */
public interface ConsoleCompatible {

    /**
     * Gets the list of compatible console IDs.
     *
     * @return a list of console identifiers
     */
    List<String> getCompatibleConsoleIds();

    /**
     * Adds a console ID to the list of compatible consoles.
     *
     * @param consoleId the console identifier to add
     */
    void addCompatibleConsole(String consoleId);

    /**
     * Checks if the product is compatible with the given console ID.
     * Provides a default implementation based on the compatible list.
     *
     * @param consoleId the console identifier to check
     * @return true if compatible, false otherwise
     */
    default boolean isCompatibleWith(String consoleId) {
        if (consoleId == null || getCompatibleConsoleIds() == null) {
            return false;
        }
        return getCompatibleConsoleIds().contains(consoleId);
    }
}