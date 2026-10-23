package com.gamezone.ui;

import com.gamezone.model.Sale;
import com.gamezone.model.SaleDetail;

import java.time.format.DateTimeFormatter;

/**
 * Presentation helper that builds the printed receipt of a {@link Sale}.
 * It keeps the text layout out of the domain class (Pure Fabrication).
 */
public final class SaleReceiptFormatter {

    private SaleReceiptFormatter() {
    }

    /**
     * Generates a formatted sale receipt string in Spanish showing breakdown
     * of subtotal, applied promotion discount, extended warranty costs, and final total.
     * As before, building the receipt recalculates the final total of the sale.
     *
     * @param sale the sale to print
     * @return formatted receipt text
     */
    public static String format(Sale sale) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        StringBuilder receipt = new StringBuilder();

        receipt.append("=========================================\n");
        receipt.append("          GAMEZONE UNICESAR              \n");
        receipt.append("          RECIBO DE VENTA                \n");
        receipt.append("=========================================\n");
        receipt.append("ID Venta: ").append(sale.getId()).append("\n");
        receipt.append("Fecha: ").append(sale.getDate().format(formatter)).append("\n");
        receipt.append("Cliente: ").append(sale.getClient().getName()).append(" (ID: ").append(sale.getClient().getId()).append(")\n");
        receipt.append("Vendedor: ").append(sale.getSeller().getName()).append(" (Código: ").append(sale.getSeller().getEmployeeCode()).append(")\n");
        receipt.append("-----------------------------------------\n");
        receipt.append("DETALLE DE PRODUCTOS:\n");

        for (SaleDetail detail : sale.getDetails()) {
            receipt.append(String.format(" - %s x%d @ $%.2f = $%.2f\n",
                    detail.getProduct().getTitle(),
                    detail.getQuantity(),
                    detail.getUnitPrice(),
                    detail.getSubtotal()));
        }

        double subtotal = sale.calculateSubtotal();
        receipt.append("-----------------------------------------\n");
        receipt.append(String.format("Subtotal Productos:     $%.2f\n", subtotal));
        receipt.append(String.format("Promoción Aplicada:     %s\n", sale.getAppliedPromotionName()));
        receipt.append(String.format("Descuento Promocional: -$%.2f\n", sale.getDiscountAmount()));
        receipt.append(String.format("Garantías Extendidas:  +$%.2f\n", sale.getAdditionalCosts()));
        receipt.append("-----------------------------------------\n");
        receipt.append(String.format("TOTAL FINAL:            $%.2f\n", sale.calculateFinalTotal()));
        receipt.append("=========================================\n");

        return receipt.toString();
    }
}