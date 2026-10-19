package com.gamezone.persistence;

import com.gamezone.exceptions.PersistenceException;
import com.gamezone.model.BasicWarranty;
import com.gamezone.model.Client;
import com.gamezone.model.Console;
import com.gamezone.model.ExtendedWarranty;
import com.gamezone.model.Sale;
import com.gamezone.model.SaleDetail;
import com.gamezone.model.Seller;
import com.gamezone.model.Warranty;
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

/**
 * Tests for {@link WarrantyRepository}. This one is an integration test: warranties
 * are resolved through the real Sale, Product, Person, Accessory and Promotion
 * repositories, all writing to temporary files.
 */
class WarrantyRepositoryTest {

    @TempDir
    Path tempDir;

    private Path warrantiesFile;
    private PersonRepository personRepository;
    private ProductRepository productRepository;
    private SaleRepository saleRepository;
    private WarrantyRepository repository;
    private Console console;
    private Sale sale;

    @BeforeEach
    void setUp() {
        warrantiesFile = tempDir.resolve("warranties.csv");
        personRepository = new PersonRepository(tempDir.resolve("clients.csv").toString(),
                tempDir.resolve("sellers.csv").toString());
        productRepository = new ProductRepository(tempDir.resolve("products.csv").toString());
        saleRepository = new SaleRepository(tempDir.resolve("sales.csv").toString());
        repository = new WarrantyRepository(saleRepository, productRepository, personRepository,
                new AccessoryRepository(tempDir.resolve("accessories.csv").toString()),
                new PromotionRepository(tempDir.resolve("promotions.csv").toString()),
                warrantiesFile.toString());

        Client client = new Client("a@b.c", "C1", "Ana", "300");
        Seller seller = new Seller("E1", "Dia", "S1", "Luis", "301");
        console = new Console("Sony", "PS5", "9", "P2", "Consola", 500.0, 5);
        sale = new Sale("V1", client, seller, LocalDate.of(2026, 3, 15));
        sale.addDetail(new SaleDetail(console, 1, 500.0));

        personRepository.saveClients(List.of(client));
        personRepository.saveSellers(List.of(seller));
        productRepository.saveAll(List.of(console));
        saleRepository.saveAll(List.of(sale));
    }

    private PersistenceException loadFails() {
        return assertThrows(PersistenceException.class, repository::loadAll);
    }

    private void writeWarranties(String... lines) throws IOException {
        Files.writeString(warrantiesFile, String.join("\n", lines) + "\n");
    }

    @Test
    @DisplayName("Round trip de garantia basica y extendida")
    void roundTrip() {
        LocalDate start = LocalDate.of(2026, 3, 15);
        repository.saveAll(List.of(
                new BasicWarranty("G1", console, sale, start),
                new ExtendedWarranty("G2", console, sale, start)));

        List<Warranty> loaded = repository.loadAll();

        assertEquals(2, loaded.size());
        assertInstanceOf(BasicWarranty.class, loaded.get(0));
        assertInstanceOf(ExtendedWarranty.class, loaded.get(1));
        assertEquals(start, loaded.get(0).getStartDate());
        assertEquals(start.plusMonths(12), loaded.get(1).getEndDate());
        assertEquals("P2", loaded.get(0).getProduct().getId());
        assertEquals("V1", loaded.get(0).getSale().getId());
    }

    @Test
    @DisplayName("Archivo inexistente: lista vacia; lineas en blanco ignoradas")
    void missingFileAndBlankLines() throws IOException {
        assertTrue(repository.loadAll().isEmpty());
        writeWarranties("", "BASIC,G1,P2,V1,2026-03-15,2026-09-15", "  ");
        assertEquals(1, repository.loadAll().size());
    }

