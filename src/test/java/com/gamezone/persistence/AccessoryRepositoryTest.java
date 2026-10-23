package com.gamezone.persistence;

import com.gamezone.exceptions.PersistenceException;
import com.gamezone.model.Accessory;
import com.gamezone.model.Cable;
import com.gamezone.model.Controller;
import com.gamezone.model.Memory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static com.gamezone.persistence.CsvTestSupport.assertCorrupt;
import static com.gamezone.persistence.CsvTestSupport.writeFile;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Tests for {@link AccessoryRepository}. */
class AccessoryRepositoryTest {

    @TempDir
    Path tempDir;

    private Path file;
    private AccessoryRepository repository;

    @BeforeEach
    void setUp() {
        file = tempDir.resolve("accessories.csv");
        repository = new AccessoryRepository(file.toString());
    }

    @Test
    @DisplayName("Round trip de mando, cable y memoria")
    void roundTrip() {
        repository.saveAll(List.of(
                new Controller("A1", "Mando", 100.0, 5, "Wireless", List.of("C1", "C2")),
                new Cable("A2", "HDMI", 20.0, 9, 2.5, "HDMI"),
                new Memory("A3", "SD", 60.0, 3, 128, "MicroSD", List.of("C1"))));

        List<Accessory> loaded = repository.loadAll();

        assertEquals(3, loaded.size());
        Controller controller = assertInstanceOf(Controller.class, loaded.get(0));
        assertEquals(List.of("C1", "C2"), controller.getCompatibleConsoleIds());
        assertEquals(2.5, assertInstanceOf(Cable.class, loaded.get(1)).getLength());
        assertEquals(128, assertInstanceOf(Memory.class, loaded.get(2)).getCapacity());
    }

    @Test
    @DisplayName("Controles sin consolas compatibles tambien se guardan y cargan")
    void controllerWithoutConsoles() {
        repository.saveAll(List.of(new Controller("A1", "Mando", 100.0, 5, "Wired", List.of())));

        assertTrue(((Controller) repository.loadAll().get(0)).getCompatibleConsoleIds().isEmpty());
    }

    @Test
    @DisplayName("Archivo inexistente: lista vacia; lineas en blanco ignoradas")
    void missingFileAndBlankLines() throws IOException {
        assertTrue(repository.loadAll().isEmpty());
        writeFile(file, "", "CABLE,A2,HDMI,20.0,9,2.5,HDMI", "  ");
        assertEquals(1, repository.loadAll().size());
    }

    @Test
    @DisplayName("saveAll rechaza nulos, textos con coma, precios y valores invalidos")
    void saveRejectsInvalidData() {
        assertThrows(IllegalArgumentException.class, () -> repository.saveAll(null));
        List<Accessory> withNull = new ArrayList<>();
        withNull.add(null);
        assertThrows(IllegalArgumentException.class, () -> repository.saveAll(withNull));
        assertThrows(IllegalArgumentException.class, () -> repository.saveAll(
                List.of(new Cable("A2", "HDMI, largo", 20.0, 9, 2.5, "HDMI"))));
        assertThrows(IllegalArgumentException.class, () -> repository.saveAll(
                List.of(new Cable("A2", "HDMI", Double.NaN, 9, 2.5, "HDMI"))));
        assertThrows(IllegalArgumentException.class, () -> repository.saveAll(
                List.of(new Controller("A1", "Mando", 1.0, 1, "Wireless", List.of("C|1")))));
        assertThrows(IllegalArgumentException.class, () -> repository.saveAll(
                List.of(new Memory("A3", "SD", 1.0, 1, 0, "MicroSD", List.of()))));
    }

    @Test
    @DisplayName("Un accesorio invalido no destruye el archivo existente")
    void invalidAccessoryLeavesFileUntouched() throws IOException {
        repository.saveAll(List.of(new Cable("A2", "HDMI", 20.0, 9, 2.5, "HDMI")));
        String before = Files.readString(file);

        assertThrows(IllegalArgumentException.class, () -> repository.saveAll(List.of(
                new Cable("A3", "Otro", 20.0, 9, 2.5, "HDMI"),
                new Cable("A,4", "Malo", 20.0, 9, 2.5, "HDMI"))));

        assertEquals(before, Files.readString(file));
    }

    @Test
    @DisplayName("load falla con columnas, tipo, numeros y valores del modelo invalidos")
    void loadFailsOnCorruptLines() throws IOException {
        writeFile(file, "CABLE,A2,HDMI");
        assertCorrupt(repository::loadAll, file, 1, "se esperaban al menos 5 columnas pero hay 3");
        writeFile(file, "DRON,A2,HDMI,20.0,9,2.5,HDMI");
        assertCorrupt(repository::loadAll, file, 1, "tipo de accesorio desconocido: 'DRON'");
        writeFile(file, "CABLE,A2,HDMI,barato,9,2.5,HDMI");
        assertCorrupt(repository::loadAll, file, 1, "el campo 'price' no es un numero valido: 'barato'");
        writeFile(file, "CABLE,A2,HDMI,-20.0,9,2.5,HDMI");
        assertCorrupt(repository::loadAll, file, 1, "el precio no puede ser negativo: -20.0");
        writeFile(file, "CABLE,A2,HDMI,20.0,-9,2.5,HDMI");
        assertCorrupt(repository::loadAll, file, 1, "el stock no puede ser negativo: -9");
        writeFile(file, "CABLE,A2,HDMI,20.0,9,0,HDMI");
        assertCorrupt(repository::loadAll, file, 1, "valores invalidos");
        writeFile(file, "CABLE,A2,HDMI,20.0,9,2.5");
        assertCorrupt(repository::loadAll, file, 1, "se esperaban 7 columnas pero hay 6");
        writeFile(file, "CABLE,,HDMI,20.0,9,2.5,HDMI");
        assertCorrupt(repository::loadAll, file, 1, "el campo 'id' esta vacio");
        writeFile(file, "MEMORY,A3,SD,60.0,3,mucha,MicroSD,C1");
        assertCorrupt(repository::loadAll, file, 1, "el campo 'capacity' no es un entero valido: 'mucha'");
        writeFile(file, "MEMORY,A3,SD,60.0,3,-5,MicroSD,C1");
        assertCorrupt(repository::loadAll, file, 1, "la capacidad debe ser mayor que cero: -5");
        writeFile(file, "CONTROLLER,A1,Mando,100.0,5,Wireless,C1||C2");
        assertCorrupt(repository::loadAll, file, 1, "el campo 'compatibleConsoleId' esta vacio");
    }

    @Test
    @DisplayName("Errores de E/S se envuelven en PersistenceException")
    void ioErrorsAreWrapped() throws IOException {
        Path dir = Files.createDirectory(tempDir.resolve("carpeta"));
        AccessoryRepository repo = new AccessoryRepository(dir.toString());

        PersistenceException onSave = assertThrows(PersistenceException.class,
                () -> repo.saveAll(List.of(new Cable("A2", "HDMI", 20.0, 9, 2.5, "HDMI"))));
        assertInstanceOf(IOException.class, onSave.getCause());

        assertInstanceOf(IOException.class, assertThrows(PersistenceException.class, repo::loadAll).getCause());
    }
}