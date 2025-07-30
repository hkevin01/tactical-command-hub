package com.tacticalcommand.tactical.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.tacticalcommand.tactical.service.NetworkMonitoringService;
import com.tacticalcommand.tactical.service.NetworkMonitoringService.NetworkPerformanceMetrics;
import com.tacticalcommand.tactical.service.NetworkMonitoringService.NetworkStatus;
import com.tacticalcommand.tactical.service.NetworkMonitoringService.SystemHealth;

/**
 * Network Monitoring REST Controller providing API endpoints for system health monitoring.
 * Supports real-time monitoring of external military integration systems and services.
 * 
 * API Endpoints:
 * - GET /api/network/status - Get comprehensive network status
 * - GET /api/network/health/{systemId} - Get specific system health
 * - GET /api/network/metrics - Get performance metrics
 * - POST /api/network/check - Trigger manual health check
 * - POST /api/network/initialize - Initialize monitoring service
 * 
 * Security:
 * - Requires OPERATOR role for status monitoring
 * - Requires ADMIN role for manual operations
 * - All endpoints require authenticated access
 * 
 * @author Tactical Command Hub Team
 * @version 1.0.0
 * @since 2025-01-29
 */
@RestController
@RequestMapping("/api/network")
@PreAuthorize("hasRole('OPERATOR')")
public class NetworkMonitoringController {

    @Autowired
    private NetworkMonitoringService networkMonitoringService;

    /**
     * Get comprehensive network status including all monitored systems.
     * 
     * @return Network status with system health and active alerts
     */
    @GetMapping("/status")
    @PreAuthorize("hasRole('OPERATOR')")
    public ResponseEntity<NetworkStatus> getNetworkStatus() {
        NetworkStatus status = networkMonitoringService.getNetworkStatus();
        return ResponseEntity.ok(status);
    }

    /**
     * Get detailed health information for specific system.
     * 
     * @param systemId System identifier (GCCS-J, NWS-API, MSG-QUEUE, etc.)
     * @return System health details
     */
    @GetMapping("/health/{systemId}")
    @PreAuthorize("hasRole('OPERATOR')")
    public ResponseEntity<SystemHealth> getSystemHealth(@PathVariable String systemId) {
        SystemHealth health = networkMonitoringService.getSystemHealth(systemId);
        if (health != null) {
            return ResponseEntity.ok(health);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get network performance metrics and trends.
     * 
     * @return Performance metrics summary
     */
    @GetMapping("/metrics")
    @PreAuthorize("hasRole('OPERATOR')")
    public ResponseEntity<NetworkPerformanceMetrics> getPerformanceMetrics() {
        NetworkPerformanceMetrics metrics = networkMonitoringService.getPerformanceMetrics();
        return ResponseEntity.ok(metrics);
    }

    /**
     * Manually trigger health check for all monitored systems.
     * 
     * @return Health check results
     */
    @PostMapping("/check")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SystemHealth>> triggerHealthCheck() {
        List<SystemHealth> results = networkMonitoringService.triggerHealthCheck();
        return ResponseEntity.ok(results);
    }

    /**
     * Initialize network monitoring service with scheduled health checks.
     * 
     * @return Initialization confirmation
     */
    @PostMapping("/initialize")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<java.util.Map<String, Object>> initializeMonitoring() {
        networkMonitoringService.initializeMonitoring();
        return ResponseEntity.ok(java.util.Map.of(
            "success", true,
            "message", "Network monitoring initialized",
            "timestamp", java.time.LocalDateTime.now()
        ));
    }
}
