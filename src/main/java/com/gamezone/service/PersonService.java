package com.gamezone.service;

import com.gamezone.exceptions.BusinessRuleException;
import com.gamezone.exceptions.InvalidDataException;
import com.gamezone.exceptions.ResourceNotFoundException;
import com.gamezone.persistence.PersonRepository;
import com.gamezone.validation.PersonValidator;
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
     * Creates the service using the provided repository instance.
     *
     * @param repository the repository used to load and save clients and sellers
     */
    public PersonService(PersonRepository repository) {
        if (repository == null) {
            throw new InvalidDataException("repositorio", "no puede ser nulo");
        }
        this.repository = repository;
        List<Client> loadedClients = repository.loadClients();
        List<Seller> loadedSellers = repository.loadSellers();
        this.clients = (loadedClients != null) ? loadedClients : new ArrayList<>();
        this.sellers = (loadedSellers != null) ? loadedSellers : new ArrayList<>();
    }

    /**
     * Validates client registration parameters and checks for existing duplicates.
     *
     * @param id    the client's identification
     * @param name  the client's full name
     * @param phone the client's phone number
     * @param email the client's email
     * @throws InvalidDataException  if a required field is empty or the email is malformed
     * @throws BusinessRuleException if a client with the same ID already exists
     */
    private void validateClientData(String id, String name, String phone, String email) {
        PersonValidator.validatePersonData(id, name, email, phone);
        PersonValidator.validateUniquePerson(clients, id);
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
     * @throws InvalidDataException      if id is null
     * @throws ResourceNotFoundException if no client with that id exists
     */
    public Client findClientById(String id) {
        if (id == null) {
            throw new InvalidDataException("identificador del cliente", "no puede ser nulo");
        }
        Client found = null;
        for (Client client : clients) {
            if (client != null && id.equalsIgnoreCase(client.getId())) {
                found = client;
                break;
            }
        }
        PersonValidator.validatePersonExists(found, id);
        return found;
    }

    /**
     * Finds a seller by their identification.
     *
     * @param id the id of the seller to find
     * @return the seller with that id
     * @throws InvalidDataException      if id is null
     * @throws ResourceNotFoundException if no seller with that id exists
     */
    public Seller findSellerById(String id) {
        if (id == null) {
            throw new InvalidDataException("identificador del vendedor", "no puede ser nulo");
        }
        Seller found = null;
        for (Seller seller : sellers) {
            if (seller != null && id.equalsIgnoreCase(seller.getId())) {
                found = seller;
                break;
            }
        }
        PersonValidator.validatePersonExists(found, id);
        return found;
    }

    /**
     * Registers a completed sale in a client's purchase history and
     * saves the updated list to the file.
     *
     * @param clientId the id of the client who made the purchase
     * @param saleId the id of the sale to register
     * @throws InvalidDataException      if saleId is null or empty
     * @throws ResourceNotFoundException if the client does not exist
     */
    public void registerPurchase(String clientId, String saleId) {
        if (saleId == null || saleId.trim().isEmpty()) {
            throw new InvalidDataException("identificador de la venta", "no puede ser nulo ni vacío");
        }
        Client client = findClientById(clientId);
        client.addPurchase(saleId);
        repository.saveClients(clients);
    }
}