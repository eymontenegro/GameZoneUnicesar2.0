package com.gamezone.service;

import com.gamezone.model.Accessory;
import com.gamezone.model.Client;
import com.gamezone.model.Console;
import com.gamezone.model.Product;
import com.gamezone.model.Promotion;
import com.gamezone.model.Sale;
import com.gamezone.model.SaleDetail;
import com.gamezone.model.Seller;
import com.gamezone.model.Warranty;
import com.gamezone.persistence.SaleRepository;

import java.time.LocalDate;
import java.util.List;

/**
 * Applies the business rules for processing transactions/sales in GameZone.
 * Coordinates stock reduction, promotional discount calculation, client purchase history updates,
 * automatic and optional warranty assignment, and persistence using SaleRepository.
 */
public class SaleService {

    private List<Sale> sales;
    private SaleRepository saleRepository;
    private PersonService personService;
    private ProductService productService;
    private AccessoryService accessoryService;
    private PromotionService promotionService;
    private WarrantyService warrantyService;

    /**
     * Constructs SaleService injecting all required service dependencies and the repository.
     * Loads existing sales from CSV matching references across services.
     *
     * @param saleRepository the repository for managing sale persistence
     * @param personService service for managing clients and sellers
     * @param productService service for managing consoles and video games
     * @param accessoryService service for managing store accessories
     * @param promotionService service for managing active promotions
     * @param warrantyService service for assigning and querying warranties
     */
    public SaleService(SaleRepository saleRepository,
                       PersonService personService,
                       ProductService productService,
                       AccessoryService accessoryService,
                       PromotionService promotionService,
                       WarrantyService warrantyService) {
        this.saleRepository = saleRepository;
        this.personService = personService;
        this.productService = productService;
        this.accessoryService = accessoryService;
        this.promotionService = promotionService;
        this.warrantyService = warrantyService;

        // Load sales linking entities from respective services
        this.sales = saleRepository.loadAll(
                personService.listClients(),
                personService.listSellers(),
                productService.listAll(),
                accessoryService.listAllAccessories(),
                promotionService.listAllPromotions()
        );
    }

    /**
     * Helper to find a product in either ProductService (VideoGames/Consoles) or AccessoryService (Accessories).
     *
     * @param productId the ID of the product or accessory to search for
     * @return the matched Product object
     * @throws IllegalArgumentException if no product or accessory exists with that ID
     */
    public Product findProductOrAccessoryById(String productId) {
        // Search in main products (VideoGame / Console)
        for (Product p : productService.listAll()) {
            if (p.getId().equals(productId)) {
                return p;
            }
        }
        // Search in accessories
        for (Accessory a : accessoryService.listAllAccessories()) {
            if (a.getId().equals(productId)) {
                return a;
            }
        }
        throw new IllegalArgumentException("No product or accessory was found with ID: " + productId);
    }

    /**
     * Processes and records a new sale.
     * Evaluates stock availability, calculates subtotal, applies the best valid promotion,
     * reduces inventory stock, assigns automatic basic warranties to consoles, assigns
     * optional extended warranties requested by the seller, updates the client purchase
     * history, and persists the sale.
     *
     * @param saleId ID of the new sale
     * @param clientId ID of the client purchasing
     * @param sellerId ID of the seller executing the transaction
     * @param items list of SaleDetail items to include in the sale
     * @param productIdsWithExtendedWarranty ids of the products in this sale that should
     *        receive an extended warranty; may be null or empty if none was requested
     * @return the fully processed Sale object
     */
    public Sale processSale(String saleId, String clientId, String sellerId, List<SaleDetail> items,
                             List<String> productIdsWithExtendedWarranty) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("The sale must contain at least one product.");
        }

        Client client = personService.findClientById(clientId);
        Seller seller = personService.findSellerById(sellerId);

        // 1. Validate sufficient stock before making any changes
        for (SaleDetail detail : items) {
            Product product = detail.getProduct();
            if (product.getStock() < detail.getQuantity()) {
                throw new IllegalStateException("Insufficient stock for product: " + product.getTitle()
                        + " (Available: " + product.getStock() + ", Requested: " + detail.getQuantity() + ")");
            }
        }

        // 2. Create Sale object
        Sale sale = new Sale(saleId, client, seller, LocalDate.now());
        for (SaleDetail detail : items) {
            sale.addDetail(detail);
        }

        // 3. Evaluate and apply the best available promotion
        Promotion bestPromotion = promotionService.findBestPromotionFor(sale);
        if (bestPromotion != null) {
            sale.setPromotion(bestPromotion);
        }

        // 4. Deduct stock from inventory
        for (SaleDetail detail : items) {
            Product product = detail.getProduct();
            if (product instanceof Accessory) {
                accessoryService.updateStock(product.getId(), detail.getQuantity());
            } else {
                productService.updateStock(product.getId(), detail.getQuantity());
            }
        }

        // 5. Assign warranties: automatic basic warranty for every console,
        // plus an optional extended warranty for the products the seller selected.
        for (SaleDetail detail : items) {
            Product product = detail.getProduct();

            if (product instanceof Console) {
                warrantyService.assignBasicWarranty(product, sale, sale.getDate());
            }

            if (productIdsWithExtendedWarranty != null
                    && productIdsWithExtendedWarranty.contains(product.getId())) {
                Warranty extendedWarranty = warrantyService.assignExtendedWarranty(product, sale, sale.getDate());
                sale.addExtraCost(extendedWarranty.getAdditionalCost());
            }
        }

        // 6. Register sale in client's purchase history
        personService.registerPurchase(clientId, saleId);

        // 7. Save sale in memory and into the CSV file
        sales.add(sale);
        saleRepository.saveAll(sales);

        return sale;
    }

    public void restoreStock(Product product, int quantity) {
        if (product instanceof Accessory) {
            accessoryService.restoreStock(product.getId(), quantity);
        } else {
            productService.restoreStock(product.getId(), quantity);
        }
    }

    /**
     * Returns the full list of registered sales.
     *
     * @return list of sales
     */
    public List<Sale> listAllSales() {
        return sales;
    }

    /**
     * Finds a sale by its identifier.
     *
     * @param saleId ID of the sale to find
     * @return matched Sale object
     * @throws IllegalArgumentException if no sale with that ID exists
     */
    public Sale findById(String saleId) {
        for (Sale sale : sales) {
            if (sale.getId().equals(saleId)) {
                return sale;
            }
        }
        throw new IllegalArgumentException("No registered sale exists with ID: " + saleId);
    }
}
