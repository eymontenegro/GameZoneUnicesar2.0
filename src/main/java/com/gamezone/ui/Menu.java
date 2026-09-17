package com.gamezone.ui;

import com.gamezone.service.PersonService;
import com.gamezone.service.ProductService;
import com.gamezone.service.SaleService;

import java.util.Scanner;

/**
 * Main console menu of the GameZone Unicesar system.
 * Displays the top-level options and delegates each module's
 * operations to the SubMenu class.
 */
public class Menu {

    private final SubMenu subMenu;
    private final Scanner scanner;

    /**
     * Creates the main menu, wiring together the services for products,
     * people, and sales, and initializing the shared console scanner.
     *
     * @param productService service handling product inventory
     * @param personService service handling clients and sellers
     * @param saleService service handling sale transactions
     */
    public Menu(ProductService productService, PersonService personService, SaleService saleService) {
        this.scanner = new Scanner(System.in);
        this.subMenu = new SubMenu(productService, personService, saleService, scanner);
    }

    /**
     * Starts the main menu loop, reading the user's selection and
     * delegating to the corresponding submenu until the user chooses to exit.
     */
    public void start() {
        boolean exit = false;
        while (!exit) {
            System.out.println("\n=================================");
            System.out.println("   GAMEZONE UNICESAR - MENÚ PRINCIPAL ");
            System.out.println("=================================");
            System.out.println("1. Gestión de Productos");
            System.out.println("2. Gestión de Personas (Clientes / Vendedores)");
            System.out.println("3. Gestión de Ventas");
            System.out.println("4. Salir de la Aplicación");
            System.out.print("Seleccione una opción: ");

            String input = scanner.nextLine().trim();
            switch (input) {
                case "1" -> subMenu.showProductMenu();
                case "2" -> subMenu.showPersonMenu();
                case "3" -> subMenu.showSaleMenu();
                case "4" -> {
                    System.out.println("Saliendo del Sistema GameZone... ¡Hasta luego!");
                    exit = true;
                }
                default -> System.out.println("Opción no válida. Por favor, intente de nuevo.");
            }
        }
    }
}
