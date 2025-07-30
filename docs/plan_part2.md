## Project Structure

```
tactical-command-hub/
├── .github/
│   ├── workflows/
│   ├── ISSUE_TEMPLATE/
│   ├── PULL_REQUEST_TEMPLATE/
│   ├── CODEOWNERS
│   ├── CONTRIBUTING.md
│   └── SECURITY.md
├── .copilot/
│   ├── copilot.yml
│   └── prompts/
├── .vscode/
│   ├── settings.json
│   ├── extensions.json
│   ├── launch.json
│   └── tasks.json
├── src/
│   ├── main/
│   │   ├── java/com/tacticalcommand/tactical/
│   │   └── resources/
│   └── test/
│       ├── java/
│       └── resources/
├── docs/
│   ├── project-plan.md
│   ├── api/
│   ├── architecture/
│   └── user-guides/
├── scripts/
│   ├── build.sh
│   ├── deploy.sh
│   ├── test.sh
│   └── setup.sh
├── data/
│   ├── sample/
│   ├── schemas/
│   └── fixtures/
├── assets/
│   ├── images/
│   ├── icons/
│   └── templates/
├── docker/
├── .gitignore
├── .editorconfig
├── pom.xml
├── Dockerfile
├── docker-compose.yml
├── README.md
└── CHANGELOG.md
```

## .gitignore

```gitignore
# Compiled class files
*.class

# Log files
*.log

# Package Files
*.jar
*.war
*.nar
*.ear
*.zip
*.tar.gz
*.rar

# Maven
target/
pom.xml.tag
pom.xml.releaseBackup
pom.xml.versionsBackup
pom.xml.next
release.properties
dependency-reduced-pom.xml
buildNumber.properties
.mvn/timing.properties
.mvn/wrapper/maven-wrapper.jar

# IDE Files
.idea/
*.iml
*.ipr
*.iws
.vscode/launch.json
.vscode/tasks.json
.settings/
.project
.classpath
.factorypath

# OS Files
.DS_Store
.DS_Store?
._*
.Spotlight-V100
.Trashes
ehthumbs.db
Thumbs.db

# Runtime
*.pid
*.seed
*.log

# Database
*.db
*.sqlite
*.sqlite3

# Environment
.env
.env.local
.env.development.local
.env.test.local
.env.production.local

# Temporary files
*.tmp
*.temp
.cache/
node_modules/

# Docker
.docker/

# Security
*.pem
*.key
secrets/
credentials/

# Build outputs
build/
dist/
out/

# Test outputs
coverage/
test-results/
*.coverage

# Python
__pycache__/
*.py[cod]
*$py.class
*.so
.Python
pip-log.txt
pip-delete-this-directory.txt

# C++
*.o
*.obj
*.exe
*.dll
*.so
*.dylib
```

## .vscode/settings.json

```json
{
  "chat.tools.autoApprove": true,
  "chat.agent.maxRequests": 100,
  
  // Java Settings
  "java.configuration.detectJvmArguments": false,
  "java.configuration.runtimes": [],
  "java.compile.nullAnalysis.mode": "automatic",
  "java.debug.settings.enableRunDebugCodeLens": true,
  "java.format.settings.url": "",
  "java.format.settings.profile": "GoogleStyle",
  "java.saveActions.organizeImports": true,
  "java.sources.organizeImports.starThreshold": 5,
  "java.sources.organizeImports.staticStarThreshold": 3,
  
  // Java Naming Conventions
  "java.completion.favoriteStaticMembers": [
    "org.junit.jupiter.api.Assertions.*",
    "org.mockito.Mockito.*"
  ],
  
  // Python Settings
  "python.defaultInterpreterPath": "./venv/bin/python",
  "python.formatting.provider": "black",
  "python.formatting.blackArgs": ["--line-length=88"],
  "python.linting.enabled": true,
  "python.linting.pylintEnabled": true,
  "python.linting.flake8Enabled": true,
  "python.analysis.typeCheckingMode": "strict",
  
  // Python Naming Standards
  "python.analysis.autoImportCompletions": true,
  "python.analysis.completeFunctionParens": true,
  
  // C++ Settings
  "C_Cpp.default.cppStandard": "c++17",
  "C_Cpp.default.cStandard": "c11",
  "C_Cpp.default.intelliSenseMode": "linux-gcc-x64",
  "C_Cpp.clang_format_style": "Google",
  "C_Cpp.formatting": "clangFormat",
  
  // File Naming Conventions
  "files.associations": {
    "*.java": "java",
    "*.py": "python",
    "*.cpp": "cpp",
    "*.hpp": "cpp",
    "*.h": "c"
  },
  
  // General Editor Settings
  "editor.formatOnSave": true,
  "editor.codeActionsOnSave": {
    "source.organizeImports": true,
    "source.fixAll": true
  },
  "editor.rulers": [80, 120],
  "editor.tabSize": 4,
  "editor.insertSpaces": true,
  "editor.detectIndentation": false,
  
  // File Naming Standards
  "explorer.fileNesting.enabled": true,
  "explorer.fileNesting.patterns": {
    "*.java": "${capture}.class",
    "*.cpp": "${capture}.o, ${capture}.obj",
    "pom.xml": "pom.xml.*"
  },
  
  // Auto-completion
  "editor.suggestSelection": "first",
  "editor.quickSuggestions": {
    "other": true,
    "comments": false,
    "strings": false
  },
  
  // Version Control
  "git.enableSmartCommit": true,
  "git.confirmSync": false,
  "git.autofetch": true,
  
  // Terminal
  "terminal.integrated.defaultProfile.linux": "bash",
  "terminal.integrated.fontSize": 14,
  
  // Workspace Trust
  "security.workspace.trust.untrustedFiles": "open",
  
  // Extensions
  "extensions.ignoreRecommendations": false,
  "extensions.showRecommendationsOnlyOnDemand": false
}
```

