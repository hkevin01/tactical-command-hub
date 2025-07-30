# Development Workflow

## Overview

This document outlines the development workflow, branching strategies, CI/CD pipelines, and code review processes for the Tactical Command Hub project.

## Branching Strategy

### GitFlow Workflow

We follow the GitFlow branching model with the following branch types:

#### Main Branches
- **`main`**: Production-ready code, deployed to production environment
- **`develop`**: Integration branch for features, deployed to staging environment

#### Supporting Branches
- **`feature/*`**: New features and enhancements
- **`release/*`**: Release preparation and bug fixes
- **`hotfix/*`**: Critical production fixes
- **`bugfix/*`**: Non-critical bug fixes

### Branch Naming Conventions

```
feature/TCH-123-add-mission-planning
bugfix/TCH-456-fix-unit-positioning
hotfix/TCH-789-security-vulnerability
release/v1.2.0
```

Format: `{type}/{ticket-id}-{description}`

## Development Process

### 1. Feature Development

```bash
# Start new feature
git checkout develop
git pull origin develop
git checkout -b feature/TCH-123-add-mission-planning

# Make changes and commit
git add .
git commit -m "feat(mission): add mission planning capabilities

- Implement mission creation API
- Add mission validation logic
- Update database schema
- Add unit tests

Closes #123"

# Push and create PR
git push origin feature/TCH-123-add-mission-planning
```

### 2. Code Review Process

#### Pull Request Requirements
- [ ] **Branch up-to-date**: Feature branch is up-to-date with develop
- [ ] **Tests passing**: All CI checks are green
- [ ] **Code coverage**: Maintain >90% code coverage
- [ ] **Documentation**: Update relevant documentation
- [ ] **Security review**: No security vulnerabilities introduced

#### Review Checklist
- [ ] **Functionality**: Code meets requirements and works as expected
- [ ] **Code quality**: Follows coding standards and best practices
- [ ] **Performance**: No performance regressions introduced
- [ ] **Security**: Security implications reviewed and addressed
- [ ] **Testing**: Adequate test coverage for new code
- [ ] **Documentation**: Code is well-documented and self-explanatory

### 3. Merge Strategy

- **Feature → Develop**: Squash and merge (clean history)
- **Develop → Main**: Merge commit (preserve merge history)
- **Hotfix → Main**: Fast-forward merge when possible

## CI/CD Pipeline

### Continuous Integration

Our CI pipeline is triggered on:
- Push to any branch
- Pull request creation/update
- Manual workflow dispatch

#### Build Stages

1. **Checkout & Setup**
   - Checkout code
   - Set up JDK 17
   - Cache Maven dependencies

2. **Code Quality**
   - Run unit tests
   - Generate code coverage report
   - Execute SpotBugs security analysis
   - Run Checkstyle for code formatting

3. **Integration Testing**
   - Start test database (PostgreSQL)
   - Run integration tests
   - Generate test reports

4. **Security Scanning**
   - OWASP dependency check
   - Container security scanning
   - Static code analysis

5. **Build & Package**
   - Build application JAR
   - Build Docker image
   - Push to artifact registry

### Continuous Deployment

#### Staging Deployment
- **Trigger**: Merge to `develop` branch
- **Target**: Staging environment
- **Process**: Automated deployment with smoke tests

#### Production Deployment
- **Trigger**: Merge to `main` branch
- **Target**: Production environment
- **Process**: Blue-green deployment with approval gates

## Code Standards

### Java Coding Standards

- **Style Guide**: Google Java Style Guide
- **Line Length**: 120 characters maximum
- **Indentation**: 4 spaces (no tabs)
- **Naming**: CamelCase for classes, camelCase for methods/variables

### Commit Message Format

We follow the Conventional Commits specification:

```
<type>[optional scope]: <description>

[optional body]

[optional footer(s)]
```

#### Types
- **feat**: New feature
- **fix**: Bug fix
- **docs**: Documentation changes
- **style**: Formatting changes
- **refactor**: Code refactoring
- **test**: Adding/updating tests
- **chore**: Maintenance tasks

#### Examples
```
feat(auth): implement JWT token refresh mechanism

Add automatic token refresh to prevent session timeouts
during long operations. Refresh occurs 5 minutes before
token expiration.

Closes #234

fix(units): correct latitude/longitude validation

Fixes issue where valid coordinates were rejected due to
incorrect range validation.

Fixes #345
```

## Testing Strategy

### Test Pyramid

1. **Unit Tests (70%)**
   - Fast, isolated tests
   - Mock external dependencies
   - High code coverage target (>90%)

2. **Integration Tests (20%)**
   - Test component interactions
   - Use TestContainers for databases
   - Verify API contracts

3. **End-to-End Tests (10%)**
   - Full user journey testing
   - Automated UI testing (when applicable)
   - Performance testing

### Testing Guidelines

- **Test Naming**: `should_doSomething_when_condition()`
- **Test Structure**: Arrange-Act-Assert pattern
- **Test Data**: Use builders and factories for test data
- **Test Isolation**: Each test should be independent

## Environment Management

### Development Environment
- **Purpose**: Local development and testing
- **Database**: H2 in-memory or local PostgreSQL
- **Configuration**: `application-dev.yml`
- **Access**: localhost:8080

