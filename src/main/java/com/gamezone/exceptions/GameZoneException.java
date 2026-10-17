/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gamezone.exceptions;

/**
 *
 * @author yanelis
 */
public abstract class GameZoneException extends RuntimeException {
    
private static final long serialVersionUID = 1L;

    /** Default code used when none is provided. */
    private static final String DEFAULT_ERROR_CODE = "GAMEZONE_ERROR";

    private final String errorCode; 
    
    /**
     * Creates an exception with a message and the default error code.
     *
     * @param message human-readable description of the problem
     */
    protected GameZoneException(String message) {
        this(message, DEFAULT_ERROR_CODE);
    }
    
    /**
     * Creates an exception with a message and a specific error code.
     *
     * @param message   human-readable description of the problem
     * @param errorCode machine-readable code that identifies the kind of error
     */
    protected GameZoneException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
    
     /**
     * Returns the error code associated with this exception.
     *
     * @return the error code
     */
    public String getErrorCode() {
        return errorCode;
    }
    
}
