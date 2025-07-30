# CSS Stylesheets

This directory contains CSS stylesheets for the Tactical Command Hub web interface.

## File Organization

### Core Styles
- `main.css` - Main application styles and layout
- `theme.css` - Color schemes and theming variables
- `typography.css` - Font definitions and text styling

### Component Styles
- `components/` - Component-specific stylesheets
  - `dashboard.css` - Dashboard layout and widgets
  - `maps.css` - Tactical map styling
  - `forms.css` - Form controls and validation
  - `tables.css` - Data table presentation

### Utility Styles
- `utilities.css` - Helper classes and utilities
- `responsive.css` - Mobile and responsive design
- `print.css` - Print-specific styling

## Development Guidelines

### CSS Architecture
- Follow BEM methodology for class naming
- Use CSS custom properties for theming
- Maintain consistent spacing scale
- Implement mobile-first responsive design

### Color Palette
- Primary: Military green (#4A5D23)
- Secondary: Steel blue (#4682B4)
- Accent: Alert orange (#FF6B35)
- Background: Light gray (#F5F5F5)
- Text: Dark charcoal (#2C2C2C)

### Responsive Breakpoints
- Mobile: 320px - 767px
- Tablet: 768px - 1023px
- Desktop: 1024px - 1439px
- Large: 1440px+

## Build Process

CSS files are processed through:
1. PostCSS for autoprefixing
2. CSS minification for production
3. Source map generation for development
4. Cache busting for deployment

## Browser Support

- Chrome 90+
- Firefox 88+
- Safari 14+
- Edge 90+
