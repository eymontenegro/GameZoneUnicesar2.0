package com.gamezone.persistence;

import com.gamezone.exceptions.PersistenceException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Package-private helpers shared by the repositories to validate values
 * before they are written to a CSV file, to read and write the files line by
 * line, and to report corrupt lines with a consistent, descriptive message.
 */
final class CsvSupport {

    /**
     * Converts one non-blank line of a file into a domain object.
     *
     * @param <T> the type of object built from each line
     */
    @FunctionalInterface
    interface LineParser<T> {

        /**
         * Parses one line.
         *
         * @param line the raw line
         * @param lineNumber the 1-based line number, used in error messages
         * @return the object built from the line
         * @throws PersistenceException if the line is malformed
         */
        T parse(String line, int lineNumber);
    }

    private CsvSupport() {
    }

    /**
     * Checks that a text value can be written to a CSV column without
     * breaking the file format.
     *
     * @param value the text to write
     * @param fieldName the field name, used in the error message
     * @param extraForbidden additional characters that the value must not contain
     * @return the same value, if it is valid
     * @throws IllegalArgumentException if the value is null, or contains a comma,
     *         a line break or any of the extra forbidden characters
     */
    static String safe(String value, String fieldName, String extraForbidden) {
        if (value == null) {
            throw new IllegalArgumentException("El campo '" + fieldName + "' no puede ser nulo");
        }
        String forbidden = ",\n\r" + extraForbidden;
        for (int i = 0; i < value.length(); i++) {
            if (forbidden.indexOf(value.charAt(i)) >= 0) {
                throw new IllegalArgumentException("El campo '" + fieldName
                        + "' contiene caracteres no permitidos en el archivo (coma, salto de linea u otro separador): "
                        + value);
            }
        }
        return value;
    }

    /**
     * Same as {@link #safe(String, String, String)} without extra forbidden characters.
     *
     * @param value the text to write
     * @param fieldName the field name, used in the error message
     * @return the same value, if it is valid
     */
    static String safe(String value, String fieldName) {
        return safe(value, fieldName, "");
    }

    /**
     * Checks that a price is a finite, non-negative number.
     *
     * @param price the price to validate
     * @param fieldName the field name, used in the error message
     * @throws IllegalArgumentException if the price is NaN, infinite or negative
     */
    static void requireValidPrice(double price, String fieldName) {
        if (Double.isNaN(price) || Double.isInfinite(price) || price < 0) {
            throw new IllegalArgumentException("El campo '" + fieldName
                    + "' debe ser un numero finito mayor o igual a cero: " + price);
        }
    }

    /**
     * Builds the exception used for a line of a file that cannot be converted
     * into a domain object.
     *
     * @param filePath the file being read
     * @param lineNumber the 1-based line number
     * @param detail what is wrong with the line
     * @param cause the original parsing error, or null
     * @return the exception to throw
     */
    static PersistenceException corruptLine(String filePath, int lineNumber, String detail, Throwable cause) {
        String message = "Archivo corrupto '" + filePath + "', linea " + lineNumber + ": " + detail;
        return (cause == null) ? new PersistenceException(message) : new PersistenceException(message, cause);
    }

    /**
     * Checks the number of columns of a parsed line.
     *
     * @param parts the columns of the line
     * @param expected the exact number of columns expected
     * @param filePath the file being read
     * @param lineNumber the 1-based line number
     * @throws PersistenceException if the number of columns does not match
     */
    static void requireColumns(String[] parts, int expected, String filePath, int lineNumber) {
        if (parts.length != expected) {
            throw corruptLine(filePath, lineNumber,
                    "se esperaban " + expected + " columnas pero hay " + parts.length, null);
        }
    }

    /**
     * Checks that a loaded value is not empty.
     *
     * @param value the loaded value
     * @param fieldName the field name, used in the error message
     * @param filePath the file being read
     * @param lineNumber the 1-based line number
     * @throws PersistenceException if the value is blank
     */
    static void requireNotBlank(String value, String fieldName, String filePath, int lineNumber) {
        if (value.isBlank()) {
            throw corruptLine(filePath, lineNumber, "el campo '" + fieldName + "' esta vacio", null);
        }
    }

