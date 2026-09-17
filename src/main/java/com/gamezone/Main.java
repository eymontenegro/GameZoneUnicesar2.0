package com.gamezone;

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
        PersonService personService = new PersonService();
        SaleService saleService = new SaleService(productService, personService);

        // UI layer initialization and execution
        Menu mainMenu = new Menu(productService, personService, saleService);
        mainMenu.start();
    }
}


