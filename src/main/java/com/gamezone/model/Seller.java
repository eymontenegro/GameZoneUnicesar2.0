package com.gamezone.model;

/**
 * Represents a seller (employee) of the store, with attributes specific
 * to sellers such as employee code and work shift.
 */
public class Seller extends Person {
    private String employeeCode;
    private String shift;

    public Seller(String employeeCode, String shift, String id, String name, String phone) {
        super(id, name, phone);
        this.employeeCode = employeeCode;
        this.shift = shift;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public String getShift() {
        return shift;
    }

    @Override
    public String getRoleDescription() {
        return getName() + " es un vendedor, codigo de empleado " + employeeCode + ", turno " + shift;
    }
}