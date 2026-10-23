package com.gamezone.ui;

import com.gamezone.model.Console;
import com.gamezone.model.Product;
import com.gamezone.model.Sale;
import com.gamezone.model.SaleDetail;
import com.gamezone.service.SaleService;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles the sale form and the sales history.
 */
public class SaleSubMenu {

    private final SaleService saleService;
    private final ConsoleInput input;

    /**
     * @param saleService service for handling transactions
     * @param input the shared console reader
     */
    public SaleSubMenu(SaleService saleService, ConsoleInput input) {
        this.saleService = saleService;
        this.input = input;
    }

    /**
     * Executes the form workflow to process a new sale transaction.
     */
    public void processSaleForm() {
        System.out.println("\n--- NUEVA VENTA ---");
        System.out.print("ID de la venta: "); String saleId = input.readLine();
        System.out.print("ID del cliente: "); String clientId = input.readLine();
        System.out.print("ID del vendedor: "); String sellerId = input.readLine();

        List<SaleDetail> items = new ArrayList<>();
        List<String> productIdsWithExtendedWarranty = new ArrayList<>();
        boolean adding = true;

        while (adding) {
            System.out.print("ID del producto o accesorio: "); String productId = input.readLine();

            Product p = saleService.findProductOrAccessoryById(productId);
            System.out.print("Cantidad para (" + p.getTitle() + " - Stock disponible: " + p.getStock() + "): ");
            int qty = input.readInt();

            items.add(new SaleDetail(p, qty, p.getPrice()));
            System.out.println("Producto agregado al carrito.");

            if (p instanceof Console) {
                System.out.print("¿Desea agregar garantía extendida para " + p.getTitle() + "? (s/n): ");
                String extendedAnswer = input.readLine().trim();
                if (extendedAnswer.equalsIgnoreCase("s") || extendedAnswer.equalsIgnoreCase("y")) {
                    productIdsWithExtendedWarranty.add(p.getId());
                }
            }

            System.out.print("¿Desea agregar otro producto? (s/n): ");
            String answer = input.readLine().trim();
            adding = answer.equalsIgnoreCase("s") || answer.equalsIgnoreCase("y");
        }

        Sale sale = saleService.processSale(saleId, clientId, sellerId, items, productIdsWithExtendedWarranty);
        System.out.println("\n==========================================");
        System.out.println("         ¡VENTA PROCESADA EXITOSAMENTE!    ");
        System.out.println("==========================================");
        System.out.println("ID de la venta: " + sale.getId());
        System.out.println("Subtotal: $" + sale.calculateTotal());

        if (sale.getAppliedPromotionName() != null && !sale.getAppliedPromotionName().equals("Ninguna")) {
            System.out.println("Promoción aplicada: " + sale.getAppliedPromotionName()
                    + " (descuento: $" + sale.getDiscountAmount() + ")");
        }

        if (sale.getAdditionalCosts() > 0) {
            System.out.println("Costo adicional por garantías extendidas: $" + sale.getAdditionalCosts());
        }

        System.out.println("Total final: $" + sale.calculateFinalTotal());
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
}