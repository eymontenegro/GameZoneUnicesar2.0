package com.gamezone.service;

import com.gamezone.persistence.PersonRepository;
import com.gamezone.model.Client;
import com.gamezone.model.Seller;
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
     * Creates the service and loads any previously saved clients and
     * sellers from their files, so the information is available as
     * soon as the application starts.
     */
    public PersonService() {
        this.repository = new PersonRepository();
        this.clients = repository.loadClients();
        this.sellers = repository.loadSellers();
    }

    /**
     * Registers a new client and immediately saves the updated list
     * to the file.
     *
     * @param id the client's identification
     * @param name the client's full name
     * @param phone the client's phone number
     * @param email the client's email
     */
    public void registerClient(String id, String name, String phone, String email) {
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
        for (Client client : clients) {
            if (client.getId().equals(id)) {
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
        for (Seller seller : sellers) {
            if (seller.getId().equals(id)) {
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
        Client client = findClientById(clientId);
        client.addPurchase(saleId);
        repository.saveClients(clients);
    }
}