# DBPulse - Database Monitoring & Visualization Platform

[![Java](https://img.shields.io/badge/Java-21-orange)]()
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.2-green)]()
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue)]()
[![Prometheus](https://img.shields.io/badge/Prometheus-2.48-red)]()
[![Grafana](https://img.shields.io/badge/Grafana-10.2-orange)]()
[![Docker](https://img.shields.io/badge/Docker-Compose-blue)]()
[![License](https://img.shields.io/badge/license-MIT-blue.svg)]()

> **Production-ready database monitoring and observability platform** built with Spring Boot, Prometheus, and Grafana. Features a functional e-commerce backend that generates realistic database activity and meaningful metrics.

---

## 🎯 What Is DBPulse?

DBPulse is more than just a monitoring setup - it's a **complete observability platform** that demonstrates real-world monitoring practices through a functional e-commerce application.

### Core Features

- ✅ **Full E-Commerce REST API** - Clients, Products, Orders management
- 📊 **Custom Business Metrics** - Order tracking, revenue monitoring, inventory alerts
- 🔍 **Performance Monitoring** - @Timed annotations on critical operations
- 💾 **Database Metrics** - Connection pool, query performance, slow query detection
- 📈 **Grafana Dashboards** - Pre-configured visualizations for all metrics
- 🚨 **Prometheus Alerts** - Automated monitoring with 11+ alert rules
- 🐳 **Docker Compose** - One-command deployment of entire stack

---

## 🏗️ Architecture

```
┌─────────────────┐
│  Load Testing   │
│     Script      │
└────────┬────────┘
         │ HTTP Requests
         ▼
┌─────────────────────────┐
│  Spring Boot App :8080  │
│  - REST API             │
│  - Custom Metrics       │
│  - @Timed Methods       │
└────────┬────────────────┘
         │ /actuator/prometheus
         ▼
┌─────────────────────────┐
│  Prometheus :9090       │
│  - Scrapes every 15s    │
│  - Stores time-series   │
│  - Evaluates alerts     │
└────────┬────────────────┘
         │ PromQL queries
         ▼
┌─────────────────────────┐
│    Grafana :3000        │
│  - 4 Pre-configured     │
│    Dashboards           │
└─────────────────────────┘
         ▲
         │ Queries
┌─────────────────────────┐
│   PostgreSQL :5432      │
│  - Application Database │
│  - HikariCP Pool        │
└─────────────────────────┘
```

---

## 🚀 Quick Start

### Prerequisites

- **Java 21 LTS** (or higher)
- **Docker & Docker Compose** -**Maven 3.6+** (or use included wrapper)
- **Git**

### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/DBPulse.git
cd DBPulse
```

### 2. Start the Stack

```bash
docker-compose up -d
```

This will start:

- PostgreSQL database (port 5432)
- Spring Boot application (port 8080)
- Prometheus (port 9090)
- Grafana (port 3000)

### 3. Wait for Services to Initialize

```bash
# Check all services are running
docker-compose ps

# Watch application logs
docker-compose logs -f application
```

### 4. Generate Test Data & Metrics

```bash
./scripts/load_test.sh
```

This creates:

- 50 test clients
- 30 products
- 100 orders
- 250+ read operations

### 5. View Dashboards

- **Application**: http://localhost:8080
- **Swagger API**: http://localhost:8080/swagger-ui.html
- **Prometheus**: http://localhost:9090
- **Grafana**: http://localhost:3000 (admin/admin)

---

## 📊 Available Metrics

### Business Metrics

- `dbpulse.orders.created` - Total orders created
- `dbpulse.revenue.total` - Total revenue
- `dbpulse.inventory.total` - Current total inventory
- `dbpulse.clients.active` - Active client count
- `dbpulse.products.low_stock` - Products below threshold

### Performance Metrics

- `dbpulse.orders.create` - Order creation duration
- `dbpulse.clients.create/get/update` - Client operation duration
- `dbpulse.products.create/update` - Product operation duration
- `http_server_requests_seconds` - HTTP request metrics

### JVM Metrics

- `jvm_memory_used_bytes` - JVM memory usage
- `jvm_gc_pause_seconds` - Garbage collection time
- `jvm_threads_live` - Thread count

### Database Metrics (HikariCP)

- `hikaricp_connections_active` - Active connections
- `hikaricp_connections_idle` - Idle connections
- `hikaricp_connections_acquire_seconds` - Connection acquisition time

---

## 🎨 Grafana Dashboards

Pre-configured dashboards available at http://localhost:3000:

1. **Database Performance** - Connection pool, query times, slow queries
2. **Application Health** - Request rate, latency, error rate, JVM metrics
3. **Business Metrics** - Orders/min, revenue trends, inventory levels
4. **JVM Metrics** - Heap usage, GC activity, thread count

---

## 🚨 Alert Rules

11 pre-configured alerts in Prometheus:

- High HTTP 5xx error rate (>5%)
- Slow HTTP requests (P95 > 2s)
- Application down
- Low inventory products (>5 products)
- Slow order creation (P95 > 3s)
- High JVM memory usage (>90%)
- High GC time
- Connection pool near exhaustion (>80%)
- Long connection wait time

---

## 🛠️ Development

### Run Locally (Without Docker)

1. **Start PostgreSQL**:

```bash
docker run -d -p 5432:5432 \
  -e POSTGRES_DB=dbpulse \
  -e POSTGRES_USER=dbpulse_user \
  -e POSTGRES_PASSWORD=dbpulse_pass \
  postgres:15-alpine
```

2. **Run Application**:

```bash
mvn spring-boot:run
```

3. **Access Endpoints**:

- API: http://localhost:8080
- Metrics: http://localhost:8080/actuator/prometheus
- Swagger: http://localhost:8080/swagger-ui.html

### Run Tests

```bash
mvn test
```

### Build Docker Image

```bash
docker build -t dbpulse:latest .
```

---

## 📁 Project Structure

```
DBPulse/
├── src/main/java/com/ibrahim/DBPulse/
│   ├── config/          # Configuration classes (Metrics, CORS, OpenAPI, etc.)
│   ├── controllers/     # REST API controllers
│   ├── services/        # Business logic with @Timed annotations
│   ├── repositories/    # JPA repositories
│   ├── entities/        # JPA entities
│   ├── dtos/           # Request/Response DTOs
│   ├── mappers/        # Entity-DTO mappers
│   └── exceptions/     # Custom exceptions
├── monitoring/
│   ├── prometheus/
│   │   ├── prometheus.yml   # Prometheus configuration
│   │   └── alerts.yml       # Alert rules
│   └── grafana/
│       └── provisioning/
│           ├── datasources/ # Datasource config
│           └── dashboards/  # Dashboard JSONs
├── scripts/
│   └── load_test.sh    # Load testing script
├── Dockerfile
├── docker-compose.yml
└── pom.xml
```

---

## 🎓 What You'll Learn

This project teaches:

### DevOps & Monitoring

- Application monitoring with Prometheus
- Metrics exposure with Spring Boot Actuator
- Grafana dashboard creation
- Alert rule configuration
- Docker containerization
- Observability best practices

### Backend Development

- Spring Boot application architecture
- REST API design with DTOs
- JPA/Hibernate operations
- Transaction management
- Exception handling patterns
- Service layer design (SOLID principles)

### Database Skills

- Connection pool optimization (HikariCP)
- Query performance monitoring
- Database metrics interpretation
- N+1 query problem solutions

---

## 🤝 Contributing

Contributions are welcome! See [CONTRIBUTING.md](CONTRIBUTING.md) for details.

### Ideas for Contributions

- Add GraphQL API
- Implement Spring Security with JWT
- Add Redis caching with cache metrics
- Create Kubernetes deployment files
- Add distributed tracing with Zipkin
- Implement message queue (RabbitMQ/Kafka)
- Add machine learning for anomaly detection
- Create CI/CD pipeline with GitHub Actions

---

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 🌟 Star History

If you find this project helpful, please consider giving it a star! ⭐

---

## 📧 Contact

- **Author**: Your Name
- **Email**: your.email@example.com
- **GitHub**: [@yourusername](https://github.com/yourusername)

---

## 🙏 Acknowledgments

- Spring Boot team for excellent documentation
- Prometheus & Grafana communities
- All contributors to this project

---

**Built with ❤️ for the DevOps community**
