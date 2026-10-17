/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gamezone.exceptions;

/**
 * Thrown when a resource (product, customer, seller, sale) is requested by
 * identifier and does not exist in the system.
 */
public class ResourceNotFoundException extends GameZoneException {
    
 private static final long serialVersionUID = 1L;

 private static final String ERROR_CODE = "RESOURCE_NOT_FOUND";
 
 /**
     * Creates the exception and builds a descriptive Spanish message.
     *
     * @param resourceType kind of resource that was searched (e.g. "producto")
     * @param identifier   identifier that was searched and not found
     */
    public ResourceNotFoundException(String resourceType, String identifier) {
        super("El recurso de tipo '" + resourceType + "' con identificador '"
                + identifier + "' no fue encontrado", ERROR_CODE);
    }
}
