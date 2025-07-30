# Security Context for Military Applications

## Authentication Patterns
- JWT token-based authentication
- Role-based access control (RBAC)
- Military rank hierarchy integration
- Multi-factor authentication requirements

## Authorization Levels
- USER: Basic read access to assigned units and missions
- COMMANDER: Command authority over subordinate units
- ADMIN: Full system administration capabilities

## Security Headers
- Content Security Policy (CSP)
- X-Frame-Options for clickjacking protection
- X-XSS-Protection for cross-site scripting
- Strict-Transport-Security for HTTPS enforcement

## Input Validation
- SQL injection prevention through parameterized queries
- XSS prevention through input sanitization
- Command injection prevention for system calls
- File upload security and validation

## Audit and Compliance
- Security event logging and monitoring
- Access attempt tracking and analysis
- Data classification and handling requirements
- Compliance with defense security standards

## Encryption Requirements
- Data at rest encryption for sensitive information
- Data in transit encryption with TLS 1.3
- Key management and rotation policies
- Secure communication protocols
