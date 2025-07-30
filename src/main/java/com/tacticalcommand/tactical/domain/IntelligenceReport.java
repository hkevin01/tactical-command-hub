package com.tacticalcommand.tactical.domain;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;

/**
 * Entity representing intelligence reports and threat intelligence data.
 * Supports classification levels, threat correlation, and geographic mapping.
 */
@Entity
@Table(name = "intelligence_reports")
public class IntelligenceReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "report_number", unique = true, nullable = false)
    private String reportNumber;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "classification", nullable = false)
    private String classification = "UNCLASSIFIED";

    @Column(name = "threat_level")
    private String threatLevel; // LOW, MEDIUM, HIGH, CRITICAL

    @Column(name = "confidence_level")
    private String confidenceLevel; // LOW, MEDIUM, HIGH, CONFIRMED

    @Column(name = "source_reliability")
    private String sourceReliability; // A-F scale

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "summary", columnDefinition = "TEXT")
    private String summary;

    @Column(name = "geographic_area")
    private String geographicArea;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "radius_km")
    private Double radiusKm;

    @Column(name = "threat_type")
    private String threatType; // KINETIC, CYBER, NBC, CONVENTIONAL

    @Column(name = "target_type")
    private String targetType; // PERSONNEL, FACILITY, EQUIPMENT, COMMUNICATION

    @Column(name = "time_sensitivity")
    private String timeSensitivity; // IMMEDIATE, PRIORITY, ROUTINE

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "dissemination_controls")
    private String disseminationControls; // NOFORN, FOUO, etc.

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by_id")
    private User reviewedBy;

    @Column(name = "review_status")
    private String reviewStatus = "PENDING"; // PENDING, APPROVED, REJECTED

    @Column(name = "source_id")
    private String sourceId; // External source identifier

    @Column(name = "source_type")
    private String sourceType; // HUMINT, SIGINT, OSINT, IMINT, etc.

    @OneToMany(mappedBy = "intelligenceReport", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<IntelligenceTag> tags = new HashSet<>();

    @OneToMany(mappedBy = "intelligenceReport", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ThreatCorrelation> correlations = new HashSet<>();

    // Default constructor
    public IntelligenceReport() {
        this.createdAt = LocalDateTime.now();
    }

    // Constructor with essential fields
    public IntelligenceReport(String reportNumber, String title, String classification) {
        this();
        this.reportNumber = reportNumber;
        this.title = title;
        this.classification = classification;
    }

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

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public User getReviewedBy() {
        return reviewedBy;
    }

    public void setReviewedBy(User reviewedBy) {
        this.reviewedBy = reviewedBy;
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

    public Set<IntelligenceTag> getTags() {
        return tags;
    }

    public void setTags(Set<IntelligenceTag> tags) {
        this.tags = tags;
    }

    public Set<ThreatCorrelation> getCorrelations() {
        return correlations;
    }

    public void setCorrelations(Set<ThreatCorrelation> correlations) {
        this.correlations = correlations;
    }

    /**
     * Check if the intelligence report has expired.
     *
     * @return true if the report has expired
     */
    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }

    /**
     * Check if the report is classified.
     *
     * @return true if classification is not UNCLASSIFIED
     */
    public boolean isClassified() {
        return !"UNCLASSIFIED".equals(classification);
    }

    /**
     * Get the threat severity as numeric value for sorting/comparison.
     *
     * @return numeric threat level (1=LOW to 4=CRITICAL)
     */
    public int getThreatSeverity() {
        switch (threatLevel != null ? threatLevel : "LOW") {
            case "CRITICAL": return 4;
            case "HIGH": return 3;
            case "MEDIUM": return 2;
            case "LOW":
            default: return 1;
        }
    }

    /**
     * Check if the report requires immediate attention.
     *
     * @return true if time sensitivity is IMMEDIATE
     */
    public boolean isImmediate() {
        return "IMMEDIATE".equals(timeSensitivity);
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IntelligenceReport)) return false;
        IntelligenceReport that = (IntelligenceReport) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "IntelligenceReport{" +
                "id=" + id +
                ", reportNumber='" + reportNumber + '\'' +
                ", title='" + title + '\'' +
                ", classification='" + classification + '\'' +
                ", threatLevel='" + threatLevel + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
