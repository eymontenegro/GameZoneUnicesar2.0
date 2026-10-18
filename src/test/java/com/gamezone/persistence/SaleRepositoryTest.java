package com.gamezone.persistence;

import com.gamezone.exceptions.PersistenceException;
import com.gamezone.model.Client;
import com.gamezone.model.Console;
import com.gamezone.model.Sale;
import com.gamezone.model.SaleDetail;
import com.gamezone.model.Seller;
import com.gamezone.model.VideoGame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for {@link SaleRepository}: normal round trip, invalid data on save,
 * corrupt files on load, and low-level I/O failures wrapped as {@link PersistenceException}.
 */
class SaleRepositoryTest {

    @TempDir
    Path tempDir;

    private Path file;
    private SaleRepository repository;
    private Client client;
    private Seller seller;
    private VideoGame game;
    private Console console;

    @BeforeEach
    void setUp() {
        file = tempDir.resolve("sales.csv");
        repository = new SaleRepository(file.toString());
        client = new Client("ana@mail.com", "C1", "Ana", "300");
        seller = new Seller("E1", "Dia", "S1", "Luis", "301");
        game = new VideoGame("PS5", "Accion", "T", "P1", "Juego", 100.0, 10);
        console = new Console("Sony", "PS5", "9", "P2", "Consola", 500.0, 5);
    }

    private Sale sampleSale(String id) {
        Sale sale = new Sale(id, client, seller, LocalDate.of(2026, 3, 15));
        sale.addDetail(new SaleDetail(game, 2, 100.0));
        sale.addDetail(new SaleDetail(console, 1, 500.0));
        sale.setAppliedPromotionName("Black Friday");
        sale.setDiscountAmount(70.0);
        sale.setAdditionalCosts(20.0);
        sale.calculateFinalTotal();
        return sale;
    }

    private List<Sale> load() {
        return repository.loadAll(List.of(client), List.of(seller), List.of(game, console), List.of(), List.of());
    }

    private void writeFile(String... lines) throws IOException {
        Files.writeString(file, String.join("\n", lines) + "\n");
    }

    private String validLine() {
        return "V1,2026-03-15,S1,C1,Ninguna,0.0,0.0,200.0,P1:2:100.0";
    }

    private PersistenceException loadFails() {
        return assertThrows(PersistenceException.class, this::load);
    }

    // ---------- normal behaviour ----------

    @Test
    @DisplayName("saveAll + loadAll devuelve la misma venta")
    void roundTripKeepsAllData() {
        repository.saveAll(List.of(sampleSale("V1")));

        List<Sale> loaded = load();

        assertEquals(1, loaded.size());
        Sale s = loaded.get(0);
        assertEquals("V1", s.getId());
        assertEquals(LocalDate.of(2026, 3, 15), s.getDate());
        assertSame(client, s.getClient());
        assertSame(seller, s.getSeller());
        assertEquals("Black Friday", s.getAppliedPromotionName());
        assertEquals(70.0, s.getDiscountAmount());
        assertEquals(20.0, s.getAdditionalCosts());
        assertEquals(650.0, s.getTotal());
        assertEquals(2, s.getDetails().size());
        assertEquals(2, s.getDetails().get(0).getQuantity());
    }

    @Test
    @DisplayName("Una venta sin productos se guarda y se carga")
    void saleWithoutDetailsRoundTrips() {
        repository.saveAll(List.of(new Sale("V9", client, seller, LocalDate.of(2026, 1, 1))));

        List<Sale> loaded = load();

        assertEquals(1, loaded.size());
        assertTrue(loaded.get(0).getDetails().isEmpty());
    }

    @Test
    @DisplayName("Si el archivo no existe, loadAll devuelve lista vacia")
    void missingFileReturnsEmptyList() {
        assertTrue(load().isEmpty());
    }

    @Test
    @DisplayName("Las lineas en blanco se ignoran")
    void blankLinesAreIgnored() throws IOException {
        writeFile("", validLine(), "   ", "");

        assertEquals(1, load().size());
    }

