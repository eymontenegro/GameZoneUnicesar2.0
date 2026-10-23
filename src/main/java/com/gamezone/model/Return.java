package com.gamezone.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Class representing a product return in the GameZone system.
 * Manages partial product returns, proportional promotional refunds (A5),
 * and warranty cancellations refund integration (A7).
 */
public class Return {

    private String id;
    private LocalDate date;
    private Sale originalSale;
    private List<Product> returnedProducts;
    private String reason;
    private double warrantyRefundAmount;
    private double refundAmount;

    /**
     * Constructs a new Return instance.
     *
     * @param id the unique return identifier
     * @param date the date of the return
     * @param originalSale the original sale transaction reference
     * @param returnedProducts the list of products being returned
     * @param reason the reason for the return
     * @param warrantyRefundAmount additional refunded cost from canceled warranties (A7)
     */
    public Return(String id, LocalDate date, Sale originalSale, List<Product> returnedProducts, String reason, double warrantyRefundAmount) {
        this.id = Objects.requireNonNull(id, "Return ID cannot be null.");
        this.date = (date != null) ? date : LocalDate.now();
        this.originalSale = Objects.requireNonNull(originalSale, "Original sale cannot be null.");
        this.returnedProducts = (returnedProducts != null) ? new ArrayList<>(returnedProducts) : new ArrayList<>();
        this.reason = (reason != null) ? reason : "No especificado";
        this.warrantyRefundAmount = Math.max(0.0, warrantyRefundAmount);
        this.calculateRefundAmount();
    }

    /**
     * Overloaded constructor without initial warranty refund.
     */
    public Return(String id, LocalDate date, Sale originalSale, List<Product> returnedProducts, String reason) {
        this(id, date, originalSale, returnedProducts, reason, 0.0);
    }
        /**
     * Rebuilds a return that was already stored, from the total refund saved
     * with it. The products refund is recalculated from the sale, and the
     * rest of the stored total is taken as the canceled warranty refund
     * (differences of half a cent or less are ignored).
     *
     * @param id               the return identifier
     * @param date             the date of the return
     * @param originalSale     the original sale
     * @param returnedProducts the returned products
     * @param reason           the reason for the return
     * @param storedRefund     the total refund stored for this return
     * @return the rebuilt return, whose refund matches the stored total
     */
    public static Return fromStoredTotal(String id, LocalDate date, Sale originalSale,
                                         List<Product> returnedProducts, String reason, double storedRefund) {
        Return restored = new Return(id, date, originalSale, returnedProducts, reason, 0.0);
        double warrantyPart = storedRefund - restored.getRefundAmount();
        if (warrantyPart > 0.005) {
            restored.setWarrantyRefundAmount(warrantyPart);
        }
        return restored;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    // Immutable relationship: No setter for originalSale (Req 3 constraint)
    public Sale getOriginalSale() {
        return originalSale;
    }

    public List<Product> getReturnedProducts() {
        return new ArrayList<>(returnedProducts);
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public double getWarrantyRefundAmount() {
        return warrantyRefundAmount;
    }

    public void setWarrantyRefundAmount(double warrantyRefundAmount) {
        this.warrantyRefundAmount = Math.max(0.0, warrantyRefundAmount);
        calculateRefundAmount();
    }

    public double getRefundAmount() {
        return refundAmount;
    }

    /**
     * Calculates the proportional refund amount based on the discount applied
     * to the original sale (A5) plus any canceled warranty refunds (A7).
     *
     * Item Refund = List Price * (1 - (Sale Discount / Sale Subtotal))
     * Total Refund = Sum of Item Refunds + Warranty Refund
     *
     * @return the calculated refund amount
     */
    public double calculateRefundAmount() {
        double subtotalSale = originalSale.calculateSubtotal();
        double discountSale = originalSale.getDiscountAmount();
        double discountRatio = (subtotalSale > 0) ? (discountSale / subtotalSale) : 0.0;

        double productsRefundTotal = 0.0;
        for (Product product : returnedProducts) {
            double itemRefund = product.getPrice() * (1.0 - discountRatio);
            productsRefundTotal += itemRefund;
        }

        this.refundAmount = productsRefundTotal + warrantyRefundAmount;
        return this.refundAmount;
    }

    /**
     * Generates a formatted return receipt string in Spanish showing item list price,
     * proportional discount breakdown, canceled warranty refunds, and net total refunded.
     *
     * @return formatted return receipt text
     */
    public String generateReturnReceipt() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        StringBuilder receipt = new StringBuilder();

        double subtotalSale = originalSale.calculateSubtotal();
        double discountSale = originalSale.getDiscountAmount();
        double discountRatio = (subtotalSale > 0) ? (discountSale / subtotalSale) : 0.0;

        receipt.append("=========================================\n");
        receipt.append("          GAMEZONE UNICESAR              \n");
        receipt.append("        RECIBO DE DEVOLUCIÓN             \n");
        receipt.append("=========================================\n");
        receipt.append("ID Devolución: ").append(id).append("\n");
        receipt.append("Fecha: ").append(date.format(formatter)).append("\n");
        receipt.append("Venta Referenciada ID: ").append(originalSale.getId()).append("\n");
        receipt.append("Motivo: ").append(reason).append("\n");
        receipt.append("-----------------------------------------\n");
        receipt.append("DETALLE DE PRODUCTOS DEVUELTOS:\n");

        double totalProductsRefund = 0.0;
        for (Product product : returnedProducts) {
            double listPrice = product.getPrice();
            double proportionalDiscount = listPrice * discountRatio;
            double netItemRefund = listPrice - proportionalDiscount;
            totalProductsRefund += netItemRefund;

            receipt.append(String.format(" - %s\n", product.getTitle()));
            receipt.append(String.format("   Precio Lista: $%.2f | Desc. Prop.: -$%.2f | Reembolso: $%.2f\n",
                    listPrice, proportionalDiscount, netItemRefund));
        }

        receipt.append("-----------------------------------------\n");
        receipt.append(String.format("Subtotal Reembolso Productos: $%.2f\n", totalProductsRefund));
        if (warrantyRefundAmount > 0) {
            receipt.append(String.format("Reembolso Garantía Anulada:  +$%.2f\n", warrantyRefundAmount));
        }
        receipt.append("-----------------------------------------\n");
        receipt.append(String.format("MONTO TOTAL REEMBOLSADO:      $%.2f\n", refundAmount));
        receipt.append("=========================================\n");

        return receipt.toString();
    }
}