## docs/project-plan.md

```markdown
# Tactical Command Hub - Project Plan

## Project Overview

The **Tactical Command Hub** is a Java-based Command and Control System Simulator designed to replicate the functionality of military joint operations platforms like GCCS-J (Global Command and Control System - Joint). This enterprise-level application demonstrates defense contracting software development practices, incorporating real-time tactical unit management, mission planning, multi-domain coordination, and comprehensive security features.

### Project Purpose
- Simulate military command and control operations
- Demonstrate enterprise Java development practices
- Implement security standards for defense applications
- Provide real-time tactical decision support systems
- Support joint operations across multiple domains (land, air, sea, cyber)

---

## Phase 1: Project Foundation & Setup
**Timeline: Weeks 1-2**

### Development Environment & Infrastructure
- ✅ **Initialize Maven project structure with proper dependency management**
  - Options: Spring Boot parent POM vs custom parent, BOM management
  - Action: Set up multi-module Maven structure with core, api, and web modules
  - Standards: Java 17+, Maven 3.8+, Spring Boot 3.x
  - **Status**: COMPLETED - Maven project with Spring Boot 3.2.0, Java 17 configured

- ✅ **Configure development tooling and IDE settings**
  - Options: IntelliJ IDEA Ultimate vs VS Code with Java extensions
  - Action: Standardize VS Code settings for team consistency, configure Checkstyle/SpotBugs
  - Standards: Google Java Style Guide, 80-character line limit
  - **Status**: COMPLETED - VS Code settings configured with chat tools and Java development

- ✅ **Establish version control workflow and branching strategy**
  - Options: GitFlow vs GitHub Flow vs custom branching model
  - Action: Implement GitFlow with develop/feature/release/hotfix branches
  - Standards: Conventional commit messages, signed commits for security
  - **Status**: COMPLETED - Git repository with GitHub integration active

- ✅ **Set up Docker containerization for local development**
  - Options: Multi-stage builds vs separate development containers
  - Action: Create Dockerfile and docker-compose.yml for PostgreSQL, Redis, and application
  - Standards: Non-root user containers, health checks, resource limits
  - **Status**: COMPLETED - Dockerfile and docker-compose.yml present

- ✅ **Initialize CI/CD pipeline with GitHub Actions**
  - Options: Matrix builds vs single platform, parallel vs sequential jobs
  - Action: Create build, test, security scan, and deploy workflows
  - Standards: Fail-fast builds, artifact retention, secure secrets management
  - **Status**: COMPLETED - GitHub Actions CI workflow configured

---

## Phase 2: Core Domain Model & Database Design
**Timeline: Weeks 3-4**

### Data Architecture & Entity Design
- ✅ **Design and implement core domain entities for military operations**
  - Options: JPA entities vs JOOQ code generation vs manual SQL mapping
  - Action: Create Unit, Mission, Operation, Personnel, Equipment entities with proper relationships
  - Standards: Domain-driven design principles, rich domain models
  - **Status**: COMPLETED - 8 domain entities implemented (BaseEntity, MilitaryUnit, Mission, MissionReport, MissionWaypoint, Role, UnitStatusHistory, User)

- ✅ **Establish database schema with proper normalization and security**
  - Options: PostgreSQL vs MySQL vs H2 embedded, Flyway vs Liquibase migrations
  - Action: Design normalized schema with audit tables, implement row-level security
  - Standards: 3NF normalization, GDPR compliance fields, encrypted sensitive data
  - **Status**: COMPLETED - PostgreSQL with H2 for testing, 3 Flyway migrations (V1-V3) implemented

- ✅ **Implement repository layer with query optimization**
  - Options: Spring Data JPA vs Spring Data JDBC vs custom implementations
  - Action: Create repositories with custom queries, implement caching strategies
  - Standards: Repository pattern, query performance optimization, connection pooling
  - **Status**: COMPLETED - Complete repository layer with custom queries implemented

- ⭕ **Set up data validation and constraint enforcement**
  - Options: Bean Validation vs custom validators vs database constraints
  - Action: Implement JSR-303 validation with custom military-specific validators
  - Standards: Fail-fast validation, meaningful error messages, input sanitization
  - **Status**: PARTIAL - Basic validation annotations present, custom validators not implemented

- ✅ **Configure database connection pooling and transaction management**
  - Options: HikariCP vs Apache DBCP vs Tomcat JDBC Pool
  - Action: Configure HikariCP with proper sizing, implement declarative transactions
  - Standards: Connection leak detection, transaction timeout configuration
  - **Status**: COMPLETED - HikariCP configured, declarative transactions with @Transactional

---

## Phase 3: Security Implementation & Authentication
**Timeline: Weeks 5-6**

### Enterprise Security Framework
- ⭕ **Implement OAuth 2.0/JWT authentication with military role hierarchy**
  - Options: Spring Security OAuth2 vs Keycloak vs Auth0 integration
  - Action: Configure JWT with refresh tokens, implement military rank-based roles
  - Standards: RBAC with principle of least privilege, secure token storage
  - **Status**: PARTIAL - JWT authentication implemented with Spring Security, Role-based access configured, but OAuth 2.0 and refresh tokens not implemented

- ✅ **Set up method-level security and authorization**
  - Options: @PreAuthorize vs @Secured vs manual security checks
  - Action: Implement fine-grained permissions using SpEL expressions
  - Standards: Secure by default, explicit permissions, audit all access attempts
  - **Status**: COMPLETED - @PreAuthorize annotations implemented across controllers

- ⭕ **Configure HTTPS/TLS and certificate management**
  - Options: Self-signed certificates vs CA-signed vs Let's Encrypt
  - Action: Implement TLS 1.3, configure certificate rotation, HSTS headers
  - Standards: Perfect Forward Secrecy, certificate pinning for production
  - **Status**: NOT IMPLEMENTED - Currently running on HTTP

- ⭕ **Implement comprehensive audit logging and security monitoring**
  - Options: Spring Boot Actuator vs Micrometer vs custom audit framework
  - Action: Log all security events, integrate with SIEM systems, implement alerting
  - Standards: Immutable audit logs, correlation IDs, GDPR compliance
  - **Status**: NOT IMPLEMENTED - No audit logging framework in place

- ⭕ **Set up input validation and OWASP security measures**
  - Options: OWASP dependency check vs Snyk vs custom security scanning
  - Action: Implement CSRF protection, XSS prevention, SQL injection protection
  - Standards: OWASP Top 10 compliance, regular security assessments
  - **Status**: NOT IMPLEMENTED - No OWASP security scanning or comprehensive input validation

---

## Phase 4: Core Business Logic & Services
**Timeline: Weeks 7-9**

### Service Layer Implementation
- [ ] **Develop unit management and tracking services**
  - Options: Synchronous vs asynchronous processing, event-driven vs request-response
  - Action: Implement unit CRUD operations, position tracking, status updates
  - Standards: Service layer pattern, transaction boundaries, error handling

- [ ] **Implement mission planning and coordination services**
  - Options: Workflow engine integration vs custom state machine vs simple status tracking
  - Action: Create mission lifecycle management, objective setting, resource allocation
  - Standards: State pattern for mission phases, saga pattern for long-running operations

- [ ] **Build real-time communication and messaging system**
  - Options: WebSockets vs Server-Sent Events vs message queues (Kafka/RabbitMQ)
  - Action: Implement secure real-time messaging between command centers and units
  - Standards: Message encryption, delivery guarantees, connection resilience

- [ ] **Create reporting and analytics services**
  - Options: Real-time dashboards vs batch reporting vs hybrid approach
  - Action: Generate operational reports, KPI dashboards, trend analysis
  - Standards: Data aggregation patterns, caching strategies, export capabilities

- [ ] **Implement integration services for external systems**
  - Options: REST APIs vs GraphQL vs message-based integration
  - Action: Create adapters for weather data, intelligence feeds, logistics systems
  - Standards: Circuit breaker pattern, retry mechanisms, graceful degradation

---

## Phase 5: API Development & Documentation
**Timeline: Weeks 10-11**

### RESTful API & Integration Layer
- [ ] **Design and implement comprehensive REST API endpoints**
  - Options: OpenAPI-first vs code-first documentation, versioning strategies
  - Action: Create CRUD operations for all entities, implement HATEOAS principles
  - Standards: RESTful design principles, consistent error responses, API versioning

- [ ] **Set up API documentation with interactive testing capabilities**
  - Options: Swagger UI vs Redoc vs custom documentation portal
  - Action: Generate OpenAPI 3.0 specifications, create interactive API explorer
  - Standards: Complete parameter documentation, example requests/responses

- [ ] **Implement API rate limiting and throttling**
  - Options: In-memory vs Redis-based vs API gateway solutions
  - Action: Configure rate limits per user/role, implement backoff strategies
  - Standards: Fair usage policies, graceful degradation under load

- [ ] **Create API monitoring and performance metrics**
  - Options: Micrometer vs custom metrics vs APM tools integration
  - Action: Track response times, error rates, throughput metrics
  - Standards: SLA monitoring, alerting thresholds, performance baselines

- [ ] **Establish API testing and contract validation**
  - Options: Postman collections vs REST Assured vs contract testing tools
  - Action: Create comprehensive API test suites, implement contract testing
  - Standards: Test coverage for all endpoints, performance testing, security testing

---

## Phase 6: User Interface & Visualization
**Timeline: Weeks 12-14**

### Frontend Development & User Experience
- [ ] **Develop tactical operations dashboard with real-time updates**
  - Options: Vue.js vs React vs Angular for frontend, WebSocket vs polling for updates
  - Action: Create responsive command center interface with live unit tracking
  - Standards: Responsive design, accessibility compliance, progressive enhancement

- [ ] **Implement interactive mapping and geospatial visualization**
  - Options: Leaflet vs MapBox vs Google Maps for mapping, real-time vs cached data
  - Action: Display unit positions, mission areas, threat zones on interactive maps
  - Standards: Performance optimization for large datasets, offline capability

- [ ] **Create mission planning and resource management interfaces**
  - Options: Drag-and-drop vs form-based vs wizard-style interfaces
  - Action: Build intuitive mission creation, resource allocation, and timeline views
  - Standards: User-centered design, workflow optimization, error prevention

- [ ] **Develop reporting and analytics visualization components**
  - Options: Chart.js vs D3.js vs commercial charting libraries
  - Action: Create interactive charts, graphs, and statistical visualizations
  - Standards: Data visualization best practices, export capabilities, drill-down functionality

- [ ] **Implement role-based UI customization and preferences**
  - Options: Client-side vs server-side personalization, theme systems
  - Action: Allow users to customize dashboards, save preferences, configure notifications
  - Standards: Accessibility standards, performance with customizations

---

## Phase 7: Testing & Quality Assurance
**Timeline: Weeks 15-16**

### Comprehensive Testing Strategy
- [ ] **Implement unit testing with high coverage standards**
  - Options: JUnit 5 vs TestNG, Mockito vs EasyMock for mocking
  - Action: Achieve 90%+ code coverage, implement test-driven development practices
  - Standards: Arrange-Act-Assert pattern, meaningful test names, fast execution

- [ ] **Develop integration testing for service interactions**
  - Options: TestContainers vs in-memory databases vs test fixtures
  - Action: Test database interactions, external API integrations, security flows
  - Standards: Test data management, environment isolation, reproducible tests

- [ ] **Create end-to-end testing for critical user workflows**
  - Options: Selenium vs Cypress vs Playwright for browser automation
  - Action: Test complete user journeys from login through mission completion
  - Standards: Page object pattern, test data setup/teardown, cross-browser testing

- [ ] **Implement performance and load testing**
  - Options: JMeter vs Gatling vs custom load testing tools
  - Action: Test system behavior under expected and peak loads
  - Standards: Response time requirements, concurrent user limits, resource utilization

- [ ] **Set up security and penetration testing**
  - Options: OWASP ZAP vs commercial security scanners vs manual testing
  - Action: Automated security scans, manual penetration testing, vulnerability assessment
  - Standards: OWASP compliance verification, security regression testing

---

## Phase 8: Deployment & Operations
**Timeline: Weeks 17-18**

### Production Readiness & Deployment
- [ ] **Configure production deployment pipeline and environments**
  - Options: Kubernetes vs Docker Swarm vs traditional server deployment
  - Action: Set up staging and production environments with proper promotion process
  - Standards: Infrastructure as code, automated deployments, rollback capabilities

- [ ] **Implement monitoring, logging, and alerting systems**
  - Options: ELK Stack vs Prometheus/Grafana vs cloud-native monitoring
  - Action: Set up application monitoring, log aggregation, alert management
  - Standards: Observability best practices, incident response procedures

- [ ] **Set up backup and disaster recovery procedures**
  - Options: Database replication vs snapshot-based backups vs continuous backup
  - Action: Implement automated backups, test recovery procedures, document processes
  - Standards: RTO/RPO requirements, backup verification, geographic distribution

- [ ] **Configure security hardening and compliance measures**
  - Options: Container security scanning vs host-based security vs network security
  - Action: Implement security baseline configurations, regular security updates
  - Standards: Compliance with security frameworks, regular security assessments

- [ ] **Establish operational procedures and documentation**
  - Options: Runbook automation vs manual procedures vs hybrid approach
  - Action: Create operational runbooks, incident response procedures, troubleshooting guides
  - Standards: Clear documentation, regular procedure testing, knowledge sharing

---

## Success Criteria & Deliverables

### Technical Deliverables
- Fully functional tactical command and control system
- Comprehensive test suite with >90% code coverage
- Complete API documentation and integration guides
- Production-ready deployment configuration
- Security compliance documentation

### Performance Targets
- API response times <200ms for 95th percentile
- Support for 1000+ concurrent users
- 99.9% uptime for critical operations
- Real-time updates with <1 second latency

### Security Requirements
- OWASP Top 10 compliance verification
- Penetration testing with no critical vulnerabilities
- Audit logging for all security-relevant operations
- Encryption of data in transit and at rest

---

## Risk Management

### Technical Risks
- **Integration complexity**: Mitigate through early prototyping and API design
- **Performance bottlenecks**: Address with load testing and performance monitoring
- **Security vulnerabilities**: Prevent with security-first development and regular assessments

### Project Risks
- **Scope creep**: Manage through clear requirements and change control process
- **Resource constraints**: Address with agile planning and priority management
- **Timeline pressure**: Mitigate with realistic estimation and buffer planning

---

## Resource Requirements

### Development Team
- 2-3 Senior Java Developers
- 1 Frontend Developer (Vue.js/TypeScript)
- 1 DevOps Engineer
- 1 Security Specialist
- 1 Product Owner/Analyst

### Infrastructure
- Development, staging, and production environments
- CI/CD pipeline with automated testing
- Security scanning and monitoring tools
- Database and caching infrastructure
```

