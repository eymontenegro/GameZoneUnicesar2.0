package com.gamezone.service;

import com.gamezone.model.Accessory;
import com.gamezone.model.Cable;
import com.gamezone.model.ConsoleCompatible;
import com.gamezone.model.Controller;
import com.gamezone.model.Memory;
import com.gamezone.persistence.AccessoryRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Applies the business rules for managing accessories: registering new
 * controllers, cables, and memories, listing them, checking console
 * compatibility, and updating stock. Uses AccessoryRepository to keep
 * persistence synchronized.
 */
public class AccessoryService {

    private final List<Accessory> accessories;
    private final AccessoryRepository repository;

    /**
     * Creates the service using the given repository and loads any
     * previously saved accessories from storage.
     *
     * @param repository the repository used to persist accessories
     */
    public AccessoryService(AccessoryRepository repository) {
        this.repository = Objects.requireNonNull(repository, "AccessoryRepository cannot be null.");
        List<Accessory> loaded = repository.loadAll();
        this.accessories = Collections.synchronizedList(loaded != null ? new ArrayList<>(loaded) : new ArrayList<>());
    }

    /**
     * Validates basic inputs (id, title, price, stock) and checks for duplicates.
     */
    private void validateBasicData(String id, String title, double price, int stock) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Accessory ID cannot be empty.");
        }
        if (existsId(id)) {
            throw new IllegalArgumentException("An accessory with ID already exists: " + id);
        }
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Accessory title cannot be empty.");
        }
        if (price < 0) {
            throw new IllegalArgumentException("Price cannot be negative.");
        }
        if (stock < 0) {
            throw new IllegalArgumentException("Stock cannot be negative.");
        }
    }

    /**
     * Checks if an accessory with the given ID already exists (case-insensitive).
     */
    private boolean existsId(String id) {
        synchronized (accessories) {
            return accessories.stream()
                    .filter(Objects::nonNull)
                    .anyMatch(a -> a.getId() != null && a.getId().equalsIgnoreCase(id.trim()));
        }
    }

    public void registerController(String id, String title, double price, int stock,
                                   String connectionType, List<String> compatibleConsoleIds) {
        validateBasicData(id, title, price, stock);
        if (connectionType == null || connectionType.trim().isEmpty()) {
            throw new IllegalArgumentException("Connection type cannot be empty.");
        }
        Controller newController = new Controller(id, title, price, stock, connectionType, compatibleConsoleIds);
        saveAccessory(newController);
    }

    public void registerCable(String id, String title, double price, int stock,
                              double length, String connectorType) {
        validateBasicData(id, title, price, stock);
        if (length <= 0) {
            throw new IllegalArgumentException("Cable length must be greater than 0.");
        }
        if (connectorType == null || connectorType.trim().isEmpty()) {
            throw new IllegalArgumentException("Connector type cannot be empty.");
        }
        Cable newCable = new Cable(id, title, price, stock, length, connectorType);
        saveAccessory(newCable);
    }

    public void registerMemory(String id, String title, double price, int stock,
                               int capacity, String type, List<String> compatibleConsoleIds) {
        validateBasicData(id, title, price, stock);
        if (capacity <= 0) {
            throw new IllegalArgumentException("Memory capacity must be greater than 0 GB.");
        }
        Memory newMemory = new Memory(id, title, price, stock, capacity, type, compatibleConsoleIds);
        saveAccessory(newMemory);
    }

    private void saveAccessory(Accessory accessory) {
        synchronized (accessories) {
            accessories.add(accessory);
            repository.saveAll(new ArrayList<>(accessories));
        }
    }

    /**
     * Returns a copy of the list of currently available accessories.
     */
    public List<Accessory> listAllAccessories() {
        synchronized (accessories) {
            return new ArrayList<>(accessories);
        }
    }

    /**
     * Returns the accessories that match a given type.
     *
     * @param type "CONTROLLER", "CABLE" or "MEMORY" (case-insensitive)
     * @return the list of accessories of that type
     */
    public List<Accessory> listAccessoriesByType(String type) {
        if (type == null || type.trim().isEmpty()) {
            return Collections.emptyList();
        }

        synchronized (accessories) {
            return accessories.stream()
                    .filter(Objects::nonNull)
                    .filter(a -> matchesType(a, type.trim()))
                    .collect(Collectors.toList());
        }
    }

    private boolean matchesType(Accessory accessory, String type) {
        if ("CONTROLLER".equalsIgnoreCase(type)) {
            return accessory instanceof Controller;
        } else if ("CABLE".equalsIgnoreCase(type)) {
            return accessory instanceof Cable;
        } else if ("MEMORY".equalsIgnoreCase(type)) {
            return accessory instanceof Memory;
        }
        return false;
    }

    /**
     * Finds the accessories compatible with a given console.
     */
    public List<Accessory> findAccessoriesCompatibleWith(String consoleId) {
        if (consoleId == null || consoleId.trim().isEmpty()) {
            return Collections.emptyList();
        }

        synchronized (accessories) {
            return accessories.stream()
                    .filter(Objects::nonNull)
                    .filter(ConsoleCompatible.class::isInstance)
                    .map(ConsoleCompatible.class::cast)
                    .filter(c -> c.isCompatibleWith(consoleId))
                    .map(c -> (Accessory) c)
                    .collect(Collectors.toList());
        }
    }

    /**
     * Finds a single accessory by its ID.
     */
    public Accessory findById(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Accessory ID cannot be null or empty.");
        }

        synchronized (accessories) {
            return accessories.stream()
                    .filter(Objects::nonNull)
                    .filter(a -> a.getId() != null && a.getId().equalsIgnoreCase(id.trim()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Unknown accessory ID: " + id));
        }
    }

    public void restoreStock(String accessoryId, int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Restoration quantity cannot be negative.");
        }
        synchronized (accessories) {
            Accessory accessory = findById(accessoryId);
            accessory.increaseStock(quantity);
            repository.saveAll(new ArrayList<>(accessories));
        }
    }

    public void updateStock(String accessoryId, int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative.");
        }
        synchronized (accessories) {
            Accessory accessory = findById(accessoryId);
            accessory.reduceStock(quantity);
            repository.saveAll(new ArrayList<>(accessories));
        }
    }
}