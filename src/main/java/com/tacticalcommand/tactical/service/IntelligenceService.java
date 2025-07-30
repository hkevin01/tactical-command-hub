package com.tacticalcommand.tactical.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tacticalcommand.tactical.domain.IntelligenceReport;
import com.tacticalcommand.tactical.domain.ThreatCorrelation;
import com.tacticalcommand.tactical.domain.User;
import com.tacticalcommand.tactical.repository.IntelligenceReportRepository;

/**
 * Service for managing intelligence reports, threat correlation, and situational awareness.
 * Provides comprehensive intelligence analysis and reporting capabilities.
 */
@Service
@Transactional
public class IntelligenceService {

    @Autowired
    private IntelligenceReportRepository intelligenceRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private MessagingService messagingService;

    /**
     * Create a new intelligence report with automatic report number generation.
     *
     * @param report Intelligence report to create
     * @return Created and saved intelligence report
     */
    public IntelligenceReport createIntelligenceReport(IntelligenceReport report) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.findByUsername(auth.getName());

        // Set author
        report.setAuthor(currentUser);

        // Generate report number if not provided
        if (report.getReportNumber() == null || report.getReportNumber().isEmpty()) {
            report.setReportNumber(generateReportNumber());
        }

        // Set default values
        if (report.getCreatedAt() == null) {
            report.setCreatedAt(LocalDateTime.now());
        }

        // Validate classification access
        if (!userService.hasRequiredClearance(currentUser, report.getClassification())) {
            throw new SecurityException("Insufficient clearance for classification: " + report.getClassification());
        }

        // Save report
        IntelligenceReport savedReport = intelligenceRepository.save(report);

        // Send notifications for high-priority reports
        if ("IMMEDIATE".equals(report.getTimeSensitivity()) || "CRITICAL".equals(report.getThreatLevel())) {
            sendHighPriorityAlert(savedReport);
        }

