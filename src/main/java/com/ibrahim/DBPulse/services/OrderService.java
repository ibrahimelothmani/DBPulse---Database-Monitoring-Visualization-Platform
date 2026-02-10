package com.ibrahim.DBPulse.services;

import com.ibrahim.DBPulse.dtos.OrderItemRequest;
import com.ibrahim.DBPulse.dtos.OrderItemResponse;
import com.ibrahim.DBPulse.dtos.OrderRequest;
import com.ibrahim.DBPulse.dtos.OrderResponse;
import com.ibrahim.DBPulse.entities.Client;
import com.ibrahim.DBPulse.entities.Order;
import com.ibrahim.DBPulse.entities.OrderItem;
import com.ibrahim.DBPulse.entities.Product;
import com.ibrahim.DBPulse.exceptions.InsufficientStockException;
import com.ibrahim.DBPulse.exceptions.ResourceNotFoundException;
import com.ibrahim.DBPulse.repositories.ClientRepository;
import com.ibrahim.DBPulse.repositories.OrderItemRepository;
import com.ibrahim.DBPulse.repositories.OrderRepository;
import com.ibrahim.DBPulse.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderService {

        private final OrderRepository orderRepository;
        private final ClientRepository clientRepository;
        private final ProductRepository productRepository;

        public OrderResponse createOrder(OrderRequest request) {
                log.info("Creating new order for client ID: {}", request.getClientId());

                // Validate client exists
                Client client = clientRepository.findById(request.getClientId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Client not found with ID: " + request.getClientId()));

                // Create order
                Order order = new Order();
                order.setClient(client);
                order.setOrderNumber(generateOrderNumber());
                order.setShippingAddress(request.getShippingAddress());
                order.setNotes(request.getNotes());
                order.setStatus(Order.OrderStatus.PENDING);

                // Process order items
                for (OrderItemRequest itemRequest : request.getItems()) {
                        Product product = productRepository.findById(itemRequest.getProductId())
                                        .orElseThrow(() -> new ResourceNotFoundException(
                                                        "Product not found with ID: " + itemRequest.getProductId()));

                        // Check stock
                        if (!product.hasEnoughStock(itemRequest.getQuantity())) {
                                throw new InsufficientStockException(
                                                String.format("Insufficient stock for product %s. Available: %d, Requested: %d",
                                                                product.getName(), product.getStockQuantity(),
                                                                itemRequest.getQuantity()));
                        }

                        // Create order item
                        OrderItem orderItem = OrderItem.createFromProduct(product, itemRequest.getQuantity());
                        order.addOrderItem(orderItem);

                        // Update product stock
                        product.setStockQuantity(product.getStockQuantity() - itemRequest.getQuantity());
                        productRepository.save(product);
                }

                // Calculate total
                order.calculateTotal();

                // Save order
                Order savedOrder = orderRepository.save(order);
                log.info("Order created successfully with order number: {}", savedOrder.getOrderNumber());

                return mapToOrderResponse(savedOrder);
        }

        @Transactional(readOnly = true)
        public OrderResponse getOrderById(Long id) {
                log.info("Fetching order with ID: {}", id);

                Order order = orderRepository.findByIdWithItems(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + id));

                return mapToOrderResponse(order);
        }

        @Transactional(readOnly = true)
        public List<OrderResponse> getAllOrders() {
                log.info("Fetching all orders");

                return orderRepository.findAll().stream()
                                .map(this::mapToOrderResponse)
                                .collect(Collectors.toList());
        }

        @Transactional(readOnly = true)
        public List<OrderResponse> getOrdersByClientId(Long clientId) {
                log.info("Fetching orders for client ID: {}", clientId);

                return orderRepository.findByClientIdWithItems(clientId).stream()
                                .map(this::mapToOrderResponse)
                                .collect(Collectors.toList());
        }

        public OrderResponse updateOrderStatus(Long id, Order.OrderStatus status) {
                log.info("Updating order {} status to {}", id, status);

                Order order = orderRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + id));

                order.setStatus(status);
                Order updatedOrder = orderRepository.save(order);

                log.info("Order status updated successfully");
                return mapToOrderResponse(updatedOrder);
        }

        private String generateOrderNumber() {
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
                String uniqueId = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
                return "ORD-" + timestamp + "-" + uniqueId;
        }

        private OrderResponse mapToOrderResponse(Order order) {
                List<OrderItemResponse> items = order.getOrderItems().stream()
                                .map(item -> new OrderItemResponse(
                                                item.getId(),
                                                item.getProduct().getId(),
                                                item.getProduct().getName(),
                                                item.getQuantity(),
                                                item.getUnitPrice(),
                                                item.getSubtotal()))
                                .collect(Collectors.toList());

                return new OrderResponse(
                                order.getId(),
                                order.getOrderNumber(),
                                order.getClient().getId(),
                                order.getClient().getFullName(),
                                items,
                                order.getTotalAmount(),
                                order.getStatus(),
                                order.getShippingAddress(),
                                order.getNotes(),
                                order.getCreatedAt(),
                                order.getUpdatedAt());
        }
}