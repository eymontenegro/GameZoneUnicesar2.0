package com.gamezone.ui;

import com.gamezone.model.Client;
import com.gamezone.model.Seller;
import com.gamezone.service.PersonService;

/**
 * Handles the clients and sellers management sub-menu.
 */
public class PersonSubMenu {

    private final PersonService personService;
    private final ConsoleInput input;

    /**
     * @param personService service for managing clients and sellers
     * @param input the shared console reader
     */
    public PersonSubMenu(PersonService personService, ConsoleInput input) {
        this.personService = personService;
        this.input = input;
    }

    /**
     * Displays and manages the clients and sellers management sub-menu.
     */
    public void show() {
        int option;
        do {
            System.out.println("\n--- GESTIÓN DE CLIENTES Y VENDEDORES ---");
            System.out.println("1. Registrar cliente");
            System.out.println("2. Listar clientes");
            System.out.println("3. Listar vendedores");
            System.out.println("0. Volver");
            System.out.print("Seleccione una opción: ");
            option = input.readInt();

            switch (option) {
                case 1 -> {
                    System.out.print("ID: "); String id = input.readLine();
                    System.out.print("Nombre completo: "); String name = input.readLine();
                    System.out.print("Teléfono: "); String phone = input.readLine();
                    System.out.print("Correo electrónico: "); String email = input.readLine();

                    personService.registerClient(id, name, phone, email);
                    System.out.println("¡Cliente registrado exitosamente!");
                }
                case 2 -> {
                    System.out.println("\n--- LISTADO DE CLIENTES ---");
                    for (Client c : personService.listClients()) {
                        System.out.println(c.getId() + " - " + c.getName() + " | Correo: " + c.getEmail() + " | Compras realizadas: " + c.getPurchaseHistory().size());
                    }
                }
                case 3 -> {
                    System.out.println("\n--- LISTADO DE VENDEDORES ---");
                    for (Seller s : personService.listSellers()) {
                        System.out.println(s.getId() + " - " + s.getName() + " | Turno: " + s.getShift());
                    }
                }
                case 0 -> {}
                default -> System.out.println("Opción inválida.");
            }
        } while (option != 0);
    }
}