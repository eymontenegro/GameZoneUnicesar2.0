package com.gamezone.ui;

import com.gamezone.model.Accessory;
import com.gamezone.model.Client;
import com.gamezone.model.Product;
import com.gamezone.model.Promotion;
import com.gamezone.model.Return;
import com.gamezone.model.Sale;
import com.gamezone.model.SaleDetail;
import com.gamezone.model.Seller;
import com.gamezone.service.AccessoryService;
import com.gamezone.service.PersonService;
import com.gamezone.service.ProductService;
import com.gamezone.service.PromotionService;
import com.gamezone.service.ReturnService;
import com.gamezone.service.SaleService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Handles operations, user input forms, and interactive sub-menus
 * for the GameZone system.
 */
public class SubMenu {

    private final ProductService productService;
    private final AccessoryService accessoryService;
    private final PersonService personService;
    private final PromotionService promotionService;
    private final SaleService saleService;
    private final ReturnService returnService;
    private final Scanner scanner;

    /**
     * Constructs a SubMenu instance injecting all necessary service
     * dependencies. The Scanner is shared with Menu to avoid two
     * separate Scanner instances reading from the same System.in stream.
     *
     * @param productService service for managing products
     * @param accessoryService service for managing accessories
     * @param personService service for managing clients and sellers
     * @param promotionService service for managing discounts and promotions
     * @param saleService service for handling transactions
     * @param returnService service for handling returns
     * @param scanner the shared Scanner used to read console input
     */
    public SubMenu(ProductService productService,
                    AccessoryService accessoryService,
                    PersonService personService,
                    PromotionService promotionService,
                    SaleService saleService,
                    ReturnService returnService,
                    Scanner scanner) {
        this.productService = productService;
        this.accessoryService = accessoryService;
        this.personService = personService;
        this.promotionService = promotionService;
        this.saleService = saleService;
        this.returnService = returnService;
        this.scanner = scanner;
    }

