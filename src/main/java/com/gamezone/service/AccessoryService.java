package com.gamezone.service;

import com.gamezone.exceptions.BusinessRuleException;
import com.gamezone.exceptions.InvalidDataException;
import com.gamezone.exceptions.ResourceNotFoundException;
import com.gamezone.model.Accessory;
import com.gamezone.model.Cable;
import com.gamezone.model.ConsoleCompatible;
import com.gamezone.model.Controller;
import com.gamezone.model.Memory;
import com.gamezone.persistence.AccessoryStorage;
import com.gamezone.validation.AccessoryValidator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Applies the business rules for managing accessories: registering new
 * controllers, cables, and memories, listing them, checking console
 * compatibility, and updating stock. Uses AccessoryStorage to keep
 * persistence synchronized.
 */
public class AccessoryService {

    private final List<Accessory> accessories;
    private final AccessoryStorage repository;

    /**
     * Creates the service using the given storage and loads any
     * previously saved accessories from it.
     *
     * @param repository the storage used to persist accessories
     */
    public AccessoryService(AccessoryStorage repository) {
        this.repository = Objects.requireNonNull(repository, "AccessoryRepository cannot be null.");
        List<Accessory> loaded = repository.loadAll();
        this.accessories = Collections.synchronizedList(loaded != null ? new ArrayList<>(loaded) : new ArrayList<>());
    }

    /**
     * Validates basic inputs (id, title, price, stock) and checks for duplicates.
     *
     * @throws InvalidDataException  if any field is invalid
     * @throws BusinessRuleException if an accessory with the same ID already exists
     */
    private void validateBasicData(String id, String title, double price, int stock) {
        AccessoryValidator.validateAccessoryData(id, title, price, stock);
        if (existsId(id)) {
            throw new BusinessRuleException("Ya existe un accesorio con el ID: " + id);
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
        AccessoryValidator.validateControllerData(connectionType);
        Controller newController = new Controller(id, title, price, stock, connectionType, compatibleConsoleIds);
        saveAccessory(newController);
    }

    public void registerCable(String id, String title, double price, int stock,
                              double length, String connectorType) {
        validateBasicData(id, title, price, stock);
        AccessoryValidator.validateCableData(length, connectorType);
        Cable newCable = new Cable(id, title, price, stock, length, connectorType);
        saveAccessory(newCable);
    }

    public void registerMemory(String id, String title, double price, int stock,
                               int capacity, String type, List<String> compatibleConsoleIds) {
        validateBasicData(id, title, price, stock);
        AccessoryValidator.validateMemoryData(capacity);
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
     *
     * @param id the id of the accessory to find
     * @return the matching accessory
     * @throws InvalidDataException      if id is null or empty
     * @throws ResourceNotFoundException if no accessory with that id exists
     */
    public Accessory findById(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new InvalidDataException("identificador del accesorio", "no puede ser nulo ni vacío");
        }

        Accessory found;
        synchronized (accessories) {
            found = accessories.stream()
                    .filter(Objects::nonNull)
                    .filter(a -> a.getId() != null && a.getId().equalsIgnoreCase(id.trim()))
                    .findFirst()
                    .orElse(null);
        }
        AccessoryValidator.validateAccessoryExists(found, id);
        return found;
    }

    public void restoreStock(String accessoryId, int quantity) {
        if (quantity < 0) {
            throw new InvalidDataException("cantidad", "no puede ser negativa");
        }
        synchronized (accessories) {
            Accessory accessory = findById(accessoryId);
            accessory.increaseStock(quantity);
            repository.saveAll(new ArrayList<>(accessories));
        }
    }

    public void updateStock(String accessoryId, int quantity) {
        if (quantity < 0) {
            throw new InvalidDataException("cantidad", "no puede ser negativa");
        }
        synchronized (accessories) {
            Accessory accessory = findById(accessoryId);
            accessory.reduceStock(quantity);
            repository.saveAll(new ArrayList<>(accessories));
        }
    }
}