package com.gamezone.validation;

import com.gamezone.exceptions.InvalidDataException;
import com.gamezone.exceptions.ResourceNotFoundException;
import com.gamezone.model.Accessory;

/**
 * Centralizes the validation rules for the accessories module.
 * <p>
 * This class only inspects the values it receives: it never accesses
 * files, services or repositories.
 */
public final class AccessoryValidator {

    private AccessoryValidator() {
        // Utility class: no instances.
    }

    /**
     * Validates the data shared by every kind of accessory.
     *
     * @param id    the accessory identifier
     * @param title the accessory title
     * @param price the accessory price
     * @param stock the initial stock
     * @throws InvalidDataException if the id or title is empty, or the price or stock is negative
     */
    public static void validateAccessoryData(String id, String title, double price, int stock) {
        if (id == null || id.trim().isEmpty()) {
            throw new InvalidDataException("identificador", "no puede estar vacío");
        }
        if (title == null || title.trim().isEmpty()) {
            throw new InvalidDataException("título", "no puede estar vacío");
        }
        if (price < 0) {
            throw new InvalidDataException("precio", "no puede ser negativo");
        }
        if (stock < 0) {
            throw new InvalidDataException("stock", "no puede ser negativo");
        }
    }

    /**
     * Validates the data specific to a controller.
     *
     * @param connectionType the connection type of the controller
     * @throws InvalidDataException if the connection type is empty
     */
    public static void validateControllerData(String connectionType) {
        if (connectionType == null || connectionType.trim().isEmpty()) {
            throw new InvalidDataException("tipo de conexión", "no puede estar vacío");
        }
    }

    /**
     * Validates the data specific to a cable.
     *
     * @param length        the cable length
     * @param connectorType the connector type of the cable
     * @throws InvalidDataException if the length is not positive or the connector type is empty
     */
    public static void validateCableData(double length, String connectorType) {
        if (length <= 0) {
            throw new InvalidDataException("longitud del cable", "debe ser mayor que 0");
        }
        if (connectorType == null || connectorType.trim().isEmpty()) {
            throw new InvalidDataException("tipo de conector", "no puede estar vacío");
        }
    }

    /**
     * Validates the data specific to a memory card.
     *
     * @param capacity the capacity in gigabytes
     * @throws InvalidDataException if the capacity is not positive
     */
    public static void validateMemoryData(int capacity) {
        if (capacity <= 0) {
            throw new InvalidDataException("capacidad de la memoria", "debe ser mayor que 0 GB");
        }
    }

    /**
     * Validates that an accessory was found.
     *
     * @param accessory   the accessory returned by a search (may be null)
     * @param accessoryId the identifier that was searched
     * @throws ResourceNotFoundException if the accessory is null
     */
    public static void validateAccessoryExists(Accessory accessory, String accessoryId) {
        if (accessory == null) {
            throw new ResourceNotFoundException("accesorio", accessoryId);
        }
    }
}