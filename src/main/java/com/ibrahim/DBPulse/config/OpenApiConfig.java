package com.ibrahim.DBPulse.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI/Swagger configuration for API documentation.
 * Access documentation at: http://localhost:8080/swagger-ui.html
 */
@Configuration
public class OpenApiConfig {

    @Value("${spring.application.name:DBPulse}")
    private String applicationName;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title(applicationName + " API Documentation")
                        .version("1.0.0")
                        .description(
                                """
                                        # DBPulse - Database Monitoring & Visualization Platform

                                        A comprehensive REST API for managing clients, products, and orders with real-time
                                        database monitoring and metrics visualization capabilities.

                                        ## Features
                                        - **Client Management**: Full CRUD operations for client data
                                        - **Product Management**: Inventory management with stock tracking
                                        - **Order Management**: Order creation, tracking, and status updates
                                        - **Search & Pagination**: Advanced search capabilities across all entities
                                        - **Metrics & Monitoring**: Built-in Prometheus metrics and health checks

                                        ## Authentication
                                        Currently, the API is open. In production, add JWT-based authentication.

                                        ## Error Handling
                                        All endpoints return standardized error responses with proper HTTP status codes:
                                        - `400 Bad Request` - Validation errors or business logic violations
                                        - `404 Not Found` - Resource not found
                                        - `409 Conflict` - Duplicate resource constraints
                                        - `500 Internal Server Error` - Unexpected server errors
                                        """)
                        .contact(new Contact()
                                .name("Ibrahim El Othmani")
                                .email("support@dbpulse.com")
                                .url("https://github.com/ibrahimelothmani/DBPulse"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Development Server"),
                        new Server()
                                .url("https://api.dbpulse.com")
                                .description("Production Server (Future)")));
    }
}
