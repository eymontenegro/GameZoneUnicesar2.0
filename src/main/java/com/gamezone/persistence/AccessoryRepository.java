package com.gamezone.persistence;

import com.gamezone.model.Accessory;
import com.gamezone.model.Cable;
import com.gamezone.model.Controller;
import com.gamezone.model.Memory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles saving and loading Accessory objects (Controller, Cable and
 * Memory) to and from a CSV file, so the accessory catalog persists
 * between application runs.
 */
public class AccessoryRepository {

    private static final String FILE_PATH = "data/accessories.csv";
    private static final String CONSOLE_SEPARATOR = "\\|";

    /**
     * Converts a single accessory into one CSV-formatted line, choosing
     * which extra columns to include based on the accessory's real type.
     *
     * @param accessory the accessory to convert (Controller, Cable or Memory)
     * @return a comma-separated line representing the accessory
     * @throws IllegalArgumentException if the accessory type is not recognized
     */
    private String accessoryToCsvLine(Accessory accessory) {
        String commonData = accessory.getId() + "," + accessory.getTitle() + ","
                + accessory.getPrice() + "," + accessory.getStock();

        String type;
        String specificData;

        if (accessory instanceof Controller controller) {
            type = "CONTROLLER";
            List<String> consolesList = controller.getCompatibleConsoleIds();
            String consoles = (consolesList != null) ? String.join("|", consolesList) : "";
            specificData = controller.getConnectionType() + "," + consoles;

        } else if (accessory instanceof Cable cable) {
            type = "CABLE";
            specificData = cable.getLength() + "," + cable.getConnectorType();

        } else if (accessory instanceof Memory memory) {
            type = "MEMORY";
            List<String> consolesList = memory.getCompatibleConsoleIds();
            String consoles = (consolesList != null) ? String.join("|", consolesList) : "";
            specificData = memory.getCapacity() + "," + memory.getType() + "," + consoles;

        } else {
            throw new IllegalArgumentException("Unknown accessory type");
        }

        return type + "," + commonData + "," + specificData;
    }

    /**
     * Saves the full list of accessories to the CSV file, overwriting any previous content.
     *
     * @param accessories the list of accessories to persist
     */
    public void saveAll(List<Accessory> accessories) {
        File file = new File(FILE_PATH);
        if (file.getParentFile() != null) {
            file.getParentFile().mkdirs();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            if (accessories != null) {
                for (Accessory accessory : accessories) {
                    if (accessory != null) {
                        writer.write(accessoryToCsvLine(accessory));
                        writer.newLine();
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error saving accessories: " + e.getMessage(), e);
        }
    }

    /**
     * Reconstructs a single Accessory (Controller, Cable or Memory) from one CSV line.
     *
     * @param line a single line read from the CSV file
     * @return the reconstructed Accessory object, or null if malformed/unrecognized
     */
    private Accessory csvLineToAccessory(String line) {
        try {
            String[] parts = line.split(",", -1);
            if (parts.length < 5) {
                return null;
            }

            String type = parts[0].trim();
            String id = parts[1].trim();
            String title = parts[2].trim();
            double price = Double.parseDouble(parts[3].trim());
            int stock = Integer.parseInt(parts[4].trim());

            if ("CONTROLLER".equalsIgnoreCase(type)) {
                if (parts.length < 6) return null;
                String connectionType = parts[5].trim();
                List<String> consoles = parseConsoles(parts.length > 6 ? parts[6].trim() : "");
                return new Controller(id, title, price, stock, connectionType, consoles);

            } else if ("CABLE".equalsIgnoreCase(type)) {
                if (parts.length < 7) return null;
                double length = Double.parseDouble(parts[5].trim());
                String connectorType = parts[6].trim();
                return new Cable(id, title, price, stock, length, connectorType);

            } else if ("MEMORY".equalsIgnoreCase(type)) {
                if (parts.length < 7) return null;
                int capacity = Integer.parseInt(parts[5].trim());
                String memoryType = parts[6].trim();
                List<String> consoles = parseConsoles(parts.length > 7 ? parts[7].trim() : "");
                return new Memory(id, title, price, stock, capacity, memoryType, consoles);
            }
        } catch (NumberFormatException e) {
            return null; // Skip malformed numerical values defensively
        }

        return null;
    }

    /**
     * Loads the full list of accessories from the CSV file.
     *
     * @return the list of accessories loaded from the file
     */
    public List<Accessory> loadAll() {
        List<Accessory> accessories = new ArrayList<>();
        File file = new File(FILE_PATH);

        if (!file.exists()) {
            return accessories;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                Accessory accessory = csvLineToAccessory(line);
                if (accessory != null) {
                    accessories.add(accessory);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error loading accessories: " + e.getMessage(), e);
        }

        return accessories;
    }

    /**
     * Helper method to parse a pipe-separated string of console IDs into a List.
     *
     * @param field pipe-separated string of console IDs
     * @return a list containing the parsed console IDs
     */
    private List<String> parseConsoles(String field) {
        if (field == null || field.isEmpty()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(List.of(field.split(CONSOLE_SEPARATOR)));
    }
}