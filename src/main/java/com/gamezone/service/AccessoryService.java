package com.gamezone.service;

import com.gamezone.persistence.AccessoryRepository;
import com.gamezone.model.Accessory;
import com.gamezone.model.Controller;
import com.gamezone.model.Cable;
import com.gamezone.model.Memory;
import com.gamezone.model.ConsoleCompatible;
import java.util.List;
import java.util.ArrayList;

/**
 * Applies the business rules for managing accessories: registering new
 * controllers, cables and memories, listing them, checking console
 * compatibility, and updating stock. Uses AccessoryRepository to keep
 * the file in sync with every change.
 */
public class AccessoryService {
    private List<Accessory> accessories;
    private AccessoryRepository repository;

    /**
     * Creates the service using the given repository and loads any
     * previously saved accessories from the file, so the inventory
     * is available as soon as the application starts.
     *
     * @param repository the repository used to persist accessories
     */
    public AccessoryService(AccessoryRepository repository) {
        this.repository = repository;
        this.accessories = repository.loadAll();
    }

    /**
     * Registers a new controller and immediately saves the updated
     * inventory to the file.
     *
     * @param id the accessory identifier
     * @param title the accessory title
     * @param price the accessory price
     * @param stock the initial available stock
     * @param connectionType the type of connection ("Wireless" or "Wired")
     * @param compatibleConsoleIds the consoles this controller works with
     */
    public void registerController(String id, String title, double price, int stock,
                                    String connectionType, List<String> compatibleConsoleIds) {
        Controller newController = new Controller(id, title, price, stock, connectionType, compatibleConsoleIds);
        accessories.add(newController);
        repository.saveAll(accessories);
    }

    /**
     * Registers a new cable and immediately saves the updated
     * inventory to the file.
     *
     * @param id the accessory identifier
     * @param title the accessory title
     * @param price the accessory price
     * @param stock the initial available stock
     * @param length the cable length in meters
     * @param connectorType the type of connector (HDMI, USB, optical, etc.)
     */
    public void registerCable(String id, String title, double price, int stock,
                               double length, String connectorType) {
        Cable newCable = new Cable(id, title, price, stock, length, connectorType);
        accessories.add(newCable);
        repository.saveAll(accessories);
    }

    /**
     * Registers a new memory and immediately saves the updated
     * inventory to the file.
     *
     * @param id the accessory identifier
     * @param title the accessory title
     * @param price the accessory price
     * @param stock the initial available stock
     * @param capacity the storage capacity in gigabytes
     * @param type the type of memory (SD, microSD, proprietary card, etc.)
     * @param compatibleConsoleIds the consoles this memory works with
     */
    public void registerMemory(String id, String title, double price, int stock,
                                int capacity, String type, List<String> compatibleConsoleIds) {
        Memory newMemory = new Memory(id, title, price, stock, capacity, type, compatibleConsoleIds);
        accessories.add(newMemory);
        repository.saveAll(accessories);
    }

    /**
     * Returns the full list of currently available accessories.
     *
     * @return the list of accessories in inventory
     */
    public List<Accessory> listAllAccessories() {
        return accessories;
    }

    /**
     * Returns the accessories that match a given type.
     *
     * @param type "CONTROLLER", "CABLE" or "MEMORY" (case-insensitive)
     * @return the list of accessories of that type
     */
    public List<Accessory> listAccessoriesByType(String type) {
        List<Accessory> result = new ArrayList<>();
        for (Accessory accessory : accessories) {
            if (type.equalsIgnoreCase("CONTROLLER") && accessory instanceof Controller) {
                result.add(accessory);
            } else if (type.equalsIgnoreCase("CABLE") && accessory instanceof Cable) {
                result.add(accessory);
            } else if (type.equalsIgnoreCase("MEMORY") && accessory instanceof Memory) {
                result.add(accessory);
            }
        }
        return result;
    }

    /**
     * Finds the accessories compatible with a given console. Only
     * accessories that implement ConsoleCompatible (Controller and
     * Memory) are considered; a Cable never matches.
     *
     * @param consoleId the id of the console to check
     * @return the list of compatible accessories
     */
    public List<Accessory> findAccessoriesCompatibleWith(String consoleId) {
        List<Accessory> result = new ArrayList<>();
        for (Accessory accessory : accessories) {
            if (accessory instanceof ConsoleCompatible) {
                ConsoleCompatible compatible = (ConsoleCompatible) accessory;
                if (compatible.isCompatibleWith(consoleId)) {
                    result.add(accessory);
                }
            }
        }
        return result;
    }

    /**
     * Finds a single accessory by its id.
     *
     * @param id the id of the accessory to find
     * @return the matching accessory
     * @throws IllegalArgumentException if no accessory with that id exists
     */
    public Accessory findById(String id) {
        for (Accessory accessory : accessories) {
            if (accessory.getId().equals(id)) {
                return accessory;
            }
        }
        throw new IllegalArgumentException("Unknown accessory id: " + id);
    }

    /**
     * Reduces the stock of a specific accessory, identified by its id,
     * and saves the updated inventory to the file.
     *
     * @param accessoryId the id of the accessory to update
     * @param quantity the quantity to subtract from the accessory's stock
     * @throws IllegalArgumentException if no accessory with that id exists
     */
    public void updateStock(String accessoryId, int quantity) {
        for (Accessory accessory : accessories) {
            if (accessory.getId().equals(accessoryId)) {
                accessory.reduceStock(quantity);
                repository.saveAll(accessories);
                return;
            }
        }
        throw new IllegalArgumentException("Unknown accessory id: " + accessoryId);
    }
}