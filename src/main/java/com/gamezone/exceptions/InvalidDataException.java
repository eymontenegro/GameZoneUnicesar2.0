/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gamezone.exceptions;

    /**
 * Thrown when an input value does not satisfy format, range or type criteria,
 * for example a negative price or an empty required text.
 */
public class InvalidDataException extends GameZoneException {

    private static final long serialVersionUID = 1L;

    private static final String ERROR_CODE = "INVALID_DATA";

    private final String fieldName;

    /**
     * Creates the exception and builds a descriptive Spanish message.
     *
     * @param fieldName name of the invalid field (e.g. "precio")
     * @param problem   description of what is wrong with the value
     */
    public InvalidDataException(String fieldName, String problem) {
        super("El campo '" + fieldName + "' es inválido: " + problem, ERROR_CODE);
        this.fieldName = fieldName;
    }

    /**
     * Returns the name of the field that failed validation.
     *
     * @return the invalid field name
     */
    public String getFieldName() {
        return fieldName;
    }
    
}
