# Thymeleaf Templates

This directory contains Thymeleaf templates for server-side rendered pages in the Tactical Command Hub application.

## File Organization

### Layout Templates
- `layout/` - Base layout templates
  - `base.html` - Main application layout
  - `admin.html` - Administrative interface layout
  - `minimal.html` - Minimal layout for standalone pages

### Page Templates
- `pages/` - Complete page templates
  - `dashboard.html` - Main dashboard view
  - `missions.html` - Mission management interface
  - `units.html` - Unit tracking and management
  - `reports.html` - Reporting and analytics

### Fragments
- `fragments/` - Reusable template fragments
  - `header.html` - Application header
  - `navigation.html` - Main navigation menu
  - `footer.html` - Application footer
  - `modals.html` - Modal dialog templates

### Forms
- `forms/` - Form templates
  - `mission-create.html` - Mission creation form
  - `unit-update.html` - Unit information update
  - `event-log.html` - Event logging interface

## Thymeleaf Features

### Template Inheritance
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org"
      th:replace="~{layout/base :: layout(~{::title}, ~{::content})}">
```

### Data Binding
```html
<div th:each="mission : ${missions}">
    <h3 th:text="${mission.name}">Mission Name</h3>
</div>
```

### Conditional Rendering
```html
<div th:if="${user.hasRole('COMMANDER')}" 
     class="admin-controls">
    <!-- Commander-only content -->
</div>
```

### URL Generation
```html
<a th:href="@{/missions/{id}(id=${mission.id})}" 
   th:text="${mission.name}">Mission Link</a>
```

## Development Guidelines

### Template Structure
- Use semantic HTML5 elements
- Implement ARIA attributes for accessibility
- Follow consistent naming conventions
- Separate presentation from logic

### Data Integration
- Use model attributes from Spring controllers
- Implement proper error handling
- Validate user input on both client and server
- Handle empty states gracefully

### Security Considerations
- Escape user input to prevent XSS
- Implement CSRF protection
- Use Spring Security integration
- Validate authorization levels

## Spring Boot Integration

### Controller Mapping
```java
@GetMapping("/dashboard")
public String dashboard(Model model) {
    model.addAttribute("missions", missionService.getActiveMissions());
    return "pages/dashboard";
}
```

### Model Attributes
- Mission data objects
- Unit information
- User authentication context
- Application configuration

### View Resolution
- Automatic template resolution
- Template caching for production
- Hot reloading in development
- Error page handling

## Styling and Assets

### CSS Integration
```html
<link th:href="@{/css/main.css}" rel="stylesheet" />
<link th:href="@{/css/dashboard.css}" rel="stylesheet" />
```

### JavaScript Integration
```html
<script th:src="@{/js/main.js}"></script>
<script th:src="@{/js/dashboard.js}"></script>
```

### Static Resources
- Images: `th:src="@{/images/logo.png}"`
- Fonts: CSS `@font-face` declarations
- Icons: SVG sprites or icon fonts

## Internationalization

### Message Properties
```html
<h1 th:text="#{dashboard.title}">Dashboard</h1>
<p th:text="#{mission.status.active}">Active</p>
```

### Locale Support
- English (default)
- Configurable for additional languages
- Date and number formatting
- Currency and measurement units

## Performance Optimization

### Template Caching
- Enable caching in production
- Disable caching in development
- Fragment caching for expensive operations
- Conditional template inclusion

### Resource Optimization
- Minify CSS and JavaScript
- Optimize image loading
- Use CDN for static assets
- Implement proper caching headers

## Testing

### Template Testing
- Unit tests for template rendering
- Integration tests with Spring MVC
- Visual regression testing
- Accessibility testing

### Mock Data
- Test data fixtures
- Mock service responses
- Error condition simulation
- Edge case handling
