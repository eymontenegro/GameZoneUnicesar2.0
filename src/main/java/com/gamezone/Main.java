package com.gamezone;

import com.gamezone.persistence.AccessoryRepository;
import com.gamezone.service.AccessoryService;
import com.gamezone.service.PersonService;
import com.gamezone.service.ProductService;
import com.gamezone.service.SaleService;
import com.gamezone.ui.Menu;

/**
 * Entry point of the GameZone Unicesar application.
 * Wires together the service layer and starts the console-based
 * user interface.
 */
public class Main {

    /**
     * Starts the GameZone Unicesar system: initializes the services
     * (loading previously saved data automatically), then launches
     * the main console menu.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        // Service layer initialization (automatically loads CSV files from data/ directory)
        ProductService productService = new ProductService();
        AccessoryService accessoryService = new AccessoryService(new AccessoryRepository());
        PersonService personService = new PersonService();
        SaleService saleService = new SaleService(productService, accessoryService, personService);

        // UI layer initialization and execution
        Menu mainMenu = new Menu(productService, accessoryService, personService, saleService);
        mainMenu.start();
    }
}
