package com.tacticalhub.command.service.report;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tacticalhub.command.domain.report.OperationalReport;
import com.tacticalhub.command.domain.report.OperationalReport.ReportStatus;
import com.tacticalhub.command.domain.report.OperationalReport.ReportType;
import com.tacticalhub.command.repository.report.OperationalReportRepository;

/**
 * Service for managing operational reports
 */
@Service
@Transactional
public class ReportService {
    
    private final OperationalReportRepository reportRepository;
    
    @Autowired
    public ReportService(OperationalReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }
    
    /**
     * Create a new operational report
     */
    public OperationalReport createReport(OperationalReport report) {
        // Generate unique report number
        String reportNumber = generateReportNumber(report.getReportType());
        report.setReportNumber(reportNumber);
        
        // Set initial status
        report.setStatus(ReportStatus.DRAFT);
        
        return reportRepository.save(report);
    }
    
    /**
     * Update an existing report
     */
    public OperationalReport updateReport(Long reportId, OperationalReport updatedReport) {
        OperationalReport existingReport = reportRepository.findById(reportId)
            .orElseThrow(() -> new RuntimeException("Report not found: " + reportId));
        
        // Update fields (preserving system fields)
        existingReport.setTitle(updatedReport.getTitle());
        existingReport.setExecutiveSummary(updatedReport.getExecutiveSummary());
        existingReport.setKeyFindings(updatedReport.getKeyFindings());
        existingReport.setRecommendations(updatedReport.getRecommendations());
        existingReport.setMetrics(updatedReport.getMetrics());
        existingReport.setAppendices(updatedReport.getAppendices());
        existingReport.setCategories(updatedReport.getCategories());
        
        if (updatedReport.getPeriodStart() != null) {
            existingReport.setPeriodStart(updatedReport.getPeriodStart());
        }
        if (updatedReport.getPeriodEnd() != null) {
            existingReport.setPeriodEnd(updatedReport.getPeriodEnd());
        }
        
        return reportRepository.save(existingReport);
    }
    
    /**
     * Submit report for review
     */
    public OperationalReport submitForReview(Long reportId) {
        OperationalReport report = reportRepository.findById(reportId)
            .orElseThrow(() -> new RuntimeException("Report not found: " + reportId));
        
        if (report.getStatus() != ReportStatus.DRAFT) {
            throw new RuntimeException("Only draft reports can be submitted for review");
        }
        
        report.setStatus(ReportStatus.UNDER_REVIEW);
        return reportRepository.save(report);
    }
    
    /**
     * Review a report
     */
    public OperationalReport reviewReport(Long reportId, Long reviewerId) {
        OperationalReport report = reportRepository.findById(reportId)
            .orElseThrow(() -> new RuntimeException("Report not found: " + reportId));
        
        if (report.getStatus() != ReportStatus.UNDER_REVIEW) {
            throw new RuntimeException("Report is not under review");
        }
        
        report.setStatus(ReportStatus.REVIEWED);
        report.setReviewedBy(reviewerId);
        report.setReviewedAt(LocalDateTime.now());
        
        return reportRepository.save(report);
    }
    
    /**
     * Approve a report
     */
    public OperationalReport approveReport(Long reportId, Long approverId) {
        OperationalReport report = reportRepository.findById(reportId)
            .orElseThrow(() -> new RuntimeException("Report not found: " + reportId));
        
        if (report.getStatus() != ReportStatus.REVIEWED) {
            throw new RuntimeException("Report must be reviewed before approval");
        }
        
        report.setStatus(ReportStatus.APPROVED);
        report.setApprovedBy(approverId);
        report.setApprovedAt(LocalDateTime.now());
        
        return reportRepository.save(report);
    }
    
    /**
     * Publish a report
     */
    public OperationalReport publishReport(Long reportId) {
        OperationalReport report = reportRepository.findById(reportId)
            .orElseThrow(() -> new RuntimeException("Report not found: " + reportId));
        
        if (report.getStatus() != ReportStatus.APPROVED) {
            throw new RuntimeException("Report must be approved before publishing");
        }
        
        report.setStatus(ReportStatus.PUBLISHED);
        return reportRepository.save(report);
    }
    
