package com.gamezone.ui;

import com.gamezone.model.Warranty;
import com.gamezone.service.WarrantyService;

import java.util.List;

/**
 * Handles the warranty management sub-menu.
 */
public class WarrantySubMenu {

    private final WarrantyService warrantyService;
    private final ConsoleInput input;

    /**
     * @param warrantyService service for assigning and querying warranties
     * @param input the shared console reader
     */
    public WarrantySubMenu(WarrantyService warrantyService, ConsoleInput input) {
        this.warrantyService = warrantyService;
        this.input = input;
    }

    /**
     * Displays and manages the warranty management sub-menu.
     */
    public void show() {
        int option;
        do {
            System.out.println("\n--- GESTIÓN DE GARANTÍAS ---");
            System.out.println("1. Consultar la garantía de un producto en una venta");
            System.out.println("2. Listar todas las garantías registradas");
            System.out.println("3. Listar las garantías vigentes");
            System.out.println("4. Listar las garantías próximas a vencer");
            System.out.println("0. Volver");
            System.out.print("Seleccione una opción: ");
            option = input.readInt();

            switch (option) {
                case 1 -> findWarrantyByProductFlow();
                case 2 -> listWarranties("--- TODAS LAS GARANTÍAS ---", warrantyService.listAllWarranties());
                case 3 -> listWarranties("--- GARANTÍAS VIGENTES ---", warrantyService.listActiveWarranties());
                case 4 -> listExpiringSoonFlow();
                case 0 -> {}
                default -> System.out.println("Opción inválida.");
            }
        } while (option != 0);
    }

    /**
     * Finds and displays the warranty associated with a specific product in a sale.
     */
    private void findWarrantyByProductFlow() {
        System.out.print("ID del producto: "); String productId = input.readLine().trim();
        System.out.print("ID de la venta: "); String saleId = input.readLine().trim();

        Warranty warranty = warrantyService.findWarrantyByProduct(productId, saleId);
        if (warranty == null) {
            System.out.println("No se encontró una garantía para ese producto en esa venta.");
            return;
        }
        System.out.println(warranty.generateWarrantyCertificate());
    }

    /**
     * Lists warranties expiring within a specified number of days ahead.
     */
    private void listExpiringSoonFlow() {
        System.out.print("¿Con cuántos días de anticipación desea consultar?: ");
        int daysAhead = input.readInt();
        if (daysAhead < 0) {
            System.out.println("El número de días debe ser válido y no negativo.");
            return;
        }
        listWarranties("--- GARANTÍAS PRÓXIMAS A VENCER (en " + daysAhead + " días) ---",
                warrantyService.listWarrantiesExpiringSoon(daysAhead));
    }

    /**
     * Prints a formatted list of warranty certificates.
     *
     * @param title the section title
     * @param warranties the list of warranties to print
     */
    private void listWarranties(String title, List<Warranty> warranties) {
        if (warranties.isEmpty()) {
            System.out.println("No hay garantías para mostrar.");
            return;
        }
        System.out.println("\n" + title);
        for (Warranty warranty : warranties) {
            System.out.println(warranty.generateWarrantyCertificate());
            System.out.println("------------------------------------");
        }
    }
}