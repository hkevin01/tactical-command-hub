package com.tacticalcommand.tactical.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Tactical Event entity representing significant events in military operations.
 * 
 * This entity tracks events such as incidents, alerts, status changes,
 * and other operationally significant occurrences that require attention
 * or documentation.
 * 
 * @author Tactical Command Hub Team
 * @version 1.0.0
 * @since 2025-01-27
 */
@Entity
@Table(name = "tactical_events", indexes = {
    @Index(name = "idx_event_time", columnList = "event_time"),
    @Index(name = "idx_event_type", columnList = "event_type"),
    @Index(name = "idx_event_severity", columnList = "severity"),
    @Index(name = "idx_event_status", columnList = "status"),
    @Index(name = "idx_event_mission", columnList = "mission_id"),
    @Index(name = "idx_event_unit", columnList = "reporting_unit_id")
})
public class TacticalEvent extends BaseEntity {

    @NotNull(message = "Event ID is required")
    @Size(max = 50, message = "Event ID must not exceed 50 characters")
    @Column(name = "event_id", nullable = false, unique = true, length = 50)
    private String eventId;

    @NotNull(message = "Event type is required")
    @Size(max = 50, message = "Event type must not exceed 50 characters")
    @Column(name = "event_type", nullable = false, length = 50)
    private String eventType;

    @NotNull(message = "Event time is required")
    @Column(name = "event_time", nullable = false)
    private LocalDateTime eventTime;

    @NotNull(message = "Description is required")
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    @Column(name = "description", nullable = false, length = 1000)
    private String description;

    @NotNull(message = "Severity is required")
    @Size(max = 20, message = "Severity must not exceed 20 characters")
    @Column(name = "severity", nullable = false, length = 20)
    private String severity; // LOW, MEDIUM, HIGH, CRITICAL

    @Size(max = 20, message = "Status must not exceed 20 characters")
    @Column(name = "status", length = 20)
    private String status = "OPEN"; // OPEN, ACKNOWLEDGED, IN_PROGRESS, RESOLVED

    // Geographic location where event occurred
    @Column(name = "latitude", precision = 10, scale = 8)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 11, scale = 8)
    private BigDecimal longitude;

    // Related mission (if applicable)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_id")
    private Mission mission;

    // Unit that reported the event
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporting_unit_id")
    private MilitaryUnit reportingUnit;

    // Event resolution details
    @Size(max = 1000, message = "Resolution must not exceed 1000 characters")
    @Column(name = "resolution", length = 1000)
    private String resolution;

    @Column(name = "resolved_time")
    private LocalDateTime resolvedTime;

    // Event acknowledgment details
    @Size(max = 100, message = "Acknowledged by must not exceed 100 characters")
    @Column(name = "acknowledged_by", length = 100)
    private String acknowledgedBy;

    @Column(name = "acknowledged_time")
    private LocalDateTime acknowledgedTime;

    // Additional metadata
    @Size(max = 500, message = "Notes must not exceed 500 characters")
    @Column(name = "notes", length = 500)
    private String notes;

    /**
     * Default constructor.
     */
    public TacticalEvent() {
        super();
        this.eventTime = LocalDateTime.now();
        this.status = "OPEN";
        this.severity = "MEDIUM";
    }

    /**
     * Constructor with required fields.
     */
    public TacticalEvent(String eventId, String eventType, String description, String severity) {
        this();
        this.eventId = eventId;
        this.eventType = eventType;
        this.description = description;
        this.severity = severity;
    }

    // Getters and Setters

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public LocalDateTime getEventTime() {
        return eventTime;
    }

    public void setEventTime(LocalDateTime eventTime) {
        this.eventTime = eventTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public Mission getMission() {
        return mission;
    }

    public void setMission(Mission mission) {
        this.mission = mission;
    }

    public MilitaryUnit getReportingUnit() {
        return reportingUnit;
    }

    public void setReportingUnit(MilitaryUnit reportingUnit) {
        this.reportingUnit = reportingUnit;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public LocalDateTime getResolvedTime() {
        return resolvedTime;
    }

    public void setResolvedTime(LocalDateTime resolvedTime) {
        this.resolvedTime = resolvedTime;
    }

    public String getAcknowledgedBy() {
        return acknowledgedBy;
    }

    public void setAcknowledgedBy(String acknowledgedBy) {
        this.acknowledgedBy = acknowledgedBy;
    }

    public LocalDateTime getAcknowledgedTime() {
        return acknowledgedTime;
    }

    public void setAcknowledgedTime(LocalDateTime acknowledgedTime) {
        this.acknowledgedTime = acknowledgedTime;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * Sets the geographic coordinates for the event.
     */
    public void setCoordinates(BigDecimal latitude, BigDecimal longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * Checks if the event is resolved.
     */
    public boolean isResolved() {
        return "RESOLVED".equals(this.status);
    }

    /**
     * Checks if the event is critical.
     */
    public boolean isCritical() {
        return "CRITICAL".equals(this.severity) || "HIGH".equals(this.severity);
    }

    @Override
    public String toString() {
        return "TacticalEvent{" +
                "id=" + getId() +
                ", eventId='" + eventId + '\'' +
                ", eventType='" + eventType + '\'' +
                ", eventTime=" + eventTime +
                ", severity='" + severity + '\'' +
                ", status='" + status + '\'' +
                ", description='" + (description != null ? description.substring(0, Math.min(50, description.length())) + "..." : "null") + '\'' +
                '}';
    }
}
