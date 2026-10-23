package com.gamezone.persistence;

import com.gamezone.exceptions.PersistenceException;
import com.gamezone.model.Accessory;
import com.gamezone.model.Cable;
import com.gamezone.model.Controller;
import com.gamezone.model.Memory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles saving and loading Accessory objects (Controller, Cable and
 * Memory) to and from a CSV file, so the accessory catalog persists
 * between application runs.
 *
 * <p>Every low-level {@link IOException} is wrapped in a
 * {@link PersistenceException}; corrupt file content is reported with the
 * file name and line number instead of being skipped silently.</p>
 */
public class AccessoryRepository {

    private static final String DEFAULT_FILE = "data/accessories.csv";
    private static final String CONSOLE_SEPARATOR = "\\|";

    private final String filePath;

    /**
     * Creates a repository that uses the default file {@code data/accessories.csv}.
     */
    public AccessoryRepository() {
        this(DEFAULT_FILE);
    }

    /**
     * Creates a repository that uses the given file (useful for tests).
     *
     * @param filePath path of the CSV file
     * @throws IllegalArgumentException if the path is null or blank
     */
    public AccessoryRepository(String filePath) {
        if (filePath == null || filePath.isBlank()) {
            throw new IllegalArgumentException("La ruta del archivo de accesorios no puede estar vacia");
        }
        this.filePath = filePath;
    }

    /**
     * Joins compatible console ids with a pipe, checking each one is safe to store.
     *
     * @param consoleIds the ids to join (may be null)
     * @return the pipe-separated text
     * @throws IllegalArgumentException if an id cannot be stored safely
     */
    private String joinConsoles(List<String> consoleIds) {
        if (consoleIds == null) {
            return "";
        }
        List<String> safeIds = new ArrayList<>();
        for (String consoleId : consoleIds) {
            safeIds.add(CsvSupport.safe(consoleId, "compatibleConsoleId", "|"));
        }
        return String.join("|", safeIds);
    }

    /**
     * Converts a single accessory into one CSV-formatted line, choosing
     * which extra columns to include based on the accessory's real type.
     *
     * @param accessory the accessory to convert (Controller, Cable or Memory)
     * @return a comma-separated line representing the accessory
     * @throws IllegalArgumentException if the accessory type is not recognized
     *         or any of its values cannot be stored safely
     */
    private String accessoryToCsvLine(Accessory accessory) {
        CsvSupport.requireValidPrice(accessory.getPrice(), "price");
        if (accessory.getStock() < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo: " + accessory.getStock());
        }
        String commonData = CsvSupport.safe(accessory.getId(), "id") + ","
                + CsvSupport.safe(accessory.getTitle(), "title") + ","
                + accessory.getPrice() + "," + accessory.getStock();

        String type;
        String specificData;

        if (accessory instanceof Controller controller) {
            type = "CONTROLLER";
            specificData = CsvSupport.safe(controller.getConnectionType(), "connectionType") + ","
                    + joinConsoles(controller.getCompatibleConsoleIds());

        } else if (accessory instanceof Cable cable) {
            type = "CABLE";
            if (Double.isNaN(cable.getLength()) || Double.isInfinite(cable.getLength()) || cable.getLength() <= 0) {
                throw new IllegalArgumentException("La longitud del cable debe ser un numero finito mayor que cero: "
                        + cable.getLength());
            }
            specificData = cable.getLength() + "," + CsvSupport.safe(cable.getConnectorType(), "connectorType");

        } else if (accessory instanceof Memory memory) {
            type = "MEMORY";
            if (memory.getCapacity() <= 0) {
                throw new IllegalArgumentException("La capacidad debe ser mayor que cero: " + memory.getCapacity());
            }
            specificData = memory.getCapacity() + "," + CsvSupport.safe(memory.getType(), "memoryType") + ","
                    + joinConsoles(memory.getCompatibleConsoleIds());

        } else {
            throw new IllegalArgumentException("Unknown accessory type");
        }

        return type + "," + commonData + "," + specificData;
    }

    /**
     * Saves the full list of accessories to the CSV file, overwriting any previous content.
     * All accessories are validated and converted before the file is opened, so an
     * invalid accessory never leaves a truncated file behind.
     *
     * @param accessories the list of accessories to persist
     * @throws IllegalArgumentException if the list or any accessory is null, of an
     *         unknown type, or has values that cannot be stored
     * @throws PersistenceException if an I/O error occurs while writing
     */
    public void saveAll(List<Accessory> accessories) {
        if (accessories == null) {
            throw new IllegalArgumentException("La lista de accesorios no puede ser nula");
        }
        List<String> lines = new ArrayList<>();
        for (Accessory accessory : accessories) {
            if (accessory == null) {
                throw new IllegalArgumentException("La lista de accesorios contiene un elemento nulo");
            }
            lines.add(accessoryToCsvLine(accessory));
        }

        File file = new File(filePath);
        CsvSupport.ensureParentDirectory(file, "los accesorios");

        CsvSupport.writeLines(filePath, lines, "accesorio(s)");
    }