    @Test
    @DisplayName("El constructor rechaza dependencias nulas y rutas vacias")
    void constructorRejectsInvalidArguments() {
        assertThrows(IllegalArgumentException.class, () -> new WarrantyRepository(
                null, productRepository, personRepository, new AccessoryRepository(), new PromotionRepository()));
        assertThrows(IllegalArgumentException.class, () -> new WarrantyRepository(
                saleRepository, productRepository, personRepository,
                new AccessoryRepository(), new PromotionRepository(), " "));
    }

    @Test
    @DisplayName("saveAll rechaza nulos e ids con coma")
    void saveRejectsInvalidData() {
        assertThrows(IllegalArgumentException.class, () -> repository.saveAll(null));
        List<Warranty> withNull = new ArrayList<>();
        withNull.add(null);
        assertThrows(IllegalArgumentException.class, () -> repository.saveAll(withNull));
        assertThrows(IllegalArgumentException.class, () -> repository.saveAll(
                List.of(new BasicWarranty("G,1", console, sale, LocalDate.now()))));
        assertThrows(IllegalArgumentException.class, () -> repository.saveAll(
                List.of(new BasicWarranty("G1", null, sale, LocalDate.now()))));
    }

    @Test
    @DisplayName("Una garantia invalida no destruye el archivo existente")
    void invalidWarrantyLeavesFileUntouched() throws IOException {
        repository.saveAll(List.of(new BasicWarranty("G1", console, sale, LocalDate.of(2026, 3, 15))));
        String before = Files.readString(warrantiesFile);

        assertThrows(IllegalArgumentException.class, () -> repository.saveAll(List.of(
                new BasicWarranty("G2", console, sale, LocalDate.now()),
                new BasicWarranty("G,3", console, sale, LocalDate.now()))));

        assertEquals(before, Files.readString(warrantiesFile));
    }

    @Test
    @DisplayName("load falla con columnas, tipo, fechas y referencias invalidas")
    void loadFailsOnCorruptLines() throws IOException {
        writeWarranties("BASIC,G1,P2");
        assertTrue(loadFails().getMessage().contains("linea 1"));
        writeWarranties("PLATINO,G1,P2,V1,2026-03-15,2026-09-15");
        loadFails();
        writeWarranties("BASIC,G1,P2,V1,15/03/2026,2026-09-15");
        loadFails();
        writeWarranties("BASIC,G1,P2,V1,2026-03-15,nunca");
        loadFails();
        writeWarranties("BASIC,,P2,V1,2026-03-15,2026-09-15");
        loadFails();
        writeWarranties("BASIC,G1,P99,V1,2026-03-15,2026-09-15");
        assertTrue(loadFails().getMessage().contains("P99"));
        writeWarranties("BASIC,G1,P2,V99,2026-03-15,2026-09-15");
        assertTrue(loadFails().getMessage().contains("V99"));
    }

    @Test
    @DisplayName("Si un archivo del que depende esta corrupto, tambien falla con PersistenceException")
    void dependencyFailureIsReportedAsPersistenceException() throws IOException {
        writeWarranties("BASIC,G1,P2,V1,2026-03-15,2026-09-15");
        Files.writeString(tempDir.resolve("sales.csv"), "esto,no,es,una,venta\n");

        PersistenceException e = loadFails();

        assertTrue(e.getMessage().contains("sales.csv"));
    }

    @Test
    @DisplayName("Errores de E/S se envuelven en PersistenceException")
    void ioErrorsAreWrapped() throws IOException {
        Path dir = Files.createDirectory(tempDir.resolve("carpeta"));
        WarrantyRepository repo = new WarrantyRepository(saleRepository, productRepository, personRepository,
                new AccessoryRepository(tempDir.resolve("accessories.csv").toString()),
                new PromotionRepository(tempDir.resolve("promotions.csv").toString()), dir.toString());

        PersistenceException onSave = assertThrows(PersistenceException.class,
                () -> repo.saveAll(List.of(new BasicWarranty("G1", console, sale, LocalDate.now()))));
        assertInstanceOf(IOException.class, onSave.getCause());

        assertInstanceOf(IOException.class, assertThrows(PersistenceException.class, repo::loadAll).getCause());
    }
}