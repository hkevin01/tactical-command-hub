# JavaScript Assets

This directory contains JavaScript files for the Tactical Command Hub web interface.

## File Organization

### Core Application
- `main.js` - Main application entry point
- `config.js` - Application configuration and constants
- `utils.js` - Common utility functions

### Modules
- `modules/` - Feature-specific JavaScript modules
  - `dashboard.js` - Dashboard functionality
  - `mapping.js` - Tactical map integration
  - `communications.js` - Real-time communication handling
  - `mission-planning.js` - Mission planning tools

### Libraries
- `lib/` - Third-party libraries and dependencies
  - Custom builds and vendor files
  - Map libraries (Leaflet, Mapbox)
  - Chart libraries (Chart.js, D3.js)

### Components
- `components/` - Reusable UI components
  - `modal.js` - Modal dialog functionality
  - `notifications.js` - Alert and notification system
  - `data-tables.js` - Enhanced table components

## Development Guidelines

### Code Standards
- ES6+ syntax with Babel transpilation
- Modular architecture with ES6 imports/exports
- JSDoc comments for documentation
- Consistent error handling patterns

### API Integration
- RESTful API communication via Fetch API
- WebSocket connections for real-time updates
- Proper error handling and retry logic
- Request/response interceptors for authentication

### Performance Considerations
- Code splitting for optimal loading
- Lazy loading of non-critical modules
- Efficient DOM manipulation
- Memory leak prevention

## Build Process

JavaScript files are processed through:
1. Babel transpilation for browser compatibility
2. Webpack bundling and optimization
3. Minification and tree shaking
4. Source map generation for debugging

## Dependencies

### Runtime Dependencies
- Modern browser APIs (Fetch, WebSocket, etc.)
- Map rendering libraries
- Chart and visualization libraries
- Date/time manipulation utilities

### Development Dependencies
- Babel for transpilation
- Webpack for bundling
- ESLint for code quality
- Jest for testing

## Browser Compatibility

Target browsers:
- Chrome 90+
- Firefox 88+
- Safari 14+
- Edge 90+