        return savedReport;
    }

    /**
     * Update an existing intelligence report.
     *
     * @param reportId ID of report to update
     * @param updatedReport Updated report data
     * @return Updated intelligence report
     */
    public IntelligenceReport updateIntelligenceReport(Long reportId, IntelligenceReport updatedReport) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.findByUsername(auth.getName());

        Optional<IntelligenceReport> reportOpt = intelligenceRepository.findById(reportId);
        if (reportOpt.isEmpty()) {
            throw new RuntimeException("Intelligence report not found: " + reportId);
        }

        IntelligenceReport existingReport = reportOpt.get();

        // Check authorization (author or analyst/commander role)
        if (!existingReport.getAuthor().equals(currentUser) && 
            !hasAnalystOrCommanderRole(currentUser)) {
            throw new SecurityException("Insufficient permissions to update report");
        }

        // Validate classification access
        if (!userService.hasRequiredClearance(currentUser, updatedReport.getClassification())) {
            throw new SecurityException("Insufficient clearance for classification: " + updatedReport.getClassification());
        }

        // Update fields
        existingReport.setTitle(updatedReport.getTitle());
        existingReport.setClassification(updatedReport.getClassification());
        existingReport.setThreatLevel(updatedReport.getThreatLevel());
        existingReport.setConfidenceLevel(updatedReport.getConfidenceLevel());
        existingReport.setSourceReliability(updatedReport.getSourceReliability());
        existingReport.setContent(updatedReport.getContent());
        existingReport.setSummary(updatedReport.getSummary());
        existingReport.setGeographicArea(updatedReport.getGeographicArea());
        existingReport.setLatitude(updatedReport.getLatitude());
        existingReport.setLongitude(updatedReport.getLongitude());
        existingReport.setRadiusKm(updatedReport.getRadiusKm());
        existingReport.setThreatType(updatedReport.getThreatType());
        existingReport.setTargetType(updatedReport.getTargetType());
        existingReport.setTimeSensitivity(updatedReport.getTimeSensitivity());
        existingReport.setExpiresAt(updatedReport.getExpiresAt());
        existingReport.setDisseminationControls(updatedReport.getDisseminationControls());
        existingReport.setUpdatedAt(LocalDateTime.now());

        return intelligenceRepository.save(existingReport);
    }

    /**
     * Get intelligence reports accessible to the current user.
     *
     * @param pageable Pagination parameters
     * @return Page of accessible intelligence reports
     */
    public Page<IntelligenceReport> getAccessibleReports(Pageable pageable) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.findByUsername(auth.getName());

        // For now, return all reports user has clearance for
        // In a real system, this would filter by user's clearance level
        return intelligenceRepository.findAll(pageable);
    }

    /**
     * Get intelligence reports by classification level.
     *
     * @param classification Security classification level
     * @param pageable Pagination parameters
     * @return Page of reports with specified classification
     */
    public Page<IntelligenceReport> getReportsByClassification(String classification, Pageable pageable) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.findByUsername(auth.getName());

        // Verify user has clearance
        if (!userService.hasRequiredClearance(currentUser, classification)) {
            throw new SecurityException("Insufficient clearance for classification: " + classification);
        }

        return intelligenceRepository.findByClassificationOrderByCreatedAtDesc(classification, pageable);
    }

    /**
     * Get intelligence reports by threat level.
     *
     * @param threatLevel Threat severity level
     * @param pageable Pagination parameters
     * @return Page of reports with specified threat level
     */
    public Page<IntelligenceReport> getReportsByThreatLevel(String threatLevel, Pageable pageable) {
        return intelligenceRepository.findByThreatLevelOrderByCreatedAtDesc(threatLevel, pageable);
    }

    /**
     * Search intelligence reports by content.
     *
     * @param searchTerm Search term
     * @param pageable Pagination parameters
     * @return Page of matching reports
     */
    public Page<IntelligenceReport> searchReports(String searchTerm, Pageable pageable) {
        return intelligenceRepository.searchReports(searchTerm, pageable);
    }

    /**
     * Get intelligence reports within geographic proximity.
     *
     * @param latitude Center latitude
     * @param longitude Center longitude
     * @param radiusKm Search radius in kilometers
     * @param pageable Pagination parameters
     * @return Page of reports within geographic area
     */
    public Page<IntelligenceReport> getReportsByGeographicProximity(
            Double latitude, Double longitude, Double radiusKm, Pageable pageable) {
        return intelligenceRepository.findByGeographicProximity(latitude, longitude, radiusKm, pageable);
    }

    /**
     * Get immediate priority reports requiring attention.
     *
     * @return List of immediate priority reports
     */
    public List<IntelligenceReport> getImmediatePriorityReports() {
        return intelligenceRepository.findImmediatePriorityReports();
    }

    /**
     * Review and approve/reject an intelligence report.
     *
     * @param reportId ID of report to review
     * @param reviewStatus New review status (APPROVED, REJECTED)
     * @param reviewComments Optional review comments
     * @return Updated report
     */
    public IntelligenceReport reviewReport(Long reportId, String reviewStatus, String reviewComments) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.findByUsername(auth.getName());

        // Verify user has analyst or commander role
        if (!hasAnalystOrCommanderRole(currentUser)) {
            throw new SecurityException("Insufficient permissions to review reports");
        }

        Optional<IntelligenceReport> reportOpt = intelligenceRepository.findById(reportId);
        if (reportOpt.isEmpty()) {
            throw new RuntimeException("Intelligence report not found: " + reportId);
        }

        IntelligenceReport report = reportOpt.get();
        report.setReviewStatus(reviewStatus);
        report.setReviewedBy(currentUser);
        report.setUpdatedAt(LocalDateTime.now());

        // If approved and high priority, send additional notifications
        if ("APPROVED".equals(reviewStatus) && ("CRITICAL".equals(report.getThreatLevel()) || 
            "IMMEDIATE".equals(report.getTimeSensitivity()))) {
            sendApprovedHighPriorityAlert(report);
        }

        return intelligenceRepository.save(report);
    }

    /**
     * Create threat correlation between reports.
     *
     * @param primaryReportId Primary report ID
     * @param correlatedReportId Correlated report ID
     * @param correlationType Type of correlation
     * @param correlationStrength Strength of correlation
     * @param description Correlation description
     * @return Created threat correlation
     */
    public ThreatCorrelation createThreatCorrelation(Long primaryReportId, Long correlatedReportId,
                                                   String correlationType, String correlationStrength,
                                                   String description) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.findByUsername(auth.getName());

        // Verify both reports exist
        Optional<IntelligenceReport> primaryOpt = intelligenceRepository.findById(primaryReportId);
        Optional<IntelligenceReport> correlatedOpt = intelligenceRepository.findById(correlatedReportId);

        if (primaryOpt.isEmpty() || correlatedOpt.isEmpty()) {
            throw new RuntimeException("One or both intelligence reports not found");
        }

        ThreatCorrelation correlation = new ThreatCorrelation();
        correlation.setIntelligenceReport(primaryOpt.get());
        correlation.setCorrelatedReport(correlatedOpt.get());
        correlation.setCorrelationType(correlationType);
        correlation.setCorrelationStrength(correlationStrength);
        correlation.setDescription(description);
        correlation.setCreatedBy(currentUser);

        // Calculate confidence score based on correlation strength
        correlation.setConfidenceScore(calculateConfidenceScore(correlationStrength));

        // Note: Since we don't have a ThreatCorrelationRepository yet, 
        // we would save it through the intelligence report relationship
        primaryOpt.get().getCorrelations().add(correlation);
        intelligenceRepository.save(primaryOpt.get());

        return correlation;
    }

    /**
     * Get intelligence statistics for dashboard.
     *
     * @return Intelligence statistics object
     */
    public IntelligenceStatistics getIntelligenceStatistics() {
        IntelligenceStatistics stats = new IntelligenceStatistics();
        
        stats.setTotalReports(intelligenceRepository.count());
        stats.setPendingReports(intelligenceRepository.countByReviewStatus("PENDING"));
        stats.setApprovedReports(intelligenceRepository.countByReviewStatus("APPROVED"));
        stats.setCriticalThreats(intelligenceRepository.countByThreatLevel("CRITICAL"));
        stats.setHighThreats(intelligenceRepository.countByThreatLevel("HIGH"));
        stats.setClassifiedReports(
            intelligenceRepository.countByClassification("CONFIDENTIAL") +
            intelligenceRepository.countByClassification("SECRET") +
            intelligenceRepository.countByClassification("TOP_SECRET")
        );
        
        // Recent activity (last 24 hours)
        LocalDateTime oneDayAgo = LocalDateTime.now().minusHours(24);
        stats.setRecentReports(intelligenceRepository.findRecentReports(oneDayAgo).size());

        return stats;
    }

    /**
     * Generate unique report number.
     *
     * @return Generated report number
     */
    private String generateReportNumber() {
        String year = String.valueOf(LocalDateTime.now().getYear());
        String randomSuffix = String.format("%06d", ThreadLocalRandom.current().nextInt(1000000));
        return "INTEL-" + year + "-" + randomSuffix;
    }

    /**
     * Send high-priority alert for critical/immediate reports.
     *
     * @param report Intelligence report to alert about
     */
    private void sendHighPriorityAlert(IntelligenceReport report) {
        try {
            String alertMessage = String.format(
                "HIGH PRIORITY INTELLIGENCE ALERT\n" +
                "Report: %s\n" +
                "Title: %s\n" +
                "Threat Level: %s\n" +
                "Time Sensitivity: %s\n" +
                "Classification: %s",
                report.getReportNumber(),
                report.getTitle(),
                report.getThreatLevel(),
                report.getTimeSensitivity(),
                report.getClassification()
            );

            messagingService.sendEmergencyAlert(alertMessage, report.getClassification());
        } catch (Exception e) {
            // Log error but don't fail report creation
            System.err.println("Failed to send high priority alert: " + e.getMessage());
        }
    }

    /**
     * Send alert for approved high-priority reports.
     *
     * @param report Approved intelligence report
     */
    private void sendApprovedHighPriorityAlert(IntelligenceReport report) {
        try {
            String alertMessage = String.format(
                "APPROVED HIGH PRIORITY INTELLIGENCE\n" +
                "Report: %s\n" +
                "Title: %s\n" +
                "Threat Level: %s\n" +
                "Status: APPROVED\n" +
                "Classification: %s",
                report.getReportNumber(),
                report.getTitle(),
                report.getThreatLevel(),
                report.getClassification()
            );

            messagingService.sendEmergencyAlert(alertMessage, report.getClassification());
        } catch (Exception e) {
            System.err.println("Failed to send approved high priority alert: " + e.getMessage());
        }
    }

    /**
     * Check if user has analyst or commander role.
     *
     * @param user User to check
     * @return true if user has appropriate role
     */
    private boolean hasAnalystOrCommanderRole(User user) {
        // This would check user roles in a real implementation
        // For now, assume users with higher clearance have these roles
        return user.getClearanceLevel() != null && 
               !"UNCLASSIFIED".equals(user.getClearanceLevel());
    }

    /**
     * Calculate confidence score based on correlation strength.
     *
     * @param correlationStrength Correlation strength
     * @return Confidence score (0.0 to 1.0)
     */
    private double calculateConfidenceScore(String correlationStrength) {
        switch (correlationStrength != null ? correlationStrength : "WEAK") {
            case "CONFIRMED": return 0.95;
            case "STRONG": return 0.80;
            case "MODERATE": return 0.60;
            case "WEAK":
            default: return 0.30;
        }
    }

    /**
     * Inner class for intelligence statistics.
     */
    public static class IntelligenceStatistics {
        private long totalReports;
        private long pendingReports;
        private long approvedReports;
        private long criticalThreats;
        private long highThreats;
        private long classifiedReports;
        private long recentReports;

        // Getters and setters
        public long getTotalReports() { return totalReports; }
        public void setTotalReports(long totalReports) { this.totalReports = totalReports; }

        public long getPendingReports() { return pendingReports; }
        public void setPendingReports(long pendingReports) { this.pendingReports = pendingReports; }

        public long getApprovedReports() { return approvedReports; }
        public void setApprovedReports(long approvedReports) { this.approvedReports = approvedReports; }

        public long getCriticalThreats() { return criticalThreats; }
        public void setCriticalThreats(long criticalThreats) { this.criticalThreats = criticalThreats; }

        public long getHighThreats() { return highThreats; }
        public void setHighThreats(long highThreats) { this.highThreats = highThreats; }

        public long getClassifiedReports() { return classifiedReports; }
        public void setClassifiedReports(long classifiedReports) { this.classifiedReports = classifiedReports; }

        public long getRecentReports() { return recentReports; }
        public void setRecentReports(long recentReports) { this.recentReports = recentReports; }
    }
}