    /**
     * Displays and manages the product management sub-menu.
     */
    public void showProductSubMenu() {
        int option;
        do {
            System.out.println("\n--- GESTIÓN DE PRODUCTOS ---");
            System.out.println("1. Registrar consola");
            System.out.println("2. Registrar videojuego");
            System.out.println("3. Listar todos los productos");
            System.out.println("4. Actualizar stock de un producto");
            System.out.println("0. Volver");
            System.out.print("Seleccione una opción: ");
            option = readInt();

            switch (option) {
                case 1 -> {
                    System.out.print("ID: "); String id = scanner.nextLine();
                    System.out.print("Título: "); String title = scanner.nextLine();
                    System.out.print("Precio: "); double price = readDouble();
                    System.out.print("Stock: "); int stock = readInt();
                    System.out.print("Marca: "); String brand = scanner.nextLine();
                    System.out.print("Modelo: "); String model = scanner.nextLine();
                    System.out.print("Generación: "); String generation = scanner.nextLine();

                    productService.registerConsole(id, title, price, stock, brand, model, generation);
                    System.out.println("¡Consola registrada exitosamente!");
                }
                case 2 -> {
                    System.out.print("ID: "); String id = scanner.nextLine();
                    System.out.print("Título: "); String title = scanner.nextLine();
                    System.out.print("Precio: "); double price = readDouble();
                    System.out.print("Stock: "); int stock = readInt();
                    System.out.print("Plataforma: "); String platform = scanner.nextLine();
                    System.out.print("Género: "); String genre = scanner.nextLine();
                    System.out.print("Clasificación de edad: "); String ageRating = scanner.nextLine();

                    productService.registerVideoGame(id, title, price, stock, platform, genre, ageRating);
                    System.out.println("¡Videojuego registrado exitosamente!");
                }
                case 3 -> {
                    System.out.println("\n--- LISTADO DE PRODUCTOS ---");
                    for (Product p : productService.listAll()) {
                        System.out.println(p.getId() + " - " + p.getTitle() + " | Precio: $" + p.getPrice() + " | Stock: " + p.getStock());
                    }
                }
                case 4 -> {
                    System.out.print("ID del producto: "); String id = scanner.nextLine();
                    System.out.print("Cantidad a descontar del stock: "); int qty = readInt();
                    try {
                        productService.updateStock(id, qty);
                        System.out.println("¡Stock actualizado exitosamente!");
                    } catch (Exception e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                }
                case 0 -> {}
                default -> System.out.println("Opción inválida.");
            }
        } while (option != 0);
    }

    /**
     * Displays and manages the accessory management sub-menu.
     */
    public void showAccessorySubMenu() {
        int option;
        do {
            System.out.println("\n--- GESTIÓN DE ACCESORIOS ---");
            System.out.println("1. Registrar control");
            System.out.println("2. Registrar cable");
            System.out.println("3. Registrar memoria");
            System.out.println("4. Listar todos los accesorios");
            System.out.println("5. Listar accesorios por tipo");
            System.out.println("6. Consultar accesorios compatibles con una consola");
            System.out.println("7. Actualizar stock de un accesorio");
            System.out.println("0. Volver");
            System.out.print("Seleccione una opción: ");
            option = readInt();

            switch (option) {
                case 1 -> {
                    System.out.print("ID: "); String id = scanner.nextLine();
                    System.out.print("Título: "); String title = scanner.nextLine();
                    System.out.print("Precio: "); double price = readDouble();
                    System.out.print("Stock: "); int stock = readInt();
                    System.out.print("Tipo de conexión (Inalámbrico / Alámbrico): "); String conn = scanner.nextLine();
                    System.out.print("IDs de consolas compatibles separados por coma (ej. PS5,PS4): ");
                    List<String> consoles = List.of(scanner.nextLine().split(","));

                    accessoryService.registerController(id, title, price, stock, conn, consoles);
                    System.out.println("¡Control registrado exitosamente!");
                }
                case 2 -> {
                    System.out.print("ID: "); String id = scanner.nextLine();
                    System.out.print("Título: "); String title = scanner.nextLine();
                    System.out.print("Precio: "); double price = readDouble();
                    System.out.print("Stock: "); int stock = readInt();
                    System.out.print("Longitud (metros): "); double length = readDouble();
                    System.out.print("Tipo de conector: "); String connector = scanner.nextLine();

                    accessoryService.registerCable(id, title, price, stock, length, connector);
                    System.out.println("¡Cable registrado exitosamente!");
                }
                case 3 -> {
                    System.out.print("ID: "); String id = scanner.nextLine();
                    System.out.print("Título: "); String title = scanner.nextLine();
                    System.out.print("Precio: "); double price = readDouble();
                    System.out.print("Stock: "); int stock = readInt();
                    System.out.print("Capacidad (GB): "); int capacity = readInt();
                    System.out.print("Tipo de memoria: "); String type = scanner.nextLine();
                    System.out.print("IDs de consolas compatibles separados por coma: ");
                    List<String> consoles = List.of(scanner.nextLine().split(","));

                    accessoryService.registerMemory(id, title, price, stock, capacity, type, consoles);
                    System.out.println("¡Memoria registrada exitosamente!");
                }
                case 4 -> {
                    System.out.println("\n--- LISTADO DE ACCESORIOS ---");
                    for (Accessory a : accessoryService.listAllAccessories()) {
                        System.out.println(a.getId() + " - " + a.getTitle() + " | Precio: $" + a.getPrice() + " | Stock: " + a.getStock());
                    }
                }
                case 5 -> {
                    System.out.print("Ingrese el tipo (CONTROLLER / CABLE / MEMORY): ");
                    String type = scanner.nextLine();
                    for (Accessory a : accessoryService.listAccessoriesByType(type)) {
                        System.out.println(a.getId() + " - " + a.getTitle());
                    }
                }
                case 6 -> {
                    System.out.print("ID de la consola: ");
                    String consoleId = scanner.nextLine();
                    for (Accessory a : accessoryService.findAccessoriesCompatibleWith(consoleId)) {
                        System.out.println(a.getId() + " - " + a.getTitle());
                    }
                }
                case 7 -> {
                    System.out.print("ID del accesorio: "); String id = scanner.nextLine();
                    System.out.print("Cantidad a descontar del stock: "); int qty = readInt();
                    try {
                        accessoryService.updateStock(id, qty);
                        System.out.println("¡Stock del accesorio actualizado exitosamente!");
                    } catch (Exception e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                }
                case 0 -> {}
                default -> System.out.println("Opción inválida.");
            }
        } while (option != 0);
    }

    /**
     * Displays and manages the clients and sellers management sub-menu.
     */
    public void showPersonSubMenu() {
        int option;
        do {
            System.out.println("\n--- GESTIÓN DE CLIENTES Y VENDEDORES ---");
            System.out.println("1. Registrar cliente");
            System.out.println("2. Listar clientes");
            System.out.println("3. Listar vendedores");
            System.out.println("0. Volver");
            System.out.print("Seleccione una opción: ");
            option = readInt();

            switch (option) {
                case 1 -> {
                    System.out.print("ID: "); String id = scanner.nextLine();
                    System.out.print("Nombre completo: "); String name = scanner.nextLine();
                    System.out.print("Teléfono: "); String phone = scanner.nextLine();
                    System.out.print("Correo electrónico: "); String email = scanner.nextLine();

                    personService.registerClient(id, name, phone, email);
                    System.out.println("¡Cliente registrado exitosamente!");
                }
                case 2 -> {
                    System.out.println("\n--- LISTADO DE CLIENTES ---");
                    for (Client c : personService.listClients()) {
                        System.out.println(c.getId() + " - " + c.getName() + " | Correo: " + c.getEmail() + " | Compras realizadas: " + c.getPurchaseHistory().size());
                    }
                }
                case 3 -> {
                    System.out.println("\n--- LISTADO DE VENDEDORES ---");
                    for (Seller s : personService.listSellers()) {
                        System.out.println(s.getId() + " - " + s.getName() + " | Turno: " + s.getShift());
                    }
                }
                case 0 -> {}
                default -> System.out.println("Opción inválida.");
            }
        } while (option != 0);
    }

    /**
     * Displays and manages the promotions management sub-menu.
     */
    public void showPromotionSubMenu() {
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
            option = readInt();

            switch (option) {
                case 1 -> {
                    System.out.print("ID: "); String id = scanner.nextLine();
                    System.out.print("Nombre de la promoción: "); String name = scanner.nextLine();
                    System.out.print("Fecha de inicio (YYYY-MM-DD): "); LocalDate start = LocalDate.parse(scanner.nextLine());
                    System.out.print("Fecha de fin (YYYY-MM-DD): "); LocalDate end = LocalDate.parse(scanner.nextLine());
                    System.out.print("Porcentaje de descuento (%): "); double pct = readDouble();

                    promotionService.registerPercentageDiscount(id, name, start, end, pct);
                    System.out.println("¡Promoción por porcentaje registrada exitosamente!");
                }
                case 2 -> {
                    System.out.print("ID: "); String id = scanner.nextLine();
                    System.out.print("Nombre de la promoción: "); String name = scanner.nextLine();
                    System.out.print("Fecha de inicio (YYYY-MM-DD): "); LocalDate start = LocalDate.parse(scanner.nextLine());
                    System.out.print("Fecha de fin (YYYY-MM-DD): "); LocalDate end = LocalDate.parse(scanner.nextLine());
                    System.out.print("Porcentaje de descuento (%): "); double pct = readDouble();
                    System.out.print("Categoría objetivo (VIDEOGAME / CONSOLE): "); String cat = scanner.nextLine();

                    promotionService.registerCategoryDiscount(id, name, start, end, pct, cat);
                    System.out.println("¡Promoción por categoría registrada exitosamente!");
                }
                case 3 -> {
                    System.out.print("ID: "); String id = scanner.nextLine();
                    System.out.print("Nombre de la promoción: "); String name = scanner.nextLine();
                    System.out.print("Fecha de inicio (YYYY-MM-DD): "); LocalDate start = LocalDate.parse(scanner.nextLine());
                    System.out.print("Fecha de fin (YYYY-MM-DD): "); LocalDate end = LocalDate.parse(scanner.nextLine());
                    System.out.print("Cantidad mínima de productos: "); int minQty = readInt();
                    System.out.print("Porcentaje de descuento (%): "); double pct = readDouble();

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

    /**
     * Displays and manages the returns management sub-menu.
     */
    public void showReturnSubMenu() {
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
            option = readInt();

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
     * Guides the user through registering a new return: sale id, list of
     * product ids to return, and a reason. Reports validation errors in
     * Spanish, as returned by ReturnService.
     */
    private void registerReturnFlow() {
        System.out.print("Ingrese el identificador de la venta original: ");
        String saleId = scanner.nextLine().trim();

        System.out.print("Ingrese los identificadores de los productos a devolver (separados por coma): ");
        String productsLine = scanner.nextLine().trim();
        List<String> productIds = new ArrayList<>();
        for (String id : productsLine.split(",")) {
            String trimmed = id.trim();
            if (!trimmed.isEmpty()) {
                productIds.add(trimmed);
            }
        }

        System.out.print("Ingrese el motivo de la devolución: ");
        String reason = scanner.nextLine();

        try {
            Return createdReturn = returnService.registerReturn(saleId, productIds, reason);
            System.out.println("\n¡Devolución registrada exitosamente!");
            System.out.println(createdReturn.generateReturnReceipt());
        } catch (IllegalArgumentException e) {
            System.out.println("No se pudo registrar la devolución: " + e.getMessage());
        }
    }

    /**
     * Lists every return currently registered in the system.
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
     * Lists the returns associated with a specific customer, entered by the user.
     */
    private void listReturnsByCustomer() {
        System.out.print("Ingrese el identificador del cliente: ");
        String customerId = scanner.nextLine().trim();

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
     * Lists the returns associated with a specific sale, entered by the user.
     */
    private void listReturnsBySale() {
        System.out.print("Ingrese el identificador de la venta: ");
        String saleId = scanner.nextLine().trim();

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
     * Asks the user for a month and year, then displays the monthly balance
     * (total sales minus total returns) for that period.
     */
    private void showMonthlyBalance() {
        int month;
        int year;
        System.out.print("Ingrese el mes (1-12): ");
        month = readInt();
        System.out.print("Ingrese el año (por ejemplo, 2026): ");
        year = readInt();

        if (month < 1 || month > 12) {
            System.out.println("El mes debe estar entre 1 y 12.");
            return;
        }

        double balance = returnService.generateMonthlyBalance(month, year);
        System.out.printf("%nBalance neto para %02d/%d: $%.2f%n", month, year, balance);
    }

    /**
     * Executes the form workflow to process a new sale transaction.
     */
    public void processSaleForm() {
        System.out.println("\n--- NUEVA VENTA ---");
        System.out.print("ID de la venta: "); String saleId = scanner.nextLine();
        System.out.print("ID del cliente: "); String clientId = scanner.nextLine();
        System.out.print("ID del vendedor: "); String sellerId = scanner.nextLine();

        List<SaleDetail> items = new ArrayList<>();
        boolean adding = true;

        while (adding) {
            System.out.print("ID del producto o accesorio: "); String productId = scanner.nextLine();
            try {
                Product p = saleService.findProductOrAccessoryById(productId);
                System.out.print("Cantidad para (" + p.getTitle() + " - Stock disponible: " + p.getStock() + "): ");
                int qty = readInt();

                items.add(new SaleDetail(p, qty, p.getPrice()));
                System.out.println("Producto agregado al carrito.");
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }

            System.out.print("¿Desea agregar otro producto? (s/n): ");
            String answer = scanner.nextLine().trim();
            adding = answer.equalsIgnoreCase("s") || answer.equalsIgnoreCase("y");
        }

        try {
            Sale sale = saleService.processSale(saleId, clientId, sellerId, items);
            System.out.println("\n==========================================");
            System.out.println("        ¡VENTA PROCESADA EXITOSAMENTE!    ");
            System.out.println("==========================================");
            System.out.println("ID de la venta: " + sale.getId());
            System.out.println("Subtotal: $" + sale.calculateTotal());

            if (sale.getPromotion() != null) {
                System.out.println("Promoción aplicada: " + sale.getPromotion().getId()
                        + " (descuento: $" + sale.calculateDiscount() + ")");
            }

            System.out.println("Total final: $" + sale.calculateFinalTotal());
        } catch (Exception e) {
            System.out.println("Error al procesar la venta: " + e.getMessage());
        }
    }

    /**
     * Lists the complete history of registered sales.
     */
    public void listSalesHistory() {
        System.out.println("\n--- HISTORIAL DE VENTAS ---");
        List<Sale> sales = saleService.listAllSales();
        if (sales.isEmpty()) {
            System.out.println("No hay ventas registradas.");
            return;
        }

        for (Sale s : sales) {
            System.out.println("ID: " + s.getId() + " | Fecha: " + s.getDate() +
                    " | Cliente: " + s.getClient().getName() +
                    " | Vendedor: " + s.getSeller().getName() +
                    " | Total: $" + s.calculateFinalTotal());
        }
    }

    /**
     * Helper method to safely read integer inputs from the console.
     *
     * @return parsed integer or -1 on failure
     */
    private int readInt() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * Helper method to safely read double inputs from the console.
     *
     * @return parsed double or 0.0 on failure
     */
    private double readDouble() {
        try {
            return Double.parseDouble(scanner.nextLine().trim());
        } catch (Exception e) {
            return 0.0;
        }
    }
}
