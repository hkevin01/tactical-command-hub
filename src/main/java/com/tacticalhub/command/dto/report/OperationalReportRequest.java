package com.tacticalhub.command.dto.report;

import java.time.LocalDateTime;
import java.util.Set;

import com.tacticalhub.command.domain.report.OperationalReport.ReportCategory;
import com.tacticalhub.command.domain.report.OperationalReport.ReportType;
import com.tacticalhub.command.domain.report.OperationalReport.SecurityClassification;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO for creating operational reports
 */
public class OperationalReportRequest {
    
    @NotBlank(message = "Title is required")
    private String title;
    
    @NotNull(message = "Report type is required")
    private ReportType reportType;
    
    @NotNull(message = "Period start is required")
    private LocalDateTime periodStart;
    
    @NotNull(message = "Period end is required")
    private LocalDateTime periodEnd;
    
    private String executiveSummary;
    private String keyFindings;
    private String recommendations;
    private String metrics;
    private String appendices;
    
    private SecurityClassification classification = SecurityClassification.UNCLASSIFIED;
    private Set<ReportCategory> categories;
    
    // Constructors
    public OperationalReportRequest() {}
    
    public OperationalReportRequest(String title, ReportType reportType, 
                                  LocalDateTime periodStart, LocalDateTime periodEnd) {
        this.title = title;
        this.reportType = reportType;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
    }
    
    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public ReportType getReportType() { return reportType; }
    public void setReportType(ReportType reportType) { this.reportType = reportType; }
    
    public LocalDateTime getPeriodStart() { return periodStart; }
    public void setPeriodStart(LocalDateTime periodStart) { this.periodStart = periodStart; }
    
    public LocalDateTime getPeriodEnd() { return periodEnd; }
    public void setPeriodEnd(LocalDateTime periodEnd) { this.periodEnd = periodEnd; }
    
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
}
