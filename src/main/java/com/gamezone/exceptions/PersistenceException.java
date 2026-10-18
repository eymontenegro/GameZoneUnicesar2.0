/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gamezone.exceptions;


/**
 * Unchecked exception thrown by the persistence layer when a repository cannot
 * read or write its data: a low-level {@link java.io.IOException}, or file
 * content that is corrupt and cannot be converted into domain objects.
 *
 * <p>The message always describes the operation that failed (and, when
 * available, the file and line involved), and the original error is kept as
 * the cause so no stack trace is lost.</p>
 */
public class PersistenceException extends RuntimeException {

    /**
     * Creates an exception with a descriptive message and no underlying cause.
     *
     * @param message description of what failed and where
     */
    public PersistenceException(String message) {
        super(message);
    }

    /**
     * Creates an exception with a descriptive message and the original cause.
     *
     * @param message description of what failed and where
     * @param cause the original low-level error (for example an IOException)
     */
    public PersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
}
