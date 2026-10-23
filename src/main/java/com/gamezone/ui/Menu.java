package com.gamezone.ui;

import com.gamezone.exceptions.BusinessRuleException;
import com.gamezone.exceptions.GameZoneException;
import com.gamezone.exceptions.InvalidDataException;
import com.gamezone.exceptions.PersistenceException;
import com.gamezone.exceptions.ResourceNotFoundException;

/**
 * Main navigation menu for GameZone. Delegates each module to its own sub-menu.
 * The sub-menus and the shared ConsoleInput are created by Main and received
 * here, so Menu does not depend on any service.
 */
public class Menu {

    private final ConsoleInput input;
    private final ProductSubMenu productSubMenu;
    private final AccessorySubMenu accessorySubMenu;
    private final PersonSubMenu personSubMenu;
    private final PromotionSubMenu promotionSubMenu;
    private final ReturnSubMenu returnSubMenu;
    private final WarrantySubMenu warrantySubMenu;
    private final SaleSubMenu saleSubMenu;

    /**
     * Creates the main menu with the shared console reader and one sub-menu per module.
     *
     * @param input the shared console reader
     * @param productSubMenu sub-menu of products
     * @param accessorySubMenu sub-menu of accessories
     * @param personSubMenu sub-menu of clients and sellers
     * @param promotionSubMenu sub-menu of promotions
     * @param returnSubMenu sub-menu of returns
     * @param warrantySubMenu sub-menu of warranties
     * @param saleSubMenu sub-menu of sales
     */
    public Menu(ConsoleInput input,
                ProductSubMenu productSubMenu,
                AccessorySubMenu accessorySubMenu,
                PersonSubMenu personSubMenu,
                PromotionSubMenu promotionSubMenu,
                ReturnSubMenu returnSubMenu,
                WarrantySubMenu warrantySubMenu,
                SaleSubMenu saleSubMenu) {
        this.input = input;
        this.productSubMenu = productSubMenu;
        this.accessorySubMenu = accessorySubMenu;
        this.personSubMenu = personSubMenu;
        this.promotionSubMenu = promotionSubMenu;
        this.returnSubMenu = returnSubMenu;
        this.warrantySubMenu = warrantySubMenu;
        this.saleSubMenu = saleSubMenu;
    }

    public void displayMenu() {
        int option;
        do {
            System.out.println("\n==========================================");
            System.out.println("          GAMEZONE - MENÚ PRINCIPAL         ");
            System.out.println("==========================================");
            System.out.println("1. Gestión de Productos (Consolas / Juegos)");
            System.out.println("2. Gestión de Accesorios");
            System.out.println("3. Gestión de Clientes y Vendedores");
            System.out.println("4. Gestión de Promociones");
            System.out.println("5. Gestión de Devoluciones");
            System.out.println("6. Gestión de Garantías");
            System.out.println("7. Procesar Nueva Venta");
            System.out.println("8. Consultar Ventas");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opción: ");

            option = input.readInt();

            // Bloque try-catch en cascada para el manejo diferenciado de excepciones (Requerimiento 6)
            try {
                switch (option) {
                    case 1 -> productSubMenu.show();
                    case 2 -> accessorySubMenu.show();
                    case 3 -> personSubMenu.show();
                    case 4 -> promotionSubMenu.show();
                    case 5 -> returnSubMenu.show();
                    case 6 -> warrantySubMenu.show();
                    case 7 -> saleSubMenu.processSaleForm();
                    case 8 -> saleSubMenu.listSalesHistory();
                    case 0 -> System.out.println("\nSaliendo del sistema...");
                    default -> System.out.println("Opción no válida.");
                }
            } catch (ResourceNotFoundException e) {
                System.out.println("\n[AVISO] No se encontró el recurso solicitado: " + e.getMessage());
            } catch (BusinessRuleException e) {
                System.out.println("\n[ERROR DE NEGOCIO] No se puede completar la operación: " + e.getMessage());
            } catch (InvalidDataException e) {
                System.out.println("\n[DATO INVÁLIDO] Los datos ingresados no son válidos: " + e.getMessage());
            } catch (PersistenceException e) {
                System.out.println("\n[ERROR DE SISTEMA] Ocurrió un problema al acceder a los datos del sistema. Contacte al administrador. (" + e.getMessage() + ")");
            } catch (GameZoneException e) {
                System.out.println("\n[ERROR] Ha ocurrido un error en la aplicación: " + e.getMessage());
            }

        } while (option != 0);
    }
}