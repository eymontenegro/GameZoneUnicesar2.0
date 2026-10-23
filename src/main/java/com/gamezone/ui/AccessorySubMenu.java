package com.gamezone.ui;

import com.gamezone.model.Accessory;
import com.gamezone.service.AccessoryService;

import java.util.List;

/**
 * Handles the accessory management sub-menu.
 */
public class AccessorySubMenu {

    private final AccessoryService accessoryService;
    private final ConsoleInput input;

    /**
     * @param accessoryService service for managing accessories
     * @param input the shared console reader
     */
    public AccessorySubMenu(AccessoryService accessoryService, ConsoleInput input) {
        this.accessoryService = accessoryService;
        this.input = input;
    }

    /**
     * Displays and manages the accessory management sub-menu.
     */
    public void show() {
        int option;
        do {
            System.out.println("\n--- GESTIÓN DE ACCESORIOS ---");
            System.out.println("1. Registrar control");
            System.out.println("2. Registrar cable");
            System.out.println("3. Registrar memoria");
            System.out.println("4. Listar todos los accesorios");
            System.out.println("5. Listar accesorios por tipo");
            System.out.println("6. Consultar accesorios compatibles con una consola");
            System.out.println("7. Actualizar stock de un accesorio");
            System.out.println("0. Volver");
            System.out.print("Seleccione una opción: ");
            option = input.readInt();

            switch (option) {
                case 1 -> {
                    System.out.print("ID: "); String id = input.readLine();
                    System.out.print("Título: "); String title = input.readLine();
                    System.out.print("Precio: "); double price = input.readDouble();
                    System.out.print("Stock: "); int stock = input.readInt();
                    System.out.print("Tipo de conexión (Inalámbrico / Alámbrico): "); String conn = input.readLine();
                    System.out.print("IDs de consolas compatibles separados por coma (ej. PS5,PS4): ");
                    List<String> consoles = List.of(input.readLine().split(","));

                    accessoryService.registerController(id, title, price, stock, conn, consoles);
                    System.out.println("¡Control registrado exitosamente!");
                }
                case 2 -> {
                    System.out.print("ID: "); String id = input.readLine();
                    System.out.print("Título: "); String title = input.readLine();
                    System.out.print("Precio: "); double price = input.readDouble();
                    System.out.print("Stock: "); int stock = input.readInt();
                    System.out.print("Longitud (metros): "); double length = input.readDouble();
                    System.out.print("Tipo de conector: "); String connector = input.readLine();

                    accessoryService.registerCable(id, title, price, stock, length, connector);
                    System.out.println("¡Cable registrado exitosamente!");
                }
                case 3 -> {
                    System.out.print("ID: "); String id = input.readLine();
                    System.out.print("Título: "); String title = input.readLine();
                    System.out.print("Precio: "); double price = input.readDouble();
                    System.out.print("Stock: "); int stock = input.readInt();
                    System.out.print("Capacidad (GB): "); int capacity = input.readInt();
                    System.out.print("Tipo de memoria: "); String type = input.readLine();
                    System.out.print("IDs de consolas compatibles separados por coma: ");
                    List<String> consoles = List.of(input.readLine().split(","));

                    accessoryService.registerMemory(id, title, price, stock, capacity, type, consoles);
                    System.out.println("¡Memoria registrada exitosamente!");
                }
                case 4 -> {
                    System.out.println("\n--- LISTADO DE ACCESORIOS ---");
                    for (Accessory a : accessoryService.listAllAccessories()) {
                        System.out.println(a.getId() + " - " + a.getTitle() + " | Precio: $" + a.getPrice() + " | Stock: " + a.getStock());
                    }
                }
                case 5 -> {
                    System.out.print("Ingrese el tipo (CONTROLLER / CABLE / MEMORY): ");
                    String type = input.readLine();
                    for (Accessory a : accessoryService.listAccessoriesByType(type)) {
                        System.out.println(a.getId() + " - " + a.getTitle());
                    }
                }
                case 6 -> {
                    System.out.print("ID de la consola: ");
                    String consoleId = input.readLine();
                    for (Accessory a : accessoryService.findAccessoriesCompatibleWith(consoleId)) {
                        System.out.println(a.getId() + " - " + a.getTitle());
                    }
                }
                case 7 -> {
                    System.out.print("ID del accesorio: "); String id = input.readLine();
                    System.out.print("Cantidad a descontar del stock: "); int qty = input.readInt();
                    accessoryService.updateStock(id, qty);
                    System.out.println("¡Stock del accesorio actualizado exitosamente!");
                }
                case 0 -> {}
                default -> System.out.println("Opción inválida.");
            }
        } while (option != 0);
    }
}