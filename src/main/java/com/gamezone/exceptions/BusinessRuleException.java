/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gamezone.exceptions;

/**
 * Thrown when an operation violates a business rule, such as insufficient
 * stock, a duplicated customer or a sale without products.
 */
public class BusinessRuleException extends GameZoneException {
    
  private static final long serialVersionUID = 1L;

    private static final String ERROR_CODE = "BUSINESS_RULE_VIOLATION";

    /**
     * Creates the exception with a message describing the violated rule.
     *
     * @param message description of the violated business rule
     */
    public BusinessRuleException(String message) {
        super(message, ERROR_CODE);
    }   
}
