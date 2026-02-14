package com.ibrahim.DBPulse.controllers;

import com.ibrahim.DBPulse.IntegrationTestBase;
import com.ibrahim.DBPulse.dtos.ClientRequest;
import com.ibrahim.DBPulse.repositories.ClientRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for ClientController.
 * Tests REST API endpoints end-to-end with real database.
 */
@AutoConfigureMockMvc
class ClientControllerTest extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ClientRepository clientRepository;

    @BeforeEach
    void setUp() {
        clientRepository.deleteAll();
    }

    @Test
    @DisplayName("Should create client successfully")
    void testCreateClient() throws Exception {
        // Given
        ClientRequest request = new ClientRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john.doe@example.com");
        request.setPhone("+1234567890");
        request.setAddress("123 Main St");
        request.setCity("New York");
        request.setCountry("USA");

        // When/Then
        mockMvc.perform(post("/api/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.fullName").value("John Doe"))
                .andExpect(jsonPath("$.active").value(true))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    @DisplayName("Should fail to create client with invalid email")
    void testCreateClient_InvalidEmail() throws Exception {
        // Given
        ClientRequest request = new ClientRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("invalid-email");
        request.setPhone("+1234567890");

        // When/Then
        mockMvc.perform(post("/api/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.validationErrors").isArray())
                .andExpect(jsonPath("$.validationErrors[*].field", hasItem("email")));
    }

    @Test
    @DisplayName("Should fail to create client with duplicate email")
    void testCreateClient_DuplicateEmail() throws Exception {
        // Given - create first client
        ClientRequest request = new ClientRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john.doe@example.com");

        mockMvc.perform(post("/api/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // When/Then - try to create duplicate
        mockMvc.perform(post("/api/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message", containsString("already exists")));
    }

    @Test
    @DisplayName("Should get client by ID")
    void testGetClientById() throws Exception {
        // Given - create a client first
        ClientRequest request = new ClientRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john.doe@example.com");

        String response = mockMvc.perform(post("/api/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andReturn().getResponse().getContentAsString();

        Long clientId = objectMapper.readTree(response).get("id").asLong();

        // When/Then
        mockMvc.perform(get("/api/clients/{id}", clientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(clientId))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    @Test
    @DisplayName("Should return 404 when client not found")
    void testGetClientById_NotFound() throws Exception {
        // When/Then
        mockMvc.perform(get("/api/clients/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message", containsString("not found")));
    }

    @Test
    @DisplayName("Should get all clients")
    void testGetAllClients() throws Exception {
        // Given - create two clients
        ClientRequest request1 = new ClientRequest();
        request1.setFirstName("John");
        request1.setLastName("Doe");
        request1.setEmail("john@example.com");

        ClientRequest request2 = new ClientRequest();
        request2.setFirstName("Jane");
        request2.setLastName("Smith");
        request2.setEmail("jane@example.com");

        mockMvc.perform(post("/api/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request1)));

        mockMvc.perform(post("/api/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request2)));

        // When/Then
        mockMvc.perform(get("/api/clients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].email", containsInAnyOrder("john@example.com", "jane@example.com")));
    }

    @Test
    @DisplayName("Should update client successfully")
    void testUpdateClient() throws Exception {
        // Given - create a client first
        ClientRequest createRequest = new ClientRequest();
        createRequest.setFirstName("John");
        createRequest.setLastName("Doe");
        createRequest.setEmail("john.doe@example.com");

        String createResponse = mockMvc.perform(post("/api/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andReturn().getResponse().getContentAsString();

        Long clientId = objectMapper.readTree(createResponse).get("id").asLong();

        // When - update the client
        ClientRequest updateRequest = new ClientRequest();
        updateRequest.setFirstName("Johnny");
        updateRequest.setLastName("Doe");
        updateRequest.setEmail("john.doe@example.com");

        // Then
        mockMvc.perform(put("/api/clients/{id}", clientId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Johnny"))
                .andExpect(jsonPath("$.fullName").value("Johnny Doe"));
    }

    @Test
    @DisplayName("Should delete client successfully")
    void testDeleteClient() throws Exception {
        // Given - create a client first
        ClientRequest request = new ClientRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john.doe@example.com");

        String response = mockMvc.perform(post("/api/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andReturn().getResponse().getContentAsString();

        Long clientId = objectMapper.readTree(response).get("id").asLong();

        // When - delete the client
        mockMvc.perform(delete("/api/clients/{id}", clientId))
                .andExpect(status().isNoContent());

        // Then - verify client is deleted
        mockMvc.perform(get("/api/clients/{id}", clientId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should deactivate client successfully")
    void testDeactivateClient() throws Exception {
        // Given - create a client first
        ClientRequest request = new ClientRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john.doe@example.com");

        String response = mockMvc.perform(post("/api/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andReturn().getResponse().getContentAsString();

        Long clientId = objectMapper.readTree(response).get("id").asLong();

        // When - deactivate the client
        mockMvc.perform(patch("/api/clients/{id}/deactivate", clientId))
                .andExpect(status().isNoContent());

        // Then - verify client is deactivated
        mockMvc.perform(get("/api/clients/{id}", clientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false));
    }
}
