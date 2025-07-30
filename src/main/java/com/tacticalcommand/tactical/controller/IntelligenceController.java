package com.tacticalcommand.tactical.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.tacticalcommand.tactical.domain.IntelligenceReport;
import com.tacticalcommand.tactical.dto.IntelligenceReportRequest;
import com.tacticalcommand.tactical.dto.IntelligenceReportResponse;
import com.tacticalcommand.tactical.service.IntelligenceService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * REST Controller for intelligence and situational awareness operations.
 * Provides endpoints for creating, managing, and analyzing intelligence reports.
 */
@RestController
@RequestMapping("/api/v1/intelligence")
@Tag(name = "Intelligence & Situational Awareness", description = "Intelligence reporting and threat analysis endpoints")
@SecurityRequirement(name = "bearerAuth")
public class IntelligenceController {

    @Autowired
    private IntelligenceService intelligenceService;

    /**
     * Create a new intelligence report.
     *
     * @param request Intelligence report details
     * @return Created intelligence report
     */
    @PostMapping("/reports")
    @Operation(summary = "Create intelligence report", 
               description = "Create a new intelligence report with threat analysis")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Intelligence report created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid report request"),
        @ApiResponse(responseCode = "403", description = "Insufficient permissions or clearance"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ANALYST') or hasRole('COMMANDER') or hasRole('OPERATOR')")
    public ResponseEntity<IntelligenceReportResponse> createIntelligenceReport(
            @Valid @RequestBody IntelligenceReportRequest request) {
        try {
            IntelligenceReport report = convertToEntity(request);
            IntelligenceReport savedReport = intelligenceService.createIntelligenceReport(report);
            IntelligenceReportResponse response = convertToResponse(savedReport);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (SecurityException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get intelligence reports accessible to current user.
     *
     * @param page Page number (0-based)
     * @param size Page size
     * @return Page of intelligence reports
     */
    @GetMapping("/reports")
    @Operation(summary = "Get intelligence reports", 
               description = "Retrieve paginated intelligence reports accessible to current user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reports retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Authentication required"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ANALYST') or hasRole('COMMANDER') or hasRole('OPERATOR') or hasRole('VIEWER')")
    public ResponseEntity<Page<IntelligenceReportResponse>> getIntelligenceReports(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<IntelligenceReport> reports = intelligenceService.getAccessibleReports(pageable);
            Page<IntelligenceReportResponse> responses = reports.map(this::convertToResponse);
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get intelligence reports by classification level.
     *
     * @param classification Security classification level
     * @param page Page number
     * @param size Page size
     * @return Page of reports with specified classification
     */
    @GetMapping("/reports/classification/{classification}")
    @Operation(summary = "Get reports by classification", 
               description = "Retrieve intelligence reports by security classification level")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reports retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Insufficient clearance for classification"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ANALYST') or hasRole('COMMANDER')")
    public ResponseEntity<Page<IntelligenceReportResponse>> getReportsByClassification(
            @Parameter(description = "Security classification level") @PathVariable String classification,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<IntelligenceReport> reports = intelligenceService.getReportsByClassification(classification, pageable);
            Page<IntelligenceReportResponse> responses = reports.map(this::convertToResponse);
            return ResponseEntity.ok(responses);
        } catch (SecurityException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get intelligence reports by threat level.
     *
     * @param threatLevel Threat severity level
     * @param page Page number
     * @param size Page size
     * @return Page of reports with specified threat level
     */
    @GetMapping("/reports/threat/{threatLevel}")
    @Operation(summary = "Get reports by threat level", 
               description = "Retrieve intelligence reports by threat severity level")
    @PreAuthorize("hasRole('ANALYST') or hasRole('COMMANDER') or hasRole('OPERATOR')")
    public ResponseEntity<Page<IntelligenceReportResponse>> getReportsByThreatLevel(
            @Parameter(description = "Threat severity level") @PathVariable String threatLevel,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<IntelligenceReport> reports = intelligenceService.getReportsByThreatLevel(threatLevel, pageable);
            Page<IntelligenceReportResponse> responses = reports.map(this::convertToResponse);
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Search intelligence reports by content.
     *
     * @param searchTerm Search term
     * @param page Page number
     * @param size Page size
     * @return Page of matching reports
     */
    @GetMapping("/reports/search")
    @Operation(summary = "Search intelligence reports", 
               description = "Search intelligence reports by content, title, or summary")
    @PreAuthorize("hasRole('ANALYST') or hasRole('COMMANDER') or hasRole('OPERATOR')")
    public ResponseEntity<Page<IntelligenceReportResponse>> searchReports(
            @Parameter(description = "Search term") @RequestParam String searchTerm,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<IntelligenceReport> reports = intelligenceService.searchReports(searchTerm, pageable);
            Page<IntelligenceReportResponse> responses = reports.map(this::convertToResponse);
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get intelligence reports within geographic proximity.
     *
     * @param latitude Center latitude
     * @param longitude Center longitude
     * @param radiusKm Search radius in kilometers
     * @param page Page number
     * @param size Page size
     * @return Page of reports within geographic area
     */
    @GetMapping("/reports/geographic")
    @Operation(summary = "Get reports by geographic area", 
               description = "Retrieve intelligence reports within geographic proximity")
    @PreAuthorize("hasRole('ANALYST') or hasRole('COMMANDER') or hasRole('OPERATOR')")
    public ResponseEntity<Page<IntelligenceReportResponse>> getReportsByGeographicProximity(
            @Parameter(description = "Center latitude") @RequestParam Double latitude,
            @Parameter(description = "Center longitude") @RequestParam Double longitude,
            @Parameter(description = "Search radius in kilometers") @RequestParam Double radiusKm,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<IntelligenceReport> reports = intelligenceService.getReportsByGeographicProximity(
                latitude, longitude, radiusKm, pageable);
            Page<IntelligenceReportResponse> responses = reports.map(this::convertToResponse);
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get immediate priority reports requiring attention.
     *
     * @return List of immediate priority reports
     */
    @GetMapping("/reports/immediate")
    @Operation(summary = "Get immediate priority reports", 
               description = "Retrieve intelligence reports requiring immediate attention")
    @PreAuthorize("hasRole('ANALYST') or hasRole('COMMANDER')")
    public ResponseEntity<List<IntelligenceReportResponse>> getImmediatePriorityReports() {
        try {
            List<IntelligenceReport> reports = intelligenceService.getImmediatePriorityReports();
            List<IntelligenceReportResponse> responses = reports.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Update an existing intelligence report.
     *
     * @param reportId ID of report to update
     * @param request Updated report details
     * @return Updated intelligence report
     */
    @PutMapping("/reports/{reportId}")
    @Operation(summary = "Update intelligence report", 
               description = "Update an existing intelligence report")
    @PreAuthorize("hasRole('ANALYST') or hasRole('COMMANDER')")
    public ResponseEntity<IntelligenceReportResponse> updateIntelligenceReport(
            @Parameter(description = "Report ID") @PathVariable Long reportId,
            @Valid @RequestBody IntelligenceReportRequest request) {
        try {
            IntelligenceReport updatedReport = convertToEntity(request);
            IntelligenceReport savedReport = intelligenceService.updateIntelligenceReport(reportId, updatedReport);
            IntelligenceReportResponse response = convertToResponse(savedReport);
            return ResponseEntity.ok(response);
        } catch (SecurityException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Review and approve/reject an intelligence report.
     *
     * @param reportId ID of report to review
     * @param reviewStatus New review status
     * @param reviewComments Optional review comments
     * @return Updated report
     */
    @PutMapping("/reports/{reportId}/review")
    @Operation(summary = "Review intelligence report", 
               description = "Review and approve or reject an intelligence report")
    @PreAuthorize("hasRole('ANALYST') or hasRole('COMMANDER')")
    public ResponseEntity<IntelligenceReportResponse> reviewReport(
            @Parameter(description = "Report ID") @PathVariable Long reportId,
            @Parameter(description = "Review status (APPROVED, REJECTED)") @RequestParam String reviewStatus,
            @Parameter(description = "Review comments") @RequestParam(required = false) String reviewComments) {
        try {
            IntelligenceReport reviewedReport = intelligenceService.reviewReport(reportId, reviewStatus, reviewComments);
            IntelligenceReportResponse response = convertToResponse(reviewedReport);
            return ResponseEntity.ok(response);
        } catch (SecurityException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get intelligence statistics for dashboard.
     *
     * @return Intelligence statistics
     */
    @GetMapping("/statistics")
    @Operation(summary = "Get intelligence statistics", 
               description = "Retrieve intelligence statistics for dashboard display")
    @PreAuthorize("hasRole('ANALYST') or hasRole('COMMANDER')")
    public ResponseEntity<IntelligenceService.IntelligenceStatistics> getIntelligenceStatistics() {
        try {
            IntelligenceService.IntelligenceStatistics stats = intelligenceService.getIntelligenceStatistics();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Create threat correlation between reports.
     *
     * @param primaryReportId Primary report ID
     * @param correlatedReportId Correlated report ID
     * @param correlationType Type of correlation
     * @param correlationStrength Strength of correlation
     * @param description Correlation description
     * @return Success response
     */
    @PostMapping("/reports/{primaryReportId}/correlations")
    @Operation(summary = "Create threat correlation", 
               description = "Create correlation between intelligence reports")
    @PreAuthorize("hasRole('ANALYST') or hasRole('COMMANDER')")
    public ResponseEntity<String> createThreatCorrelation(
            @Parameter(description = "Primary report ID") @PathVariable Long primaryReportId,
            @Parameter(description = "Correlated report ID") @RequestParam Long correlatedReportId,
            @Parameter(description = "Correlation type") @RequestParam String correlationType,
            @Parameter(description = "Correlation strength") @RequestParam String correlationStrength,
            @Parameter(description = "Correlation description") @RequestParam(required = false) String description) {
        try {
            intelligenceService.createThreatCorrelation(
                primaryReportId, correlatedReportId, correlationType, correlationStrength, description);
            return ResponseEntity.ok("Threat correlation created successfully");
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Convert request DTO to entity.
     *
     * @param request Request DTO
     * @return Intelligence report entity
     */
    private IntelligenceReport convertToEntity(IntelligenceReportRequest request) {
        IntelligenceReport report = new IntelligenceReport();
        report.setTitle(request.getTitle());
        report.setClassification(request.getClassification());
        report.setThreatLevel(request.getThreatLevel());
        report.setConfidenceLevel(request.getConfidenceLevel());
        report.setSourceReliability(request.getSourceReliability());
        report.setContent(request.getContent());
        report.setSummary(request.getSummary());
        report.setGeographicArea(request.getGeographicArea());
        report.setLatitude(request.getLatitude());
        report.setLongitude(request.getLongitude());
        report.setRadiusKm(request.getRadiusKm());
        report.setThreatType(request.getThreatType());
        report.setTargetType(request.getTargetType());
        report.setTimeSensitivity(request.getTimeSensitivity());
        report.setExpiresAt(request.getExpiresAt());
        report.setDisseminationControls(request.getDisseminationControls());
        report.setSourceId(request.getSourceId());
        report.setSourceType(request.getSourceType());
        return report;
    }

    /**
     * Convert entity to response DTO.
     *
     * @param report Intelligence report entity
     * @return Response DTO
     */
    private IntelligenceReportResponse convertToResponse(IntelligenceReport report) {
        IntelligenceReportResponse response = new IntelligenceReportResponse();
        response.setId(report.getId());
        response.setReportNumber(report.getReportNumber());
        response.setTitle(report.getTitle());
        response.setClassification(report.getClassification());
        response.setThreatLevel(report.getThreatLevel());
        response.setConfidenceLevel(report.getConfidenceLevel());
        response.setSourceReliability(report.getSourceReliability());
        response.setContent(report.getContent());
        response.setSummary(report.getSummary());
        response.setGeographicArea(report.getGeographicArea());
        response.setLatitude(report.getLatitude());
        response.setLongitude(report.getLongitude());
        response.setRadiusKm(report.getRadiusKm());
        response.setThreatType(report.getThreatType());
        response.setTargetType(report.getTargetType());
        response.setTimeSensitivity(report.getTimeSensitivity());
        response.setCreatedAt(report.getCreatedAt());
        response.setUpdatedAt(report.getUpdatedAt());
        response.setExpiresAt(report.getExpiresAt());
        response.setDisseminationControls(report.getDisseminationControls());
        response.setAuthorName(report.getAuthor() != null ? report.getAuthor().getUsername() : null);
        response.setReviewedByName(report.getReviewedBy() != null ? report.getReviewedBy().getUsername() : null);
        response.setReviewStatus(report.getReviewStatus());
        response.setSourceId(report.getSourceId());
        response.setSourceType(report.getSourceType());
        response.setExpired(report.isExpired());
        response.setClassified(report.isClassified());
        response.setThreatSeverity(report.getThreatSeverity());
        return response;
    }
}
