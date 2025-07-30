package com.tacticalhub.command.controller.analytics;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.tacticalhub.command.domain.analytics.AnalyticsMetric;
import com.tacticalhub.command.service.analytics.AnalyticsService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * REST Controller for analytics and metrics management
 */
@RestController
@RequestMapping("/api/v1/analytics")
@Tag(name = "Analytics", description = "Analytics and Metrics API")
@SecurityRequirement(name = "bearerAuth")
public class AnalyticsController {
    
    private final AnalyticsService analyticsService;
    
    @Autowired
    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }
    
    @Operation(summary = "Record a new metric")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Metric recorded successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PostMapping("/metrics")
    @PreAuthorize("hasRole('ANALYST') or hasRole('COMMANDER')")
    public ResponseEntity<AnalyticsMetric> recordMetric(
            @Parameter(description = "Metric name") @RequestParam String metricName,
            @Parameter(description = "Metric type") @RequestParam AnalyticsMetric.MetricType metricType,
            @Parameter(description = "Metric category") @RequestParam AnalyticsMetric.MetricCategory category,
            @Parameter(description = "Metric value") @RequestParam Double value,
            @Parameter(description = "Unit ID (optional)") @RequestParam(required = false) Long unitId,
            @Parameter(description = "Mission ID (optional)") @RequestParam(required = false) Long missionId,
            @Parameter(description = "Dimensions (optional)") @RequestParam(required = false) Map<String, String> dimensions) {
        
        AnalyticsMetric metric = analyticsService.recordMetric(
            metricName, metricType, category, value, LocalDateTime.now(), unitId, missionId, dimensions);
        
        return ResponseEntity.ok(metric);
    }
    
    @Operation(summary = "Get metrics by name and time range")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Metrics retrieved successfully")
    })
    @GetMapping("/metrics/{metricName}")
    @PreAuthorize("hasRole('VIEWER') or hasRole('ANALYST') or hasRole('COMMANDER')")
    public ResponseEntity<List<AnalyticsMetric>> getMetrics(
            @Parameter(description = "Metric name") @PathVariable String metricName,
            @Parameter(description = "Start time") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @Parameter(description = "End time") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        
        List<AnalyticsMetric> metrics = analyticsService.getMetrics(metricName, startTime, endTime);
        return ResponseEntity.ok(metrics);
    }
    
    @Operation(summary = "Get metrics by category")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Metrics retrieved successfully")
    })
    @GetMapping("/metrics/category/{category}")
    @PreAuthorize("hasRole('VIEWER') or hasRole('ANALYST') or hasRole('COMMANDER')")
    public ResponseEntity<List<AnalyticsMetric>> getMetricsByCategory(
            @Parameter(description = "Metric category") @PathVariable AnalyticsMetric.MetricCategory category,
            @Parameter(description = "Start time") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @Parameter(description = "End time") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        
        List<AnalyticsMetric> metrics = analyticsService.getMetricsByCategory(category, startTime, endTime);
        return ResponseEntity.ok(metrics);
    }
    
    @Operation(summary = "Get metrics for a specific unit")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Unit metrics retrieved successfully")
    })
    @GetMapping("/metrics/unit/{unitId}")
    @PreAuthorize("hasRole('VIEWER') or hasRole('ANALYST') or hasRole('COMMANDER')")
    public ResponseEntity<List<AnalyticsMetric>> getUnitMetrics(
            @Parameter(description = "Unit ID") @PathVariable Long unitId,
            @Parameter(description = "Start time") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @Parameter(description = "End time") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        
        List<AnalyticsMetric> metrics = analyticsService.getUnitMetrics(unitId, startTime, endTime);
        return ResponseEntity.ok(metrics);
    }
    
    @Operation(summary = "Get metric statistics")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully")
    })
    @GetMapping("/metrics/{metricName}/statistics")
    @PreAuthorize("hasRole('ANALYST') or hasRole('COMMANDER')")
    public ResponseEntity<AnalyticsService.MetricStatistics> getMetricStatistics(
            @Parameter(description = "Metric name") @PathVariable String metricName,
            @Parameter(description = "Start time") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @Parameter(description = "End time") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        
        AnalyticsService.MetricStatistics statistics = analyticsService.getMetricStatistics(metricName, startTime, endTime);
        return ResponseEntity.ok(statistics);
    }
    
    @Operation(summary = "Get operational dashboard")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Dashboard data retrieved successfully")
    })
    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('VIEWER') or hasRole('ANALYST') or hasRole('COMMANDER')")
    public ResponseEntity<AnalyticsService.OperationalDashboard> getOperationalDashboard() {
        AnalyticsService.OperationalDashboard dashboard = analyticsService.getOperationalDashboard();
        return ResponseEntity.ok(dashboard);
    }
    
    @Operation(summary = "Get trend analysis for a metric")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Trend analysis retrieved successfully")
    })
    @GetMapping("/metrics/{metricName}/trends")
    @PreAuthorize("hasRole('ANALYST') or hasRole('COMMANDER')")
    public ResponseEntity<AnalyticsService.TrendAnalysis> getTrendAnalysis(
            @Parameter(description = "Metric name") @PathVariable String metricName,
            @Parameter(description = "Start time") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @Parameter(description = "End time") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        
        AnalyticsService.TrendAnalysis analysis = analyticsService.getTrendAnalysis(metricName, startTime, endTime);
        return ResponseEntity.ok(analysis);
    }
    
    @Operation(summary = "Get performance alerts")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Performance alerts retrieved successfully")
    })
    @GetMapping("/alerts")
    @PreAuthorize("hasRole('ANALYST') or hasRole('COMMANDER')")
    public ResponseEntity<List<AnalyticsService.PerformanceAlert>> getPerformanceAlerts() {
        List<AnalyticsService.PerformanceAlert> alerts = analyticsService.getPerformanceAlerts();
        return ResponseEntity.ok(alerts);
    }
}
