# Image Assets

This directory contains image assets for the Tactical Command Hub application.

## File Organization

### Icons
- `icons/` - Application icons and symbols
  - `unit-markers/` - Military unit symbols and markers
  - `status-indicators/` - Status and state indicators
  - `navigation/` - UI navigation icons
  - `tactical/` - Tactical symbols and overlays

### Logos and Branding
- `logo/` - Application logos and branding elements
  - `logo-full.svg` - Full application logo
  - `logo-icon.svg` - Icon-only version
  - `favicon.ico` - Browser favicon
  - `apple-touch-icon.png` - Mobile home screen icon

### Map Assets
- `maps/` - Map-related imagery
  - `overlays/` - Map overlay images
  - `terrain/` - Terrain visualization assets
  - `markers/` - Custom map markers

### UI Elements
- `ui/` - User interface graphics
  - `backgrounds/` - Background patterns and textures
  - `buttons/` - Custom button graphics
  - `borders/` - Decorative borders and frames

## Image Specifications

### Format Guidelines
- SVG for scalable graphics and icons
- PNG for images requiring transparency
- JPG for photographs and complex images
- WebP for modern browsers (with fallbacks)

### Size Standards
- Icons: 16x16, 24x24, 32x32, 48x48 pixels
- Logos: Multiple sizes from 64x64 to 512x512
- Thumbnails: 150x150 pixels
- Hero images: 1920x1080 pixels (16:9 ratio)

### Optimization
- Compress images for web delivery
- Provide retina-ready variants (@2x, @3x)
- Use appropriate color profiles
- Implement lazy loading for large images

## Military Symbology

### Unit Markers
Following NATO military symbology standards:
- Friendly units: Blue markers
- Enemy units: Red markers  
- Neutral units: Green markers
- Unknown units: Yellow markers

### Tactical Symbols
- Command posts
- Communication nodes
- Supply routes
- Objective markers
- Hazard indicators

## Development Guidelines

### File Naming
- Use kebab-case for filenames
- Include size suffixes where applicable
- Use descriptive, meaningful names
- Maintain consistent naming patterns

### Accessibility
- Provide alt text descriptions
- Ensure sufficient color contrast
- Support high contrast modes
- Consider color-blind accessibility

### Performance
- Optimize file sizes for web delivery
- Use appropriate formats for content type
- Implement responsive image techniques
- Consider CDN delivery for large assets

## Asset Sources

### Original Assets
- Custom-designed graphics for application branding
- Military symbols following established standards
- UI elements designed for tactical interfaces

### Third-Party Assets
- Licensed stock photography
- Open-source icon sets (with attribution)
- Military symbology references

## Version Control

### Git LFS
Large binary assets may use Git LFS:
- Image files over 1MB
- High-resolution source files
- Video and animation assets

### Asset Management
- Track asset usage and dependencies
- Maintain source files for future editing
- Document licensing and attribution requirements
