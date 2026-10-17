/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gamezone.exceptions;

/**
 * Thrown when reading or writing a file fails (corrupt file, permission denied,
 * full disk, and so on). It keeps the original cause to preserve traceability.
 */
public class PersistenceException extends GameZoneException {

    private static final long serialVersionUID = 1L;

    private static final String ERROR_CODE = "PERSISTENCE_ERROR";

    /**
     * Creates the exception keeping the original cause.
     *
     * @param message context of the failure (file and operation)
     * @param cause   original exception, usually an {@link java.io.IOException}
     */
    public PersistenceException(String message, Throwable cause) {
        super(message, ERROR_CODE);
        initCause(cause);
    }
}
