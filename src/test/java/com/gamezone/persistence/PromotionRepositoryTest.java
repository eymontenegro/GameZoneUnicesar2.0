package com.gamezone.persistence;

import com.gamezone.exceptions.PersistenceException;
import com.gamezone.model.BulkPurchaseDiscount;
import com.gamezone.model.CategoryDiscount;
import com.gamezone.model.PercentageDiscount;
import com.gamezone.model.Promotion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Tests for {@link PromotionRepository}. */
class PromotionRepositoryTest {

    private static final LocalDate START = LocalDate.of(2026, 1, 1);
    private static final LocalDate END = LocalDate.of(2026, 12, 31);

    @TempDir
    Path tempDir;

    private Path file;
    private PromotionRepository repository;

    @BeforeEach
    void setUp() {
        file = tempDir.resolve("promotions.csv");
        repository = new PromotionRepository(file.toString());
    }

    private PersistenceException loadFails() {
        return assertThrows(PersistenceException.class, repository::loadAll);
    }

    private void writeFile(String... lines) throws IOException {
        Files.writeString(file, String.join("\n", lines) + "\n");
    }

    @Test
    @DisplayName("Round trip de los tres tipos de promocion")
    void roundTrip() {
        repository.saveAll(List.of(
                new PercentageDiscount("P1", "General", START, END, 15.0),
                new CategoryDiscount("P2", "Juegos", START, END, 20.0, "VIDEOGAME"),
                new BulkPurchaseDiscount("P3", "Volumen", START, END, 3, 10.0)));

        List<Promotion> loaded = repository.loadAll();

        assertEquals(3, loaded.size());
        assertEquals(15.0, assertInstanceOf(PercentageDiscount.class, loaded.get(0)).getPercentage());
        assertEquals("VIDEOGAME", assertInstanceOf(CategoryDiscount.class, loaded.get(1)).getTargetCategory());
        assertEquals(3, assertInstanceOf(BulkPurchaseDiscount.class, loaded.get(2)).getMinimumQuantity());
        assertEquals(START, loaded.get(0).getStartDate());
    }

    @Test
    @DisplayName("Archivo inexistente: lista vacia")
    void missingFileReturnsEmpty() {
        assertTrue(repository.loadAll().isEmpty());
    }

    @Test
    @DisplayName("saveAll rechaza nulos, textos con coma y fechas invertidas")
    void saveRejectsInvalidData() {
        assertThrows(IllegalArgumentException.class, () -> repository.saveAll(null));
        List<Promotion> withNull = new ArrayList<>();
        withNull.add(null);
        assertThrows(IllegalArgumentException.class, () -> repository.saveAll(withNull));
        assertThrows(IllegalArgumentException.class, () -> repository.saveAll(
                List.of(new PercentageDiscount("P1", "Oferta, 15%", START, END, 15.0))));
        assertThrows(IllegalArgumentException.class, () -> repository.saveAll(
                List.of(new PercentageDiscount("P1", "Al reves", END, START, 15.0))));
    }

    @Test
    @DisplayName("load falla con columnas, fechas, numeros, rangos y tipo invalidos")
    void loadFailsOnCorruptLines() throws IOException {
        writeFile("PERCENTAGE,P1,General");
        assertTrue(loadFails().getMessage().contains("linea 1"));
        writeFile("DOBLE,P1,General,2026-01-01,2026-12-31,15.0");
        loadFails();
        writeFile("PERCENTAGE,P1,General,01/01/2026,2026-12-31,15.0");
        loadFails();
        writeFile("PERCENTAGE,P1,General,2026-12-31,2026-01-01,15.0");
        loadFails();
        writeFile("PERCENTAGE,P1,General,2026-01-01,2026-12-31,mucho");
        loadFails();
        writeFile("PERCENTAGE,P1,General,2026-01-01,2026-12-31,150.0");
        loadFails();
        writeFile("PERCENTAGE,P1,General,2026-01-01,2026-12-31,-5.0");
        loadFails();
        writeFile("CATEGORY,P2,Juegos,2026-01-01,2026-12-31,20.0");
        loadFails();
        writeFile("CATEGORY,P2,Juegos,2026-01-01,2026-12-31,20.0,");
        loadFails();
        writeFile("BULK,P3,Volumen,2026-01-01,2026-12-31,0,10.0");
        loadFails();
        writeFile("BULK,P3,Volumen,2026-01-01,2026-12-31,tres,10.0");
        loadFails();
        writeFile("PERCENTAGE,,General,2026-01-01,2026-12-31,15.0");
        loadFails();
    }

    @Test
    @DisplayName("Errores de E/S se envuelven en PersistenceException")
    void ioErrorsAreWrapped() throws IOException {
        Path dir = Files.createDirectory(tempDir.resolve("carpeta"));
        PromotionRepository repo = new PromotionRepository(dir.toString());

        PersistenceException onSave = assertThrows(PersistenceException.class,
                () -> repo.saveAll(List.of(new PercentageDiscount("P1", "General", START, END, 15.0))));
        assertInstanceOf(IOException.class, onSave.getCause());

        assertInstanceOf(IOException.class, assertThrows(PersistenceException.class, repo::loadAll).getCause());
    }
}