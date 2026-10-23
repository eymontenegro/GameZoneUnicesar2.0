package com.gamezone.ui;

import java.util.Scanner;

/**
 * Owns the single Scanner that reads the console and offers the safe
 * reading helpers shared by Menu and every sub-menu, so console input is
 * never read by two different Scanner objects at the same time.
 */
public class ConsoleInput {

    private final Scanner scanner;

    /**
     * Creates an input reader over the given Scanner.
     *
     * @param scanner the Scanner used to read console input
     */
    public ConsoleInput(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Reads one full line from the console.
     *
     * @return the line typed by the user
     */
    public String readLine() {
        return scanner.nextLine();
    }

    /**
     * Helper method to safely read integer inputs from the console.
     *
     * @return parsed integer or -1 on failure
     */
    public int readInt() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * Helper method to safely read double inputs from the console.
     *
     * @return parsed double or 0.0 on failure
     */
    public double readDouble() {
        try {
            return Double.parseDouble(scanner.nextLine().trim());
        } catch (Exception e) {
            return 0.0;
        }
    }
}