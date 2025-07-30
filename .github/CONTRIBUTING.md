# Contributing to Tactical Command Hub

Thank you for your interest in contributing to the Tactical Command Hub project! This document provides guidelines and information for contributors.

## Table of Contents
- [Code of Conduct](#code-of-conduct)
- [Getting Started](#getting-started)
- [Development Setup](#development-setup)
- [Contributing Process](#contributing-process)
- [Code Standards](#code-standards)
- [Testing Requirements](#testing-requirements)
- [Security Guidelines](#security-guidelines)
- [Documentation](#documentation)

## Code of Conduct

This project follows a professional code of conduct. All contributors are expected to:
- Be respectful and inclusive
- Focus on constructive feedback
- Maintain professional communication
- Respect different viewpoints and experiences

## Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.8+
- Docker and Docker Compose
- Git
- VS Code (recommended) with Java extensions

### Development Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/hkevin01/tactical-command-hub.git
   cd tactical-command-hub
   ```

2. **Set up the development environment**
   ```bash
   # Copy environment template
   cp .env.example .env
   
   # Edit .env with your local settings
   
   # Start dependencies
   docker-compose up -d postgres
   ```

3. **Build and run the application**
   ```bash
   mvn clean compile
   mvn spring-boot:run
   ```

4. **Run tests**
   ```bash
   mvn test
   ```

## Contributing Process

### Branch Strategy
We use a simplified Git workflow:
- `main` - Production-ready code
- `develop` - Integration branch for features
- `feature/*` - Feature development branches
- `bugfix/*` - Bug fix branches
- `hotfix/*` - Critical production fixes

### Making Contributions

1. **Create an issue** (if one doesn't exist)
   - Describe the problem or enhancement
   - Include relevant labels
   - Wait for discussion and approval

2. **Fork and create a branch**
   ```bash
   git checkout -b feature/your-feature-name
   ```

3. **Make your changes**
   - Follow code standards
   - Add tests for new functionality
   - Update documentation as needed

4. **Test your changes**
   ```bash
   mvn clean test
   mvn verify
   ```

5. **Commit your changes**
   ```bash
   git commit -m "feat: add new feature description"
   ```
   
   Use conventional commit format:
   - `feat:` - New features
   - `fix:` - Bug fixes
   - `docs:` - Documentation changes
   - `style:` - Code style changes
   - `refactor:` - Code refactoring
   - `test:` - Test additions/modifications
   - `chore:` - Maintenance tasks

6. **Push and create a Pull Request**
   ```bash
   git push origin feature/your-feature-name
   ```
   
   Create a PR using the provided template.

## Code Standards

### Java Coding Standards
- Follow Google Java Style Guide
- Use meaningful variable and method names
- Keep methods focused and small
- Add JavaDoc for public APIs
- Maximum line length: 120 characters
- Use proper exception handling

### Spring Boot Guidelines
- Use proper dependency injection
- Follow layered architecture (Controller → Service → Repository)
- Use DTOs for API boundaries
- Implement proper validation
- Use appropriate HTTP status codes

### Database Guidelines
- Use Flyway for database migrations
- Follow naming conventions for tables and columns
- Add proper indexes for performance
- Include foreign key constraints

## Testing Requirements

### Unit Tests
- Minimum 80% code coverage
- Use JUnit 5 and Mockito
- Follow AAA pattern (Arrange, Act, Assert)
- Test edge cases and error conditions

### Integration Tests
- Use TestContainers for database tests
- Test complete request/response cycles
- Verify security configurations
- Test transaction boundaries

### End-to-End Tests
- Test critical user workflows
- Use realistic test data
- Verify system integration points

## Security Guidelines

### Code Security
- Never commit secrets or credentials
- Use parameterized queries to prevent SQL injection
- Validate all input data
- Implement proper authentication and authorization
- Follow OWASP security guidelines

### Dependency Security
- Keep dependencies updated
- Run security scans regularly
- Review and approve new dependencies

## Documentation

### Code Documentation
- Add JavaDoc for public classes and methods
- Include inline comments for complex logic
- Update README for significant changes

### API Documentation
- Use OpenAPI/Swagger annotations
- Provide example requests/responses
- Document error responses

### Architecture Documentation
- Update architecture diagrams for structural changes
- Document design decisions
- Maintain deployment guides

## Pull Request Guidelines

### Before Submitting
- [ ] Code follows style guidelines
- [ ] Tests are added and passing
- [ ] Documentation is updated
- [ ] Security implications are considered
- [ ] Performance impact is assessed
- [ ] Breaking changes are documented

### PR Description
- Clear description of changes
- Link to related issues
- Include screenshots for UI changes
- List any breaking changes
- Describe testing performed

### Review Process
- All PRs require at least one review
- Address all review comments
- Keep PRs focused and reasonably sized
- Squash commits before merging

## Issue Guidelines

### Bug Reports
- Use the bug report template
- Include steps to reproduce
- Provide error messages and logs
- Include environment information

### Feature Requests
- Use the feature request template
- Describe the problem being solved
- Provide use cases and examples
- Consider implementation complexity

## Release Process

### Version Numbering
- Follow Semantic Versioning (SemVer)
- Major.Minor.Patch format
- Document breaking changes

### Release Notes
- Summarize all changes
- Highlight new features
- List bug fixes
- Note any breaking changes

## Questions and Support

- Create an issue for bugs or feature requests
- Use discussions for questions and ideas
- Review existing documentation first
- Be specific and provide context

## Recognition

Contributors will be recognized in:
- Release notes
- Contributors section of README
- Annual contributor acknowledgments

Thank you for contributing to Tactical Command Hub!
