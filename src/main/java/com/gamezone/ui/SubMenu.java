package com.gamezone.ui;

import com.gamezone.model.Accessory;
import com.gamezone.model.Client;
import com.gamezone.model.Product;
import com.gamezone.model.Sale;
import com.gamezone.model.SaleDetail;
import com.gamezone.model.Seller;
import com.gamezone.service.AccessoryService;
import com.gamezone.service.PersonService;
import com.gamezone.service.ProductService;
import com.gamezone.service.SaleService;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Console submenus for the GameZone Unicesar system.
 * Provides the interactive options for managing products, accessories,
 * people (clients and sellers), and sales, delegating business logic
 * to the corresponding service classes.
 */
public class SubMenu {

    private final ProductService productService;
    private final AccessoryService accessoryService;
    private final PersonService personService;
    private final SaleService saleService;
    private final Scanner scanner;

    /**
     * Creates a new SubMenu using the given services and a shared scanner
     * for reading console input.
     *
     * @param productService service handling product inventory
     * @param accessoryService service handling accessory inventory and compatibility
     * @param personService service handling clients and sellers
     * @param saleService service handling sale transactions
     * @param scanner shared scanner used to read user input from the console
     */
    public SubMenu(ProductService productService, AccessoryService accessoryService,
                    PersonService personService, SaleService saleService, Scanner scanner) {
        this.productService = productService;
        this.accessoryService = accessoryService;
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
    // ACCESSORY SUBMENU
    // ==========================================

    /**
     * Displays the accessory management submenu, allowing the user to
     * register controllers, cables, and memories, list accessories
     * (all or by type), and query compatibility with a console,
     * until the user chooses to return to the main menu.
     */
    public void showAccessoryMenu() {
        boolean exit = false;
        while (!exit) {
            System.out.println("\n=== GESTIÓN DE ACCESORIOS ===");
            System.out.println("1. Registrar Control");
            System.out.println("2. Registrar Cable");
            System.out.println("3. Registrar Memoria");
            System.out.println("4. Listar Todos los Accesorios");
            System.out.println("5. Listar Accesorios por Tipo");
            System.out.println("6. Consultar Accesorios Compatibles con una Consola");
            System.out.println("7. Volver al Menú Principal");
            System.out.print("Seleccione una opción: ");

            int option = readInt();
            switch (option) {
                case 1 -> registerController();
                case 2 -> registerCable();
                case 3 -> registerMemory();
                case 4 -> listAccessories();
                case 5 -> listAccessoriesByType();
                case 6 -> listCompatibleAccessories();
                case 7 -> exit = true;
                default -> System.out.println("Opción no válida. Intente de nuevo.");
            }
        }
    }

    private void registerController() {
        System.out.println("\n--- Registrar Control ---");
        System.out.print("ID: ");
        String id = scanner.nextLine().trim();
        System.out.print("Título: ");
        String title = scanner.nextLine().trim();
        System.out.print("Precio: ");
        double price = readDouble();
        System.out.print("Stock: ");
        int stock = readInt();
        System.out.print("Tipo de Conexión (Wireless/Wired): ");
        String connectionType = scanner.nextLine().trim();
        System.out.print("IDs de consolas compatibles (separados por coma): ");
        String rawConsoles = scanner.nextLine().trim();
        List<String> compatibleConsoleIds = parseConsoleIds(rawConsoles);

        try {
            accessoryService.registerController(id, title, price, stock, connectionType, compatibleConsoleIds);
            System.out.println("¡Control registrado exitosamente!");
        } catch (Exception e) {
            System.out.println("Error al registrar el control: " + e.getMessage());
        }
    }

    private void registerCable() {
        System.out.println("\n--- Registrar Cable ---");
        System.out.print("ID: ");
        String id = scanner.nextLine().trim();
        System.out.print("Título: ");
        String title = scanner.nextLine().trim();
        System.out.print("Precio: ");
        double price = readDouble();
        System.out.print("Stock: ");
        int stock = readInt();
        System.out.print("Longitud (metros): ");
        double length = readDouble();
        System.out.print("Tipo de Conector (HDMI, USB, óptico, etc.): ");
        String connectorType = scanner.nextLine().trim();

        try {
            accessoryService.registerCable(id, title, price, stock, length, connectorType);
            System.out.println("¡Cable registrado exitosamente!");
        } catch (Exception e) {
            System.out.println("Error al registrar el cable: " + e.getMessage());
        }
    }

    private void registerMemory() {
        System.out.println("\n--- Registrar Memoria ---");
        System.out.print("ID: ");
        String id = scanner.nextLine().trim();
        System.out.print("Título: ");
        String title = scanner.nextLine().trim();
        System.out.print("Precio: ");
        double price = readDouble();
        System.out.print("Stock: ");
        int stock = readInt();
        System.out.print("Capacidad (GB): ");
        int capacity = readInt();
        System.out.print("Tipo (SD, microSD, tarjeta propietaria): ");
        String type = scanner.nextLine().trim();
        System.out.print("IDs de consolas compatibles (separados por coma): ");
        String rawConsoles = scanner.nextLine().trim();
        List<String> compatibleConsoleIds = parseConsoleIds(rawConsoles);

        try {
            accessoryService.registerMemory(id, title, price, stock, capacity, type, compatibleConsoleIds);
            System.out.println("¡Memoria registrada exitosamente!");
        } catch (Exception e) {
            System.out.println("Error al registrar la memoria: " + e.getMessage());
        }
    }

    private void listAccessories() {
        System.out.println("\n--- Catálogo de Accesorios ---");
        List<Accessory> accessories = accessoryService.listAllAccessories();
        if (accessories.isEmpty()) {
            System.out.println("No hay accesorios registrados.");
            return;
        }
        for (Accessory a : accessories) {
            System.out.println("[" + a.getId() + "] " + a.describe() + " | Precio: $" + a.getPrice() + " | Stock: " + a.getStock());
        }
    }

    private void listAccessoriesByType() {
        System.out.print("\nIngrese el tipo de accesorio (CONTROLLER/CABLE/MEMORY): ");
        String type = scanner.nextLine().trim();
        List<Accessory> accessories = accessoryService.listAccessoriesByType(type);
        if (accessories.isEmpty()) {
            System.out.println("No hay accesorios registrados de ese tipo.");
            return;
        }
        for (Accessory a : accessories) {
            System.out.println("[" + a.getId() + "] " + a.describe() + " | Stock: " + a.getStock());
        }
    }

    private void listCompatibleAccessories() {
        System.out.print("\nIngrese el ID de la Consola: ");
        String consoleId = scanner.nextLine().trim();
        List<Accessory> accessories = accessoryService.findAccessoriesCompatibleWith(consoleId);
        if (accessories.isEmpty()) {
            System.out.println("No hay accesorios compatibles registrados para esa consola.");
            return;
        }
        for (Accessory a : accessories) {
            System.out.println("[" + a.getId() + "] " + a.describe());
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

    /**
     * Registers a new sale, allowing the user to add both products
     * (video games, consoles) and accessories (controllers, cables,
     * memories) to the same transaction, searching for the item's id
     * across both catalogs.
     */
    private void registerSale() {
        System.out.println("\n--- Registrar Nueva Venta ---");
        System.out.print("Ingrese la Identificación del Cliente: ");
        String clientDoc = scanner.nextLine().trim();
        System.out.print("Ingrese el Código de Empleado del Vendedor: ");
        String sellerCode = scanner.nextLine().trim();

        List<SaleDetail> details = new ArrayList<>();
        boolean addingProducts = true;

        while (addingProducts) {
            System.out.print("\nIngrese el ID del Producto o Accesorio a agregar: ");
            String itemId = scanner.nextLine().trim();

            Product targetProduct = findProductOrAccessory(itemId);

            if (targetProduct == null) {
                System.out.println("No se encontró ningún producto o accesorio con el ID: " + itemId);
            } else {
                System.out.println("Seleccionado: " + targetProduct.getTitle() + " | Stock Actual: " + targetProduct.getStock());
                System.out.print("Ingrese la Cantidad: ");
                int quantity = readInt();

                try {
                    SaleDetail detail = new SaleDetail(targetProduct, quantity, targetProduct.getPrice());
                    details.add(detail);
                    System.out.println("Ítem agregado a los detalles de la venta.");
                } catch (Exception e) {
                    System.out.println("Error al agregar el detalle: " + e.getMessage());
                }
            }

            System.out.print("¿Desea agregar otro producto o accesorio? (s/n): ");
            String response = scanner.nextLine().trim().toLowerCase();
            if (!response.equals("s")) {
                addingProducts = false;
            }
        }

        if (details.isEmpty()) {
            System.out.println("Venta cancelada: No se agregaron productos ni accesorios.");
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

    /**
     * Searches for an item by id first among regular products
     * (video games, consoles) and then among accessories, since
     * both are treated as Product for sale purposes.
     *
     * @param id the id of the product or accessory to find
     * @return the matching Product (or Accessory), or null if not found
     */
    private Product findProductOrAccessory(String id) {
        for (Product p : productService.listAll()) {
            if (p.getId().equals(id)) {
                return p;
            }
        }
        for (Accessory a : accessoryService.listAllAccessories()) {
            if (a.getId().equals(id)) {
                return a;
            }
        }
        return null;
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

    /**
     * Parses a comma-separated string of console IDs typed by the user
     * into a list of trimmed console ID strings, ignoring empty entries.
     *
     * @param rawInput the comma-separated console IDs entered by the user
     * @return a list of console IDs (empty if the input was blank)
     */
    private List<String> parseConsoleIds(String rawInput) {
        List<String> ids = new ArrayList<>();
        if (rawInput == null || rawInput.isBlank()) {
            return ids;
        }
        for (String part : rawInput.split(",")) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                ids.add(trimmed);
            }
        }
        return ids;
    }
}
