package com.tacticalhub.command.dto.report;

import java.time.LocalDateTime;
import java.util.Set;

import com.tacticalhub.command.domain.report.OperationalReport.ReportCategory;
import com.tacticalhub.command.domain.report.OperationalReport.ReportStatus;
import com.tacticalhub.command.domain.report.OperationalReport.ReportType;
import com.tacticalhub.command.domain.report.OperationalReport.SecurityClassification;

/**
 * DTO for operational report responses
 */
public class OperationalReportResponse {
    
    private Long id;
    private String reportNumber;
    private String title;
    private ReportType reportType;
    private ReportStatus status;
    private LocalDateTime periodStart;
    private LocalDateTime periodEnd;
    private Long authorId;
    private String authorName;
    private String executiveSummary;
    private String keyFindings;
    private String recommendations;
    private String metrics;
    private String appendices;
    private SecurityClassification classification;
    private Set<ReportCategory> categories;
    
    private Long reviewedBy;
    private String reviewedByName;
    private LocalDateTime reviewedAt;
    
    private Long approvedBy;
    private String approvedByName;
    private LocalDateTime approvedAt;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors
    public OperationalReportResponse() {}
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getReportNumber() { return reportNumber; }
    public void setReportNumber(String reportNumber) { this.reportNumber = reportNumber; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public ReportType getReportType() { return reportType; }
    public void setReportType(ReportType reportType) { this.reportType = reportType; }
    
    public ReportStatus getStatus() { return status; }
    public void setStatus(ReportStatus status) { this.status = status; }
    
    public LocalDateTime getPeriodStart() { return periodStart; }
    public void setPeriodStart(LocalDateTime periodStart) { this.periodStart = periodStart; }
    
    public LocalDateTime getPeriodEnd() { return periodEnd; }
    public void setPeriodEnd(LocalDateTime periodEnd) { this.periodEnd = periodEnd; }
    
    public Long getAuthorId() { return authorId; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }
    
    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }
    
    public String getExecutiveSummary() { return executiveSummary; }
    public void setExecutiveSummary(String executiveSummary) { this.executiveSummary = executiveSummary; }
    
    public String getKeyFindings() { return keyFindings; }
    public void setKeyFindings(String keyFindings) { this.keyFindings = keyFindings; }
    
    public String getRecommendations() { return recommendations; }
    public void setRecommendations(String recommendations) { this.recommendations = recommendations; }
    
    public String getMetrics() { return metrics; }
    public void setMetrics(String metrics) { this.metrics = metrics; }
    
    public String getAppendices() { return appendices; }
    public void setAppendices(String appendices) { this.appendices = appendices; }
    
    public SecurityClassification getClassification() { return classification; }
    public void setClassification(SecurityClassification classification) { this.classification = classification; }
    
    public Set<ReportCategory> getCategories() { return categories; }
    public void setCategories(Set<ReportCategory> categories) { this.categories = categories; }
    
    public Long getReviewedBy() { return reviewedBy; }
    public void setReviewedBy(Long reviewedBy) { this.reviewedBy = reviewedBy; }
    
    public String getReviewedByName() { return reviewedByName; }
    public void setReviewedByName(String reviewedByName) { this.reviewedByName = reviewedByName; }
    
    public LocalDateTime getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(LocalDateTime reviewedAt) { this.reviewedAt = reviewedAt; }
    
    public Long getApprovedBy() { return approvedBy; }
    public void setApprovedBy(Long approvedBy) { this.approvedBy = approvedBy; }
    
    public String getApprovedByName() { return approvedByName; }
    public void setApprovedByName(String approvedByName) { this.approvedByName = approvedByName; }
    
    public LocalDateTime getApprovedAt() { return approvedAt; }
    public void setApprovedAt(LocalDateTime approvedAt) { this.approvedAt = approvedAt; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
