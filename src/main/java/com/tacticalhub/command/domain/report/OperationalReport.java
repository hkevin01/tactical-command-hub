package com.tacticalhub.command.domain.report;

import java.time.LocalDateTime;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Entity representing operational reports for command analysis
 */
@Entity
@Table(name = "operational_reports", indexes = {
    @Index(name = "idx_operational_report_period", columnList = "periodStart, periodEnd"),
    @Index(name = "idx_operational_report_type", columnList = "reportType"),
    @Index(name = "idx_operational_report_status", columnList = "status"),
    @Index(name = "idx_operational_report_author", columnList = "authorId")
})
public class OperationalReport {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Column(nullable = false, unique = true)
    private String reportNumber;
    
    @NotBlank
    @Column(nullable = false)
    private String title;
    
    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false)
    private ReportType reportType;
    
    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false)
    private ReportStatus status = ReportStatus.DRAFT;
    
    @NotNull
    @Column(nullable = false)
    private LocalDateTime periodStart;
    
    @NotNull
    @Column(nullable = false)
    private LocalDateTime periodEnd;
    
    @NotNull
    @Column(nullable = false)
    private Long authorId;
    
    @Lob
    @Column(columnDefinition = "TEXT")
    private String executiveSummary;
    
    @Lob
    @Column(columnDefinition = "TEXT")
    private String keyFindings;
    
    @Lob
    @Column(columnDefinition = "TEXT")
    private String recommendations;
    
    @Lob
    @Column(columnDefinition = "TEXT")
    private String metrics;
    
    @Lob
    @Column(columnDefinition = "TEXT")
    private String appendices;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SecurityClassification classification = SecurityClassification.UNCLASSIFIED;
    
    @ElementCollection
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "report_categories")
    private Set<ReportCategory> categories;
    
    @Column
    private Long reviewedBy;
    
    @Column
    private LocalDateTime reviewedAt;
    
    @Column
    private Long approvedBy;
    
    @Column
    private LocalDateTime approvedAt;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    // Constructors
    public OperationalReport() {}
    
    public OperationalReport(String reportNumber, String title, ReportType reportType,
                           LocalDateTime periodStart, LocalDateTime periodEnd, Long authorId) {
        this.reportNumber = reportNumber;
        this.title = title;
        this.reportType = reportType;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
        this.authorId = authorId;
    }
    
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
    
    public LocalDateTime getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(LocalDateTime reviewedAt) { this.reviewedAt = reviewedAt; }
    
    public Long getApprovedBy() { return approvedBy; }
    public void setApprovedBy(Long approvedBy) { this.approvedBy = approvedBy; }
    
    public LocalDateTime getApprovedAt() { return approvedAt; }
    public void setApprovedAt(LocalDateTime approvedAt) { this.approvedAt = approvedAt; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public enum ReportType {
        DAILY_OPERATIONAL,
        WEEKLY_SUMMARY,
        MONTHLY_ANALYSIS,
        QUARTERLY_REVIEW,
        ANNUAL_ASSESSMENT,
        INCIDENT_REPORT,
        AFTER_ACTION_REVIEW,
        INTELLIGENCE_SUMMARY,
        MISSION_DEBRIEF,
        SPECIAL_REPORT
    }
    
    public enum ReportStatus {
        DRAFT,
        UNDER_REVIEW,
        REVIEWED,
        APPROVED,
        PUBLISHED,
        ARCHIVED
    }
    
    public enum SecurityClassification {
        UNCLASSIFIED,
        CONFIDENTIAL,
        SECRET,
        TOP_SECRET
    }
    
    public enum ReportCategory {
        OPERATIONS,
        INTELLIGENCE,
        LOGISTICS,
        PERSONNEL,
        COMMUNICATIONS,
        SECURITY,
        TRAINING,
        EQUIPMENT,
        WEATHER,
        MEDICAL
    }
}
