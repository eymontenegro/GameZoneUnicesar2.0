package com.gamezone.ui;

import com.gamezone.model.Client;
import com.gamezone.model.Console;
import com.gamezone.model.Product;
import com.gamezone.model.Sale;
import com.gamezone.model.SaleDetail;
import com.gamezone.model.Seller;
import com.gamezone.model.VideoGame;
import com.gamezone.service.PersonService;
import com.gamezone.service.ProductService;
import com.gamezone.service.SaleService;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Console submenus for the GameZone Unicesar system.
 * Provides the interactive options for managing products, people
 * (clients and sellers), and sales, delegating business logic to
 * the corresponding service classes.
 */
public class SubMenu {

    private final ProductService productService;
    private final PersonService personService;
    private final SaleService saleService;
    private final Scanner scanner;

    /**
     * Creates a new SubMenu using the given services and a shared scanner
     * for reading console input.
     *
     * @param productService service handling product inventory
     * @param personService service handling clients and sellers
     * @param saleService service handling sale transactions
     * @param scanner shared scanner used to read user input from the console
     */
    public SubMenu(ProductService productService, PersonService personService, SaleService saleService, Scanner scanner) {
        this.productService = productService;
        this.personService = personService;
        this.saleService = saleService;
        this.scanner = scanner;
    }

    // ==========================================
    // PRODUCT SUBMENU
    // ==========================================

    /**
     * Displays the product management submenu, allowing the user to
     * register video games, register consoles, and list the inventory,
     * until the user chooses to return to the main menu.
     */
    public void showProductMenu() {
        boolean exit = false;
        while (!exit) {
            System.out.println("\n=== GESTIÓN DE PRODUCTOS ===");
            System.out.println("1. Registrar Videojuego");
            System.out.println("2. Registrar Consola");
            System.out.println("3. Listar Todos los Productos");
            System.out.println("4. Volver al Menú Principal");
            System.out.print("Seleccione una opción: ");

            int option = readInt();
            switch (option) {
                case 1 -> registerVideoGame();
                case 2 -> registerConsole();
                case 3 -> listProducts();
                case 4 -> exit = true;
                default -> System.out.println("Opción no válida. Intente de nuevo.");
            }
        }
    }

    private void registerVideoGame() {
        System.out.println("\n--- Registrar Videojuego ---");
        System.out.print("ID: ");
        String id = scanner.nextLine().trim();
        System.out.print("Título: ");
        String title = scanner.nextLine().trim();
        System.out.print("Precio: ");
        double price = readDouble();
        System.out.print("Stock: ");
        int stock = readInt();
        System.out.print("Plataforma (ej. PS5, PC, Switch): ");
        String platform = scanner.nextLine().trim();
        System.out.print("Género: ");
        String genre = scanner.nextLine().trim();
        System.out.print("Clasificación de Edad (ej. E, T, M): ");
        String ageRating = scanner.nextLine().trim();

        try {
            productService.registerVideoGame(id, title, price, stock, platform, genre, ageRating);
            System.out.println("¡Videojuego registrado exitosamente!");
        } catch (Exception e) {
            System.out.println("Error al registrar el videojuego: " + e.getMessage());
        }
    }

    private void registerConsole() {
        System.out.println("\n--- Registrar Consola ---");
        System.out.print("ID: ");
        String id = scanner.nextLine().trim();
        System.out.print("Título: ");
        String title = scanner.nextLine().trim();
        System.out.print("Precio: ");
        double price = readDouble();
        System.out.print("Stock: ");
        int stock = readInt();
        System.out.print("Marca: ");
        String brand = scanner.nextLine().trim();
        System.out.print("Modelo: ");
        String model = scanner.nextLine().trim();
        System.out.print("Generación: ");
        String generation = scanner.nextLine().trim();

        try {
            productService.registerConsole(id, title, price, stock, brand, model, generation);
            System.out.println("¡Consola registrada exitosamente!");
        } catch (Exception e) {
            System.out.println("Error al registrar la consola: " + e.getMessage());
        }
    }

    private void listProducts() {
        System.out.println("\n--- Catálogo de Productos ---");
        List<Product> products = productService.listAll();
        if (products.isEmpty()) {
            System.out.println("No hay productos registrados.");
            return;
        }
        for (Product p : products) {
            System.out.println("[" + p.getId() + "] " + p.describe() + " | Precio: $" + p.getPrice() + " | Stock: " + p.getStock());
        }
    }

    // ==========================================
    // PEOPLE SUBMENU (CLIENTS AND SALESPERSONS)
    // ==========================================

    /**
     * Displays the people management submenu, allowing the user to
     * register clients and list clients or sellers, until the user
     * chooses to return to the main menu.
     */
    public void showPersonMenu() {
        boolean exit = false;
        while (!exit) {
            System.out.println("\n=== GESTIÓN DE PERSONAS ===");
            System.out.println("1. Registrar Cliente");
            System.out.println("2. Listar Clientes");
            System.out.println("3. Listar Vendedores");
            System.out.println("4. Volver al Menú Principal");
            System.out.print("Seleccione una opción: ");

            int option = readInt();
            switch (option) {
                case 1 -> registerClient();
                case 2 -> listClients();
                case 3 -> listSellers();
                case 4 -> exit = true;
                default -> System.out.println("Opción no válida. Intente de nuevo.");
            }
        }
    }

