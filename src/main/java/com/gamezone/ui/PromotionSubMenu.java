package com.gamezone.ui;

import com.gamezone.model.Promotion;
import com.gamezone.service.PromotionService;

import java.time.LocalDate;

/**
 * Handles the promotions management sub-menu.
 */
public class PromotionSubMenu {

    private final PromotionService promotionService;
    private final ConsoleInput input;

    /**
     * @param promotionService service for managing discounts and promotions
     * @param input the shared console reader
     */
    public PromotionSubMenu(PromotionService promotionService, ConsoleInput input) {
        this.promotionService = promotionService;
        this.input = input;
    }

    /**
     * Displays and manages the promotions management sub-menu.
     */
    public void show() {
        int option;
        do {
            System.out.println("\n--- GESTIÓN DE PROMOCIONES ---");
            System.out.println("1. Registrar promoción por porcentaje");
            System.out.println("2. Registrar promoción por categoría");
            System.out.println("3. Registrar promoción por volumen de compra");
            System.out.println("4. Listar todas las promociones");
            System.out.println("5. Listar promociones vigentes");
            System.out.println("0. Volver");
            System.out.print("Seleccione una opción: ");
            option = input.readInt();

            switch (option) {
                case 1 -> {
                    System.out.print("ID: "); String id = input.readLine();
                    System.out.print("Nombre de la promoción: "); String name = input.readLine();
                    System.out.print("Fecha de inicio (YYYY-MM-DD): "); LocalDate start = LocalDate.parse(input.readLine());
                    System.out.print("Fecha de fin (YYYY-MM-DD): "); LocalDate end = LocalDate.parse(input.readLine());
                    System.out.print("Porcentaje de descuento (%): "); double pct = input.readDouble();

                    promotionService.registerPercentageDiscount(id, name, start, end, pct);
                    System.out.println("¡Promoción por porcentaje registrada exitosamente!");
                }
                case 2 -> {
                    System.out.print("ID: "); String id = input.readLine();
                    System.out.print("Nombre de la promoción: "); String name = input.readLine();
                    System.out.print("Fecha de inicio (YYYY-MM-DD): "); LocalDate start = LocalDate.parse(input.readLine());
                    System.out.print("Fecha de fin (YYYY-MM-DD): "); LocalDate end = LocalDate.parse(input.readLine());
                    System.out.print("Porcentaje de descuento (%): "); double pct = input.readDouble();
                    System.out.print("Categoría objetivo (VIDEOGAME / CONSOLE): "); String cat = input.readLine();

                    promotionService.registerCategoryDiscount(id, name, start, end, pct, cat);
                    System.out.println("¡Promoción por categoría registrada exitosamente!");
                }
                case 3 -> {
                    System.out.print("ID: "); String id = input.readLine();
                    System.out.print("Nombre de la promoción: "); String name = input.readLine();
                    System.out.print("Fecha de inicio (YYYY-MM-DD): "); LocalDate start = LocalDate.parse(input.readLine());
                    System.out.print("Fecha de fin (YYYY-MM-DD): "); LocalDate end = LocalDate.parse(input.readLine());
                    System.out.print("Cantidad mínima de productos: "); int minQty = input.readInt();
                    System.out.print("Porcentaje de descuento (%): "); double pct = input.readDouble();

                    promotionService.registerBulkPurchaseDiscount(id, name, start, end, minQty, pct);
                    System.out.println("¡Promoción por volumen registrada exitosamente!");
                }
                case 4 -> {
                    System.out.println("\n--- TODAS LAS PROMOCIONES ---");
                    for (Promotion p : promotionService.listAllPromotions()) {
                        System.out.println(p.getId() + " - " + p.getName());
                    }
                }
                case 5 -> {
                    System.out.println("\n--- PROMOCIONES VIGENTES ---");
                    for (Promotion p : promotionService.listActivePromotions()) {
                        System.out.println(p.getId() + " - " + p.getName());
                    }
                }
                case 0 -> {}
                default -> System.out.println("Opción inválida.");
            }
        } while (option != 0);
    }
}