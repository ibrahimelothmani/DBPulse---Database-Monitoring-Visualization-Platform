package com.ibrahim.DBPulse.config;

import com.ibrahim.DBPulse.repositories.ClientRepository;
import com.ibrahim.DBPulse.repositories.OrderRepository;
import com.ibrahim.DBPulse.repositories.ProductRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Custom Micrometer metrics configuration for DBPulse.
 * Provides business metrics for monitoring orders, revenue, inventory, and clients.
 */
@Configuration
@Slf4j
public class MetricsConfig {

    /**
     * Add common tags to all metrics for easier filtering in Grafana.
     */
    @Bean
    public MeterBinder metricsCommonTags() {
        return (registry) -> registry.config()
                .commonTags(
                        "application", "dbpulse",
                        "environment", "production"
                );
    }

    /**
     * Counter for total orders created.
     * Incremented in OrderService.createOrder()
     */
    @Bean
    public Counter orderCreatedCounter(MeterRegistry registry) {
        return Counter.builder("dbpulse.orders.created")
                .description("Total number of orders created")
                .tag("type", "business")
                .register(registry);
    }

    /**
     * Counter for total revenue.
     * Incremented by order total amount in OrderService.createOrder()
     */
    @Bean
    public Counter revenueCounter(MeterRegistry registry) {
        return Counter.builder("dbpulse.revenue.total")
                .description("Total revenue from all orders")
                .baseUnit("currency")
                .tag("type", "business")
                .register(registry);
    }

    /**
     * Gauge for current total inventory across all products.
     * Updated automatically by Micrometer.
     */
    @Bean
    public Gauge inventoryGauge(MeterRegistry registry, ProductRepository productRepository) {
        return Gauge.builder("dbpulse.inventory.total", productRepository,
                        repo -> {
                            try {
                                return repo.findAll().stream()
                                        .mapToLong(product -> product.getStockQuantity() != null ? product.getStockQuantity() : 0)
                                        .sum();
                            } catch (Exception e) {
                                log.warn("Error calculating total inventory", e);
                                return 0;
                            }
                        })
                .description("Total inventory count across all products")
                .tag("type", "business")
                .register(registry);
    }

    /**
     * Gauge for number of active clients.
     */
    @Bean
    public Gauge activeClientsGauge(MeterRegistry registry, ClientRepository clientRepository) {
        return Gauge.builder("dbpulse.clients.active", clientRepository,
                        repo -> {
                            try {
                                return repo.countByActiveTrue();
                            } catch (Exception e) {
                                log.warn("Error counting active clients", e);
                                return 0;
                            }
                        })
                .description("Number of currently active clients")
                .tag("type", "business")
                .register(registry);
    }

    /**
     * Gauge for total number of orders.
     */
    @Bean
    public Gauge totalOrdersGauge(MeterRegistry registry, OrderRepository orderRepository) {
        return Gauge.builder("dbpulse.orders.total", orderRepository,
                        repo -> {
                            try {
                                return repo.count();
                            } catch (Exception e) {
                                log.warn("Error counting total orders", e);
                                return 0;
                            }
                        })
                .description("Total number of orders in the system")
                .tag("type", "business")
                .register(registry);
    }

    /**
     * Gauge for low stock products count.
     */
    @Bean
    public Gauge lowStockProductsGauge(MeterRegistry registry, ProductRepository productRepository) {
        return Gauge.builder("dbpulse.products.low_stock", productRepository,
                        repo -> {
                            try {
                                return repo.findLowStockProducts(10).size();
                            } catch (Exception e) {
                                log.warn("Error counting low stock products", e);
                                return 0;
                            }
                        })
                .description("Number of products with stock below threshold (10)")
                .tag("type", "inventory")
                .tag("threshold", "10")
                .register(registry);
    }
}
