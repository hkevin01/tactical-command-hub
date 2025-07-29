#!/bin/bash

# Tactical Command Hub - Build Script
# This script builds the application with comprehensive testing and quality checks

set -e  # Exit on any error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
PROJECT_NAME="tactical-command-hub"
MAVEN_OPTS="-Xmx1024m"
BUILD_DIR="target"

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}  Tactical Command Hub - Build Script  ${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""

# Function to print step headers
print_step() {
    echo -e "${YELLOW}>>> $1${NC}"
}

# Function to print success messages
print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

# Function to print error messages
print_error() {
    echo -e "${RED}✗ $1${NC}"
}

# Check prerequisites
print_step "Checking prerequisites..."

# Check Java version
if command -v java &> /dev/null; then
    JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f1)
    if [ "$JAVA_VERSION" -ge 17 ]; then
        print_success "Java $JAVA_VERSION detected"
    else
        print_error "Java 17 or higher required. Found: $JAVA_VERSION"
        exit 1
    fi
else
    print_error "Java not found. Please install Java 17 or higher."
    exit 1
fi

# Check Maven
if command -v mvn &> /dev/null; then
    MAVEN_VERSION=$(mvn -version | head -n 1 | awk '{print $3}')
    print_success "Maven $MAVEN_VERSION detected"
else
    print_error "Maven not found. Please install Maven 3.8 or higher."
    exit 1
fi

# Check Docker (optional)
if command -v docker &> /dev/null; then
    DOCKER_VERSION=$(docker --version | awk '{print $3}' | sed 's/,//')
    print_success "Docker $DOCKER_VERSION detected"
    DOCKER_AVAILABLE=true
else
    print_error "Docker not found. Docker features will be skipped."
    DOCKER_AVAILABLE=false
fi

echo ""

# Clean previous builds
print_step "Cleaning previous builds..."
mvn clean > /dev/null 2>&1
print_success "Previous builds cleaned"

# Compile source code
print_step "Compiling source code..."
mvn compile -q
print_success "Source code compiled successfully"

# Run unit tests
print_step "Running unit tests..."
mvn test -q
if [ $? -eq 0 ]; then
    print_success "All unit tests passed"
else
    print_error "Unit tests failed"
    exit 1
fi

# Run integration tests
print_step "Running integration tests..."
mvn verify -P integration-tests -q
if [ $? -eq 0 ]; then
    print_success "All integration tests passed"
else
    print_error "Integration tests failed"
    exit 1
fi

# Generate test coverage report
print_step "Generating test coverage report..."
mvn jacoco:report -q
if [ -f "${BUILD_DIR}/site/jacoco/index.html" ]; then
    print_success "Coverage report generated at ${BUILD_DIR}/site/jacoco/index.html"
else
    print_error "Failed to generate coverage report"
fi

# Run security checks
print_step "Running security analysis..."
mvn spotbugs:check -q
if [ $? -eq 0 ]; then
    print_success "Security analysis passed"
else
    print_error "Security issues detected"
    exit 1
fi

# Package application
print_step "Packaging application..."
mvn package -DskipTests -q
if [ -f "${BUILD_DIR}/${PROJECT_NAME}-"*.jar ]; then
    JAR_FILE=$(ls ${BUILD_DIR}/${PROJECT_NAME}-*.jar | head -n 1)
    print_success "Application packaged: $(basename $JAR_FILE)"
else
    print_error "Failed to package application"
    exit 1
fi

# Build Docker image (if Docker is available)
if [ "$DOCKER_AVAILABLE" = true ]; then
    print_step "Building Docker image..."
    docker build -t ${PROJECT_NAME}:latest . > /dev/null 2>&1
    if [ $? -eq 0 ]; then
        print_success "Docker image built: ${PROJECT_NAME}:latest"
    else
        print_error "Failed to build Docker image"
    fi
fi

# Generate API documentation
print_step "Generating API documentation..."
if mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev --server.port=0" &> /dev/null &
then
    SPRING_PID=$!
    sleep 10  # Wait for application to start
    kill $SPRING_PID > /dev/null 2>&1
    wait $SPRING_PID 2>/dev/null
    print_success "API documentation generated"
else
    print_error "Failed to generate API documentation"
fi

# Display build summary
echo ""
echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}          BUILD SUMMARY                 ${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""

# Calculate build artifacts size
if [ -f "$JAR_FILE" ]; then
    JAR_SIZE=$(du -h "$JAR_FILE" | cut -f1)
    echo -e "📦 Application JAR: ${GREEN}$(basename $JAR_FILE)${NC} (${JAR_SIZE})"
fi

if [ -f "${BUILD_DIR}/site/jacoco/index.html" ]; then
    echo -e "📊 Coverage Report: ${GREEN}${BUILD_DIR}/site/jacoco/index.html${NC}"
fi

if [ "$DOCKER_AVAILABLE" = true ]; then
    DOCKER_SIZE=$(docker images ${PROJECT_NAME}:latest --format "table {{.Size}}" | tail -n 1)
    echo -e "🐳 Docker Image: ${GREEN}${PROJECT_NAME}:latest${NC} (${DOCKER_SIZE})"
fi

echo ""
echo -e "${GREEN}✅ Build completed successfully!${NC}"
echo ""

# Display next steps
echo -e "${BLUE}Next Steps:${NC}"
echo -e "  • Run application: ${YELLOW}java -jar ${JAR_FILE}${NC}"
if [ "$DOCKER_AVAILABLE" = true ]; then
    echo -e "  • Run with Docker: ${YELLOW}docker run -p 8080:8080 ${PROJECT_NAME}:latest${NC}"
fi
echo -e "  • View coverage: ${YELLOW}open ${BUILD_DIR}/site/jacoco/index.html${NC}"
echo -e "  • API Documentation: ${YELLOW}http://localhost:8080/swagger-ui.html${NC}"
echo ""

exit 0
