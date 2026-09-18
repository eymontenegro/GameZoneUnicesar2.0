package com.gamezone.service;

import com.gamezone.model.Accessory;
import com.gamezone.model.Client;
import com.gamezone.model.Product;
import com.gamezone.model.Sale;
import com.gamezone.model.SaleDetail;
import com.gamezone.model.Seller;
import com.gamezone.persistence.SaleRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides business logic for registering and querying sales.
 * Orchestrates stock reduction (delegating to ProductService or
 * AccessoryService depending on the item's type) and maintains
 * sale persistence.
 */
public class SaleService {

    private SaleRepository repository;
    private ProductService productService;
    private AccessoryService accessoryService;
    private PersonService personService;
    private List<Sale> sales;

    /**
     * Creates a new SaleService using references to existing ProductService,
     * AccessoryService, and PersonService. Loads saved sales and links them
     * with existing clients, sellers, and products.
     *
     * @param productService service handling product inventory
     * @param accessoryService service handling accessory inventory
     * @param personService service handling clients and sellers
     */
    public SaleService(ProductService productService, AccessoryService accessoryService, PersonService personService) {
        this.productService = productService;
        this.accessoryService = accessoryService;
        this.personService = personService;
        this.repository = new SaleRepository();
        this.sales = repository.loadSales(
            personService.listClients(),
            personService.listSellers(),
            productService.listAll()
        );
    }

     /**
     * Registers a new sale transaction, validates stock, reduces stock for
     * every item (products and accessories), confirms the sale, and saves
     * updated sales data.
     *
     * @param clientIdentification identification of the client making the purchase
     * @param sellerEmployeeCode code of the seller processing the transaction
     * @param details list of sale details containing products, accessories, and quantities
     * @return the newly registered Sale object
     */
    public Sale registerSale(String clientIdentification, String sellerEmployeeCode, List<SaleDetail> details) {
        Client client = findClient(clientIdentification);
        Seller seller = findSeller(sellerEmployeeCode);

        // Validate stock availability before processing (works for both
        // products and accessories, since Accessory extends Product)
        for (SaleDetail detail : details) {
            if (detail.getProduct().getStock() < detail.getQuantity()) {
                throw new IllegalArgumentException("Stock insuficiente para el producto: " 
                        + detail.getProduct().getTitle());
            }
        }

        Sale sale = new Sale(LocalDate.now(), client, seller, details);
        
        // Confirm sale (validates non-empty details list and updates client history)
        sale.confirm();

        // Service layer orchestrates stock reduction, delegating to the
        // service that owns each item's inventory
        for (SaleDetail detail : details) {
            Product item = detail.getProduct();
            if (item instanceof Accessory) {
                accessoryService.updateStock(item.getId(), detail.getQuantity());
            } else {
                productService.updateStock(item.getId(), detail.getQuantity());
            }
        }

        sales.add(sale);
        repository.saveSales(sales);
        return sale;
    }

       /**
     * Returns the list of all registered sales.
     *
     * @return list of sales
     */
    public List<Sale> listSales() {
        return sales;
    }

    private Client findClient(String identification) {
        for (Client c : personService.listClients()) {
            if (c.getId().equals(identification)) {
                return c;
            }
        }
        throw new IllegalArgumentException("No se encontró el cliente con la identificación: " + identification);
    }

    private Seller findSeller(String code) {
        for (Seller s : personService.listSellers()) {
            if (s.getEmployeeCode().equals(code)) {
                return s;
            }
        }
        throw new IllegalArgumentException("No se encontró el vendedor con el código de empleado: " + code);
    }
}