    /**
     * Checks that a parsed line has a number of columns inside an allowed range.
     *
     * @param parts the columns of the line
     * @param min the minimum number of columns
     * @param max the maximum number of columns
     * @param filePath the file being read
     * @param lineNumber the 1-based line number
     * @throws PersistenceException if the number of columns is out of range
     */
    static void requireColumnsBetween(String[] parts, int min, int max, String filePath, int lineNumber) {
        if (parts.length < min || parts.length > max) {
            String expected = (min == max) ? String.valueOf(min) : min + " a " + max;
            throw corruptLine(filePath, lineNumber,
                    "se esperaban " + expected + " columnas pero hay " + parts.length, null);
        }
    }

    /**
     * Parses a finite decimal number read from a file.
     *
     * @param text the raw column value
     * @param fieldName the field name, used in the error message
     * @param filePath the file being read
     * @param lineNumber the 1-based line number
     * @return the parsed number
     * @throws PersistenceException if the text is not a finite number
     */
    static double parseDouble(String text, String fieldName, String filePath, int lineNumber) {
        double value;
        try {
            value = Double.parseDouble(text.trim());
        } catch (NumberFormatException e) {
            throw corruptLine(filePath, lineNumber,
                    "el campo '" + fieldName + "' no es un numero valido: '" + text + "'", e);
        }
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            throw corruptLine(filePath, lineNumber,
                    "el campo '" + fieldName + "' debe ser un numero finito: '" + text + "'", null);
        }
        return value;
    }

    /**
     * Parses an integer read from a file.
     *
     * @param text the raw column value
     * @param fieldName the field name, used in the error message
     * @param filePath the file being read
     * @param lineNumber the 1-based line number
     * @return the parsed integer
     * @throws PersistenceException if the text is not an integer
     */
    static int parseInt(String text, String fieldName, String filePath, int lineNumber) {
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            throw corruptLine(filePath, lineNumber,
                    "el campo '" + fieldName + "' no es un entero valido: '" + text + "'", e);
        }
    }

    /**
     * Parses an ISO date (yyyy-MM-dd) read from a file.
     *
     * @param text the raw column value
     * @param fieldName the field name, used in the error message
     * @param filePath the file being read
     * @param lineNumber the 1-based line number
     * @return the parsed date
     * @throws PersistenceException if the text is not a valid date
     */
    static LocalDate parseDate(String text, String fieldName, String filePath, int lineNumber) {
        try {
            return LocalDate.parse(text.trim());
        } catch (DateTimeParseException e) {
            throw corruptLine(filePath, lineNumber,
                    "el campo '" + fieldName + "' no es una fecha valida (yyyy-MM-dd): '" + text + "'", e);
        }
    }

    /**
     * Writes the given lines to a file, overwriting any previous content, and
     * wraps any I/O error. It does not create the folder: call
     * {@link #ensureParentDirectory(File, String)} first when that is wanted.
     *
     * @param filePath the file to write
     * @param lines the lines to write
     * @param what plural description of the saved entities, used in the error message
     * @throws PersistenceException if an I/O error occurs
     */
    static void writeLines(String filePath, List<String> lines, String what) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            throw new PersistenceException("No se pudieron guardar " + lines.size() + " " + what
                    + " en '" + filePath + "'", e);
        }
    }

    /**
     * Reads a file line by line and converts each non-blank line with the
     * given parser. A file that does not exist yet (first run) gives an empty
     * list; blank lines are skipped but still counted in the line numbers.
     *
     * @param <T> the type of object built from each line
     * @param filePath the file to read
     * @param what plural description of the loaded entities, used in the error message
     * @param parser converts one line into an object
     * @return the objects built from the file, in file order
     * @throws PersistenceException if an I/O error occurs or a line is malformed
     */
    static <T> List<T> readLines(String filePath, String what, LineParser<T> parser) {
        List<T> items = new ArrayList<>();
        File file = new File(filePath);

        if (!file.exists()) {
            return items;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.isBlank()) {
                    continue;
                }
                items.add(parser.parse(line, lineNumber));
            }
        } catch (IOException e) {
            throw new PersistenceException("No se pudieron leer " + what + " desde '" + filePath + "'", e);
        }

        return items;
    }

    /**
     * Makes sure the folder that will contain a file exists.
     *
     * @param file the file that is about to be written
     * @param what plural description of the data, used in the error message
     * @throws PersistenceException if the folder cannot be created
     */
    static void ensureParentDirectory(File file, String what) {
        File parent = file.getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs() && !parent.exists()) {
            throw new PersistenceException("No se pudo crear el directorio '" + parent.getPath()
                    + "' para guardar " + what);
        }
    }
}