## Additional Configuration Files

### .github/workflows/ci.yml
```yaml
name: CI/CD Pipeline

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v4
    - name: Set up JDK 17
      uses: actions/setup-java@v4
      with:
        java-version: '17'
        distribution: 'temurin'
    - name: Cache Maven packages
      uses: actions/cache@v3
      with:
        path: ~/.m2
        key: ${{ runner.os }}-m2-${{ hashFiles('**/pom.xml') }}
    - name: Run tests
      run: mvn clean verify
    - name: Generate test report
      uses: dorny/test-reporter@v1
      if: success() || failure()
      with:
        name: Maven Tests
        path: target/surefire-reports/*.xml
        reporter: java-junit
```

### .copilot/copilot.yml
```yaml
# GitHub Copilot configuration for tactical-command-hub

# Coding standards and preferences
coding_standards:
  language: java
  style_guide: google
  max_line_length: 120
  indent_size: 4

# Project-specific context
project_context:
  domain: military_command_control
  architecture: spring_boot_microservices
  security_level: high
  patterns:
    - repository
    - service_layer
    - dto
    - builder

# Preferred libraries and frameworks
dependencies:
  - spring-boot
  - spring-security
  - spring-data-jpa
  - junit5
  - mockito
  - testcontainers

# Code generation preferences
code_generation:
  generate_tests: true
  include_javadoc: true
  use_lombok: false
  validation_annotations: true
```

This comprehensive project structure provides a solid foundation for developing the tactical command hub system with proper organization, documentation, and development practices aligned with enterprise Java development standards.