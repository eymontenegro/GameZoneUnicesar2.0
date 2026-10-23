package com.gamezone.persistence;

import com.gamezone.exceptions.PersistenceException;
import org.junit.jupiter.api.function.Executable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Helpers shared by the repository tests, so each test class does not keep its
 * own copy of how a CSV file is written or how a failed load is checked.
 */
final class CsvTestSupport {

    private CsvTestSupport() {
    }

    /**
     * Writes the given lines to a file, one per line, overwriting its content.
     *
     * @param file the file to write
     * @param lines the lines to write
     * @throws IOException if the file cannot be written
     */
    static void writeFile(Path file, String... lines) throws IOException {
        Files.writeString(file, String.join("\n", lines) + "\n");
    }

    /**
     * Checks that a load fails with a {@link PersistenceException}.
     *
     * @param load the load operation
     * @return the exception that was thrown, so the caller can check it further
     */
    static PersistenceException loadFails(Executable load) {
        return assertThrows(PersistenceException.class, load);
    }

    /**
     * Checks that a load fails because of a corrupt line, and that the message
     * says which file and line it is and what is wrong with it.
     *
     * @param load the load operation
     * @param file the file that holds the corrupt line
     * @param lineNumber the 1-based number of the corrupt line
     * @param fragment text that the message must contain (the reason of the failure)
     */
    static void assertCorrupt(Executable load, Path file, int lineNumber, String fragment) {
        String message = loadFails(load).getMessage();
        assertTrue(message.contains(file.toString()), "El mensaje no indica el archivo: " + message);
        assertTrue(message.contains("linea " + lineNumber), "El mensaje no indica la linea " + lineNumber + ": " + message);
        assertTrue(message.contains(fragment), "El mensaje no contiene '" + fragment + "': " + message);
    }
}