    @Test
    @DisplayName("saveAll crea la carpeta si no existe")
    void saveAllCreatesParentDirectory() {
        Path nested = tempDir.resolve("a/b/sales.csv");
        SaleRepository repo = new SaleRepository(nested.toString());

        repo.saveAll(List.of(sampleSale("V1")));

        assertTrue(Files.exists(nested));
    }

    // ---------- invalid data on save ----------

    @Test
    @DisplayName("Constructor rechaza rutas nulas o vacias")
    void constructorRejectsBlankPath() {
        assertThrows(IllegalArgumentException.class, () -> new SaleRepository(null));
        assertThrows(IllegalArgumentException.class, () -> new SaleRepository("  "));
    }

    @Test
    @DisplayName("saveAll rechaza lista nula o con elementos nulos")
    void saveAllRejectsNulls() {
        assertThrows(IllegalArgumentException.class, () -> repository.saveAll(null));
        List<Sale> withNull = new java.util.ArrayList<>();
        withNull.add(null);
        assertThrows(IllegalArgumentException.class, () -> repository.saveAll(withNull));
    }

    @Test
    @DisplayName("saveAll rechaza ids con coma o salto de linea")
    void saveAllRejectsUnsafeText() {
        assertThrows(IllegalArgumentException.class, () -> repository.saveAll(List.of(sampleSale("V,1"))));
        assertThrows(IllegalArgumentException.class, () -> repository.saveAll(List.of(sampleSale("V\n1"))));
    }

    @Test
    @DisplayName("saveAll rechaza id de producto con ':' o ';'")
    void saveAllRejectsProductIdWithSeparators() {
        VideoGame bad = new VideoGame("PS5", "Accion", "T", "P:1", "Juego", 10.0, 1);
        Sale sale = new Sale("V1", client, seller, LocalDate.now());
        sale.addDetail(new SaleDetail(bad, 1, 10.0));

        assertThrows(IllegalArgumentException.class, () -> repository.saveAll(List.of(sale)));
    }

    @Test
    @DisplayName("saveAll rechaza NaN e infinito")
    void saveAllRejectsNonFiniteNumbers() {
        Sale sale = sampleSale("V1");
        sale.setTotal(Double.POSITIVE_INFINITY);

        assertThrows(IllegalArgumentException.class, () -> repository.saveAll(List.of(sale)));
    }

    @Test
    @DisplayName("Una venta invalida no destruye el archivo existente")
    void invalidSaleLeavesExistingFileUntouched() throws IOException {
        repository.saveAll(List.of(sampleSale("V1")));
        String before = Files.readString(file);

        assertThrows(IllegalArgumentException.class,
                () -> repository.saveAll(List.of(sampleSale("V2"), sampleSale("V,3"))));

        assertEquals(before, Files.readString(file));
    }

    // ---------- corrupt file on load ----------

    @Test
    @DisplayName("Linea con columnas de menos")
    void loadFailsWithTooFewColumns() throws IOException {
        writeFile("V1,2026-03-15,S1");
        PersistenceException e = loadFails();
        assertTrue(e.getMessage().contains("linea 1"));
        assertTrue(e.getMessage().contains("columnas"));
    }

    @Test
    @DisplayName("Linea con columnas de mas")
    void loadFailsWithTooManyColumns() throws IOException {
        writeFile(validLine() + ",extra");
        loadFails();
    }

    @Test
    @DisplayName("Fecha invalida: el mensaje trae archivo y numero de linea")
    void loadFailsWithBadDate() throws IOException {
        writeFile(validLine(), "V2,15/03/2026,S1,C1,Ninguna,0.0,0.0,0.0,");
        PersistenceException e = loadFails();
        assertTrue(e.getMessage().contains("linea 2"));
        assertTrue(e.getMessage().contains("sales.csv"));
        assertNotNull(e.getCause());
    }

