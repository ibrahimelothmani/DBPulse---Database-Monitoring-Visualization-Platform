package com.ibrahim.DBPulse.services;

import com.ibrahim.DBPulse.dtos.ClientRequest;
import com.ibrahim.DBPulse.dtos.ClientResponse;
import com.ibrahim.DBPulse.entities.Client;
import com.ibrahim.DBPulse.exceptions.DuplicateResourceException;
import com.ibrahim.DBPulse.exceptions.ResourceNotFoundException;
import com.ibrahim.DBPulse.repositories.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ClientService using Mockito.
 * Tests business logic in isolation from database.
 */
@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientService clientService;

    private Client testClient;
    private ClientRequest clientRequest;

    @BeforeEach
    void setUp() {
        testClient = new Client();
        testClient.setId(1L);
        testClient.setFirstName("John");
        testClient.setLastName("Doe");
        testClient.setEmail("john.doe@example.com");
        testClient.setPhone("+1234567890");
        testClient.setAddress("123 Main St");
        testClient.setCity("New York");
        testClient.setCountry("USA");
        testClient.setActive(true);

        clientRequest = new ClientRequest();
        clientRequest.setFirstName("John");
        clientRequest.setLastName("Doe");
        clientRequest.setEmail("john.doe@example.com");
        clientRequest.setPhone("+1234567890");
        clientRequest.setAddress("123 Main St");
        clientRequest.setCity("New York");
        clientRequest.setCountry("USA");
    }

    @Test
    @DisplayName("Should create client successfully")
    void testCreateClient_Success() {
        // Given
        when(clientRepository.existsByEmail(anyString())).thenReturn(false);
        when(clientRepository.save(any(Client.class))).thenReturn(testClient);

        // When
        ClientResponse response = clientService.createClient(clientRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getEmail()).isEqualTo("john.doe@example.com");

        verify(clientRepository).existsByEmail("john.doe@example.com");
        verify(clientRepository).save(any(Client.class));
    }

    @Test
    @DisplayName("Should throw exception when creating client with duplicate email")
    void testCreateClient_DuplicateEmail() {
        // Given
        when(clientRepository.existsByEmail(anyString())).thenReturn(true);

        // When/Then
        assertThatThrownBy(() -> clientService.createClient(clientRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("already exists");

        verify(clientRepository).existsByEmail("john.doe@example.com");
        verify(clientRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should get client by ID successfully")
    void testGetClientById_Success() {
        // Given
        when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));

        // When
        ClientResponse response = clientService.getClientById(1L);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getFullName()).isEqualTo("John Doe");

        verify(clientRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when client not found")
    void testGetClientById_NotFound() {
        // Given
        when(clientRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When/Then
        assertThatThrownBy(() -> clientService.getClientById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("not found");

        verify(clientRepository).findById(999L);
    }

    @Test
    @DisplayName("Should get all clients")
    void testGetAllClients() {
        // Given
        List<Client> clients = List.of(testClient);
        when(clientRepository.findAll()).thenReturn(clients);

        // When
        List<ClientResponse> responses = clientService.getAllClients();

        // Then
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getEmail()).isEqualTo("john.doe@example.com");

        verify(clientRepository).findAll();
    }

    @Test
    @DisplayName("Should update client successfully")
    void testUpdateClient_Success() {
        // Given
        clientRequest.setFirstName("Johnny");
        when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));
        when(clientRepository.existsByEmail(anyString())).thenReturn(false);
        when(clientRepository.save(any(Client.class))).thenReturn(testClient);

        // When
        ClientResponse response = clientService.updateClient(1L, clientRequest);

        // Then
        assertThat(response).isNotNull();
        verify(clientRepository).findById(1L);
        verify(clientRepository).save(any(Client.class));
    }

    @Test
    @DisplayName("Should throw exception when updating with duplicate email")
    void testUpdateClient_DuplicateEmail() {
        // Given
        clientRequest.setEmail("another@example.com");
        when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));
        when(clientRepository.existsByEmail("another@example.com")).thenReturn(true);

        // When/Then
        assertThatThrownBy(() -> clientService.updateClient(1L, clientRequest))
                .isInstanceOf(DuplicateResourceException.class);

        verify(clientRepository).findById(1L);
        verify(clientRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should delete client successfully")
    void testDeleteClient_Success() {
        // Given
        when(clientRepository.existsById(1L)).thenReturn(true);

        // When
        clientService.deleteClient(1L);

        // Then
        verify(clientRepository).existsById(1L);
        verify(clientRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent client")
    void testDeleteClient_NotFound() {
        // Given
        when(clientRepository.existsById(anyLong())).thenReturn(false);

        // When/Then
        assertThatThrownBy(() -> clientService.deleteClient(999L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(clientRepository).existsById(999L);
        verify(clientRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Should deactivate client successfully")
    void testDeactivateClient() {
        // Given
        when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));
        when(clientRepository.save(any(Client.class))).thenReturn(testClient);

        // When
        clientService.deactivateClient(1L);

        // Then
        verify(clientRepository).findById(1L);
        verify(clientRepository).save(argThat(client -> !client.getActive()));
    }
}
