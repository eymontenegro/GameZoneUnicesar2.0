package com.gamezone.persistence;

import com.gamezone.model.Accessory;
import com.gamezone.model.Controller;
import com.gamezone.model.Cable;
import com.gamezone.model.Memory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.io.File;

/**
 * Handles saving and loading Accessory objects (Controller, Cable and
 * Memory) to and from a CSV file, so the accessory catalog persists
 * between application runs.
 */
public class AccessoryRepository {

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

        if (accessory instanceof Controller) {
            Controller controller = (Controller) accessory;
            type = "CONTROLLER";
            String consoles = String.join("|", controller.getCompatibleConsoleIds());
            specificData = controller.getConnectionType() + "," + consoles;

        } else if (accessory instanceof Cable) {
            Cable cable = (Cable) accessory;
            type = "CABLE";
            specificData = cable.getLength() + "," + cable.getConnectorType();

        } else if (accessory instanceof Memory) {
            Memory memory = (Memory) accessory;
            type = "MEMORY";
            String consoles = String.join("|", memory.getCompatibleConsoleIds());
            specificData = memory.getCapacity() + "," + memory.getType() + "," + consoles;

        } else {
            throw new IllegalArgumentException("Unknown accessory type");
        }

        return type + "," + commonData + "," + specificData;
    }

    /**
     * Saves the full list of accessories to the CSV file, overwriting
     * any previous content.
     *
     * @param accessories the list of accessories to persist
     * @throws RuntimeException if an I/O error occurs while writing
     */
    public void saveAll(List<Accessory> accessories) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("data/accessories.csv"))) {
            for (Accessory accessory : accessories) {
                writer.write(accessoryToCsvLine(accessory));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error saving accessories: " + e.getMessage(), e);
        }
    }

    /**
     * Reconstructs a single Accessory (Controller, Cable or Memory) from
     * one CSV-formatted line, based on the type indicated in the first
     * column.
     *
     * @param line a single line read from the CSV file
     * @return the reconstructed Accessory object
     * @throws IllegalArgumentException if the accessory type is not recognized
     */
    private Accessory csvLineToAccessory(String line) {
        String[] parts = line.split(",");
        String type = parts[0];
        String id = parts[1];
        String title = parts[2];
        double price = Double.parseDouble(parts[3]);
        int stock = Integer.parseInt(parts[4]);

        if (type.equals("CONTROLLER")) {
            String connectionType = parts[5];
            List<String> consoles = parseConsoles(parts.length > 6 ? parts[6] : "");
            return new Controller(id, title, price, stock, connectionType, consoles);

        } else if (type.equals("CABLE")) {
            double length = Double.parseDouble(parts[5]);
            String connectorType = parts[6];
            return new Cable(id, title, price, stock, length, connectorType);

        } else if (type.equals("MEMORY")) {
            int capacity = Integer.parseInt(parts[5]);
            String memoryType = parts[6];
            List<String> consoles = parseConsoles(parts.length > 7 ? parts[7] : "");
            return new Memory(id, title, price, stock, capacity, memoryType, consoles);

        } else {
            throw new IllegalArgumentException("Unknown accessory type: " + type);
        }
    }

    /**
     * Loads the full list of accessories from the CSV file. If the file
     * does not exist yet (first run), returns an empty list instead
     * of failing.
     *
     * @return the list of accessories loaded from the file
     * @throws RuntimeException if an I/O error occurs while reading
     */
    public List<Accessory> loadAll() {
        List<Accessory> accessories = new ArrayList<>();
        File file = new File("data/accessories.csv");

        if (!file.exists()) {
            return accessories;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                accessories.add(csvLineToAccessory(line));
            }
        } catch (IOException e) {
            throw new RuntimeException("Error loading accessories: " + e.getMessage(), e);
        }

        return accessories;
    }

    private List<String> parseConsoles(String field) {
        if (field == null || field.isEmpty()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(List.of(field.split(CONSOLE_SEPARATOR)));
    }
}