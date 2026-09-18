package com.gamezone.ui;

import com.gamezone.service.AccessoryService;
import com.gamezone.service.PersonService;
import com.gamezone.service.ProductService;
import com.gamezone.service.PromotionService;
import com.gamezone.service.ReturnService;
import com.gamezone.service.SaleService;

import java.util.Scanner;

/**
 * Main navigation menu for GameZone. Delegates form handling to SubMenu.
 * Owns the single Scanner instance shared with SubMenu, so console input
 * is never read by two different Scanner objects at the same time.
 */
public class Menu {

    private final SubMenu subMenu;
    private final Scanner scanner;

    public Menu(ProductService productService,
                AccessoryService accessoryService,
                PersonService personService,
                PromotionService promotionService,
                SaleService saleService,
                ReturnService returnService) {
        this.scanner = new Scanner(System.in);
        this.subMenu = new SubMenu(productService, accessoryService, personService,
                promotionService, saleService, returnService, this.scanner);
    }

    public void displayMenu() {
        int option;
        do {
            System.out.println("\n==========================================");
            System.out.println("          GAMEZONE - MENÚ PRINCIPAL       ");
            System.out.println("==========================================");
            System.out.println("1. Gestión de Productos (Consolas / Juegos)");
            System.out.println("2. Gestión de Accesorios");
            System.out.println("3. Gestión de Clientes y Vendedores");
            System.out.println("4. Gestión de Promociones");
            System.out.println("5. Gestión de Devoluciones");
            System.out.println("6. Procesar Nueva Venta");
            System.out.println("7. Consultar Ventas");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opción: ");

            option = readInt();

            switch (option) {
                case 1 -> subMenu.showProductSubMenu();
                case 2 -> subMenu.showAccessorySubMenu();
                case 3 -> subMenu.showPersonSubMenu();
                case 4 -> subMenu.showPromotionSubMenu();
                case 5 -> subMenu.showReturnSubMenu();
                case 6 -> subMenu.processSaleForm();
                case 7 -> subMenu.listSalesHistory();
                case 0 -> System.out.println("\nSaliendo del sistema...");
                default -> System.out.println("Opción no válida.");
            }
        } while (option != 0);
    }

    private int readInt() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (Exception e) {
            return -1;
        }
    }
}
