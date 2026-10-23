package com.gamezone.ui;

import com.gamezone.model.Product;
import com.gamezone.service.ProductService;

/**
 * Handles the product (consoles and video games) management sub-menu.
 */
public class ProductSubMenu {

    private final ProductService productService;
    private final ConsoleInput input;

    /**
     * @param productService service for managing products
     * @param input the shared console reader
     */
    public ProductSubMenu(ProductService productService, ConsoleInput input) {
        this.productService = productService;
        this.input = input;
    }

    /**
     * Displays and manages the product management sub-menu.
     */
    public void show() {
        int option;
        do {
            System.out.println("\n--- GESTIÓN DE PRODUCTOS ---");
            System.out.println("1. Registrar consola");
            System.out.println("2. Registrar videojuego");
            System.out.println("3. Listar todos los productos");
            System.out.println("4. Actualizar stock de un producto");
            System.out.println("0. Volver");
            System.out.print("Seleccione una opción: ");
            option = input.readInt();

            switch (option) {
                case 1 -> {
                    System.out.print("ID: "); String id = input.readLine();
                    System.out.print("Título: "); String title = input.readLine();
                    System.out.print("Precio: "); double price = input.readDouble();
                    System.out.print("Stock: "); int stock = input.readInt();
                    System.out.print("Marca: "); String brand = input.readLine();
                    System.out.print("Modelo: "); String model = input.readLine();
                    System.out.print("Generación: "); String generation = input.readLine();

                    productService.registerConsole(id, title, price, stock, brand, model, generation);
                    System.out.println("¡Consola registrada exitosamente!");
                }
                case 2 -> {
                    System.out.print("ID: "); String id = input.readLine();
                    System.out.print("Título: "); String title = input.readLine();
                    System.out.print("Precio: "); double price = input.readDouble();
                    System.out.print("Stock: "); int stock = input.readInt();
                    System.out.print("Plataforma: "); String platform = input.readLine();
                    System.out.print("Género: "); String genre = input.readLine();
                    System.out.print("Clasificación de edad: "); String ageRating = input.readLine();

                    productService.registerVideoGame(id, title, price, stock, platform, genre, ageRating);
                    System.out.println("¡Videojuego registrado exitosamente!");
                }
                case 3 -> {
                    System.out.println("\n--- LISTADO DE PRODUCTOS ---");
                    for (Product p : productService.listAll()) {
                        System.out.println(p.getId() + " - " + p.getTitle() + " | Precio: $" + p.getPrice() + " | Stock: " + p.getStock());
                    }
                }
                case 4 -> {
                    System.out.print("ID del producto: "); String id = input.readLine();
                    System.out.print("Cantidad a descontar del stock: "); int qty = input.readInt();
                    productService.updateStock(id, qty);
                    System.out.println("¡Stock actualizado exitosamente!");
                }
                case 0 -> {}
                default -> System.out.println("Opción inválida.");
            }
        } while (option != 0);
    }
}