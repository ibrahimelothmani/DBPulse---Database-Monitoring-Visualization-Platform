package com.ibrahim.DBPulse.controllers;

import com.ibrahim.DBPulse.dtos.ClientRequest;
import com.ibrahim.DBPulse.dtos.ClientResponse;
import com.ibrahim.DBPulse.services.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Client management.
 * Provides CRUD operations and search functionality for clients.
 */
@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
@Slf4j
public class ClientController {

    private final ClientService clientService;

    /**
     * Create a new client.
     * POST /api/clients
     */
    @PostMapping
    public ResponseEntity<ClientResponse> createClient(@Valid @RequestBody ClientRequest request) {
        log.info("REST request to create client with email: {}", request.getEmail());
        ClientResponse response = clientService.createClient(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Get a client by ID.
     * GET /api/clients/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ClientResponse> getClientById(@PathVariable Long id) {
        log.info("REST request to get client with ID: {}", id);
        ClientResponse response = clientService.getClientById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Get all clients.
     * GET /api/clients
     */
    @GetMapping
    public ResponseEntity<List<ClientResponse>> getAllClients() {
        log.info("REST request to get all clients");
        List<ClientResponse> clients = clientService.getAllClients();
        return ResponseEntity.ok(clients);
    }

    /**
     * Search clients with pagination.
     * GET /api/clients/search?searchTerm=xxx&page=0&size=10
     */
    @GetMapping("/search")
    public ResponseEntity<Page<ClientResponse>> searchClients(
            @RequestParam String searchTerm,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        log.info("REST request to search clients with term: {}", searchTerm);
        Page<ClientResponse> clients = clientService.searchClients(searchTerm, pageable);
        return ResponseEntity.ok(clients);
    }

    /**
     * Update a client.
     * PUT /api/clients/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ClientResponse> updateClient(
            @PathVariable Long id,
            @Valid @RequestBody ClientRequest request) {

        log.info("REST request to update client with ID: {}", id);
        ClientResponse response = clientService.updateClient(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete a client.
     * DELETE /api/clients/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        log.info("REST request to delete client with ID: {}", id);
        clientService.deleteClient(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Deactivate a client (soft delete).
     * PATCH /api/clients/{id}/deactivate
     */
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateClient(@PathVariable Long id) {
        log.info("REST request to deactivate client with ID: {}", id);
        clientService.deactivateClient(id);
        return ResponseEntity.noContent().build();
    }
}
