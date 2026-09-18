
package com.gamezone.model;

import java.time.LocalDate;
import java.util.List;

/**
 * Class representing a product return in the system.
 */
public class Return {
    private String id;
    private LocalDate date;
    private Sale originalSale;
    private List<Product> returnedProducts;
    private String reason;
    private double refundAmount;

    /**
     * Constructs a new Return.
     *
     * @param id the unique return identifier
     * @param date the date of the return
     * @param originalSale the original sale referenced
     * @param returnedProducts the list of products being returned
     * @param reason the reason for the return
     * @param refundAmount the total refunded amount
     */
    public Return(String id, LocalDate date, Sale originalSale, List<Product> returnedProducts, String reason, double refundAmount) {
        this.id = id;
        this.date = date;
        this.originalSale = originalSale;
        this.returnedProducts = returnedProducts;
        this.reason = reason;
        this.refundAmount = refundAmount;
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

    public Sale getOriginalSale() {
        return originalSale;
    }

    public List<Product> getReturnedProducts() {
        return returnedProducts;
    }

    public void setReturnedProducts(List<Product> returnedProducts) {
        this.returnedProducts = returnedProducts;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public double getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(double refundAmount) {
        this.refundAmount = refundAmount;
    }

    /**
     * Calculates the refund amount by summing the prices of the returned products.
     * Assigns the result to the refundAmount attribute and returns it.
     *
     * @return the calculated refund amount
     */
    public double calculateRefundAmount() {
        double total = 0.0;
        for (Product product : returnedProducts) {
            total += product.getPrice();
        }
        this.refundAmount = total;
        return total;
    }

    /**
     * Generates a formatted return receipt in Spanish.
     *
     * @return the return receipt string
     */
    public String generateReturnReceipt() {
        String receipt = "--- RECIBO DE DEVOLUCIÓN ---\n";
        receipt += "ID Devolución: " + id + "\n";
        receipt += "Fecha: " + date + "\n";
        receipt += "Venta Original ID: " + originalSale.getId() + "\n";
        receipt += "Motivo: " + reason + "\n";
        receipt += "Productos Devueltos:\n";
        
        for (Product product : returnedProducts) {
            receipt += "- " + product.getTitle() + " : $" + product.getPrice() + "\n";
        }
        
        receipt += "Monto Reembolsado: $" + refundAmount + "\n";
        receipt += "----------------------------\n";
        return receipt;
    }
}
