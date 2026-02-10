package com.ibrahim.DBPulse.services;

import com.ibrahim.DBPulse.dtos.ClientRequest;
import com.ibrahim.DBPulse.dtos.ClientResponse;
import com.ibrahim.DBPulse.entities.Client;
import com.ibrahim.DBPulse.exceptions.DuplicateResourceException;
import com.ibrahim.DBPulse.exceptions.ResourceNotFoundException;
import com.ibrahim.DBPulse.mappers.EntityMapper;
import com.ibrahim.DBPulse.repositories.ClientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ClientService {

    private final ClientRepository clientRepository;

    public ClientResponse createClient(ClientRequest request) {
        log.info("Creating new client with email: {}", request.getEmail());

        if (clientRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Client with email " + request.getEmail() + " already exists");
        }

        Client client = EntityMapper.toEntity(request);
        Client savedClient = clientRepository.save(client);

        log.info("Client created successfully with ID: {}", savedClient.getId());
        return EntityMapper.toResponse(savedClient);
    }

    @Transactional(readOnly = true)
    public ClientResponse getClientById(Long id) {
        log.info("Fetching client with ID: {}", id);

        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with ID: " + id));

        return EntityMapper.toResponse(client);
    }

    @Transactional(readOnly = true)
    public List<ClientResponse> getAllClients() {
        log.info("Fetching all clients");

        return clientRepository.findAll().stream()
                .map(EntityMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<ClientResponse> searchClients(String searchTerm, Pageable pageable) {
        log.info("Searching clients with term: {}", searchTerm);

        Page<Client> clientPage = clientRepository.searchClients(searchTerm, pageable);
        return clientPage.map(EntityMapper::toResponse);
    }

    public ClientResponse updateClient(Long id, ClientRequest request) {
        log.info("Updating client with ID: {}", id);

        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with ID: " + id));

        // Check if email is being changed and if it's already taken
        if (!client.getEmail().equals(request.getEmail()) &&
                clientRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Client with email " + request.getEmail() + " already exists");
        }

        client.setFirstName(request.getFirstName());
        client.setLastName(request.getLastName());
        client.setEmail(request.getEmail());
        client.setPhone(request.getPhone());
        client.setAddress(request.getAddress());
        client.setCity(request.getCity());
        client.setCountry(request.getCountry());

        Client updatedClient = clientRepository.save(client);
        log.info("Client updated successfully with ID: {}", updatedClient.getId());

        return EntityMapper.toResponse(updatedClient);
    }

    public void deleteClient(Long id) {
        log.info("Deleting client with ID: {}", id);

        if (!clientRepository.existsById(id)) {
            throw new ResourceNotFoundException("Client not found with ID: " + id);
        }

        clientRepository.deleteById(id);
        log.info("Client deleted successfully with ID: {}", id);
    }

    public void deactivateClient(Long id) {
        log.info("Deactivating client with ID: {}", id);

        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with ID: " + id));

        client.setActive(false);
        clientRepository.save(client);

        log.info("Client deactivated successfully with ID: {}", id);
    }
}