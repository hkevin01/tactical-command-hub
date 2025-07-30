package com.tacticalhub.command.controller.report;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.tacticalhub.command.domain.report.OperationalReport;
import com.tacticalhub.command.dto.report.OperationalReportRequest;
import com.tacticalhub.command.dto.report.OperationalReportResponse;
import com.tacticalhub.command.service.report.ReportService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * REST Controller for operational report management
 */
@RestController
@RequestMapping("/api/v1/reports")
@Tag(name = "Reports", description = "Operational Report Management API")
@SecurityRequirement(name = "bearerAuth")
public class ReportController {
    
    private final ReportService reportService;
    
    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }
    
    @Operation(summary = "Create a new operational report")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Report created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PostMapping
    @PreAuthorize("hasRole('COMMANDER') or hasRole('ANALYST')")
    public ResponseEntity<OperationalReportResponse> createReport(
            @Valid @RequestBody OperationalReportRequest request,
            Authentication authentication) {
        
        // Convert request to entity
        OperationalReport report = convertToEntity(request);
        report.setAuthorId(Long.parseLong(authentication.getName()));
        
        // Create the report
        OperationalReport createdReport = reportService.createReport(report);
        
        // Convert to response
        OperationalReportResponse response = convertToResponse(createdReport);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @Operation(summary = "Get report by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Report retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Report not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/{reportId}")
    @PreAuthorize("hasRole('VIEWER') or hasRole('ANALYST') or hasRole('COMMANDER')")
    public ResponseEntity<OperationalReportResponse> getReport(
            @Parameter(description = "Report ID") @PathVariable Long reportId) {
        
        return reportService.getReportById(reportId)
            .map(report -> ResponseEntity.ok(convertToResponse(report)))
            .orElse(ResponseEntity.notFound().build());
    }
    
    @Operation(summary = "Update an existing report")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Report updated successfully"),
        @ApiResponse(responseCode = "404", description = "Report not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PutMapping("/{reportId}")
    @PreAuthorize("hasRole('COMMANDER') or hasRole('ANALYST')")
    public ResponseEntity<OperationalReportResponse> updateReport(
            @Parameter(description = "Report ID") @PathVariable Long reportId,
            @Valid @RequestBody OperationalReportRequest request) {
        
        OperationalReport updatedData = convertToEntity(request);
        OperationalReport updatedReport = reportService.updateReport(reportId, updatedData);
        
        return ResponseEntity.ok(convertToResponse(updatedReport));
    }
    
    @Operation(summary = "Submit report for review")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Report submitted for review"),
        @ApiResponse(responseCode = "404", description = "Report not found"),
        @ApiResponse(responseCode = "400", description = "Report cannot be submitted")
    })
    @PostMapping("/{reportId}/submit")
    @PreAuthorize("hasRole('ANALYST') or hasRole('COMMANDER')")
    public ResponseEntity<OperationalReportResponse> submitForReview(
            @Parameter(description = "Report ID") @PathVariable Long reportId) {
        
        OperationalReport report = reportService.submitForReview(reportId);
        return ResponseEntity.ok(convertToResponse(report));
    }
    
    @Operation(summary = "Review a report")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Report reviewed successfully"),
        @ApiResponse(responseCode = "404", description = "Report not found"),
        @ApiResponse(responseCode = "400", description = "Report cannot be reviewed")
    })
    @PostMapping("/{reportId}/review")
    @PreAuthorize("hasRole('COMMANDER')")
    public ResponseEntity<OperationalReportResponse> reviewReport(
            @Parameter(description = "Report ID") @PathVariable Long reportId,
            Authentication authentication) {
        
        Long reviewerId = Long.parseLong(authentication.getName());
        OperationalReport report = reportService.reviewReport(reportId, reviewerId);
        return ResponseEntity.ok(convertToResponse(report));
    }
    
    @Operation(summary = "Approve a report")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Report approved successfully"),
        @ApiResponse(responseCode = "404", description = "Report not found"),
        @ApiResponse(responseCode = "400", description = "Report cannot be approved")
    })
    @PostMapping("/{reportId}/approve")
    @PreAuthorize("hasRole('COMMANDER')")
    public ResponseEntity<OperationalReportResponse> approveReport(
            @Parameter(description = "Report ID") @PathVariable Long reportId,
            Authentication authentication) {
        
        Long approverId = Long.parseLong(authentication.getName());
        OperationalReport report = reportService.approveReport(reportId, approverId);
        return ResponseEntity.ok(convertToResponse(report));
    }
    
    @Operation(summary = "Publish a report")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Report published successfully"),
        @ApiResponse(responseCode = "404", description = "Report not found"),
        @ApiResponse(responseCode = "400", description = "Report cannot be published")
    })
    @PostMapping("/{reportId}/publish")
    @PreAuthorize("hasRole('COMMANDER')")
    public ResponseEntity<OperationalReportResponse> publishReport(
            @Parameter(description = "Report ID") @PathVariable Long reportId) {
        
        OperationalReport report = reportService.publishReport(reportId);
        return ResponseEntity.ok(convertToResponse(report));
    }
    
    @Operation(summary = "Get reports by type")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reports retrieved successfully")
    })
    @GetMapping("/type/{reportType}")
    @PreAuthorize("hasRole('VIEWER') or hasRole('ANALYST') or hasRole('COMMANDER')")
    public ResponseEntity<List<OperationalReportResponse>> getReportsByType(
            @Parameter(description = "Report type") @PathVariable OperationalReport.ReportType reportType) {
        
        List<OperationalReport> reports = reportService.getReportsByType(reportType);
        List<OperationalReportResponse> response = reports.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Get reports by status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reports retrieved successfully")
    })
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('VIEWER') or hasRole('ANALYST') or hasRole('COMMANDER')")
    public ResponseEntity<List<OperationalReportResponse>> getReportsByStatus(
            @Parameter(description = "Report status") @PathVariable OperationalReport.ReportStatus status) {
        
        List<OperationalReport> reports = reportService.getReportsByStatus(status);
        List<OperationalReportResponse> response = reports.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Get reports by date range")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reports retrieved successfully")
    })
    @GetMapping("/date-range")
    @PreAuthorize("hasRole('VIEWER') or hasRole('ANALYST') or hasRole('COMMANDER')")
    public ResponseEntity<List<OperationalReportResponse>> getReportsByDateRange(
            @Parameter(description = "Start date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "End date") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        List<OperationalReport> reports = reportService.getReportsByDateRange(startDate, endDate);
        List<OperationalReportResponse> response = reports.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Search reports")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Search completed successfully")
    })
    @GetMapping("/search")
    @PreAuthorize("hasRole('VIEWER') or hasRole('ANALYST') or hasRole('COMMANDER')")
    public ResponseEntity<List<OperationalReportResponse>> searchReports(
            @Parameter(description = "Search term") @RequestParam String q) {
        
        List<OperationalReport> reports = reportService.searchReports(q);
        List<OperationalReportResponse> response = reports.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Get reports pending review")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pending reports retrieved successfully")
    })
    @GetMapping("/pending/review")
    @PreAuthorize("hasRole('COMMANDER')")
    public ResponseEntity<List<OperationalReportResponse>> getReportsPendingReview() {
        List<OperationalReport> reports = reportService.getReportsPendingReview();
        List<OperationalReportResponse> response = reports.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Get reports pending approval")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pending reports retrieved successfully")
    })
    @GetMapping("/pending/approval")
    @PreAuthorize("hasRole('COMMANDER')")
    public ResponseEntity<List<OperationalReportResponse>> getReportsPendingApproval() {
        List<OperationalReport> reports = reportService.getReportsPendingApproval();
        List<OperationalReportResponse> response = reports.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "Get report statistics")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully")
    })
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ANALYST') or hasRole('COMMANDER')")
    public ResponseEntity<ReportService.ReportStatistics> getReportStatistics() {
        ReportService.ReportStatistics statistics = reportService.getReportStatistics();
        return ResponseEntity.ok(statistics);
    }
    
    // Helper methods
    
    private OperationalReport convertToEntity(OperationalReportRequest request) {
        OperationalReport report = new OperationalReport();
        report.setTitle(request.getTitle());
        report.setReportType(request.getReportType());
        report.setPeriodStart(request.getPeriodStart());
        report.setPeriodEnd(request.getPeriodEnd());
        report.setExecutiveSummary(request.getExecutiveSummary());
        report.setKeyFindings(request.getKeyFindings());
        report.setRecommendations(request.getRecommendations());
        report.setMetrics(request.getMetrics());
        report.setAppendices(request.getAppendices());
        report.setClassification(request.getClassification());
        report.setCategories(request.getCategories());
        return report;
    }
    
    private OperationalReportResponse convertToResponse(OperationalReport report) {
        OperationalReportResponse response = new OperationalReportResponse();
        response.setId(report.getId());
        response.setReportNumber(report.getReportNumber());
        response.setTitle(report.getTitle());
        response.setReportType(report.getReportType());
        response.setStatus(report.getStatus());
        response.setPeriodStart(report.getPeriodStart());
        response.setPeriodEnd(report.getPeriodEnd());
        response.setAuthorId(report.getAuthorId());
        response.setExecutiveSummary(report.getExecutiveSummary());
        response.setKeyFindings(report.getKeyFindings());
        response.setRecommendations(report.getRecommendations());
        response.setMetrics(report.getMetrics());
        response.setAppendices(report.getAppendices());
        response.setClassification(report.getClassification());
        response.setCategories(report.getCategories());
        response.setReviewedBy(report.getReviewedBy());
        response.setReviewedAt(report.getReviewedAt());
        response.setApprovedBy(report.getApprovedBy());
        response.setApprovedAt(report.getApprovedAt());
        response.setCreatedAt(report.getCreatedAt());
        response.setUpdatedAt(report.getUpdatedAt());
        return response;
    }
}
