package com.gamezone.persistence;

import com.gamezone.exceptions.PersistenceException;
import com.gamezone.model.Client;
import com.gamezone.model.Seller;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Tests for {@link PersonRepository}. */
class PersonRepositoryTest {

    @TempDir
    Path tempDir;

    private Path clientsFile;
    private Path sellersFile;
    private PersonRepository repository;

    @BeforeEach
    void setUp() {
        clientsFile = tempDir.resolve("clients.csv");
        sellersFile = tempDir.resolve("sellers.csv");
        repository = new PersonRepository(clientsFile.toString(), sellersFile.toString());
    }

    @Test
    @DisplayName("Round trip de clientes (con historial) y vendedores")
    void roundTrip() {
        Client client = new Client("ana@mail.com", "C1", "Ana", "300");
        client.addPurchase("V1");
        client.addPurchase("V2");
        repository.saveClients(List.of(client));
        repository.saveSellers(List.of(new Seller("E1", "Dia", "S1", "Luis", "301")));

        List<Client> clients = repository.loadClients();
        List<Seller> sellers = repository.loadSellers();

        assertEquals(1, clients.size());
        assertEquals(List.of("V1", "V2"), clients.get(0).getPurchaseHistory());
        assertEquals("E1", sellers.get(0).getEmployeeCode());
    }

    @Test
    @DisplayName("Archivos inexistentes: listas vacias")
    void missingFilesReturnEmpty() {
        assertTrue(repository.loadClients().isEmpty());
        assertTrue(repository.loadSellers().isEmpty());
    }

    @Test
    @DisplayName("Guardar datos invalidos lanza IllegalArgumentException")
    void saveRejectsInvalidData() {
        assertThrows(IllegalArgumentException.class, () -> repository.saveClients(null));
        assertThrows(IllegalArgumentException.class, () -> repository.saveSellers(null));
        assertThrows(IllegalArgumentException.class, () -> repository.saveClients(
                List.of(new Client("a@b.c", "C,1", "Ana", "300"))));
        assertThrows(IllegalArgumentException.class, () -> repository.saveSellers(
                List.of(new Seller("E1", "Dia", "S1", null, "301"))));
    }

    @Test
    @DisplayName("Lineas corruptas de clientes y vendedores")
    void loadFailsOnCorruptLines() throws IOException {
        Files.writeString(clientsFile, "C1,Ana\n");
        PersistenceException e = assertThrows(PersistenceException.class, repository::loadClients);
        assertTrue(e.getMessage().contains("linea 1"));

        Files.writeString(clientsFile, ",Ana,300,a@b.c\n");
        assertThrows(PersistenceException.class, repository::loadClients);

        Files.writeString(sellersFile, "S1,Luis,301,E1\n");
        assertThrows(PersistenceException.class, repository::loadSellers);
    }

    @Test
    @DisplayName("Errores de E/S se envuelven en PersistenceException")
    void ioErrorsAreWrapped() throws IOException {
        Path dir = Files.createDirectory(tempDir.resolve("carpeta"));
        PersonRepository repo = new PersonRepository(dir.toString(), dir.toString());

        PersistenceException onSave = assertThrows(PersistenceException.class,
                () -> repo.saveClients(List.of(new Client("a@b.c", "C1", "Ana", "300"))));
        assertInstanceOf(IOException.class, onSave.getCause());

        assertInstanceOf(IOException.class,
                assertThrows(PersistenceException.class, repo::loadClients).getCause());
        assertInstanceOf(IOException.class,
                assertThrows(PersistenceException.class, repo::loadSellers).getCause());
    }
}