    /**
     * Reconstructs a single Accessory (Controller, Cable or Memory) from one CSV line.
     *
     * @param line a single line read from the CSV file
     * @param lineNumber the 1-based number of the line, used in error messages
     * @return the reconstructed Accessory object
     * @throws PersistenceException if the line is malformed
     */
    private Accessory csvLineToAccessory(String line, int lineNumber) {
        String[] parts = line.split(",", -1);
        if (parts.length < 5) {
            throw CsvSupport.corruptLine(filePath, lineNumber,
                    "se esperaban al menos 5 columnas pero hay " + parts.length, null);
        }

        String type = parts[0].trim();
        String id = parts[1].trim();
        String title = parts[2].trim();
        CsvSupport.requireNotBlank(id, "id", filePath, lineNumber);
        CsvSupport.requireNotBlank(title, "title", filePath, lineNumber);

        double price = CsvSupport.parseDouble(parts[3], "price", filePath, lineNumber);
        int stock = CsvSupport.parseInt(parts[4], "stock", filePath, lineNumber);
        if (price < 0) {
            throw CsvSupport.corruptLine(filePath, lineNumber, "el precio no puede ser negativo: " + price, null);
        }
        if (stock < 0) {
            throw CsvSupport.corruptLine(filePath, lineNumber, "el stock no puede ser negativo: " + stock, null);
        }

        try {
            if ("CONTROLLER".equalsIgnoreCase(type)) {
                CsvSupport.requireColumnsBetween(parts, 6, 7, filePath, lineNumber);
                String connectionType = parts[5].trim();
                CsvSupport.requireNotBlank(connectionType, "connectionType", filePath, lineNumber);
                List<String> consoles = parseConsoles(parts.length > 6 ? parts[6].trim() : "", lineNumber);
                return new Controller(id, title, price, stock, connectionType, consoles);

            } else if ("CABLE".equalsIgnoreCase(type)) {
                CsvSupport.requireColumns(parts, 7, filePath, lineNumber);
                double length = CsvSupport.parseDouble(parts[5], "length", filePath, lineNumber);
                String connectorType = parts[6].trim();
                CsvSupport.requireNotBlank(connectorType, "connectorType", filePath, lineNumber);
                return new Cable(id, title, price, stock, length, connectorType);

            } else if ("MEMORY".equalsIgnoreCase(type)) {
                CsvSupport.requireColumnsBetween(parts, 7, 8, filePath, lineNumber);
                int capacity = CsvSupport.parseInt(parts[5], "capacity", filePath, lineNumber);
                if (capacity <= 0) {
                    throw CsvSupport.corruptLine(filePath, lineNumber,
                            "la capacidad debe ser mayor que cero: " + capacity, null);
                }
                String memoryType = parts[6].trim();
                CsvSupport.requireNotBlank(memoryType, "memoryType", filePath, lineNumber);
                List<String> consoles = parseConsoles(parts.length > 7 ? parts[7].trim() : "", lineNumber);
                return new Memory(id, title, price, stock, capacity, memoryType, consoles);
            }
        } catch (IllegalArgumentException | NullPointerException e) {
            throw CsvSupport.corruptLine(filePath, lineNumber, "valores invalidos: " + e.getMessage(), e);
        }

        throw CsvSupport.corruptLine(filePath, lineNumber, "tipo de accesorio desconocido: '" + type + "'", null);
    }

    /**
     * Loads the full list of accessories from the CSV file. If the file does
     * not exist yet (first run), returns an empty list. Blank lines are ignored.
     *
     * @return the list of accessories loaded from the file
     * @throws PersistenceException if an I/O error occurs while reading, or
     *         the file contains a malformed line
     */
    public List<Accessory> loadAll() {
        return CsvSupport.readLines(filePath, "los accesorios", this::csvLineToAccessory);
    }

    /**
     * Parses a pipe-separated string of console IDs into a List.
     *
     * @param field pipe-separated string of console IDs
     * @param lineNumber the 1-based line number, used in error messages
     * @return a list containing the parsed console IDs
     * @throws PersistenceException if the text contains an empty id
     */
    private List<String> parseConsoles(String field, int lineNumber) {
        if (field == null || field.isEmpty()) {
            return new ArrayList<>();
        }
        List<String> ids = new ArrayList<>();
        for (String id : field.split(CONSOLE_SEPARATOR, -1)) {
            CsvSupport.requireNotBlank(id, "compatibleConsoleId", filePath, lineNumber);
            ids.add(id.trim());
        }
        return ids;
    }
}