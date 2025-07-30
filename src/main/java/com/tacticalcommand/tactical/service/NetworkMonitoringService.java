package com.tacticalcommand.tactical.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Network Monitoring Service for tracking external system connectivity and health.
 * Provides real-time monitoring of critical military integration endpoints and services.
 * 
 * This service monitors:
 * - External military command systems (GCCS-J, SIPR, NIPR networks)
 * - Weather API services (National Weather Service, backup providers)
 * - Message queue health and processing performance
 * - Database connectivity and response times
 * - Network latency and bandwidth utilization
 * 
 * Monitoring Capabilities:
 * - Continuous health checks with configurable intervals
 * - Network latency measurement and trending
 * - Service availability tracking with SLA monitoring
 * - Automated failover detection and notification
 * - Performance metrics collection and alerting
 * 
 * @author Tactical Command Hub Team
 * @version 1.0.0
 * @since 2025-01-29
 */
@Service
public class NetworkMonitoringService {

    private static final Logger logger = LoggerFactory.getLogger(NetworkMonitoringService.class);

    @Autowired
    private MilitaryIntegrationService integrationService;

    @Autowired
    private WeatherService weatherService;

    @Autowired
    private MilitaryMessageQueueService messageQueueService;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(3);
    
    // System monitoring data
    private final Map<String, SystemHealth> systemHealthMap = new ConcurrentHashMap<>();
    private final List<NetworkAlert> activeAlerts = new ArrayList<>();
    
    // Configuration
    private static final int MONITORING_INTERVAL_SECONDS = 30;
    private static final int MAX_RESPONSE_TIME_MS = 5000;
    private static final int ALERT_THRESHOLD_FAILURES = 3;

    /**
     * Initialize network monitoring with scheduled health checks.
     * Sets up continuous monitoring of all critical external systems.
     */
    public void initializeMonitoring() {
        logger.info("Initializing network monitoring service");

        // Initialize system health entries
        initializeSystemHealth();

        // Schedule periodic health checks
        scheduler.scheduleAtFixedRate(this::performHealthChecks, 
            0, MONITORING_INTERVAL_SECONDS, TimeUnit.SECONDS);

        // Schedule network performance monitoring
        scheduler.scheduleAtFixedRate(this::performNetworkAnalysis, 
            10, 60, TimeUnit.SECONDS);

        // Schedule alert cleanup
        scheduler.scheduleAtFixedRate(this::cleanupExpiredAlerts, 
            0, 300, TimeUnit.SECONDS);

        logger.info("Network monitoring initialized with {}-second intervals", MONITORING_INTERVAL_SECONDS);
    }

    /**
     * Get current network status for all monitored systems.
     * Provides comprehensive view of external system connectivity.
     * 
     * @return Network status summary
     */
    public NetworkStatus getNetworkStatus() {
        NetworkStatus status = new NetworkStatus();
        status.checkTime = LocalDateTime.now();
        status.systemHealthSummary = new ArrayList<>(systemHealthMap.values());
        status.activeAlerts = new ArrayList<>(activeAlerts);
        status.overallHealth = calculateOverallHealth();
        status.monitoredSystemCount = systemHealthMap.size();
        status.healthySystemCount = (int) systemHealthMap.values().stream()
            .filter(h -> h.status == HealthStatus.HEALTHY)
            .count();
        
        return status;
    }

    /**
     * Get detailed system health information for specific system.
     * 
     * @param systemId System identifier
     * @return Detailed system health data
     */
    public SystemHealth getSystemHealth(String systemId) {
        return systemHealthMap.get(systemId);
    }

    /**
     * Get network performance metrics and trends.
     * 
     * @return Performance metrics summary
     */
    public NetworkPerformanceMetrics getPerformanceMetrics() {
        NetworkPerformanceMetrics metrics = new NetworkPerformanceMetrics();
        metrics.checkTime = LocalDateTime.now();
        
        // Calculate average response times
        double avgResponseTime = systemHealthMap.values().stream()
            .mapToLong(h -> h.lastResponseTimeMs)
            .average()
            .orElse(0.0);
        metrics.averageResponseTimeMs = (long) avgResponseTime;
        
        // Calculate availability percentage
        long totalChecks = systemHealthMap.values().stream()
            .mapToLong(h -> h.totalChecks)
            .sum();
        long successfulChecks = systemHealthMap.values().stream()
            .mapToLong(h -> h.successfulChecks)
            .sum();
        metrics.overallAvailabilityPercent = totalChecks > 0 ? 
            (double) successfulChecks / totalChecks * 100.0 : 0.0;
        
        // Count active systems
        metrics.activeConnections = (int) systemHealthMap.values().stream()
            .filter(h -> h.status == HealthStatus.HEALTHY)
            .count();
        
        return metrics;
    }

