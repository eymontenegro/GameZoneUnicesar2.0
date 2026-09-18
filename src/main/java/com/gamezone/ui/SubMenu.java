package com.gamezone.ui;

import com.gamezone.model.Accessory;
import com.gamezone.model.Client;
import com.gamezone.model.Product;
import com.gamezone.model.Promotion;
import com.gamezone.model.Sale;
import com.gamezone.model.SaleDetail;
import com.gamezone.model.Seller;
import com.gamezone.service.AccessoryService;
import com.gamezone.service.PersonService;
import com.gamezone.service.ProductService;
import com.gamezone.service.PromotionService;
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
    private final Scanner scanner;

    /**
     * Constructs a SubMenu instance injecting all necessary service dependencies.
     * 
     * @productService service for managing products
     * @accessoryService service for managing accessories
     * @personService service for managing clients and sellers
     * @promotionService service for managing discounts and promotions
     * @saleService service for handling transactions
     */
    public SubMenu(ProductService productService,
                   AccessoryService accessoryService,
                   PersonService personService,
                   PromotionService promotionService,
                   SaleService saleService) {
        this.productService = productService;
        this.accessoryService = accessoryService;
        this.personService = personService;
        this.promotionService = promotionService;
        this.saleService = saleService;
        this.scanner = new Scanner(System.in);
    }

    /**
     * Displays and manages the product management sub-menu.
     */
    public void showProductSubMenu() {
        int option;
        do {
            System.out.println("\n--- PRODUCT MANAGEMENT ---");
            System.out.println("1. Register Console");
            System.out.println("2. Register Video Game");
            System.out.println("3. List All Products");
            System.out.println("4. Update Product Stock");
            System.out.println("0. Back");
            System.out.print("Select an option: ");
            option = readInt();

            switch (option) {
                case 1 -> {
                    System.out.print("ID: "); String id = scanner.nextLine();
                    System.out.print("Title: "); String title = scanner.nextLine();
                    System.out.print("Price: "); double price = readDouble();
                    System.out.print("Stock: "); int stock = readInt();
                    System.out.print("Brand: "); String brand = scanner.nextLine();
                    System.out.print("Model: "); String model = scanner.nextLine();
                    System.out.print("Generation: "); String generation = scanner.nextLine();

                    productService.registerConsole(id, title, price, stock, brand, model, generation);
                    System.out.println("Console registered successfully!");
                }
                case 2 -> {
                    System.out.print("ID: "); String id = scanner.nextLine();
                    System.out.print("Title: "); String title = scanner.nextLine();
                    System.out.print("Price: "); double price = readDouble();
                    System.out.print("Stock: "); int stock = readInt();
                    System.out.print("Platform: "); String platform = scanner.nextLine();
                    System.out.print("Genre: "); String genre = scanner.nextLine();
                    System.out.print("Age Rating: "); String ageRating = scanner.nextLine();

                    productService.registerVideoGame(id, title, price, stock, platform, genre, ageRating);
                    System.out.println("Video game registered successfully!");
                }
                case 3 -> {
                    System.out.println("\n--- PRODUCT LIST ---");
                    for (Product p : productService.listAll()) {
                        System.out.println(p.getId() + " - " + p.getTitle() + " | Price: $" + p.getPrice() + " | Stock: " + p.getStock());
                    }
                }
                case 4 -> {
                    System.out.print("Product ID: "); String id = scanner.nextLine();
                    System.out.print("Quantity to reduce from stock: "); int qty = readInt();
                    try {
                        productService.updateStock(id, qty);
                        System.out.println("Stock updated successfully!");
                    } catch (Exception e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                }
                case 0 -> {}
                default -> System.out.println("Invalid option.");
            }
        } while (option != 0);
    }

    /**
     * Displays and manages the accessory management sub-menu.
     */
    public void showAccessorySubMenu() {
        int option;
        do {
            System.out.println("\n--- ACCESSORY MANAGEMENT ---");
            System.out.println("1. Register Controller");
            System.out.println("2. Register Cable");
            System.out.println("3. Register Memory");
            System.out.println("4. List All Accessories");
            System.out.println("5. List Accessories by Type");
            System.out.println("6. Find Accessories Compatible with Console");
            System.out.println("7. Update Accessory Stock");
            System.out.println("0. Back");
            System.out.print("Select an option: ");
            option = readInt();

            switch (option) {
                case 1 -> {
                    System.out.print("ID: "); String id = scanner.nextLine();
                    System.out.print("Title: "); String title = scanner.nextLine();
                    System.out.print("Price: "); double price = readDouble();
                    System.out.print("Stock: "); int stock = readInt();
                    System.out.print("Connection Type (Wireless / Wired): "); String conn = scanner.nextLine();
                    System.out.print("Compatible console IDs separated by comma (e.g., PS5,PS4): ");
                    List<String> consoles = List.of(scanner.nextLine().split(","));

                    accessoryService.registerController(id, title, price, stock, conn, consoles);
                    System.out.println("Controller registered successfully!");
                }
                case 2 -> {
                    System.out.print("ID: "); String id = scanner.nextLine();
                    System.out.print("Title: "); String title = scanner.nextLine();
                    System.out.print("Price: "); double price = readDouble();
                    System.out.print("Stock: "); int stock = readInt();
                    System.out.print("Length (meters): "); double length = readDouble();
                    System.out.print("Connector Type: "); String connector = scanner.nextLine();

                    accessoryService.registerCable(id, title, price, stock, length, connector);
                    System.out.println("Cable registered successfully!");
                }
                case 3 -> {
                    System.out.print("ID: "); String id = scanner.nextLine();
                    System.out.print("Title: "); String title = scanner.nextLine();
                    System.out.print("Price: "); double price = readDouble();
                    System.out.print("Stock: "); int stock = readInt();
                    System.out.print("Capacity (GB): "); int capacity = readInt();
                    System.out.print("Memory Type: "); String type = scanner.nextLine();
                    System.out.print("Compatible console IDs separated by comma: ");
                    List<String> consoles = List.of(scanner.nextLine().split(","));

                    accessoryService.registerMemory(id, title, price, stock, capacity, type, consoles);
                    System.out.println("Memory registered successfully!");
                }
                case 4 -> {
                    System.out.println("\n--- ACCESSORY LIST ---");
                    for (Accessory a : accessoryService.listAllAccessories()) {
                        System.out.println(a.getId() + " - " + a.getTitle() + " | Price: $" + a.getPrice() + " | Stock: " + a.getStock());
                    }
                }
                case 5 -> {
                    System.out.print("Enter type (CONTROLLER / CABLE / MEMORY): ");
                    String type = scanner.nextLine();
                    for (Accessory a : accessoryService.listAccessoriesByType(type)) {
                        System.out.println(a.getId() + " - " + a.getTitle());
                    }
                }
                case 6 -> {
                    System.out.print("Enter Console ID: ");
                    String consoleId = scanner.nextLine();
                    for (Accessory a : accessoryService.findAccessoriesCompatibleWith(consoleId)) {
                        System.out.println(a.getId() + " - " + a.getTitle());
                    }
                }
                case 7 -> {
                    System.out.print("Accessory ID: "); String id = scanner.nextLine();
                    System.out.print("Quantity to reduce from stock: "); int qty = readInt();
                    try {
                        accessoryService.updateStock(id, qty);
                        System.out.println("Accessory stock updated successfully!");
                    } catch (Exception e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                }
                case 0 -> {}
                default -> System.out.println("Invalid option.");
            }
        } while (option != 0);
    }

    /**
     * Displays and manages the clients and sellers management sub-menu.
     */
    public void showPersonSubMenu() {
        int option;
        do {
            System.out.println("\n--- CLIENT & SELLER MANAGEMENT ---");
            System.out.println("1. Register Client");
            System.out.println("2. List Clients");
            System.out.println("3. List Sellers");
            System.out.println("0. Back");
            System.out.print("Select an option: ");
            option = readInt();

            switch (option) {
                case 1 -> {
                    System.out.print("ID: "); String id = scanner.nextLine();
                    System.out.print("Full Name: "); String name = scanner.nextLine();
                    System.out.print("Phone: "); String phone = scanner.nextLine();
                    System.out.print("Email: "); String email = scanner.nextLine();

                    personService.registerClient(id, name, phone, email);
                    System.out.println("Client registered successfully!");
                }
                case 2 -> {
                    System.out.println("\n--- CLIENT LIST ---");
                    for (Client c : personService.listClients()) {
                        System.out.println(c.getId() + " - " + c.getName() + " | Email: " + c.getEmail() + " | Purchases Made: " + c.getPurchaseHistory().size());
                    }
                }
                case 3 -> {
                    System.out.println("\n--- SELLER LIST ---");
                    for (Seller s : personService.listSellers()) {
                        System.out.println(s.getId() + " - " + s.getName() + " | Shift: " + s.getShift());
                    }
                }
                case 0 -> {}
                default -> System.out.println("Invalid option.");
            }
        } while (option != 0);
    }

    /**
     * Displays and manages the promotions management sub-menu.
     */
    public void showPromotionSubMenu() {
        int option;
        do {
            System.out.println("\n--- PROMOTION MANAGEMENT ---");
            System.out.println("1. Register Percentage Discount");
            System.out.println("2. Register Category Discount");
            System.out.println("3. Register Bulk Purchase Discount");
            System.out.println("4. List All Promotions");
            System.out.println("5. List Active Promotions");
            System.out.println("0. Back");
            System.out.print("Select an option: ");
            option = readInt();

            switch (option) {
                case 1 -> {
                    System.out.print("ID: "); String id = scanner.nextLine();
                    System.out.print("Promotion Name: "); String name = scanner.nextLine();
                    System.out.print("Start Date (YYYY-MM-DD): "); LocalDate start = LocalDate.parse(scanner.nextLine());
                    System.out.print("End Date (YYYY-MM-DD): "); LocalDate end = LocalDate.parse(scanner.nextLine());
                    System.out.print("Discount Percentage (%): "); double pct = readDouble();

                    promotionService.registerPercentageDiscount(id, name, start, end, pct);
                    System.out.println("Percentage discount registered successfully!");
                }
                case 2 -> {
                    System.out.print("ID: "); String id = scanner.nextLine();
                    System.out.print("Promotion Name: "); String name = scanner.nextLine();
                    System.out.print("Start Date (YYYY-MM-DD): "); LocalDate start = LocalDate.parse(scanner.nextLine());
                    System.out.print("End Date (YYYY-MM-DD): "); LocalDate end = LocalDate.parse(scanner.nextLine());
                    System.out.print("Discount Percentage (%): "); double pct = readDouble();
                    System.out.print("Target Category (VIDEOGAME / CONSOLE): "); String cat = scanner.nextLine();

                    promotionService.registerCategoryDiscount(id, name, start, end, pct, cat);
                    System.out.println("Category discount registered successfully!");
                }
                case 3 -> {
                    System.out.print("ID: "); String id = scanner.nextLine();
                    System.out.print("Promotion Name: "); String name = scanner.nextLine();
                    System.out.print("Start Date (YYYY-MM-DD): "); LocalDate start = LocalDate.parse(scanner.nextLine());
                    System.out.print("End Date (YYYY-MM-DD): "); LocalDate end = LocalDate.parse(scanner.nextLine());
                    System.out.print("Minimum Product Quantity: "); int minQty = readInt();
                    System.out.print("Discount Percentage (%): "); double pct = readDouble();

                    promotionService.registerBulkPurchaseDiscount(id, name, start, end, minQty, pct);
                    System.out.println("Bulk purchase discount registered successfully!");
                }
                case 4 -> {
                    System.out.println("\n--- ALL PROMOTIONS ---");
                    for (Promotion p : promotionService.listAllPromotions()) {
                        System.out.println(p.getId() + " - " + p.getName());
                    }
                }
                case 5 -> {
                    System.out.println("\n--- ACTIVE PROMOTIONS ---");
                    for (Promotion p : promotionService.listActivePromotions()) {
                        System.out.println(p.getId() + " - " + p.getName());
                    }
                }
                case 0 -> {}
                default -> System.out.println("Invalid option.");
            }
        } while (option != 0);
    }

    /**
     * Executes the form workflow to process a new sale transaction.
     */
    public void processSaleForm() {
        System.out.println("\n--- NEW SALE ---");
        System.out.print("Sale ID: "); String saleId = scanner.nextLine();
        System.out.print("Client ID: "); String clientId = scanner.nextLine();
        System.out.print("Seller ID: "); String sellerId = scanner.nextLine();

        List<SaleDetail> items = new ArrayList<>();
        boolean adding = true;

        while (adding) {
            System.out.print("Product or Accessory ID: "); String productId = scanner.nextLine();
            try {
                Product p = saleService.findProductOrAccessoryById(productId);
                System.out.print("Quantity for (" + p.getTitle() + " - Available Stock: " + p.getStock() + "): ");
                int qty = readInt();

                items.add(new SaleDetail(p, qty, p.getPrice()));
                System.out.println("Product added to cart.");
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }

            System.out.print("Do you want to add another product? (y/n): ");
            if (!scanner.nextLine().equalsIgnoreCase("y") && !scanner.nextLine().equalsIgnoreCase("s")) {
                // Accepts both 'y' and Spanish 's' just in case, or adapt as needed
            }
            // Keeping the existing loop control logic matching user preference
            System.out.print("¿Desea agregar otro producto? (s/n): ");
            if (!scanner.nextLine().equalsIgnoreCase("s")) {
                adding = false;
            }
        }

        try {
            Sale sale = saleService.processSale(saleId, clientId, sellerId, items);
            System.out.println("\n==========================================");
            System.out.println("       SALE PROCESSED SUCCESSFULLY!       ");
            System.out.println("==========================================");
            System.out.println("Sale ID: " + sale.getId());
            
            // Subtotal calculation using getPrice()
            double subtotal = 0.0;
            for (SaleDetail detail : sale.getDetails()) {
                subtotal += detail.getQuantity() * detail.getProduct().getPrice();
            }
            System.out.println("Subtotal: $" + subtotal);
            
            if (sale.getPromotion() != null) {
                System.out.println("Applied Promotion ID: " + sale.getPromotion().getId());
            }
            
            System.out.println("Final Total: $" + sale.calculateTotal());
        } catch (Exception e) {
            System.out.println("Error processing sale: " + e.getMessage());
        }
    }

    /**
     * Lists the complete history of registered sales.
     */
    public void listSalesHistory() {
        System.out.println("\n--- SALES HISTORY ---");
        List<Sale> sales = saleService.listAllSales();
        if (sales.isEmpty()) {
            System.out.println("No registered sales found.");
            return;
        }

        for (Sale s : sales) {
            System.out.println("ID: " + s.getId() + " | Date: " + s.getDate() +
                    " | Client: " + s.getClient().getName() +
                    " | Seller: " + s.getSeller().getName() +
                    " | Total: $" + s.calculateTotal());
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