    private void registerClient() {
        System.out.println("\n--- Registrar Cliente ---");
        System.out.print("Nombre Completo: ");
        String name = scanner.nextLine().trim();
        System.out.print("Número de Identificación: ");
        String id = scanner.nextLine().trim();
        System.out.print("Teléfono: ");
        String phone = scanner.nextLine().trim();
        System.out.print("Correo Electrónico: ");
        String email = scanner.nextLine().trim();

        try {
            personService.registerClient(name, id, phone, email);
            System.out.println("¡Cliente registrado exitosamente!");
        } catch (Exception e) {
            System.out.println("Error al registrar el cliente: " + e.getMessage());
        }
    }

    private void listClients() {
        System.out.println("\n--- Clientes Registrados ---");
        List<Client> clients = personService.listClients();
        if (clients.isEmpty()) {
            System.out.println("No hay clientes registrados.");
            return;
        }
        for (Client c : clients) {
            System.out.println(c.getRoleDescription() + " | ID: " + c.getId() + " | Teléfono: " + c.getPhone());
        }
    }

    private void listSellers() {
        System.out.println("\n--- Vendedores de la Tienda ---");
        List<Seller> sellers = personService.listSellers();
        if (sellers.isEmpty()) {
            System.out.println("No hay vendedores registrados.");
            return;
        }
        for (Seller s : sellers) {
            System.out.println(s.getRoleDescription() + " | Teléfono: " + s.getPhone());
        }
    }

    // ==========================================
    // SALES SUBMENU
    // ==========================================

    /**
     * Displays the sale management submenu, allowing the user to
     * register a new sale and consult the full sale history, until
     * the user chooses to return to the main menu.
     */
    public void showSaleMenu() {
        boolean exit = false;
        while (!exit) {
            System.out.println("\n=== GESTIÓN DE VENTAS ===");
            System.out.println("1. Registrar Nueva Venta");
            System.out.println("2. Consultar Historial de Ventas");
            System.out.println("3. Volver al Menú Principal");
            System.out.print("Seleccione una opción: ");

            int option = readInt();
            switch (option) {
                case 1 -> registerSale();
                case 2 -> listSales();
                case 3 -> exit = true;
                default -> System.out.println("Opción no válida. Intente de nuevo.");
            }
        }
    }

    private void registerSale() {
        System.out.println("\n--- Registrar Nueva Venta ---");
        System.out.print("Ingrese la Identificación del Cliente: ");
        String clientDoc = scanner.nextLine().trim();
        System.out.print("Ingrese el Código de Empleado del Vendedor: ");
        String sellerCode = scanner.nextLine().trim();

        List<SaleDetail> details = new ArrayList<>();
        boolean addingProducts = true;

        while (addingProducts) {
            System.out.print("\nIngrese el ID del Producto a agregar: ");
            String productId = scanner.nextLine().trim();

            Product targetProduct = null;
            for (Product p : productService.listAll()) {
                if (p.getId().equals(productId)) {
                    targetProduct = p;
                    break;
                }
            }

            if (targetProduct == null) {
                System.out.println("No se encontró ningún producto con el ID: " + productId);
            } else {
                System.out.println("Seleccionado: " + targetProduct.getTitle() + " | Stock Actual: " + targetProduct.getStock());
                System.out.print("Ingrese la Cantidad: ");
                int quantity = readInt();

                try {
                    SaleDetail detail = new SaleDetail(targetProduct, quantity, targetProduct.getPrice());
                    details.add(detail);
                    System.out.println("Producto agregado a los detalles de la venta.");
                } catch (Exception e) {
                    System.out.println("Error al agregar el detalle: " + e.getMessage());
                }
            }

            System.out.print("¿Desea agregar otro producto? (s/n): ");
            String response = scanner.nextLine().trim().toLowerCase();
            if (!response.equals("s")) {
                addingProducts = false;
            }
        }

        if (details.isEmpty()) {
            System.out.println("Venta cancelada: No se agregaron productos.");
            return;
        }

        try {
            Sale sale = saleService.registerSale(clientDoc, sellerCode, details);
            System.out.println("\n¡Venta realizada con éxito!");
            System.out.println("Monto Total: $" + sale.calculateTotal());
        } catch (Exception e) {
            System.out.println("No se pudo completar la venta: " + e.getMessage());
        }
    }

    private void listSales() {
        System.out.println("\n--- Historial de Ventas ---");
        List<Sale> sales = saleService.listSales();
        if (sales.isEmpty()) {
            System.out.println("No hay ventas registradas.");
            return;
        }

        for (Sale s : sales) {
            System.out.println("Fecha: " + s.getDate() + 
                               " | Cliente: " + s.getClient().getName() + 
                               " | Vendedor: " + s.getSeller().getName() + 
                               " | Total: $" + s.calculateTotal());
            System.out.println("  Detalles:");
            for (SaleDetail detail : s.getDetails()) {
                System.out.println("    - " + detail.getProduct().getTitle() + 
                                   " x" + detail.getQuantity() + 
                                   " @ $" + detail.getUnitPrice() + 
                                   " = $" + detail.getSubtotal());
            }
        }
    }

    // Helpers for safe console input reading
    private int readInt() {
        try {
            int val = Integer.parseInt(scanner.nextLine().trim());
            return val;
        } catch (NumberFormatException e) {
            System.out.print("Número no válido. Ingrese de nuevo: ");
            return readInt();
        }
    }

    private double readDouble() {
        try {
            double val = Double.parseDouble(scanner.nextLine().trim());
            return val;
        } catch (NumberFormatException e) {
            System.out.print("Precio no válido. Ingrese de nuevo: ");
            return readDouble();
        }
    }
}