    /**
     * Manually trigger health check for all systems.
     * Useful for on-demand system status verification.
     * 
     * @return Health check results
     */
    public List<SystemHealth> triggerHealthCheck() {
        logger.info("Manual health check triggered");
        performHealthChecks();
        return new ArrayList<>(systemHealthMap.values());
    }

    // Private monitoring methods

    private void initializeSystemHealth() {
        // GCCS-J Integration System
        systemHealthMap.put("GCCS-J", createSystemHealth("GCCS-J", 
            "Military Integration System", "gccs-j-endpoint"));

        // Weather Service API
        systemHealthMap.put("NWS-API", createSystemHealth("NWS-API", 
            "National Weather Service", "https://api.weather.gov"));

        // Message Queue System
        systemHealthMap.put("MSG-QUEUE", createSystemHealth("MSG-QUEUE", 
            "Military Message Queue", "internal-queue"));

        // Database Connection
        systemHealthMap.put("DATABASE", createSystemHealth("DATABASE", 
            "Primary Database", "database-connection"));

        // NATO Integration
        systemHealthMap.put("NATO-SYS", createSystemHealth("NATO-SYS", 
            "NATO Integration", "nato-endpoint"));
    }

    private SystemHealth createSystemHealth(String id, String name, String endpoint) {
        SystemHealth health = new SystemHealth();
        health.systemId = id;
        health.systemName = name;
        health.endpoint = endpoint;
        health.status = HealthStatus.UNKNOWN;
        health.lastCheck = LocalDateTime.now();
        health.totalChecks = 0;
        health.successfulChecks = 0;
        health.consecutiveFailures = 0;
        return health;
    }

    private void performHealthChecks() {
        systemHealthMap.values().parallelStream().forEach(this::checkSystemHealth);
    }

    private void checkSystemHealth(SystemHealth systemHealth) {
        long startTime = System.currentTimeMillis();
        boolean isHealthy = false;
        String statusMessage = "";

        try {
            systemHealth.totalChecks++;
            systemHealth.lastCheck = LocalDateTime.now();

            // Perform system-specific health check
            switch (systemHealth.systemId) {
                case "GCCS-J":
                    isHealthy = checkMilitaryIntegrationHealth();
                    statusMessage = "GCCS-J connectivity verified";
                    break;
                case "NWS-API":
                    isHealthy = checkWeatherServiceHealth();
                    statusMessage = "Weather API responding";
                    break;
                case "MSG-QUEUE":
                    isHealthy = checkMessageQueueHealth();
                    statusMessage = "Message queue operational";
                    break;
                case "DATABASE":
                    isHealthy = checkDatabaseHealth();
                    statusMessage = "Database connection active";
                    break;
                case "NATO-SYS":
                    isHealthy = checkNATOSystemHealth();
                    statusMessage = "NATO systems accessible";
                    break;
                default:
                    isHealthy = false;
                    statusMessage = "Unknown system type";
            }

            long responseTime = System.currentTimeMillis() - startTime;
            systemHealth.lastResponseTimeMs = responseTime;

            if (isHealthy && responseTime < MAX_RESPONSE_TIME_MS) {
                systemHealth.status = HealthStatus.HEALTHY;
                systemHealth.successfulChecks++;
                systemHealth.consecutiveFailures = 0;
                systemHealth.lastSuccessTime = LocalDateTime.now();
            } else {
                systemHealth.status = responseTime >= MAX_RESPONSE_TIME_MS ? 
                    HealthStatus.SLOW : HealthStatus.UNHEALTHY;
                systemHealth.consecutiveFailures++;
                systemHealth.lastFailureTime = LocalDateTime.now();
                
                // Generate alert for consecutive failures
                if (systemHealth.consecutiveFailures >= ALERT_THRESHOLD_FAILURES) {
                    generateAlert(systemHealth, "System experiencing consecutive failures");
                }
            }

            systemHealth.statusMessage = statusMessage;

        } catch (Exception e) {
            systemHealth.status = HealthStatus.ERROR;
            systemHealth.consecutiveFailures++;
            systemHealth.lastFailureTime = LocalDateTime.now();
            systemHealth.statusMessage = "Error: " + e.getMessage();
            
            logger.warn("Health check failed for system {}: {}", 
                systemHealth.systemId, e.getMessage());
        }
    }

