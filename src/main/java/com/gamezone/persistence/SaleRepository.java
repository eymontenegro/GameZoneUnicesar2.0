package com.gamezone.persistence;

import com.gamezone.model.Client;
import com.gamezone.model.Product;
import com.gamezone.model.Sale;
import com.gamezone.model.SaleDetail;
import com.gamezone.model.Seller;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles the persistence of Sale data using CSV files.
 * This class is responsible for saving and loading sales and their details
 * to and from the file system.
 */
public class SaleRepository {

    private static final String SALES_FILE = "data/sales.csv";
    private static final String SALE_DETAILS_FILE = "data/sale_details.csv";

     /**
     * Saves the given list of sales and their associated details to CSV files.
     *
     * @param sales the list of sales to save
     */
    public void saveSales(List<Sale> sales) {
        try (FileWriter saleWriter = new FileWriter(SALES_FILE);
             FileWriter detailWriter = new FileWriter(SALE_DETAILS_FILE)) {

            int saleId = 1;
            for (Sale sale : sales) {
                // Save main sale info: saleId, date, clientIdentification, sellerEmployeeCode
                saleWriter.write(saleId + ","
                        + sale.getDate() + ","
                        + sale.getClient().getId() + ","
                        + sale.getSeller().getEmployeeCode() + "\n");

                // Save each detail linked to this saleId: saleId, productId, quantity, unitPrice
                for (SaleDetail detail : sale.getDetails()) {
                    detailWriter.write(saleId + ","
                            + detail.getProduct().getId() + ","
                            + detail.getQuantity() + ","
                            + detail.getUnitPrice() + "\n");
                }
                saleId++;
            }
        } catch (IOException e) {
            System.out.println("Error al guardar las ventas: " + e.getMessage());
        }
    }

    /**
     * Loads the list of sales from the CSV files, reconstructing relations
     * using the provided lists of clients, sellers, and products.
     *
     * @param clients list of available clients
     * @param sellers list of available sellers
     * @param products list of available products
     * @return the list of sales loaded from the file
     */
    public List<Sale> loadSales(List<Client> clients, List<Seller> sellers, List<Product> products) {
        List<Sale> sales = new ArrayList<>();
        List<String[]> detailsData = loadDetailsData();

        try (BufferedReader reader = new BufferedReader(new FileReader(SALES_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    int saleId = Integer.parseInt(parts[0]);
                    LocalDate date = LocalDate.parse(parts[1]);
                    String clientDoc = parts[2];
                    String sellerCode = parts[3];

                    Client client = findClientByIdentification(clients, clientDoc);
                    Seller seller = findSellerByCode(sellers, sellerCode);

                    if (client != null && seller != null) {
                        List<SaleDetail> details = extractDetailsForSale(saleId, detailsData, products);
                        if (!details.isEmpty()) {
                            Sale sale = new Sale(date, client, seller, details);
                            client.addPurchase(sale.getId()); // Reconstructs the client history without re-invoking confirm()
                            sales.add(sale);
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("No se encontraron datos de ventas anteriores. Iniciando vacío.");
        }
        return sales;
    }

    private List<String[]> loadDetailsData() {
        List<String[]> detailsData = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(SALE_DETAILS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    detailsData.add(parts);
                }
            }
        } catch (IOException e) {
            System.out.println("No se encontraron datos de detalles de ventas anteriores.");
        }
        return detailsData;
    }

    private List<SaleDetail> extractDetailsForSale(int targetSaleId, List<String[]> detailsData, List<Product> products) {
        List<SaleDetail> details = new ArrayList<>();
        for (String[] parts : detailsData) {
            int saleId = Integer.parseInt(parts[0]);
            if (saleId == targetSaleId) {
                String productId = parts[1];
                int quantity = Integer.parseInt(parts[2]);
                double unitPrice = Double.parseDouble(parts[3]);

                Product product = findProductById(products, productId);
                if (product != null) {
                    details.add(new SaleDetail(product, quantity, unitPrice));
                }
            }
        }
        return details;
    }

    private Client findClientByIdentification(List<Client> clients, String id) {
        for (Client c : clients) {
            if (c.getId().equals(id)) {
                return c;
            }
        }
        return null;
    }

    private Seller findSellerByCode(List<Seller> sellers, String code) {
        for (Seller s : sellers) {
            if (s.getEmployeeCode().equals(code)) {
                return s;
            }
        }
        return null;
    }

    private Product findProductById(List<Product> products, String id) {
        for (Product p : products) {
            if (p.getId().equals(id)) {
                return p;
            }
        }
        return null;
    }
}
