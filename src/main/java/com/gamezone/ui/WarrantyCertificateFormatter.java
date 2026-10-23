package com.gamezone.ui;

import com.gamezone.model.Warranty;

import java.time.format.DateTimeFormatter;

/**
 * Builds the console text of a warranty certificate. The layout of this
 * user-facing document lives here, so {@link Warranty} only keeps the
 * warranty data and rules.
 */
public final class WarrantyCertificateFormatter {

    private WarrantyCertificateFormatter() {
        // Utility class: no instances.
    }

    /**
     * Formats the certificate of a warranty in Spanish.
     *
     * @param warranty the warranty to print
     * @return formatted certificate text
     */
    public static String format(Warranty warranty) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return "=== CERTIFICADO DE GARANTÍA ===\n" +
                "ID Garantía: " + warranty.getId() + "\n" +
                "Tipo: " + warranty.getWarrantyType() + "\n" +
                "Producto: " + warranty.getProduct().getTitle() + "\n" +
                "Fecha de Inicio: " + warranty.getStartDate().format(formatter) + "\n" +
                "Fecha de Vencimiento: " + warranty.getEndDate().format(formatter) + "\n" +
                "Costo Adicional: $" + warranty.getAdditionalCost() + "\n" +
                "===============================";
    }
}
