# DBPulse - Database Monitoring & Visualization Platform

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen)
![Prometheus](https://img.shields.io/badge/Prometheus-Latest-orange)
![Grafana](https://img.shields.io/badge/Grafana-Latest-blue)
![License](https://img.shields.io/badge/License-MIT-yellow)

A comprehensive DevOps solution for real-time database monitoring and visualization using Spring Boot, Prometheus, and Grafana. This project demonstrates production-grade observability practices for database performance metrics, connection pool monitoring, and query analytics.

## 🎯 Project Overview

DBPulse provides a complete monitoring stack that collects, stores, and visualizes critical database metrics in real-time. It helps developers and DevOps teams identify performance bottlenecks, track resource utilization, and maintain database health in production environments.

## ✨ Features

- **Real-time Metrics Collection**: Automated collection of database performance metrics
- **Connection Pool Monitoring**: Track active, idle, and waiting connections
- **Query Performance Analysis**: Monitor query execution times and identify slow queries
- **Transaction Tracking**: Measure transaction counts and duration
- **Resource Utilization**: CPU, memory, and disk usage monitoring
- **Custom Dashboards**: Pre-configured Grafana dashboards for instant insights
- **Alert Management**: Configurable alerts for critical thresholds
- **Scalable Architecture**: Containerized deployment with Docker

## 🏗️ Architecture

```
┌─────────────────┐
│  Spring Boot    │
│  Application    │ ──► Exposes metrics at /actuator/prometheus
└─────────────────┘
         │
         ▼
┌─────────────────┐
│   Prometheus    │ ──► Scrapes and stores time-series data
└─────────────────┘
         │
         ▼
┌─────────────────┐
│    Grafana      │ ──► Visualizes metrics with dashboards
└─────────────────┘
```

## 🚀 Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- Docker & Docker Compose
- Your database (PostgreSQL, MySQL, etc.)

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/dbpulse.git
   cd dbpulse
   ```

2. **Configure database connection**
   
   Edit `src/main/resources/application.yml`:
   ```yaml
   spring:
     datasource:
       url: jdbc:postgresql://localhost:5432/yourdb
       username: your_username
       password: your_password
   ```

3. **Build the application**
   ```bash
   mvn clean install
   ```

4. **Start the monitoring stack**
   ```bash
   docker-compose up -d
   ```

5. **Run the Spring Boot application**
   ```bash
   mvn spring-boot:run
   ```

### Access the Services

- **Spring Boot Application**: http://localhost:8080
- **Prometheus**: http://localhost:9090
- **Grafana**: http://localhost:3000 (default credentials: admin/admin)

## 📊 Monitored Metrics

### Database Metrics
- Connection pool status (active, idle, max, min)
- Query execution time (avg, max, percentiles)
- Transaction rate and duration
- Slow query count
- Error rates and exceptions
- Database size and growth

### Application Metrics
- JVM memory usage
- Garbage collection statistics
- Thread pool metrics
- HTTP request rates
- API endpoint performance

## 🔧 Configuration

### Prometheus Configuration

The `prometheus.yml` file defines scraping intervals and targets:

```yaml
scrape_configs:
  - job_name: 'spring-boot-app'
    metrics_path: '/actuator/prometheus'
    scrape_interval: 15s
    static_configs:
      - targets: ['host.docker.internal:8080']
```

### Grafana Dashboards

Pre-configured dashboards are available in the `grafana/dashboards/` directory:
- `database-overview.json` - Main database metrics
- `connection-pool.json` - Connection pool analysis
- `query-performance.json` - Query execution metrics

## 🐳 Docker Deployment

```bash
# Start all services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop all services
docker-compose down
```

## 📈 Usage Examples

### Viewing Metrics

1. Navigate to Grafana (http://localhost:3000)
2. Add Prometheus as a data source
3. Import the provided dashboard JSON files
4. Start monitoring your database in real-time

### Setting Up Alerts

Configure alert rules in `prometheus/alerts.yml`:

```yaml
groups:
  - name: database_alerts
    rules:
      - alert: HighConnectionPoolUsage
        expr: hikaricp_connections_active / hikaricp_connections_max > 0.8
        for: 5m
        annotations:
          summary: "Connection pool usage is above 80%"
```

## 🛠️ Tech Stack

| Technology | Purpose |
|-----------|---------|
| Spring Boot | Application framework and metrics exposure |
| Micrometer | Metrics collection and instrumentation |
| Prometheus | Time-series database for metrics storage |
| Grafana | Visualization and dashboarding |
| Docker | Containerization and deployment |
| HikariCP | Connection pool monitoring |

## 📁 Project Structure

```
dbpulse/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/yourcompany/dbpulse/
│   │   │       ├── config/          # Metrics configuration
│   │   │       ├── controller/      # REST endpoints
│   │   │       ├── repository/      # Database access
│   │   │       └── service/         # Business logic
│   │   └── resources/
│   │       ├── application.yml
│   │       └── prometheus/
│   └── test/
├── grafana/
│   ├── dashboards/                  # Grafana dashboard JSON
│   └── provisioning/                # Auto-provisioning configs
├── prometheus/
│   ├── prometheus.yml               # Prometheus config
│   └── alerts.yml                   # Alert rules
├── docker-compose.yml
├── Dockerfile
├── pom.xml
└── README.md
```

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request


## 🙏 Acknowledgments

- Spring Boot team for the excellent actuator framework
- Prometheus community for robust monitoring solutions
- Grafana Labs for powerful visualization tools

## 🗺️ Roadmap

- [ ] Add support for multiple database types
- [ ] Implement custom metric collectors
- [ ] Create mobile-responsive dashboards
- [ ] Add machine learning-based anomaly detection
- [ ] Integrate with popular alerting platforms (PagerDuty, Slack)
- [ ] Develop REST API for metric queries
- [ ] Add historical trend analysis
