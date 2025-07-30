# Multi-stage Docker build for Tactical Command Hub

# Build stage
FROM maven:3.9.4-eclipse-temurin-17 AS builder

WORKDIR /app

# Copy Maven configuration files
COPY pom.xml .
COPY .mvn/ .mvn/
COPY mvnw .

# Download dependencies (cached layer)
RUN mvn dependency:go-offline -B

# Copy source code
COPY src/ src/

# Build application
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-alpine

# Add metadata
LABEL maintainer="tactical-command-hub@tacticalcommand.com"
LABEL version="1.0.0"
LABEL description="Tactical Command Hub - Military C2 System Simulator"

# Create application user for security
RUN addgroup -g 1001 -S tactical && \
  adduser -u 1001 -S tactical -G tactical

# Install required packages
RUN apk add --no-cache \
  curl \
  tzdata \
  && rm -rf /var/cache/apk/*

# Set timezone
ENV TZ=UTC

# Create application directory
WORKDIR /app

# Copy built application from builder stage
COPY --from=builder /app/target/tactical-command-hub-*.jar app.jar

# Copy configuration files
COPY --from=builder /app/src/main/resources/application.yml application.yml

# Change ownership to tactical user
RUN chown -R tactical:tactical /app

# Switch to non-root user
USER tactical

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Expose port
EXPOSE 8080

# Set JVM options for containerized environment
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -Djava.security.egd=file:/dev/./urandom"

# Start application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
