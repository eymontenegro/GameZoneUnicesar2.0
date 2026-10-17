package com.gamezone.service;

import com.gamezone.persistence.PersonRepository;
import com.gamezone.model.Client;
import com.gamezone.model.Seller;

import java.util.ArrayList;
import java.util.List;

/**
 * Applies the business rules for managing people: registering new
 * clients, listing clients and sellers, and consulting a client's
 * purchase history. Sellers are preloaded and are not registered
 * through this service.
 * Uses PersonRepository to keep the files in sync with every change.
 */
public class PersonService {

    private List<Client> clients;
    private List<Seller> sellers;
    private PersonRepository repository;

    /**
     * Creates the service using a default repository instance and loads
     * any previously saved clients and sellers from their files.
     */
    public PersonService() {
        this(new PersonRepository());
    }

    /**
     * Creates the service using the provided repository instance.
     *
     * @param repository the repository used to load and save clients and sellers
     */
    public PersonService(PersonRepository repository) {
        if (repository == null) {
            throw new IllegalArgumentException("El repositorio no puede ser nulo.");
        }
        this.repository = repository;
        List<Client> loadedClients = repository.loadClients();
        List<Seller> loadedSellers = repository.loadSellers();
        this.clients = (loadedClients != null) ? loadedClients : new ArrayList<>();
        this.sellers = (loadedSellers != null) ? loadedSellers : new ArrayList<>();
    }

    /**
     * Validates client registration parameters and checks for existing duplicates.
     */
    private void validateClientData(String id, String name, String phone, String email) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID del cliente no puede estar vacío.");
        }
        if (existsClientById(id)) {
            throw new IllegalArgumentException("Ya existe un cliente registrado con el ID: " + id);
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del cliente no puede estar vacío.");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("El correo electrónico no puede estar vacío.");
        }
    }

    /**
     * Checks if a client with the given ID already exists.
     */
    public boolean existsClientById(String id) {
        if (id == null) return false;
        for (Client client : clients) {
            if (client != null && id.equalsIgnoreCase(client.getId())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Registers a new client and immediately saves the updated list to the file.
     *
     * @param id the client's identification
     * @param name the client's full name
     * @param phone the client's phone number
     * @param email the client's email
     */
    public void registerClient(String id, String name, String phone, String email) {
        validateClientData(id, name, phone, email);
        Client newClient = new Client(email, id, name, phone);
        clients.add(newClient);
        repository.saveClients(clients);
    }

    /**
     * Returns the full list of registered clients.
     *
     * @return the list of clients
     */
    public List<Client> listClients() {
        return clients;
    }

    /**
     * Returns the full list of registered sellers.
     *
     * @return the list of sellers
     */
    public List<Seller> listSellers() {
        return sellers;
    }

    /**
     * Finds a client by their identification.
     *
     * @param id the id of the client to find
     * @return the client with that id
     * @throws IllegalArgumentException if no client with that id exists
     */
    public Client findClientById(String id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID del cliente no puede ser nulo.");
        }
        for (Client client : clients) {
            if (client != null && id.equalsIgnoreCase(client.getId())) {
                return client;
            }
        }
        throw new IllegalArgumentException("No existe un cliente con el id: " + id);
    }

    /**
     * Finds a seller by their identification.
     *
     * @param id the id of the seller to find
     * @return the seller with that id
     * @throws IllegalArgumentException if no seller with that id exists
     */
    public Seller findSellerById(String id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID del vendedor no puede ser nulo.");
        }
        for (Seller seller : sellers) {
            if (seller != null && id.equalsIgnoreCase(seller.getId())) {
                return seller;
            }
        }
        throw new IllegalArgumentException("No existe un vendedor con el id: " + id);
    }

    /**
     * Registers a completed sale in a client's purchase history and
     * saves the updated list to the file.
     *
     * @param clientId the id of the client who made the purchase
     * @param saleId the id of the sale to register
     */
    public void registerPurchase(String clientId, String saleId) {
        if (saleId == null || saleId.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID de la venta no puede estar vacío.");
        }
        Client client = findClientById(clientId);
        client.addPurchase(saleId);
        repository.saveClients(clients);
    }
}