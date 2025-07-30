package com.tacticalcommand.tactical.domain;

import java.time.LocalDateTime;

import jakarta.persistence.*;

/**
 * Entity representing threat correlations between intelligence reports.
 * Enables linking related threats and building threat intelligence networks.
 */
@Entity
@Table(name = "threat_correlations")
public class ThreatCorrelation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "intelligence_report_id")
    private IntelligenceReport intelligenceReport;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "correlated_report_id")
    private IntelligenceReport correlatedReport;

    @Column(name = "correlation_type", nullable = false)
    private String correlationType; // LOCATION, TIME, THREAT_ACTOR, METHOD, TARGET

    @Column(name = "correlation_strength")
    private String correlationStrength; // WEAK, MODERATE, STRONG, CONFIRMED

    @Column(name = "confidence_score")
    private Double confidenceScore; // 0.0 to 1.0

    @Column(name = "description")
    private String description;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id")
    private User createdBy;

    @Column(name = "validation_status")
    private String validationStatus = "PENDING"; // PENDING, VALIDATED, REJECTED

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "validated_by_id")
    private User validatedBy;

    @Column(name = "validated_at")
    private LocalDateTime validatedAt;

    // Default constructor
    public ThreatCorrelation() {
        this.createdAt = LocalDateTime.now();
    }

    // Constructor
    public ThreatCorrelation(IntelligenceReport intelligenceReport, 
                           IntelligenceReport correlatedReport,
                           String correlationType) {
        this();
        this.intelligenceReport = intelligenceReport;
        this.correlatedReport = correlatedReport;
        this.correlationType = correlationType;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public IntelligenceReport getIntelligenceReport() {
        return intelligenceReport;
    }

    public void setIntelligenceReport(IntelligenceReport intelligenceReport) {
        this.intelligenceReport = intelligenceReport;
    }

    public IntelligenceReport getCorrelatedReport() {
        return correlatedReport;
    }

    public void setCorrelatedReport(IntelligenceReport correlatedReport) {
        this.correlatedReport = correlatedReport;
    }

    public String getCorrelationType() {
        return correlationType;
    }

    public void setCorrelationType(String correlationType) {
        this.correlationType = correlationType;
    }

    public String getCorrelationStrength() {
        return correlationStrength;
    }

    public void setCorrelationStrength(String correlationStrength) {
        this.correlationStrength = correlationStrength;
    }

    public Double getConfidenceScore() {
        return confidenceScore;
    }

    public void setConfidenceScore(Double confidenceScore) {
        this.confidenceScore = confidenceScore;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public String getValidationStatus() {
        return validationStatus;
    }

    public void setValidationStatus(String validationStatus) {
        this.validationStatus = validationStatus;
    }

    public User getValidatedBy() {
        return validatedBy;
    }

    public void setValidatedBy(User validatedBy) {
        this.validatedBy = validatedBy;
    }

    public LocalDateTime getValidatedAt() {
        return validatedAt;
    }

    public void setValidatedAt(LocalDateTime validatedAt) {
        this.validatedAt = validatedAt;
    }

    /**
     * Get correlation strength as numeric value for scoring.
     *
     * @return numeric strength (1=WEAK to 4=CONFIRMED)
     */
    public int getStrengthScore() {
        switch (correlationStrength != null ? correlationStrength : "WEAK") {
            case "CONFIRMED": return 4;
            case "STRONG": return 3;
            case "MODERATE": return 2;
            case "WEAK":
            default: return 1;
        }
    }

    /**
     * Check if correlation is validated.
     *
     * @return true if validation status is VALIDATED
     */
    public boolean isValidated() {
        return "VALIDATED".equals(validationStatus);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ThreatCorrelation)) return false;
        ThreatCorrelation that = (ThreatCorrelation) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "ThreatCorrelation{" +
                "id=" + id +
                ", correlationType='" + correlationType + '\'' +
                ", correlationStrength='" + correlationStrength + '\'' +
                ", confidenceScore=" + confidenceScore +
                ", createdAt=" + createdAt +
                '}';
    }
}
