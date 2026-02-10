package com.ibrahim.DBPulse.mappers;

import com.ibrahim.DBPulse.dtos.ClientRequest;
import com.ibrahim.DBPulse.dtos.ClientResponse;
import com.ibrahim.DBPulse.dtos.ProductRequest;
import com.ibrahim.DBPulse.dtos.ProductResponse;
import com.ibrahim.DBPulse.entities.Client;
import com.ibrahim.DBPulse.entities.Product;

/**
 * Utility class for mapping between entities and DTOs.
 * Provides static methods for bidirectional conversions.
 */
public class EntityMapper {

    private EntityMapper() {
        // Private constructor to prevent instantiation
    }

    // ==================== Client Mappings ====================

    /**
     * Convert ClientRequest to Client entity.
     */
    public static Client toEntity(ClientRequest request) {
        if (request == null) {
            return null;
        }

        Client client = new Client();
        client.setFirstName(request.getFirstName());
        client.setLastName(request.getLastName());
        client.setEmail(request.getEmail());
        client.setPhone(request.getPhone());
        client.setAddress(request.getAddress());
        client.setCity(request.getCity());
        client.setCountry(request.getCountry());

        return client;
    }

    /**
     * Convert Client entity to ClientResponse.
     */
    public static ClientResponse toResponse(Client client) {
        if (client == null) {
            return null;
        }

        return ClientResponse.builder()
                .id(client.getId())
                .firstName(client.getFirstName())
                .lastName(client.getLastName())
                .email(client.getEmail())
                .phone(client.getPhone())
                .address(client.getAddress())
                .city(client.getCity())
                .country(client.getCountry())
                .active(client.getActive())
                .fullName(client.getFullName())
                .createdAt(client.getCreatedAt())
                .updatedAt(client.getUpdatedAt())
                .build();
    }

    // ==================== Product Mappings ====================

    /**
     * Convert ProductRequest to Product entity.
     */
    public static Product toEntity(ProductRequest request) {
        if (request == null) {
            return null;
        }

        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setSku(request.getSku());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setCategory(request.getCategory());

        return product;
    }

    /**
     * Convert Product entity to ProductResponse.
     */
    public static ProductResponse toResponse(Product product) {
        if (product == null) {
            return null;
        }

        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setSku(product.getSku());
        response.setPrice(product.getPrice());
        response.setStockQuantity(product.getStockQuantity());
        response.setCategory(product.getCategory());
        response.setActive(product.getActive());
        response.setInStock(product.isInStock());
        response.setCreatedAt(product.getCreatedAt());
        response.setUpdatedAt(product.getUpdatedAt());

        return response;
    }
}
