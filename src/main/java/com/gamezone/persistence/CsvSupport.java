/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gamezone.persistence;

import com.gamezone.exceptions.PersistenceException;

/**
 * Package-private helpers shared by the repositories to validate values
 * before they are written to a CSV file and to report corrupt lines with a
 * consistent, descriptive message.
 */
final class CsvSupport {

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
}
