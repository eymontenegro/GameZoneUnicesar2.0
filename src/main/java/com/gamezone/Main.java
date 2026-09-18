package com.gamezone;

import com.gamezone.persistence.AccessoryRepository;
import com.gamezone.persistence.PromotionRepository;
import com.gamezone.persistence.ReturnRepository;
import com.gamezone.persistence.SaleRepository;
import com.gamezone.service.AccessoryService;
import com.gamezone.service.PersonService;
import com.gamezone.service.ProductService;
import com.gamezone.service.PromotionService;
import com.gamezone.service.ReturnService;
import com.gamezone.service.SaleService;
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
        AccessoryService accessoryService = new AccessoryService(new AccessoryRepository());
        PersonService personService = new PersonService();

        PromotionRepository promotionRepository = new PromotionRepository();
        PromotionService promotionService = new PromotionService(promotionRepository);

        // Initialize SaleService with the 5 required parameters
        SaleService saleService = new SaleService(
                new SaleRepository(),
                personService,
                productService,
                accessoryService,
                promotionService
        );

        
        // ReturnRepository only needs SaleService, since SaleService already
        // knows how to resolve both products and accessories by id.
        ReturnRepository returnRepository = new ReturnRepository();
        ReturnService returnService = new ReturnService(returnRepository, saleService, productService);


        // Initialize the main menu with all 6 services and display it
        Menu mainMenu = new Menu(productService, accessoryService, personService,
                promotionService, saleService, returnService);
        mainMenu.displayMenu();
    }
}
