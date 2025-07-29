#!/bin/bash

# Tactical Command Hub - Deployment Script
# This script handles deployment to various environments

set -e  # Exit on any error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
PROJECT_NAME="tactical-command-hub"
DEFAULT_ENVIRONMENT="development"
DEFAULT_PORT="8080"

# Environment-specific configurations
declare -A ENV_CONFIGS
ENV_CONFIGS[development]="dev"
ENV_CONFIGS[staging]="staging" 
ENV_CONFIGS[production]="prod"

echo -e "${BLUE}==============================================${NC}"
echo -e "${BLUE}  Tactical Command Hub - Deployment Script  ${NC}"
echo -e "${BLUE}==============================================${NC}"
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

# Function to show usage
show_usage() {
    echo "Usage: $0 [OPTIONS]"
    echo ""
    echo "Options:"
    echo "  -e, --environment    Target environment (development|staging|production)"
    echo "  -m, --mode          Deployment mode (docker|jar|compose)"
    echo "  -p, --port          Application port (default: 8080)"
    echo "  -h, --help          Show this help message"
    echo ""
    echo "Examples:"
    echo "  $0 -e development -m jar"
    echo "  $0 -e production -m docker -p 8080"
    echo "  $0 -e staging -m compose"
    echo ""
}

# Parse command line arguments
ENVIRONMENT="$DEFAULT_ENVIRONMENT"
MODE="jar"
PORT="$DEFAULT_PORT"

while [[ $# -gt 0 ]]; do
    case $1 in
        -e|--environment)
            ENVIRONMENT="$2"
            shift 2
            ;;
        -m|--mode)
            MODE="$2"
            shift 2
            ;;
        -p|--port)
            PORT="$2"
            shift 2
            ;;
        -h|--help)
            show_usage
            exit 0
            ;;
        *)
            print_error "Unknown option: $1"
            show_usage
            exit 1
            ;;
    esac
done

# Validate environment
if [[ ! " ${!ENV_CONFIGS[@]} " =~ " ${ENVIRONMENT} " ]]; then
    print_error "Invalid environment: $ENVIRONMENT"
    echo "Valid environments: ${!ENV_CONFIGS[@]}"
    exit 1
fi

# Validate mode
if [[ ! "$MODE" =~ ^(docker|jar|compose)$ ]]; then
    print_error "Invalid mode: $MODE"
    echo "Valid modes: docker, jar, compose"
    exit 1
fi

PROFILE="${ENV_CONFIGS[$ENVIRONMENT]}"

echo -e "🎯 ${BLUE}Environment:${NC} $ENVIRONMENT"
echo -e "📦 ${BLUE}Mode:${NC} $MODE"
echo -e "🔌 ${BLUE}Port:${NC} $PORT"
echo -e "⚙️  ${BLUE}Profile:${NC} $PROFILE"
echo ""

# Check prerequisites
print_step "Checking prerequisites..."

# Check if application is built
JAR_FILE=$(ls target/${PROJECT_NAME}-*.jar 2>/dev/null | head -n 1)
if [ ! -f "$JAR_FILE" ]; then
    print_error "Application JAR not found. Please run build.sh first."
    exit 1
fi
print_success "Application JAR found: $(basename $JAR_FILE)"

# Mode-specific prerequisite checks
case $MODE in
    docker)
        if ! command -v docker &> /dev/null; then
            print_error "Docker not found. Please install Docker."
            exit 1
        fi
        print_success "Docker available"
        ;;
    compose)
        if ! command -v docker-compose &> /dev/null; then
            print_error "Docker Compose not found. Please install Docker Compose."
            exit 1
        fi
        print_success "Docker Compose available"
        ;;
esac

# Stop any running instances
print_step "Stopping existing instances..."

case $MODE in
    jar)
        # Kill any running JAR processes
        if pgrep -f "$PROJECT_NAME" > /dev/null; then
            pkill -f "$PROJECT_NAME"
            sleep 2
            print_success "Stopped existing JAR process"
        fi
        ;;
    docker)
        # Stop Docker container if running
        if docker ps -q -f name="$PROJECT_NAME" | grep -q .; then
            docker stop "$PROJECT_NAME" > /dev/null 2>&1
            docker rm "$PROJECT_NAME" > /dev/null 2>&1
            print_success "Stopped existing Docker container"
        fi
        ;;
    compose)
        # Stop Docker Compose services
        if [ -f "docker-compose.yml" ]; then
            docker-compose down > /dev/null 2>&1
            print_success "Stopped Docker Compose services"
        fi
        ;;
esac

# Prepare environment-specific configuration
print_step "Preparing environment configuration..."

# Create environment-specific directory
ENV_DIR="deployment/$ENVIRONMENT"
mkdir -p "$ENV_DIR"

# Generate application properties for environment
cat > "$ENV_DIR/application-$PROFILE.properties" << EOF
# Environment-specific configuration for $ENVIRONMENT
server.port=$PORT
spring.profiles.active=$PROFILE

# Database configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/tactical_command_hub_$ENVIRONMENT
spring.datasource.username=tactical_user
spring.datasource.password=tactical_password

# Security configuration
jwt.secret=tactical-command-hub-jwt-secret-$ENVIRONMENT-$(date +%s)
jwt.expiration=86400

# Logging configuration
logging.level.com.tacticalcommand=INFO
logging.file.name=logs/tactical-command-hub-$ENVIRONMENT.log

# Monitoring configuration
management.endpoints.web.exposure.include=health,info,metrics,prometheus
management.endpoint.health.show-details=when-authorized
EOF

