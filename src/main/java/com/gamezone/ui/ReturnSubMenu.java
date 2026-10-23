package com.gamezone.ui;

import com.gamezone.model.Return;
import com.gamezone.service.ReturnService;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles the returns management sub-menu.
 */
public class ReturnSubMenu {

    private final ReturnService returnService;
    private final ConsoleInput input;

    /**
     * @param returnService service for handling returns
     * @param input the shared console reader
     */
    public ReturnSubMenu(ReturnService returnService, ConsoleInput input) {
        this.returnService = returnService;
        this.input = input;
    }

    /**
     * Displays and manages the returns management sub-menu.
     */
    public void show() {
        int option;
        do {
            System.out.println("\n--- GESTIÓN DE DEVOLUCIONES ---");
            System.out.println("1. Registrar una nueva devolución");
            System.out.println("2. Consultar todas las devoluciones");
            System.out.println("3. Consultar devoluciones por cliente");
            System.out.println("4. Consultar devoluciones por venta");
            System.out.println("5. Consultar balance mensual");
            System.out.println("0. Volver");
            System.out.print("Seleccione una opción: ");
            option = input.readInt();

            switch (option) {
                case 1 -> registerReturnFlow();
                case 2 -> listAllReturns();
                case 3 -> listReturnsByCustomer();
                case 4 -> listReturnsBySale();
                case 5 -> showMonthlyBalance();
                case 0 -> {}
                default -> System.out.println("Opción inválida.");
            }
        } while (option != 0);
    }

    /**
     * Guides the user through the process of registering a product return.
     */
    private void registerReturnFlow() {
        System.out.print("Ingrese el identificador de la venta original: ");
        String saleId = input.readLine().trim();

        System.out.print("Ingrese los identificadores de los productos a devolver (separados por coma): ");
        String productsLine = input.readLine().trim();
        List<String> productIds = new ArrayList<>();
        for (String id : productsLine.split(",")) {
            String trimmed = id.trim();
            if (!trimmed.isEmpty()) {
                productIds.add(trimmed);
            }
        }

        System.out.print("Ingrese el motivo de la devolución: ");
        String reason = input.readLine();

        Return createdReturn = returnService.registerReturn(saleId, productIds, reason);
        System.out.println("\n¡Devolución registrada exitosamente!");
        System.out.println(createdReturn.generateReturnReceipt());
    }

    /**
     * Lists all returns registered in the system.
     */
    private void listAllReturns() {
        List<Return> returns = returnService.viewAllReturns();
        if (returns.isEmpty()) {
            System.out.println("No hay devoluciones registradas.");
            return;
        }
        System.out.println("\n--- TODAS LAS DEVOLUCIONES ---");
        for (Return r : returns) {
            System.out.println(r.generateReturnReceipt());
            System.out.println("------------------------------------");
        }
    }

    /**
     * Lists returns associated with a specific customer.
     */
    private void listReturnsByCustomer() {
        System.out.print("Ingrese el identificador del cliente: ");
        String customerId = input.readLine().trim();

        List<Return> returns = returnService.viewReturnsByCustomer(customerId);
        if (returns.isEmpty()) {
            System.out.println("Este cliente no tiene devoluciones registradas.");
            return;
        }
        System.out.println("\n--- DEVOLUCIONES DEL CLIENTE " + customerId + " ---");
        for (Return r : returns) {
            System.out.println(r.generateReturnReceipt());
            System.out.println("------------------------------------");
        }
    }

    /**
     * Lists returns associated with a specific sale transaction.
     */
    private void listReturnsBySale() {
        System.out.print("Ingrese el identificador de la venta: ");
        String saleId = input.readLine().trim();

        List<Return> returns = returnService.viewReturnsBySale(saleId);
        if (returns.isEmpty()) {
            System.out.println("Esta venta no tiene devoluciones registradas.");
            return;
        }
        System.out.println("\n--- DEVOLUCIONES DE LA VENTA " + saleId + " ---");
        for (Return r : returns) {
            System.out.println(r.generateReturnReceipt());
            System.out.println("------------------------------------");
        }
    }

    /**
     * Computes and displays the net monthly balance (sales minus returns).
     */
    private void showMonthlyBalance() {
        int month;
        int year;
        System.out.print("Ingrese el mes (1-12): ");
        month = input.readInt();
        System.out.print("Ingrese el año (por ejemplo, 2026): ");
        year = input.readInt();

        if (month < 1 || month > 12) {
            System.out.println("El mes debe estar entre 1 y 12.");
            return;
        }

        double balance = returnService.generateMonthlyBalance(month, year);
        System.out.printf("%nBalance neto para %02d/%d: $%.2f%n", month, year, balance);
    }
}