    @Test
    @DisplayName("Montos no numericos, negativos o infinitos")
    void loadFailsWithBadAmounts() throws IOException {
        writeFile("V1,2026-03-15,S1,C1,Ninguna,abc,0.0,0.0,");
        loadFails();
        writeFile("V1,2026-03-15,S1,C1,Ninguna,-5.0,0.0,0.0,");
        loadFails();
        writeFile("V1,2026-03-15,S1,C1,Ninguna,0.0,0.0,Infinity,");
        loadFails();
        writeFile("V1,2026-03-15,S1,C1,Ninguna,0.0,0.0,NaN,");
        loadFails();
    }

    @Test
    @DisplayName("Id de venta vacio")
    void loadFailsWithBlankId() throws IOException {
        writeFile(",2026-03-15,S1,C1,Ninguna,0.0,0.0,0.0,");
        loadFails();
    }

    @Test
    @DisplayName("Cliente o vendedor inexistente")
    void loadFailsWithUnknownClientOrSeller() throws IOException {
        writeFile("V1,2026-03-15,S1,C99,Ninguna,0.0,0.0,0.0,");
        assertTrue(loadFails().getMessage().contains("C99"));
        writeFile("V1,2026-03-15,S99,C1,Ninguna,0.0,0.0,0.0,");
        assertTrue(loadFails().getMessage().contains("S99"));
    }

    @Test
    @DisplayName("Detalles mal formados: formato, producto, cantidad, precio")
    void loadFailsWithBadDetails() throws IOException {
        String head = "V1,2026-03-15,S1,C1,Ninguna,0.0,0.0,0.0,";
        writeFile(head + "P1:2");
        loadFails();
        writeFile(head + "P99:1:10.0");
        loadFails();
        writeFile(head + "P1:dos:10.0");
        loadFails();
        writeFile(head + "P1:1:gratis");
        loadFails();
        writeFile(head + "P1:0:10.0");
        loadFails();
        writeFile(head + "P1:-3:10.0");
        loadFails();
        writeFile(head + "P1:1:-10.0");
        loadFails();
        writeFile(head + "P1:1:10.0;");
        loadFails();
    }

    @Test
    @DisplayName("Un archivo binario o basura no lanza excepciones inesperadas")
    void garbageFileOnlyThrowsPersistenceException() throws IOException {
        Files.write(file, new byte[]{0, 1, 2, (byte) 0xFF, (byte) 0xFE, '\n', '%', '$', ',', ','});
        loadFails();
    }

    // ---------- low-level IOException wrapped ----------

    @Test
    @DisplayName("Error de E/S al guardar se envuelve en PersistenceException con causa IOException")
    void saveWrapsIOException() throws IOException {
        Path dir = Files.createDirectory(tempDir.resolve("es-una-carpeta"));
        SaleRepository repo = new SaleRepository(dir.toString());

        PersistenceException e = assertThrows(PersistenceException.class,
                () -> repo.saveAll(List.of(sampleSale("V1"))));

        assertInstanceOf(IOException.class, e.getCause());
        assertTrue(e.getMessage().contains(dir.toString()));
        assertTrue(e.getMessage().contains("1 venta"));
    }

    @Test
    @DisplayName("Error de E/S al leer se envuelve en PersistenceException con causa IOException")
    void loadWrapsIOException() throws IOException {
        Path dir = Files.createDirectory(tempDir.resolve("es-una-carpeta"));
        SaleRepository repo = new SaleRepository(dir.toString());

        PersistenceException e = assertThrows(PersistenceException.class,
                () -> repo.loadAll(List.of(client), List.of(seller), List.of(game), List.of(), List.of()));

        assertInstanceOf(IOException.class, e.getCause());
        assertTrue(e.getMessage().contains(dir.toString()));
    }

    @Test
    @DisplayName("No se puede crear la carpeta padre: PersistenceException")
    void saveFailsWhenParentCannotBeCreated() throws IOException {
        Path blocker = Files.writeString(tempDir.resolve("archivo.txt"), "x");
        SaleRepository repo = new SaleRepository(blocker.resolve("sub/sales.csv").toString());

        assertThrows(PersistenceException.class, () -> repo.saveAll(List.of(sampleSale("V1"))));
    }
}