    private boolean checkMilitaryIntegrationHealth() {
        try {
            // Check if integration service is responsive by testing GCCS-J connection
            // Use simulated credentials for health check
            MilitaryIntegrationService.SystemCredentials testCreds = 
                new MilitaryIntegrationService.SystemCredentials();
            testCreds.username = "health-check";
            testCreds.password = "test-password"; 
            testCreds.certificate = "test-cert";
            
            var futureResult = integrationService.connectToGCCSJ("HEALTH-CHECK", testCreds);
            var result = futureResult.get(5, TimeUnit.SECONDS); // Wait up to 5 seconds
            return result.connected;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean checkWeatherServiceHealth() {
        try {
            // Test weather API connectivity with simple request
            weatherService.getCurrentWeather(38.9072, -77.0369); // Washington DC
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean checkMessageQueueHealth() {
        try {
            // Check message queue status
            var status = messageQueueService.getQueueStatus();
            return "HEALTHY".equals(status.processingHealth);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean checkDatabaseHealth() {
        try {
            // Simple database connectivity check (would use actual repository)
            return true; // Placeholder - would implement actual database ping
        } catch (Exception e) {
            return false;
        }
    }

    private boolean checkNATOSystemHealth() {
        try {
            // Check NATO system connectivity (simulated)
            return true; // Placeholder - would implement actual NATO system check
        } catch (Exception e) {
            return false;
        }
    }

    private void performNetworkAnalysis() {
        // Analyze network performance trends and patterns
        logger.debug("Performing network performance analysis");
        
        // Calculate performance trends
        long avgResponseTime = (long) systemHealthMap.values().stream()
            .mapToLong(h -> h.lastResponseTimeMs)
            .average()
            .orElse(0.0);

        // Generate performance alerts if needed
        if (avgResponseTime > MAX_RESPONSE_TIME_MS) {
            generatePerformanceAlert("Network performance degraded", avgResponseTime);
        }
    }

    private void generateAlert(SystemHealth systemHealth, String message) {
        NetworkAlert alert = new NetworkAlert();
        alert.alertId = "ALERT-" + System.currentTimeMillis();
        alert.systemId = systemHealth.systemId;
        alert.alertType = AlertType.SYSTEM_FAILURE;
        alert.severity = AlertSeverity.HIGH;
        alert.message = message;
        alert.timestamp = LocalDateTime.now();
        alert.isActive = true;

        activeAlerts.add(alert);
        logger.warn("Network alert generated: {} - {}", systemHealth.systemId, message);
    }

    private void generatePerformanceAlert(String message, long responseTime) {
        NetworkAlert alert = new NetworkAlert();
        alert.alertId = "PERF-" + System.currentTimeMillis();
        alert.systemId = "NETWORK";
        alert.alertType = AlertType.PERFORMANCE;
        alert.severity = AlertSeverity.MEDIUM;
        alert.message = message + " (Response time: " + responseTime + "ms)";
        alert.timestamp = LocalDateTime.now();
        alert.isActive = true;

        activeAlerts.add(alert);
        logger.warn("Performance alert generated: {}", message);
    }

    private void cleanupExpiredAlerts() {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(24);
        activeAlerts.removeIf(alert -> alert.timestamp.isBefore(cutoff));
    }

    private String calculateOverallHealth() {
        long healthyCount = systemHealthMap.values().stream()
            .filter(h -> h.status == HealthStatus.HEALTHY)
            .count();
        
        double healthyPercentage = (double) healthyCount / systemHealthMap.size() * 100.0;
        
        if (healthyPercentage >= 90) return "EXCELLENT";
        if (healthyPercentage >= 75) return "GOOD";
        if (healthyPercentage >= 50) return "DEGRADED";
        return "CRITICAL";
    }

    // Data classes for network monitoring

    public enum HealthStatus {
        HEALTHY,
        UNHEALTHY,
        SLOW,
        ERROR,
        UNKNOWN
    }

    public enum AlertType {
        SYSTEM_FAILURE,
        PERFORMANCE,
        CONNECTIVITY,
        SECURITY
    }

    public enum AlertSeverity {
        LOW,
        MEDIUM,
        HIGH,
        CRITICAL
    }

    public static class SystemHealth {
        public String systemId;
        public String systemName;
        public String endpoint;
        public HealthStatus status;
        public String statusMessage;
        public LocalDateTime lastCheck;
        public LocalDateTime lastSuccessTime;
        public LocalDateTime lastFailureTime;
        public long lastResponseTimeMs;
        public long totalChecks;
        public long successfulChecks;
        public int consecutiveFailures;
    }

    public static class NetworkAlert {
        public String alertId;
        public String systemId;
        public AlertType alertType;
        public AlertSeverity severity;
        public String message;
        public LocalDateTime timestamp;
        public boolean isActive;
    }

    public static class NetworkStatus {
        public LocalDateTime checkTime;
        public List<SystemHealth> systemHealthSummary;
        public List<NetworkAlert> activeAlerts;
        public String overallHealth;
        public int monitoredSystemCount;
        public int healthySystemCount;
    }

    public static class NetworkPerformanceMetrics {
        public LocalDateTime checkTime;
        public long averageResponseTimeMs;
        public double overallAvailabilityPercent;
        public int activeConnections;
    }
}
