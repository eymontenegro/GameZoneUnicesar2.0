package com.gamezone;

import com.gamezone.persistence.AccessoryRepository;
import com.gamezone.persistence.PersonRepository;
import com.gamezone.persistence.ProductRepository;
import com.gamezone.persistence.PromotionRepository;
import com.gamezone.persistence.ReturnRepository;
import com.gamezone.persistence.SaleRepository;
import com.gamezone.persistence.WarrantyRepository;
import com.gamezone.service.AccessoryService;
import com.gamezone.service.PersonService;
import com.gamezone.service.ProductService;
import com.gamezone.service.PromotionService;
import com.gamezone.service.ReturnService;
import com.gamezone.service.SaleService;
import com.gamezone.service.WarrantyService;
import com.gamezone.ui.Menu;

/**
 * Main application entry point for the GameZone Unicesar system.
 * Responsible for configuring CSV persistence file paths, initializing repositories,
 * wiring business layer dependencies, and bootstrapping the interactive console user interface.
 *
 * @author GameZone Technical Team
 * @version 1.0
 */
public class Main {

    /**
     * Private constructor to prevent instantiation of this utility/main class.
     */
    private Main() {
        // Utility class
    }

    /**
     * Main execution method that starts the GameZone application lifecycle.
     *
     * @param args command-line arguments passed during application startup
     */
    public static void main(String[] args) {
        // 1. Define CSV storage file paths for the persistence layer
        String clientsPath = "data/clients.csv";
        String sellersPath = "data/sellers.csv";
        String productsPath = "data/products.csv";
        String accessoriesPath = "data/accessories.csv";
        String promotionsPath = "data/promotions.csv";
        String salesPath = "data/sales.csv";
        String returnsPath = "data/returns.csv";
        String warrantiesPath = "data/warranties.csv";

        // 2. Initialize Repositories with their respective file paths and cross-dependencies
        PersonRepository personRepository = new PersonRepository(clientsPath, sellersPath);
        ProductRepository productRepository = new ProductRepository(productsPath);
        AccessoryRepository accessoryRepository = new AccessoryRepository(accessoriesPath);
        PromotionRepository promotionRepository = new PromotionRepository(promotionsPath);
        SaleRepository saleRepository = new SaleRepository(salesPath);
        ReturnRepository returnRepository = new ReturnRepository(returnsPath);
        
        WarrantyRepository warrantyRepository = new WarrantyRepository(
                saleRepository,
                productRepository,
                personRepository,
                accessoryRepository,
                promotionRepository,
                warrantiesPath
        );

        // 3. Initialize Business Services in correct dependency hierarchy order
        ProductService productService = new ProductService(productRepository);
        AccessoryService accessoryService = new AccessoryService(accessoryRepository);
        PersonService personService = new PersonService(personRepository);
        PromotionService promotionService = new PromotionService(promotionRepository);
        WarrantyService warrantyService = new WarrantyService(warrantyRepository);

        SaleService saleService = new SaleService(
                saleRepository,
                personService,
                productService,
                accessoryService,
                promotionService,
                warrantyService
        );

        ReturnService returnService = new ReturnService(returnRepository, saleService);

        // 4. Initialize the main navigation menu controller with all fully wired services and launch it
        Menu mainMenu = new Menu(
                productService,
                accessoryService,
                personService,
                promotionService,
                saleService,
                returnService,
                warrantyService
        );

        mainMenu.displayMenu();
    }
}