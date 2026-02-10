package com.ibrahim.DBPulse.repositories;

import com.ibrahim.DBPulse.entities.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // Find by order number
    Optional<Order> findByOrderNumber(String orderNumber);

    // Find orders by client ID
    List<Order> findByClientId(Long clientId);

    // Find orders by client ID with pagination
    Page<Order> findByClientId(Long clientId, Pageable pageable);

    // Find orders by status
    List<Order> findByStatus(Order.OrderStatus status);

    // Find orders with items (avoiding N+1 query problem)
    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.orderItems WHERE o.id = :id")
    Optional<Order> findByIdWithItems(@Param("id") Long id);

    // Find orders by client with items
    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.orderItems WHERE o.client.id = :clientId")
    List<Order> findByClientIdWithItems(@Param("clientId") Long clientId);

    // Find orders within date range
    @Query("SELECT o FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate")
    List<Order> findOrdersBetweenDates(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    // Find orders by status and date range
    @Query("SELECT o FROM Order o WHERE o.status = :status AND o.createdAt BETWEEN :startDate AND :endDate")
    List<Order> findByStatusAndDateRange(
            @Param("status") Order.OrderStatus status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    // Get total order amount by client
    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.client.id = :clientId")
    BigDecimal getTotalOrderAmountByClient(@Param("clientId") Long clientId);

    // Count orders by status
    long countByStatus(Order.OrderStatus status);

    // Get recent orders (last N days)
    @Query("SELECT o FROM Order o WHERE o.createdAt >= :since ORDER BY o.createdAt DESC")
    List<Order> findRecentOrders(@Param("since") LocalDateTime since);

    // Search orders by order number or client name
    @Query("SELECT o FROM Order o JOIN o.client c WHERE " +
            "LOWER(o.orderNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(c.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(c.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Order> searchOrders(@Param("searchTerm") String searchTerm, Pageable pageable);

    // Get total revenue
    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.status != 'CANCELLED'")
    BigDecimal getTotalRevenue();

    // Get revenue for specific period
    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE " +
            "o.status != 'CANCELLED' AND o.createdAt BETWEEN :startDate AND :endDate")
    BigDecimal getRevenueForPeriod(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}
