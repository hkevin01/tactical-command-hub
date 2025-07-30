package com.tacticalcommand.tactical.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Request DTO for creating/updating intelligence reports.
 */
public class IntelligenceReportRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotNull(message = "Classification is required")
    private String classification = "UNCLASSIFIED";

    private String threatLevel = "LOW";
    private String confidenceLevel = "MEDIUM";
    private String sourceReliability = "C";

    @NotBlank(message = "Content is required")
    private String content;

    private String summary;
    private String geographicArea;
    private Double latitude;
    private Double longitude;
    private Double radiusKm;
    private String threatType;
    private String targetType;
    private String timeSensitivity = "ROUTINE";
    private LocalDateTime expiresAt;
    private String disseminationControls;
    private String sourceId;
    private String sourceType;

    // Default constructor
    public IntelligenceReportRequest() {}

    // Getters and Setters
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

    @Override
    public String toString() {
        return "IntelligenceReportRequest{" +
                "title='" + title + '\'' +
                ", classification='" + classification + '\'' +
                ", threatLevel='" + threatLevel + '\'' +
                ", timeSensitivity='" + timeSensitivity + '\'' +
                '}';
    }
}
