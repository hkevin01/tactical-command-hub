# Static Web Assets

This directory contains static web assets for the Tactical Command Hub application.

## Directory Structure

```
static/
├── css/           # Stylesheets and themes
├── js/            # JavaScript files and libraries
├── images/        # Images, icons, and graphics
├── fonts/         # Web fonts and typography
└── templates/     # HTML templates and fragments
```

## Asset Organization

### CSS Files
- `app.css` - Main application styles
- `tactical-theme.css` - Military-themed styling
- `responsive.css` - Mobile and tablet responsiveness

### JavaScript Files
- `app.js` - Main application JavaScript
- `tactical-map.js` - Interactive mapping functionality
- `websocket-client.js` - Real-time communication

### Images
- `icons/` - UI icons and tactical symbols
- `maps/` - Map overlays and geographic assets
- `logos/` - Application and military unit logos

## Development Notes

Static assets are served directly by Spring Boot at the root path `/`. 
For production deployments, consider using a CDN or separate web server for optimal performance.