    /**
     * Archive a report
     */
    public OperationalReport archiveReport(Long reportId) {
        OperationalReport report = reportRepository.findById(reportId)
            .orElseThrow(() -> new RuntimeException("Report not found: " + reportId));
        
        report.setStatus(ReportStatus.ARCHIVED);
        return reportRepository.save(report);
    }
    
    /**
     * Get report by ID
     */
    @Transactional(readOnly = true)
    public Optional<OperationalReport> getReportById(Long reportId) {
        return reportRepository.findById(reportId);
    }
    
    /**
     * Get report by report number
     */
    @Transactional(readOnly = true)
    public Optional<OperationalReport> getReportByNumber(String reportNumber) {
        return reportRepository.findByReportNumber(reportNumber);
    }
    
    /**
     * Get reports by author
     */
    @Transactional(readOnly = true)
    public List<OperationalReport> getReportsByAuthor(Long authorId) {
        return reportRepository.findByAuthorId(authorId);
    }
    
    /**
     * Get reports by type
     */
    @Transactional(readOnly = true)
    public List<OperationalReport> getReportsByType(ReportType reportType) {
        return reportRepository.findByReportType(reportType);
    }
    
    /**
     * Get reports by status
     */
    @Transactional(readOnly = true)
    public List<OperationalReport> getReportsByStatus(ReportStatus status) {
        return reportRepository.findByStatus(status);
    }
    
    /**
     * Get reports by date range
     */
    @Transactional(readOnly = true)
    public List<OperationalReport> getReportsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return reportRepository.findByPeriodRange(startDate, endDate);
    }
    
    /**
     * Search reports
     */
    @Transactional(readOnly = true)
    public List<OperationalReport> searchReports(String searchTerm) {
        return reportRepository.searchReports(searchTerm);
    }
    
    /**
     * Get reports pending review
     */
    @Transactional(readOnly = true)
    public List<OperationalReport> getReportsPendingReview() {
        return reportRepository.findPendingReview();
    }
    
    /**
     * Get reports pending approval
     */
    @Transactional(readOnly = true)
    public List<OperationalReport> getReportsPendingApproval() {
        return reportRepository.findPendingApproval();
    }
    
    /**
     * Get report statistics
     */
    @Transactional(readOnly = true)
    public ReportStatistics getReportStatistics() {
        List<Object[]> typeStats = reportRepository.getReportTypeStatistics();
        List<Object[]> statusStats = reportRepository.getReportStatusStatistics();
        
        return new ReportStatistics(typeStats, statusStats);
    }
    
    /**
     * Generate unique report number
     */
    private String generateReportNumber(ReportType reportType) {
        String prefix = getReportPrefix(reportType);
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        
        // Get count of reports created today for this type
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        long dailyCount = reportRepository.countReportsInDateRange(startOfDay, endOfDay);
        
        return String.format("%s-%s-%03d", prefix, timestamp, dailyCount + 1);
    }
    
    /**
     * Get report prefix based on type
     */
    private String getReportPrefix(ReportType reportType) {
        switch (reportType) {
            case DAILY_OPERATIONAL: return "OPREP-D";
            case WEEKLY_SUMMARY: return "OPREP-W";
            case MONTHLY_ANALYSIS: return "OPREP-M";
            case QUARTERLY_REVIEW: return "OPREP-Q";
            case ANNUAL_ASSESSMENT: return "OPREP-A";
            case INCIDENT_REPORT: return "INCREP";
            case AFTER_ACTION_REVIEW: return "AAR";
            case INTELLIGENCE_SUMMARY: return "INTSUM";
            case MISSION_DEBRIEF: return "MISREP";
            case SPECIAL_REPORT: return "SPECREP";
            default: return "REPORT";
        }
    }
    
    /**
     * Report statistics data class
     */
    public static class ReportStatistics {
        private final List<Object[]> typeStatistics;
        private final List<Object[]> statusStatistics;
        
        public ReportStatistics(List<Object[]> typeStatistics, List<Object[]> statusStatistics) {
            this.typeStatistics = typeStatistics;
            this.statusStatistics = statusStatistics;
        }
        
        public List<Object[]> getTypeStatistics() { return typeStatistics; }
        public List<Object[]> getStatusStatistics() { return statusStatistics; }
    }
}
