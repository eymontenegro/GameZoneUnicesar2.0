package com.gamezone.service;

import com.gamezone.exceptions.GameZoneException;
import com.gamezone.exceptions.InvalidDataException;
import com.gamezone.exceptions.ResourceNotFoundException;
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
import com.gamezone.validation.ProductValidator;
import com.gamezone.validation.SaleValidator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Applies the business rules for processing transactions/sales in GameZone.
 * Coordinates stock reduction, promotional discount calculation, client purchase history updates,
 * automatic and optional warranty assignment, and persistence using SaleRepository.
 */
public class SaleService {

    private final List<Sale> sales;
    private final SaleRepository saleRepository;
    private final PersonService personService;
    private final ProductService productService;
    private final AccessoryService accessoryService;
    private final PromotionService promotionService;
    private final WarrantyService warrantyService;

    public SaleService(SaleRepository saleRepository,
                       PersonService personService,
                       ProductService productService,
                       AccessoryService accessoryService,
                       PromotionService promotionService,
                       WarrantyService warrantyService) {
        this.saleRepository = Objects.requireNonNull(saleRepository, "SaleRepository cannot be null");
        this.personService = Objects.requireNonNull(personService, "PersonService cannot be null");
        this.productService = Objects.requireNonNull(productService, "ProductService cannot be null");
        this.accessoryService = Objects.requireNonNull(accessoryService, "AccessoryService cannot be null");
        this.promotionService = Objects.requireNonNull(promotionService, "PromotionService cannot be null");
        this.warrantyService = Objects.requireNonNull(warrantyService, "WarrantyService cannot be null");

        List<Sale> loadedSales = saleRepository.loadAll(
                personService.listClients(),
                personService.listSellers(),
                productService.listAll(),
                accessoryService.listAllAccessories(),
                promotionService.listAllPromotions()
        );
        this.sales = Collections.synchronizedList(loadedSales != null ? new ArrayList<>(loadedSales) : new ArrayList<>());
    }

    public Product findProductOrAccessoryById(String productId) {
        if (productId == null || productId.trim().isEmpty()) {
            throw new InvalidDataException("identificador del producto", "no puede ser nulo ni vacío");
        }

        return productService.listAll().stream()
                .filter(p -> p != null && productId.equals(p.getId()))
                .findFirst()
                .map(p -> (Product) p)
                .orElseGet(() -> accessoryService.listAllAccessories().stream()
                        .filter(a -> a != null && productId.equals(a.getId()))
                        .findFirst()
                        .orElseThrow(() -> new ResourceNotFoundException("producto o accesorio", productId))
                );
    }

    public Sale processSale(String saleId, String clientId, String sellerId, List<SaleDetail> items,
                            List<String> productIdsWithExtendedWarranty) {
        Client client = personService.findClientById(clientId);
        Seller seller = personService.findSellerById(sellerId);

        SaleValidator.validateSaleData(client, seller, items);
        SaleValidator.validateSaleDetails(items);

        // 1. Validate stock
        for (SaleDetail detail : items) {
            ProductValidator.validateStockAvailability(detail.getProduct(), detail.getQuantity());
        }

        // 2. Build Sale
        Sale sale = new Sale(saleId, client, seller, LocalDate.now());
        items.forEach(sale::addDetail);

        // 3. Apply promotion
        Promotion bestPromotion = promotionService.findBestPromotionFor(sale);
        if (bestPromotion != null) {
            sale.setAppliedPromotionName(bestPromotion.getName());
            sale.setDiscountAmount(bestPromotion.calculateDiscount(sale));
        }

        List<SaleDetail> processedDetails = new ArrayList<>();
        try {
            // 4. Deduct Stock
            for (SaleDetail detail : items) {
                Product product = detail.getProduct();
                if (product instanceof Accessory) {
                    accessoryService.updateStock(product.getId(), detail.getQuantity());
                } else {
                    productService.updateStock(product.getId(), detail.getQuantity());
                }
                processedDetails.add(detail);
            }

            // 5. Assign Warranties
            for (SaleDetail detail : items) {
                Product product = detail.getProduct();

                if (product instanceof Console) {
                    warrantyService.assignBasicWarranty(product, sale, sale.getDate());
                }

                if (productIdsWithExtendedWarranty != null && productIdsWithExtendedWarranty.contains(product.getId())) {
                    Warranty extendedWarranty = warrantyService.assignExtendedWarranty(product, sale, sale.getDate());
                    sale.addExtraCost(extendedWarranty.getAdditionalCost());
                }
            }

            // 6. Register Client Purchase
            personService.registerPurchase(clientId, saleId);

            // 7. Persist
            sales.add(sale);
            saleRepository.saveAll(new ArrayList<>(sales));

            return sale;
        } catch (Exception ex) {
            // Rollback stock deductions if processing fails midway
            for (SaleDetail detail : processedDetails) {
                restoreStock(detail.getProduct(), detail.getQuantity());
            }
            if (ex instanceof GameZoneException) {
                throw (GameZoneException) ex;
            }
            throw new RuntimeException("Failed to process sale ID " + saleId + ". Transaction rolled back.", ex);
        }
    }

    public void restoreStock(Product product, int quantity) {
        if (product == null) {
            return;
        }
        if (product instanceof Accessory) {
            accessoryService.restoreStock(product.getId(), quantity);
        } else {
            productService.restoreStock(product.getId(), quantity);
        }
    }

    public List<Sale> listAllSales() {
        synchronized (sales) {
            return new ArrayList<>(sales);
        }
    }

    public Sale findById(String saleId) {
        if (saleId == null || saleId.trim().isEmpty()) {
            throw new InvalidDataException("identificador de la venta", "no puede ser nulo ni vacío");
        }

        Sale found;
        synchronized (sales) {
            found = sales.stream()
                    .filter(s -> s != null && saleId.equals(s.getId()))
                    .findFirst()
                    .orElse(null);
        }
        SaleValidator.validateSaleExists(found, saleId);
        return found;
    }
}