print_success "Environment configuration prepared"

# Deploy based on mode
print_step "Deploying application..."

case $MODE in
    jar)
        echo -e "${BLUE}Deploying as standalone JAR...${NC}"
        
        # Create logs directory
        mkdir -p logs
        
        # Start application in background
        nohup java -jar \
            -Dspring.profiles.active=$PROFILE \
            -Dspring.config.additional-location=file:$ENV_DIR/ \
            "$JAR_FILE" \
            > logs/tactical-command-hub-$ENVIRONMENT.log 2>&1 &
        
        APP_PID=$!
        echo $APP_PID > "$ENV_DIR/app.pid"
        
        # Wait and check if application started successfully
        sleep 10
        if kill -0 $APP_PID 2>/dev/null; then
            print_success "Application started successfully (PID: $APP_PID)"
        else
            print_error "Application failed to start"
            exit 1
        fi
        ;;
        
    docker)
        echo -e "${BLUE}Deploying as Docker container...${NC}"
        
        # Build Docker image if not exists
        if ! docker images | grep -q "$PROJECT_NAME"; then
            print_step "Building Docker image..."
            docker build -t $PROJECT_NAME:latest . > /dev/null
            print_success "Docker image built"
        fi
        
        # Run Docker container
        docker run -d \
            --name "$PROJECT_NAME" \
            -p $PORT:8080 \
            -e SPRING_PROFILES_ACTIVE=$PROFILE \
            -v "$(pwd)/$ENV_DIR:/app/config" \
            -v "$(pwd)/logs:/app/logs" \
            $PROJECT_NAME:latest > /dev/null
        
        print_success "Docker container started"
        ;;
        
    compose)
        echo -e "${BLUE}Deploying with Docker Compose...${NC}"
        
        if [ ! -f "docker-compose.yml" ]; then
            print_error "docker-compose.yml not found"
            exit 1
        fi
        
        # Set environment variables for Docker Compose
        export SPRING_PROFILES_ACTIVE=$PROFILE
        export SERVER_PORT=$PORT
        export ENVIRONMENT=$ENVIRONMENT
        
        # Start services
        docker-compose up -d
        
        print_success "Docker Compose services started"
        ;;
esac

# Health check
print_step "Performing health check..."

# Wait for application to be ready
HEALTH_URL="http://localhost:$PORT/actuator/health"
RETRY_COUNT=0
MAX_RETRIES=30

while [ $RETRY_COUNT -lt $MAX_RETRIES ]; do
    if curl -f -s "$HEALTH_URL" > /dev/null 2>&1; then
        print_success "Application is healthy"
        break
    fi
    
    RETRY_COUNT=$((RETRY_COUNT + 1))
    echo -e "${YELLOW}Waiting for application to be ready... ($RETRY_COUNT/$MAX_RETRIES)${NC}"
    sleep 2
done

if [ $RETRY_COUNT -eq $MAX_RETRIES ]; then
    print_error "Health check failed after $MAX_RETRIES attempts"
    exit 1
fi

# Display deployment summary
echo ""
echo -e "${BLUE}============================================${NC}"
echo -e "${BLUE}         DEPLOYMENT SUMMARY                ${NC}"
echo -e "${BLUE}============================================${NC}"
echo ""

echo -e "🎯 ${GREEN}Environment:${NC} $ENVIRONMENT"
echo -e "📦 ${GREEN}Mode:${NC} $MODE"
echo -e "🔌 ${GREEN}Port:${NC} $PORT"
echo -e "⚙️  ${GREEN}Profile:${NC} $PROFILE"

case $MODE in
    jar)
        echo -e "🔧 ${GREEN}PID File:${NC} $ENV_DIR/app.pid"
        echo -e "📝 ${GREEN}Log File:${NC} logs/tactical-command-hub-$ENVIRONMENT.log"
        ;;
    docker)
        echo -e "🐳 ${GREEN}Container:${NC} $PROJECT_NAME"
        ;;
    compose)
        echo -e "🐳 ${GREEN}Services:${NC} See docker-compose.yml"
        ;;
esac

echo ""
echo -e "${GREEN}✅ Deployment completed successfully!${NC}"
echo ""

# Display access information
echo -e "${BLUE}Access Information:${NC}"
echo -e "  • Application URL: ${YELLOW}http://localhost:$PORT${NC}"
echo -e "  • API Documentation: ${YELLOW}http://localhost:$PORT/swagger-ui.html${NC}"
echo -e "  • Health Check: ${YELLOW}http://localhost:$PORT/actuator/health${NC}"
echo -e "  • Metrics: ${YELLOW}http://localhost:$PORT/actuator/metrics${NC}"
echo ""

# Display management commands
echo -e "${BLUE}Management Commands:${NC}"
case $MODE in
    jar)
        echo -e "  • Stop: ${YELLOW}kill \$(cat $ENV_DIR/app.pid)${NC}"
        echo -e "  • Logs: ${YELLOW}tail -f logs/tactical-command-hub-$ENVIRONMENT.log${NC}"
        ;;
    docker)
        echo -e "  • Stop: ${YELLOW}docker stop $PROJECT_NAME${NC}"
        echo -e "  • Logs: ${YELLOW}docker logs -f $PROJECT_NAME${NC}"
        ;;
    compose)
        echo -e "  • Stop: ${YELLOW}docker-compose down${NC}"
        echo -e "  • Logs: ${YELLOW}docker-compose logs -f${NC}"
        ;;
esac

echo ""

exit 0
