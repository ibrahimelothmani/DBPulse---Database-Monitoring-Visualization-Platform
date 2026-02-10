package com.ibrahim.DBPulse.repositories;

import com.ibrahim.DBPulse.entities.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    // Find by email
    Optional<Client> findByEmail(String email);

    // Check if email exists
    boolean existsByEmail(String email);

    // Find active clients
    List<Client> findByActiveTrue();

    // Find clients by country
    List<Client> findByCountry(String country);

    // Find clients by city
    List<Client> findByCity(String city);

    // Search clients by name or email
    @Query("SELECT c FROM Client c WHERE " +
            "LOWER(c.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(c.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(c.email) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Client> searchClients(@Param("searchTerm") String searchTerm, Pageable pageable);

    // Find clients with orders
    @Query("SELECT DISTINCT c FROM Client c LEFT JOIN FETCH c.orders WHERE c.id = :id")
    Optional<Client> findByIdWithOrders(@Param("id") Long id);

    // Count active clients
    long countByActiveTrue();
}
