# API Documentation

This directory contains comprehensive API documentation for the Tactical Command Hub.

## Contents

### Core API Documentation
- `authentication.md` - Authentication and authorization endpoints
- `military-units.md` - Military unit management API
- `missions.md` - Mission planning and execution API
- `personnel.md` - Personnel management API
- `reports.md` - Reporting and analytics API

### Technical Documentation
- `openapi-spec.yml` - OpenAPI 3.0 specification
- `postman-collection.json` - Postman collection for API testing
- `error-codes.md` - Complete error code reference
- `rate-limiting.md` - API rate limiting documentation

### Integration Guides
- `getting-started.md` - Quick start guide for API integration
- `authentication-guide.md` - Detailed authentication setup
- `webhook-integration.md` - Webhook configuration and usage
- `batch-operations.md` - Bulk operations and data import

## API Overview

The Tactical Command Hub provides a RESTful API for:
- Military unit management and tracking
- Mission planning and coordination
- Personnel and role management
- Real-time status updates
- Reporting and analytics

## Base URL
- Development: `http://localhost:8080/api/v1`
- Production: `https://tactical-hub.mil/api/v1`

## Authentication
All API endpoints require authentication using JWT tokens. See `authentication.md` for detailed setup instructions.

## Interactive Documentation
When the application is running, interactive API documentation is available at:
- Swagger UI: `/swagger-ui.html`
- OpenAPI JSON: `/v3/api-docs`

## Support
For API support and questions:
- Review the documentation in this directory
- Check the interactive Swagger UI
- Create an issue in the GitHub repository
