package com.ibrahim.DBPulse.repositories;

import com.ibrahim.DBPulse.IntegrationTestBase;
import com.ibrahim.DBPulse.entities.Client;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

/**
 * Integration tests for ClientRepository.
 * Uses TestContainers for realistic database testing.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ClientRepositoryTest extends IntegrationTestBase {

    @Autowired
    private ClientRepository clientRepository;

    private Client testClient;

    @BeforeEach
    void setUp() {
        clientRepository.deleteAll();

        testClient = new Client();
        testClient.setFirstName("John");
        testClient.setLastName("Doe");
        testClient.setEmail("john.doe@example.com");
        testClient.setPhone("+1234567890");
        testClient.setAddress("123 Main St");
        testClient.setCity("New York");
        testClient.setCountry("USA");
        testClient.setActive(true);
    }

    @Test
    @DisplayName("Should save client successfully")
    void testSaveClient() {
        // When
        Client savedClient = clientRepository.save(testClient);

        // Then
        assertThat(savedClient.getId()).isNotNull();
        assertThat(savedClient.getFirstName()).isEqualTo("John");
        assertThat(savedClient.getEmail()).isEqualTo("john.doe@example.com");
        assertThat(savedClient.getCreatedAt()).isNotNull();
        assertThat(savedClient.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should find client by email")
    void testFindByEmail() {
        // Given
        clientRepository.save(testClient);

        // When
        Optional<Client> found = clientRepository.findByEmail("john.doe@example.com");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getFirstName()).isEqualTo("John");
    }

    @Test
    @DisplayName("Should return empty when email not found")
    void testFindByEmail_NotFound() {
        // When
        Optional<Client> found = clientRepository.findByEmail("nonexistent@example.com");

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Should check if email exists")
    void testExistsByEmail() {
        // Given
        clientRepository.save(testClient);

        // When
        boolean exists = clientRepository.existsByEmail("john.doe@example.com");
        boolean notExists = clientRepository.existsByEmail("other@example.com");

        // Then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("Should find all active clients")
    void testFindByActiveTrue() {
        // Given
        clientRepository.save(testClient);

        Client inactiveClient = new Client();
        inactiveClient.setFirstName("Jane");
        inactiveClient.setLastName("Smith");
        inactiveClient.setEmail("jane@example.com");
        inactiveClient.setActive(false);
        clientRepository.save(inactiveClient);

        // When
        List<Client> activeClients = clientRepository.findByActiveTrue();

        // Then
        assertThat(activeClients).hasSize(1);
        assertThat(activeClients.get(0).getEmail()).isEqualTo("john.doe@example.com");
    }

    @Test
    @DisplayName("Should find clients by country")
    void testFindByCountry() {
        // Given
        clientRepository.save(testClient);

        Client canadianClient = new Client();
        canadianClient.setFirstName("Jane");
        canadianClient.setLastName("Smith");
        canadianClient.setEmail("jane@example.com");
        canadianClient.setCountry("Canada");
        clientRepository.save(canadianClient);

        // When
        List<Client> usaClients = clientRepository.findByCountry("USA");

        // Then
        assertThat(usaClients).hasSize(1);
        assertThat(usaClients.get(0).getEmail()).isEqualTo("john.doe@example.com");
    }

    @Test
    @DisplayName("Should search clients by name or email")
    void testSearchClients() {
        // Given
        clientRepository.save(testClient);

        Client anotherClient = new Client();
        anotherClient.setFirstName("Jane");
        anotherClient.setLastName("Smith");
        anotherClient.setEmail("jane.smith@example.com");
        clientRepository.save(anotherClient);

        // When - search by first name
        Page<Client> results = clientRepository.searchClients("john", PageRequest.of(0, 10));

        // Then
        assertThat(results.getContent()).hasSize(1);
        assertThat(results.getContent().get(0).getFirstName()).isEqualTo("John");
    }

    @Test
    @DisplayName("Should count active clients")
    void testCountByActiveTrue() {
        // Given
        clientRepository.save(testClient);

        Client inactiveClient = new Client();
        inactiveClient.setFirstName("Jane");
        inactiveClient.setLastName("Smith");
        inactiveClient.setEmail("jane@example.com");
        inactiveClient.setActive(false);
        clientRepository.save(inactiveClient);

        // When
        long activeCount = clientRepository.countByActiveTrue();

        // Then
        assertThat(activeCount).isEqualTo(1);
    }

    @Test
    @DisplayName("Should update client successfully")
    void testUpdateClient() {
        // Given
        Client savedClient = clientRepository.save(testClient);

        // When
        savedClient.setFirstName("Johnny");
        Client updatedClient = clientRepository.save(savedClient);

        // Then
        assertThat(updatedClient.getFirstName()).isEqualTo("Johnny");
        assertThat(updatedClient.getUpdatedAt()).isAfterOrEqualTo(updatedClient.getCreatedAt());
    }

    @Test
    @DisplayName("Should delete client successfully")
    void testDeleteClient() {
        // Given
        Client savedClient = clientRepository.save(testClient);

        // When
        clientRepository.delete(savedClient);

        // Then
        assertThat(clientRepository.findById(savedClient.getId())).isEmpty();
    }
}
