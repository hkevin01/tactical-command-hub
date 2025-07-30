package com.tacticalcommand.tactical.dto;

import java.time.LocalDateTime;

/**
 * Response DTO for intelligence reports.
 */
public class IntelligenceReportResponse {

    private Long id;
    private String reportNumber;
    private String title;
    private String classification;
    private String threatLevel;
    private String confidenceLevel;
    private String sourceReliability;
    private String content;
    private String summary;
    private String geographicArea;
    private Double latitude;
    private Double longitude;
    private Double radiusKm;
    private String threatType;
    private String targetType;
    private String timeSensitivity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime expiresAt;
    private String disseminationControls;
    private String authorName;
    private String reviewedByName;
    private String reviewStatus;
    private String sourceId;
    private String sourceType;
    private boolean expired;
    private boolean classified;
    private int threatSeverity;

    // Default constructor
    public IntelligenceReportResponse() {}

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReportNumber() {
        return reportNumber;
    }

    public void setReportNumber(String reportNumber) {
        this.reportNumber = reportNumber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    public String getThreatLevel() {
        return threatLevel;
    }

    public void setThreatLevel(String threatLevel) {
        this.threatLevel = threatLevel;
    }

    public String getConfidenceLevel() {
        return confidenceLevel;
    }

    public void setConfidenceLevel(String confidenceLevel) {
        this.confidenceLevel = confidenceLevel;
    }

    public String getSourceReliability() {
        return sourceReliability;
    }

    public void setSourceReliability(String sourceReliability) {
        this.sourceReliability = sourceReliability;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getGeographicArea() {
        return geographicArea;
    }

    public void setGeographicArea(String geographicArea) {
        this.geographicArea = geographicArea;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getRadiusKm() {
        return radiusKm;
    }

    public void setRadiusKm(Double radiusKm) {
        this.radiusKm = radiusKm;
    }

    public String getThreatType() {
        return threatType;
    }

    public void setThreatType(String threatType) {
        this.threatType = threatType;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public String getTimeSensitivity() {
        return timeSensitivity;
    }

    public void setTimeSensitivity(String timeSensitivity) {
        this.timeSensitivity = timeSensitivity;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public String getDisseminationControls() {
        return disseminationControls;
    }

    public void setDisseminationControls(String disseminationControls) {
        this.disseminationControls = disseminationControls;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getReviewedByName() {
        return reviewedByName;
    }

    public void setReviewedByName(String reviewedByName) {
        this.reviewedByName = reviewedByName;
    }

    public String getReviewStatus() {
        return reviewStatus;
    }

    public void setReviewStatus(String reviewStatus) {
        this.reviewStatus = reviewStatus;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public boolean isExpired() {
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    public boolean isClassified() {
        return classified;
    }

    public void setClassified(boolean classified) {
        this.classified = classified;
    }

    public int getThreatSeverity() {
        return threatSeverity;
    }

    public void setThreatSeverity(int threatSeverity) {
        this.threatSeverity = threatSeverity;
    }

    /**
     * Check if the report requires immediate attention.
     *
     * @return true if time sensitivity is IMMEDIATE
     */
    public boolean isImmediate() {
        return "IMMEDIATE".equals(timeSensitivity);
    }

    /**
     * Check if the report is high priority (CRITICAL or HIGH threat).
     *
     * @return true if threat level is CRITICAL or HIGH
     */
    public boolean isHighPriority() {
        return "CRITICAL".equals(threatLevel) || "HIGH".equals(threatLevel);
    }

    @Override
    public String toString() {
        return "IntelligenceReportResponse{" +
                "id=" + id +
                ", reportNumber='" + reportNumber + '\'' +
                ", title='" + title + '\'' +
                ", classification='" + classification + '\'' +
                ", threatLevel='" + threatLevel + '\'' +
                ", timeSensitivity='" + timeSensitivity + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