### Staging Environment
- **Purpose**: Integration testing and QA
- **Database**: PostgreSQL (staging instance)
- **Configuration**: `application-staging.yml`
- **Access**: https://staging.tactical-command.mil
- **Deployment**: Automatic on develop branch merge

### Production Environment
- **Purpose**: Live system for end users
- **Database**: PostgreSQL (production cluster)
- **Configuration**: `application-prod.yml`
- **Access**: https://tactical-command.mil
- **Deployment**: Manual approval required

## Security Workflow

### Security Review Process

1. **Automated Scanning**
   - Dependency vulnerability scanning
   - Static code analysis
   - Container security scanning

2. **Manual Review**
   - Security-sensitive code review
   - Authentication/authorization changes
   - Data handling modifications

3. **Security Testing**
   - Penetration testing (quarterly)
   - Vulnerability assessments
   - Security regression testing

### Security Incident Response

1. **Detection**: Automated alerts or manual reporting
2. **Assessment**: Severity classification and impact analysis
3. **Response**: Immediate containment and hotfix development
4. **Recovery**: System restoration and monitoring
5. **Lessons Learned**: Post-incident review and improvements

## Release Management

### Release Planning

- **Major Releases**: Quarterly (1.0, 2.0, 3.0)
- **Minor Releases**: Monthly (1.1, 1.2, 1.3)
- **Patch Releases**: As needed (1.1.1, 1.1.2)

### Release Process

1. **Code Freeze**: No new features, only bug fixes
2. **Release Candidate**: Deploy RC to staging for testing
3. **Final Testing**: Comprehensive testing and validation
4. **Production Deployment**: Blue-green deployment to production
5. **Post-Release**: Monitoring and hotfix readiness

## Monitoring and Maintenance

### Application Monitoring

- **Health Checks**: Endpoint availability monitoring
- **Performance Metrics**: Response times and throughput
- **Error Tracking**: Exception monitoring and alerting
- **Resource Usage**: CPU, memory, and database performance

### Maintenance Windows

- **Scheduled Maintenance**: Second Sunday of each month, 2-6 AM UTC
- **Emergency Maintenance**: As needed for critical issues
- **Notification**: 48-hour advance notice for scheduled maintenance

## Tools and Integrations

### Development Tools
- **IDE**: VS Code with Java extensions
- **Build Tool**: Maven 3.8+
- **Version Control**: Git with GitHub
- **Database**: PostgreSQL (production), H2 (testing)

### Quality Assurance
- **Testing**: JUnit 5, Mockito, TestContainers
- **Code Quality**: SonarQube, SpotBugs, Checkstyle
- **Security**: OWASP Dependency Check, Snyk

### DevOps Tools
- **CI/CD**: GitHub Actions
- **Containerization**: Docker, Docker Compose
- **Orchestration**: Kubernetes (production)
- **Monitoring**: Prometheus, Grafana, ELK Stack

## Documentation Standards

### Code Documentation
- **JavaDoc**: All public APIs must have JavaDoc
- **Inline Comments**: Complex business logic explanation
- **README**: Keep project README up-to-date

### API Documentation
- **OpenAPI**: Maintain OpenAPI 3.0 specifications
- **Examples**: Provide request/response examples
- **Versioning**: Document API versioning strategy

### Architecture Documentation
- **ADR**: Architecture Decision Records for major decisions
- **Diagrams**: System architecture and database diagrams
- **Runbooks**: Operational procedures and troubleshooting guides

## Communication and Collaboration

### Daily Workflow
- **Stand-ups**: Daily team sync (async via Slack)
- **Code Reviews**: Within 24 hours of PR creation
- **Issue Tracking**: Use GitHub Issues for all work items

### Weekly Workflow
- **Sprint Planning**: Monday morning planning session
- **Retrospective**: Friday afternoon team retrospective
- **Demo**: Stakeholder demonstration of completed features

### Communication Channels
- **Slack**: #tactical-command-dev for daily communication
- **Email**: official-announcements@tacticalcommand.com for announcements
- **GitHub**: All technical discussions in issues/PRs

---

## Getting Started

### New Developer Onboarding

1. **Repository Setup**
   ```bash
   git clone https://github.com/tacticalcommand/tactical-command-hub.git
   cd tactical-command-hub
   ./scripts/setup.sh
   ```

2. **Development Environment**
   ```bash
   # Start local services
   docker-compose up -d postgres redis
   
   # Run application
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
   ```

3. **First Contribution**
   - Read CONTRIBUTING.md
   - Pick a "good first issue"
   - Follow the feature development process above

### Quick Reference

#### Common Commands
```bash
# Run tests
./mvnw test

# Run with specific profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Build Docker image
docker build -t tactical-command-hub .

# Run integration tests
./mvnw verify -P integration-tests
```

#### Useful Links
- [Project Board](https://github.com/tacticalcommand/tactical-command-hub/projects)
- [API Documentation](https://tactical-command.mil/swagger-ui.html)
- [Deployment Dashboard](https://jenkins.tacticalcommand.com/tactical-command)
- [Monitoring Dashboard](https://grafana.tacticalcommand.com/tactical-command)

---

*This workflow document is living documentation and should be updated as our processes evolve.*
