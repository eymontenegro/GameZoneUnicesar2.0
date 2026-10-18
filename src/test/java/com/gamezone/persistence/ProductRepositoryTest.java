package com.gamezone.persistence;

import com.gamezone.exceptions.PersistenceException;
import com.gamezone.model.Console;
import com.gamezone.model.Product;
import com.gamezone.model.VideoGame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Tests for {@link ProductRepository}. */
class ProductRepositoryTest {

    @TempDir
    Path tempDir;

    private Path file;
    private ProductRepository repository;

    @BeforeEach
    void setUp() {
        file = tempDir.resolve("products.csv");
        repository = new ProductRepository(file.toString());
    }

    private PersistenceException loadFails() {
        return assertThrows(PersistenceException.class, repository::load);
    }

    private void writeFile(String... lines) throws IOException {
        Files.writeString(file, String.join("\n", lines) + "\n");
    }

    @Test
    @DisplayName("Round trip de videojuego y consola")
    void roundTrip() {
        repository.saveAll(List.of(
                new VideoGame("PS5", "Accion", "T", "P1", "Juego", 100.0, 10),
                new Console("Sony", "PS5", "9", "P2", "Consola", 500.0, 5)));

        List<Product> loaded = repository.load();

        assertEquals(2, loaded.size());
        assertInstanceOf(VideoGame.class, loaded.get(0));
        assertInstanceOf(Console.class, loaded.get(1));
        assertEquals(500.0, loaded.get(1).getPrice());
    }

    @Test
    @DisplayName("Archivo inexistente: lista vacia")
    void missingFileReturnsEmpty() {
        assertTrue(repository.load().isEmpty());
    }

    @Test
    @DisplayName("saveAll rechaza nulos, textos con coma y precios invalidos")
    void saveRejectsInvalidData() {
        assertThrows(IllegalArgumentException.class, () -> repository.saveAll(null));
        List<Product> withNull = new ArrayList<>();
        withNull.add(null);
        assertThrows(IllegalArgumentException.class, () -> repository.saveAll(withNull));
        assertThrows(IllegalArgumentException.class, () -> repository.saveAll(
                List.of(new VideoGame("PS5", "Accion", "T", "P1", "Halo, Infinite", 10.0, 1))));
        assertThrows(IllegalArgumentException.class, () -> repository.saveAll(
                List.of(new VideoGame("PS5", "Accion", "T", "P1", "Juego", Double.NaN, 1))));
    }

    @Test
    @DisplayName("load falla con columnas, tipo, precio y stock invalidos")
    void loadFailsOnCorruptLines() throws IOException {
        writeFile("VIDEOGAME,P1,Juego");
        assertTrue(loadFails().getMessage().contains("linea 1"));
        writeFile("DRON,P1,Juego,10.0,1,a,b,c");
        loadFails();
        writeFile("VIDEOGAME,P1,Juego,barato,1,a,b,c");
        loadFails();
        writeFile("VIDEOGAME,P1,Juego,-10.0,1,a,b,c");
        loadFails();
        writeFile("VIDEOGAME,P1,Juego,10.0,uno,a,b,c");
        loadFails();
        writeFile("VIDEOGAME,P1,Juego,10.0,-1,a,b,c");
        loadFails();
        writeFile("VIDEOGAME,,Juego,10.0,1,a,b,c");
        loadFails();
    }

    @Test
    @DisplayName("Errores de E/S se envuelven en PersistenceException")
    void ioErrorsAreWrapped() throws IOException {
        Path dir = Files.createDirectory(tempDir.resolve("carpeta"));
        ProductRepository repo = new ProductRepository(dir.toString());

        PersistenceException onSave = assertThrows(PersistenceException.class,
                () -> repo.saveAll(List.of(new VideoGame("PS5", "Accion", "T", "P1", "Juego", 1.0, 1))));
        assertInstanceOf(IOException.class, onSave.getCause());

        PersistenceException onLoad = assertThrows(PersistenceException.class, repo::load);
        assertInstanceOf(IOException.class, onLoad.getCause());
    }
}