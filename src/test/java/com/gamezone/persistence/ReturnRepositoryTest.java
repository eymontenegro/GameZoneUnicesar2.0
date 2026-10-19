package com.gamezone.persistence;

import com.gamezone.exceptions.PersistenceException;
import com.gamezone.model.Client;
import com.gamezone.model.Console;
import com.gamezone.model.Product;
import com.gamezone.model.Return;
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
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Tests for {@link ReturnRepository}. */
class ReturnRepositoryTest {

    @TempDir
    Path tempDir;

    private Path file;
    private ReturnRepository repository;
    private Sale sale;
    private VideoGame game;
    private Console console;

    @BeforeEach
    void setUp() {
        file = tempDir.resolve("returns.csv");
        repository = new ReturnRepository(file.toString());
        game = new VideoGame("PS5", "Accion", "T", "P1", "Juego", 100.0, 10);
        console = new Console("Sony", "PS5", "9", "P2", "Consola", 500.0, 5);
        sale = new Sale("V1", new Client("a@b.c", "C1", "Ana", "300"),
                new Seller("E1", "Dia", "S1", "Luis", "301"), LocalDate.of(2026, 3, 15));
        sale.addDetail(new SaleDetail(game, 1, 100.0));
        sale.addDetail(new SaleDetail(console, 1, 500.0));
        sale.setDiscountAmount(60.0);
        sale.calculateFinalTotal();
    }

    private PersistenceException loadFails() {
        return assertThrows(PersistenceException.class, () -> repository.loadAll(List.of(sale)));
    }

    private void writeFile(String... lines) throws IOException {
        Files.writeString(file, String.join("\n", lines) + "\n");
    }

    private Return sampleReturn(String id, double warrantyRefund) {
        return new Return(id, LocalDate.of(2026, 4, 1), sale, List.of(game, console), "Defecto", warrantyRefund);
    }

    @Test
    @DisplayName("Round trip: venta, productos y fecha se recuperan")
    void roundTrip() {
        repository.saveAll(List.of(sampleReturn("R1", 0.0)));

        List<Return> loaded = repository.loadAll(List.of(sale));

        assertEquals(1, loaded.size());
        Return r = loaded.get(0);
        assertEquals("R1", r.getId());
        assertSame(sale, r.getOriginalSale());
        assertEquals(LocalDate.of(2026, 4, 1), r.getDate());
        assertEquals("Defecto", r.getReason());
        assertEquals(2, r.getReturnedProducts().size());
    }

    @Test
    @DisplayName("El reembolso total no se duplica al recargar (sin y con garantia)")
    void refundIsNotDoubledAfterReload() {
        Return noWarranty = sampleReturn("R1", 0.0);
        Return withWarranty = sampleReturn("R2", 50.0);
        repository.saveAll(List.of(noWarranty, withWarranty));

        List<Return> loaded = repository.loadAll(List.of(sale));

        assertEquals(noWarranty.getRefundAmount(), loaded.get(0).getRefundAmount(), 0.001);
        assertEquals(withWarranty.getRefundAmount(), loaded.get(1).getRefundAmount(), 0.001);
        assertEquals(50.0, loaded.get(1).getWarrantyRefundAmount(), 0.001);
    }

    @Test
    @DisplayName("Devolucion sin productos se guarda y se carga")
    void returnWithoutProducts() {
        repository.saveAll(List.of(new Return("R1", LocalDate.of(2026, 4, 1), sale, List.of(), "Nada", 0.0)));

        assertTrue(repository.loadAll(List.of(sale)).get(0).getReturnedProducts().isEmpty());
    }

    @Test
    @DisplayName("Archivo inexistente: lista vacia; lineas en blanco ignoradas")
    void missingFileAndBlankLines() throws IOException {
        assertTrue(repository.loadAll(List.of(sale)).isEmpty());
        writeFile("", "R1,V1,2026-04-01,Defecto,100.0,P1", "  ");
        assertEquals(1, repository.loadAll(List.of(sale)).size());
    }

    @Test
    @DisplayName("saveAll rechaza nulos y textos con coma")
    void saveRejectsInvalidData() {
        assertThrows(IllegalArgumentException.class, () -> repository.saveAll(null));
        List<Return> withNull = new ArrayList<>();
        withNull.add(null);
        assertThrows(IllegalArgumentException.class, () -> repository.saveAll(withNull));
        assertThrows(IllegalArgumentException.class, () -> repository.saveAll(List.of(
                new Return("R1", LocalDate.now(), sale, List.of(game), "Roto, sin caja", 0.0))));
        assertThrows(IllegalArgumentException.class, () -> repository.saveAll(List.of(sampleReturn("R,1", 0.0))));
    }

    @Test
    @DisplayName("Una devolucion invalida no destruye el archivo existente")
    void invalidReturnLeavesFileUntouched() throws IOException {
        repository.saveAll(List.of(sampleReturn("R1", 0.0)));
        String before = Files.readString(file);

        assertThrows(IllegalArgumentException.class,
                () -> repository.saveAll(List.of(sampleReturn("R2", 0.0), sampleReturn("R,3", 0.0))));

        assertEquals(before, Files.readString(file));
    }

    @Test
    @DisplayName("load falla con columnas, fecha, monto y referencias invalidas")
    void loadFailsOnCorruptLines() throws IOException {
        writeFile("R1,V1,2026-04-01");
        assertTrue(loadFails().getMessage().contains("linea 1"));
        writeFile("R1,V1,01-04-2026,Defecto,100.0,P1");
        loadFails();
        writeFile("R1,V1,2026-04-01,Defecto,mucho,P1");
        loadFails();
        writeFile("R1,V1,2026-04-01,Defecto,-5.0,P1");
        loadFails();
        writeFile(",V1,2026-04-01,Defecto,100.0,P1");
        loadFails();
        writeFile("R1,V99,2026-04-01,Defecto,100.0,P1");
        assertTrue(loadFails().getMessage().contains("V99"));
        writeFile("R1,V1,2026-04-01,Defecto,100.0,P99");
        assertTrue(loadFails().getMessage().contains("P99"));
        writeFile("R1,V1,2026-04-01,Defecto,100.0,P1;");
        loadFails();
    }

    @Test
    @DisplayName("Errores de E/S se envuelven en PersistenceException")
    void ioErrorsAreWrapped() throws IOException {
        Path dir = Files.createDirectory(tempDir.resolve("carpeta"));
        ReturnRepository repo = new ReturnRepository(dir.toString());

        PersistenceException onSave = assertThrows(PersistenceException.class,
                () -> repo.saveAll(List.of(sampleReturn("R1", 0.0))));
        assertInstanceOf(IOException.class, onSave.getCause());

        assertInstanceOf(IOException.class,
                assertThrows(PersistenceException.class, () -> repo.loadAll(List.of(sale))).getCause());
    }
}