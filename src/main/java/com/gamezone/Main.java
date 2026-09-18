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
 * Main entry point for the GameZone application.
 * Initializes repositories, business services, and launches the main console menu.
 */
public class Main {

    /**
     * Main execution method.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        // Initialize independent services and repositories
        ProductService productService = new ProductService();
        AccessoryRepository accessoryRepository = new AccessoryRepository();
        AccessoryService accessoryService = new AccessoryService(accessoryRepository);
        PersonService personService = new PersonService();

        PromotionRepository promotionRepository = new PromotionRepository();
        PromotionService promotionService = new PromotionService(promotionRepository);

        SaleRepository saleRepository = new SaleRepository();

        // WarrantyRepository needs its own ProductRepository and PersonRepository
        // instances to resolve Sale/Product references, since ProductService and
        // PersonService keep their repositories private. These extra instances
        // only read/write the same CSV files and hold no in-memory state, so
        // having a second instance alongside the one used inside each service
        // is safe.
        WarrantyRepository warrantyRepository = new WarrantyRepository(
                saleRepository,
                new ProductRepository(),
                new PersonRepository(),
                accessoryRepository,
                promotionRepository
        );
        WarrantyService warrantyService = new WarrantyService(warrantyRepository);

        // Initialize SaleService with the required parameters, including WarrantyService
        SaleService saleService = new SaleService(
                saleRepository,
                personService,
                productService,
                accessoryService,
                promotionService,
                warrantyService
        );

        // ReturnRepository only needs SaleService, since SaleService already
        // knows how to resolve both products and accessories by id.
        ReturnRepository returnRepository = new ReturnRepository();
        ReturnService returnService = new ReturnService(returnRepository, saleService, productService);

        // Initialize the main menu with all services, including WarrantyService, and display it
        Menu mainMenu = new Menu(productService, accessoryService, personService,
                promotionService, saleService, returnService, warrantyService);
        mainMenu.displayMenu();
    }
}
