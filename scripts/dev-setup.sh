#!/bin/bash

# Tactical Command Hub - Development Setup Script
# This script sets up the development environment

set -e

echo "🚀 Setting up Tactical Command Hub Development Environment"
echo "==========================================================="

# Check system requirements
echo "📋 Checking system requirements..."

# Check Java
if ! command -v java &> /dev/null; then
    echo "❌ Java is not installed. Please install Java 17 or later."
    exit 1
fi

JAVA_VERSION=$(java -version 2>&1 | grep version | cut -d' ' -f3 | tr -d '"' | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 17 ]; then
    echo "❌ Java version $JAVA_VERSION is too old. Please install Java 17 or later."
    exit 1
fi
echo "✅ Java $JAVA_VERSION detected"

# Check Maven
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven is not installed. Please install Maven 3.8 or later."
    exit 1
fi
echo "✅ Maven detected"

# Check Docker (optional but recommended)
if command -v docker &> /dev/null; then
    echo "✅ Docker detected"
    DOCKER_AVAILABLE=true
else
    echo "⚠️  Docker not found - some features may be limited"
    DOCKER_AVAILABLE=false
fi

# Check PostgreSQL client
if command -v psql &> /dev/null; then
    echo "✅ PostgreSQL client detected"
    POSTGRES_CLIENT=true
else
    echo "⚠️  PostgreSQL client not found - database operations may be limited"
    POSTGRES_CLIENT=false
fi

echo ""
echo "🔧 Setting up project dependencies..."

# Install Maven dependencies
echo "📦 Installing Maven dependencies..."
mvn clean install -DskipTests

echo ""
echo "🗄️  Setting up database..."

# Create .env file if it doesn't exist
if [ ! -f .env ]; then
    echo "📝 Creating .env file..."
    cat > .env << EOF
# Database Configuration
DB_HOST=localhost
DB_PORT=5432
DB_NAME=tactical_command_hub
DB_USERNAME=tactical_user
DB_PASSWORD=tactical_pass

# Application Configuration
SERVER_PORT=8080
SPRING_PROFILES_ACTIVE=dev

# Security Configuration
JWT_SECRET=your-256-bit-secret-key-here-change-in-production
JWT_EXPIRATION=86400000

# External API Keys (replace with actual keys)
WEATHER_API_KEY=your-weather-api-key
MAPS_API_KEY=your-maps-api-key

# Logging Configuration
LOG_LEVEL=INFO
LOG_FILE=logs/tactical-command-hub.log
EOF
    echo "✅ Created .env file with default configuration"
    echo "⚠️  Please update .env with your actual configuration values"
else
    echo "✅ .env file already exists"
fi

# Create logs directory
if [ ! -d "logs" ]; then
    mkdir -p logs
    echo "✅ Created logs directory"
fi

# Create data directory for backups
if [ ! -d "data/backups" ]; then
    mkdir -p data/backups
    echo "✅ Created data/backups directory"
fi

# Setup database (if PostgreSQL client is available)
if [ "$POSTGRES_CLIENT" = true ]; then
    echo "🗄️  Setting up development database..."
    echo "Note: Make sure PostgreSQL is running and accessible"
    
    # Create database and user (requires superuser privileges)
    # This is commented out as it requires manual setup
    # createdb tactical_command_hub 2>/dev/null || echo "Database may already exist"
    
    echo "ℹ️  To setup the database manually, run:"
    echo "   createdb tactical_command_hub"
    echo "   psql -d tactical_command_hub -c \"CREATE USER tactical_user WITH PASSWORD 'tactical_pass';\""
    echo "   psql -d tactical_command_hub -c \"GRANT ALL PRIVILEGES ON DATABASE tactical_command_hub TO tactical_user;\""
fi

# Docker setup (if available)
if [ "$DOCKER_AVAILABLE" = true ]; then
    echo "🐳 Docker is available for containerized development"
    echo "ℹ️  To start services with Docker Compose, run:"
    echo "   docker-compose up -d"
fi

echo ""
echo "🎯 Development environment setup complete!"
echo "==========================================="
echo ""
echo "Next steps:"
echo "1. Update .env file with your configuration"
echo "2. Setup database connection"
echo "3. Run tests: ./scripts/test.sh"
echo "4. Start development server: ./scripts/start-dev.sh"
echo ""
echo "For more information, see docs/development.md"
