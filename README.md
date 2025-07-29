# Tactical Command Hub

[![CI/CD Pipeline](https://github.com/your-org/tactical-command-hub/workflows/CI/CD%20Pipeline/badge.svg)](https://github.com/your-org/tactical-command-hub/actions)
[![codecov](https://codecov.io/gh/your-org/tactical-command-hub/branch/main/graph/badge.svg)](https://codecov.io/gh/your-org/tactical-command-hub)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

A **Java-based Command and Control System Simulator** designed to replicate the functionality of military joint operations platforms like GCCS-J (Global Command and Control System - Joint). This enterprise-level application demonstrates defense contracting software development practices, incorporating real-time tactical unit management, mission planning, multi-domain coordination, and comprehensive security features.

## 🎯 Project Overview

The Tactical Command Hub simulates military command and control operations while demonstrating:
- **Enterprise Java Development Practices**
- **Security Standards for Defense Applications**
- **Real-time Tactical Decision Support Systems**
- **Joint Operations Across Multiple Domains** (land, air, sea, cyber)

### Key Features

- **🔐 Enterprise Security**: JWT-based authentication with role-based access control
- **🚀 Real-time Operations**: WebSocket-based real-time unit tracking and communications
- **🗺️ Geospatial Integration**: Interactive mapping with unit positioning and mission areas
- **📊 Mission Planning**: Comprehensive mission lifecycle management
- **📈 Analytics & Reporting**: Operational dashboards and intelligence reports
- **🐳 Containerized Deployment**: Docker and Docker Compose ready
- **📝 API Documentation**: Complete OpenAPI 3.0 specification

## 🏗️ Architecture

```
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│   Frontend      │    │   API Gateway    │    │   Database      │
│   (Vue.js)      │◄──►│   (Spring Boot)  │◄──►│   (PostgreSQL)  │
└─────────────────┘    └──────────────────┘    └─────────────────┘
                                │
                         ┌──────┴──────┐
                         │             │
                    ┌────▼────┐   ┌────▼────┐
                    │ Redis   │   │ Message │
                    │ Cache   │   │ Queue   │
                    └─────────┘   └─────────┘
```

### Technology Stack

- **Backend**: Java 17, Spring Boot 3.x, Spring Security, Spring Data JPA
- **Database**: PostgreSQL (production), H2 (testing), Flyway migrations
- **Security**: JWT tokens, BCrypt password hashing, RBAC
- **Testing**: JUnit 5, Mockito, TestContainers, Spring Boot Test
- **Documentation**: OpenAPI 3.0, Swagger UI
- **Build**: Maven 3.8+, Docker, Docker Compose
- **Monitoring**: Prometheus, Grafana, Spring Boot Actuator
- **CI/CD**: GitHub Actions, automated testing and deployment

## 🚀 Quick Start

### Prerequisites

- **Java 17** or higher
- **Maven 3.8+**
- **Docker & Docker Compose** (for containerized setup)
- **PostgreSQL 15** (for local development)

### Option 1: Docker Compose (Recommended)

```bash
# Clone the repository
git clone https://github.com/your-org/tactical-command-hub.git
cd tactical-command-hub

# Start all services with Docker Compose
docker-compose up -d

# Check service health
docker-compose ps
```

The application will be available at:
- **API**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **Grafana Dashboard**: http://localhost:3000 (admin/admin123)
- **Prometheus**: http://localhost:9090

### Option 2: Local Development

```bash
# Clone and build
git clone https://github.com/your-org/tactical-command-hub.git
cd tactical-command-hub

# Set up PostgreSQL database
createdb tactical_db

# Configure environment variables
cp .env.example .env
# Edit .env with your database credentials

# Build and run
mvn clean compile
mvn spring-boot:run
```

### Option 3: IDE Setup

1. **Import Project**: Import as Maven project in your IDE
2. **Configure JDK**: Set project JDK to Java 17+
3. **Database Setup**: Configure PostgreSQL connection in `application.yml`
4. **Run Application**: Execute `TacticalCommandHubApplication.main()`

## 🔑 Authentication

The system uses JWT-based authentication with the following default users:

| Username | Password | Role | Description |
|----------|----------|------|-------------|
| `admin` | `admin123` | COMMANDER | Full system access |
| `commander1` | `admin123` | COMMANDER | Mission command authority |
| `operator1` | `admin123` | OPERATOR | Operational access |
| `analyst1` | `admin123` | ANALYST | Intelligence analysis |
| `viewer1` | `admin123` | VIEWER | Read-only access |

### Login Example

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

Response:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "username": "admin",
  "fullName": "System Administrator",
  "roles": ["COMMANDER"],
  "expiresIn": 86400
}
```

## 📊 API Usage Examples

### Create Military Unit

```bash
curl -X POST http://localhost:8080/api/v1/units \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "callsign": "ALPHA-1",
    "unitName": "1st Infantry Division",
    "unitType": "INFANTRY",
    "domain": "LAND",
    "status": "ACTIVE",
    "readinessLevel": "C1",
    "latitude": 39.048667,
    "longitude": -76.886944,
    "personnelCount": 250
  }'
```

### Get All Units

```bash
curl -X GET "http://localhost:8080/api/v1/units?page=0&size=10" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Update Unit Position

```bash
curl -X PUT http://localhost:8080/api/v1/units/1/position \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "latitude": 39.050000,
    "longitude": -76.890000,
    "altitude": 150.0
  }'
```

## 🧪 Testing

### Run Unit Tests

```bash
mvn test
```

### Run Integration Tests

```bash
mvn verify -P integration-tests
```

### Run with Coverage

```bash
mvn clean test jacoco:report
# Coverage report: target/site/jacoco/index.html
```

### Test with TestContainers

```bash
mvn verify -P testcontainers
```

## 📋 Database Schema

### Core Entities

- **military_units**: Tactical unit information and positioning
- **missions**: Mission planning and execution tracking
- **mission_waypoints**: Navigation points for mission routes
- **mission_reports**: Real-time mission status updates
- **unit_status_history**: Audit trail for unit status changes
- **users**: System user accounts
- **roles**: Role-based access control definitions

### Sample Data

The application includes comprehensive sample data with:
- **8 Military Units** across different domains (Land, Air, Sea, Cyber)
- **4 Active Missions** with various status levels
- **20+ Mission Waypoints** with navigation sequences
- **Multiple Mission Reports** showing operational progression
- **5 System Users** with different role assignments

## 🔧 Configuration

### Environment Variables

```bash
# Database Configuration
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/tactical_db
SPRING_DATASOURCE_USERNAME=tactical_user
SPRING_DATASOURCE_PASSWORD=tactical_pass

# JWT Configuration
TACTICAL_JWT_SECRET=your-secret-key-here
TACTICAL_JWT_EXPIRATION=86400

# Redis Configuration (Optional)
SPRING_REDIS_HOST=localhost
SPRING_REDIS_PORT=6379

# Monitoring Configuration
MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE=health,info,metrics,prometheus
```

### Profiles

- **`default`**: Local development with H2 database
- **`dev`**: Development environment with PostgreSQL
- **`test`**: Testing environment with H2
- **`prod`**: Production environment with full security
- **`docker`**: Docker containerized deployment

## 🚀 Deployment

### Production Deployment

```bash
# Build production image
docker build -t tactical-command-hub:latest .

# Deploy with production settings
docker run -d \
  --name tactical-command-hub \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://prod-db:5432/tactical_db \
  tactical-command-hub:latest
```

### Kubernetes Deployment

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: tactical-command-hub
spec:
  replicas: 3
  selector:
    matchLabels:
      app: tactical-command-hub
  template:
    metadata:
      labels:
        app: tactical-command-hub
    spec:
      containers:
      - name: tactical-command-hub
        image: tactical-command-hub:latest
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "prod"
```

## 📊 Monitoring & Observability

### Health Checks

- **Application Health**: `/actuator/health`
- **Database Connectivity**: Automatic health indicators
- **Custom Health Checks**: Service-specific health endpoints

### Metrics

- **Prometheus Metrics**: `/actuator/prometheus`
- **JVM Metrics**: Memory, GC, thread pool usage
- **Application Metrics**: Custom business metrics
- **Database Metrics**: Connection pool, query performance

### Logging

- **Structured Logging**: JSON format for production
- **Security Audit Logs**: All authentication and authorization events
- **Performance Logs**: Request/response times and database queries
- **Error Tracking**: Centralized error collection and alerting

## 🔒 Security Features

### Authentication & Authorization

- **JWT Token-based Authentication**
- **Role-based Access Control (RBAC)**
- **Password Encryption** with BCrypt
- **Token Refresh** mechanism
- **Session Management**

### Security Measures

- **HTTPS/TLS 1.3** encryption
- **CORS Configuration** for cross-origin requests
- **SQL Injection Protection** via parameterized queries
- **XSS Prevention** through input validation
- **CSRF Protection** for state-changing operations
- **Security Headers** (HSTS, X-Frame-Options, etc.)

### Audit & Compliance

- **Comprehensive Audit Logging**
- **Data Encryption** at rest and in transit
- **Access Control Matrix** documentation
- **Regular Security Assessments**
- **OWASP Top 10 Compliance**

## 📈 Performance Specifications

### Response Time Targets

- **API Endpoints**: < 200ms for 95th percentile
- **Database Queries**: < 50ms for simple queries
- **Authentication**: < 100ms for token validation

### Scalability

- **Concurrent Users**: 1000+ simultaneous users
- **Request Throughput**: 10,000+ requests per minute
- **Database Connections**: Optimized connection pooling
- **Horizontal Scaling**: Load balancer ready

### Availability

- **Uptime Target**: 99.9% availability
- **Health Monitoring**: Continuous health checks
- **Graceful Degradation**: Fallback mechanisms
- **Disaster Recovery**: Automated backup and recovery

## 🤝 Contributing

We welcome contributions! Please see our [Contributing Guidelines](CONTRIBUTING.md) for details.

### Development Workflow

1. **Fork** the repository
2. **Create** a feature branch (`git checkout -b feature/amazing-feature`)
3. **Commit** your changes (`git commit -m 'Add some amazing feature'`)
4. **Push** to the branch (`git push origin feature/amazing-feature`)
5. **Open** a Pull Request

### Code Standards

- **Google Java Style Guide** formatting
- **90%+ Test Coverage** requirement
- **Comprehensive Javadoc** documentation
- **Security Review** for all changes
- **Performance Testing** for significant features

## 📚 Documentation

- **[API Documentation](docs/api/)**: Complete OpenAPI specifications
- **[Architecture Guide](docs/architecture/)**: System design and components
- **[User Guides](docs/user-guides/)**: End-user documentation
- **[Deployment Guide](docs/deployment/)**: Production deployment instructions
- **[Security Guide](docs/security/)**: Security implementation details

## 🐛 Troubleshooting

### Common Issues

**Database Connection Issues**
```bash
# Check PostgreSQL service
sudo systemctl status postgresql
# Verify connection
psql -h localhost -U tactical_user -d tactical_db
```

**JWT Token Issues**
```bash
# Verify token in JWT debugger
echo "YOUR_TOKEN" | base64 -d
```

**Docker Issues**
```bash
# Check container logs
docker logs tactical-command-hub
# Restart services
docker-compose restart
```

### Support

- **Documentation**: Check the [docs/](docs/) directory
- **Issues**: [GitHub Issues](https://github.com/your-org/tactical-command-hub/issues)
- **Discussions**: [GitHub Discussions](https://github.com/your-org/tactical-command-hub/discussions)

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- **Spring Boot Team** for the excellent framework
- **PostgreSQL Community** for reliable database technology
- **Defense Industry Standards** for security and operational requirements
- **Open Source Community** for the tools and libraries that make this possible

---

**Tactical Command Hub** - Simulating Military Command and Control Operations with Enterprise Java Excellence

*Last Updated: July 2025*
