#!/bin/bash

# Tactical Command Hub - Development Environment Setup Script
# This script sets up the complete development environment

set -e  # Exit on any error

echo "🚀 Tactical Command Hub - Development Environment Setup"
echo "======================================================"

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check if command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Check prerequisites
check_prerequisites() {
    print_status "Checking prerequisites..."
    
    local missing_tools=()
    
    if ! command_exists java; then
        missing_tools+=("Java 17+")
    else
        java_version=$(java -version 2>&1 | head -n1 | cut -d'"' -f2 | cut -d'.' -f1)
        if [ "$java_version" -lt 17 ]; then
            missing_tools+=("Java 17+ (current: Java $java_version)")
        fi
    fi
    
    if ! command_exists mvn; then
        missing_tools+=("Maven 3.8+")
    fi
    
    if ! command_exists docker; then
        missing_tools+=("Docker")
    fi
    
    if ! command_exists docker-compose; then
        missing_tools+=("Docker Compose")
    fi
    
    if ! command_exists git; then
        missing_tools+=("Git")
    fi
    
    if [ ${#missing_tools[@]} -ne 0 ]; then
        print_error "Missing required tools:"
        for tool in "${missing_tools[@]}"; do
            echo "  - $tool"
        done
        echo ""
        echo "Please install the missing tools and run this script again."
        exit 1
    fi
    
    print_success "All prerequisites are installed"
}

# Create environment file if it doesn't exist
setup_environment() {
    print_status "Setting up environment configuration..."
    
    if [ ! -f .env ]; then
        print_status "Creating .env file from template..."
        cat > .env << 'EOF'
# Tactical Command Hub - Environment Configuration

# Database Configuration
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/tactical_command_hub
SPRING_DATASOURCE_USERNAME=tactical_user
SPRING_DATASOURCE_PASSWORD=tactical_password_123

# JWT Configuration
JWT_SECRET=tactical_jwt_secret_key_change_in_production_2024
JWT_EXPIRATION=86400000

# Redis Configuration (if using caching)
SPRING_REDIS_HOST=localhost
SPRING_REDIS_PORT=6379

# Logging Configuration
LOGGING_LEVEL_COM_TACTICALCOMMAND=DEBUG
LOGGING_LEVEL_ORG_SPRINGFRAMEWORK=INFO

# Server Configuration
SERVER_PORT=8080
SERVER_SERVLET_CONTEXT_PATH=/api/v1

# Spring Profiles
SPRING_PROFILES_ACTIVE=development
EOF
        print_success "Created .env file with default configuration"
    else
        print_warning ".env file already exists, skipping creation"
    fi
}

# Setup database
setup_database() {
    print_status "Setting up PostgreSQL database..."
    
    # Check if PostgreSQL container is already running
    if docker ps | grep -q tactical-postgres; then
        print_warning "PostgreSQL container is already running"
    else
        print_status "Starting PostgreSQL container..."
        docker-compose up -d postgres
        
        # Wait for PostgreSQL to be ready
        print_status "Waiting for PostgreSQL to be ready..."
        sleep 10
        
        # Check if PostgreSQL is accessible
        local retry_count=0
        while ! docker exec tactical-postgres pg_isready -U tactical_user -d tactical_command_hub >/dev/null 2>&1; do
            if [ $retry_count -eq 30 ]; then
                print_error "PostgreSQL failed to start within 30 seconds"
                exit 1
            fi
            print_status "Waiting for PostgreSQL... ($((retry_count + 1))/30)"
            sleep 1
            retry_count=$((retry_count + 1))
        done
        
        print_success "PostgreSQL is ready"
    fi
}

# Run database migrations
run_migrations() {
    print_status "Running database migrations..."
    
    if mvn flyway:info >/dev/null 2>&1; then
        mvn flyway:migrate
        print_success "Database migrations completed"
    else
        print_error "Failed to run database migrations"
        exit 1
    fi
}

# Build the application
build_application() {
    print_status "Building the application..."
    
    if mvn clean compile >/dev/null 2>&1; then
        print_success "Application compiled successfully"
    else
        print_error "Failed to compile application"
        exit 1
    fi
}

# Run tests
run_tests() {
    print_status "Running tests..."
    
    if mvn test >/dev/null 2>&1; then
        print_success "All tests passed"
    else
        print_warning "Some tests failed - check output above"
    fi
}

# Setup IDE configuration
setup_ide() {
    print_status "Verifying IDE configuration..."
    
    if [ -f .vscode/settings.json ]; then
        print_success "VS Code settings are configured"
    else
        print_warning "VS Code settings not found - manual setup may be required"
    fi
    
    if [ -f .vscode/extensions.json ]; then
        print_success "VS Code extensions are configured"
    else
        print_warning "VS Code extensions configuration not found"
    fi
}

# Display next steps
show_next_steps() {
    echo ""
    echo "🎉 Setup completed successfully!"
    echo ""
    echo "Next steps:"
    echo "1. Open VS Code: code ."
    echo "2. Install recommended extensions when prompted"
    echo "3. Start the application:"
    echo "   mvn spring-boot:run"
    echo ""
    echo "4. Access the application:"
    echo "   - Main API: http://localhost:8080/api/v1"
    echo "   - Swagger UI: http://localhost:8080/swagger-ui.html"
    echo "   - Health Check: http://localhost:8080/actuator/health"
    echo ""
    echo "5. View logs:"
    echo "   tail -f logs/application.log"
    echo ""
    echo "6. Stop services when done:"
    echo "   docker-compose down"
    echo ""
    echo "For more information, see README.md and docs/ directory"
}

# Main setup process
main() {
    echo ""
    print_status "Starting development environment setup..."
    echo ""
    
    check_prerequisites
    setup_environment
    setup_database
    run_migrations
    build_application
    run_tests
    setup_ide
    
    show_next_steps
}

# Handle script interruption
cleanup() {
    print_warning "Setup interrupted"
    exit 1
}

trap cleanup INT

# Run main function
main "$@"
