package com.tacticalcommand.tactical.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Mission entity representing operational missions in the tactical command system.
 * 
 * This entity models military missions with objectives, assigned units, and
 * operational parameters for comprehensive mission planning and execution tracking.
 * 
 * @author Tactical Command Hub Team
 * @version 1.0.0
 * @since 2025-07-29
 */
@Entity
@Table(name = "missions", indexes = {
    @Index(name = "idx_mission_code", columnList = "mission_code"),
    @Index(name = "idx_mission_status", columnList = "status"),
    @Index(name = "idx_mission_priority", columnList = "priority"),
    @Index(name = "idx_mission_start_time", columnList = "start_time")
})
public class Mission extends BaseEntity {

    @NotBlank(message = "Mission code is required")
    @Size(max = 50, message = "Mission code must not exceed 50 characters")
    @Column(name = "mission_code", nullable = false, unique = true, length = 50)
    private String missionCode;

    @NotBlank(message = "Mission name is required")
    @Size(max = 100, message = "Mission name must not exceed 100 characters")
    @Column(name = "mission_name", nullable = false, length = 100)
    private String missionName;

    @NotNull(message = "Mission type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "mission_type", nullable = false, length = 30)
    private MissionType missionType;

    @NotNull(message = "Mission status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private MissionStatus status;

    @NotNull(message = "Priority level is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false, length = 10)
    private Priority priority;

    @Size(max = 1000, message = "Objective must not exceed 1000 characters")
    @Column(name = "objective", length = 1000)
    private String objective;

    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    @Column(name = "description", length = 2000)
    private String description;

    @NotNull
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "estimated_duration_hours")
    private Integer estimatedDurationHours;

    // Target location coordinates
    @Column(name = "target_latitude", precision = 10, scale = 8)
    private BigDecimal targetLatitude;

    @Column(name = "target_longitude", precision = 11, scale = 8)
    private BigDecimal targetLongitude;

    @Size(max = 200, message = "Target location must not exceed 200 characters")
    @Column(name = "target_location", length = 200)
    private String targetLocation;

    // Mission commander
    @Size(max = 100, message = "Commander name must not exceed 100 characters")
    @Column(name = "commander", length = 100)
    private String commander;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_unit_id")
    private MilitaryUnit assignedUnit;

    @OneToMany(mappedBy = "mission", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MissionWaypoint> waypoints = new ArrayList<>();

    @OneToMany(mappedBy = "mission", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MissionReport> reports = new ArrayList<>();

    @Column(name = "completion_percentage")
    private Integer completionPercentage;

    @Size(max = 1000, message = "Notes must not exceed 1000 characters")
    @Column(name = "notes", length = 1000)
    private String notes;

    /**
     * Default constructor.
     */
    public Mission() {
        super();
        this.status = MissionStatus.PLANNING;
        this.priority = Priority.MEDIUM;
        this.completionPercentage = 0;
    }

    /**
     * Constructor with required fields.
     */
    public Mission(String missionCode, String missionName, MissionType missionType, LocalDateTime startTime) {
        this();
        this.missionCode = missionCode;
        this.missionName = missionName;
        this.missionType = missionType;
        this.startTime = startTime;
    }

    // Getters and Setters

    public String getMissionCode() {
        return missionCode;
    }

    public void setMissionCode(String missionCode) {
        this.missionCode = missionCode;
    }

    public String getMissionName() {
        return missionName;
    }

    public void setMissionName(String missionName) {
        this.missionName = missionName;
    }

    public MissionType getMissionType() {
        return missionType;
    }

    public void setMissionType(MissionType missionType) {
        this.missionType = missionType;
    }

    public MissionStatus getStatus() {
        return status;
    }

    public void setStatus(MissionStatus status) {
        this.status = status;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public String getObjective() {
        return objective;
    }

    public void setObjective(String objective) {
        this.objective = objective;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Integer getEstimatedDurationHours() {
        return estimatedDurationHours;
    }

    public void setEstimatedDurationHours(Integer estimatedDurationHours) {
        this.estimatedDurationHours = estimatedDurationHours;
    }

    public BigDecimal getTargetLatitude() {
        return targetLatitude;
    }

    public void setTargetLatitude(BigDecimal targetLatitude) {
        this.targetLatitude = targetLatitude;
    }

    public BigDecimal getTargetLongitude() {
        return targetLongitude;
    }

    public void setTargetLongitude(BigDecimal targetLongitude) {
        this.targetLongitude = targetLongitude;
    }

    public String getTargetLocation() {
        return targetLocation;
    }

    public void setTargetLocation(String targetLocation) {
        this.targetLocation = targetLocation;
    }

    public String getCommander() {
        return commander;
    }

    public void setCommander(String commander) {
        this.commander = commander;
    }

    public MilitaryUnit getAssignedUnit() {
        return assignedUnit;
    }

    public void setAssignedUnit(MilitaryUnit assignedUnit) {
        this.assignedUnit = assignedUnit;
    }

    public List<MissionWaypoint> getWaypoints() {
        return waypoints;
    }

    public void setWaypoints(List<MissionWaypoint> waypoints) {
        this.waypoints = waypoints;
    }

    public List<MissionReport> getReports() {
        return reports;
    }

    public void setReports(List<MissionReport> reports) {
        this.reports = reports;
    }

    public Integer getCompletionPercentage() {
        return completionPercentage;
    }

    public void setCompletionPercentage(Integer completionPercentage) {
        this.completionPercentage = completionPercentage;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * Sets the target coordinates for the mission.
     */
    public void setTargetCoordinates(BigDecimal latitude, BigDecimal longitude) {
        this.targetLatitude = latitude;
        this.targetLongitude = longitude;
    }

    /**
     * Adds a waypoint to the mission.
     */
    public void addWaypoint(MissionWaypoint waypoint) {
        waypoints.add(waypoint);
        waypoint.setMission(this);
    }

    /**
     * Adds a report to the mission.
     */
    public void addReport(MissionReport report) {
        reports.add(report);
        report.setMission(this);
    }

    /**
     * Enum for mission types.
     */
    public enum MissionType {
        RECONNAISSANCE, PATROL, ASSAULT, DEFENSE, LOGISTICS, TRAINING, 
        SEARCH_AND_RESCUE, PEACEKEEPING, CYBER_OPERATION, INTELLIGENCE
    }

    /**
     * Enum for mission status.
     */
    public enum MissionStatus {
        PLANNING, APPROVED, ACTIVE, SUSPENDED, COMPLETED, CANCELLED, ABORTED
    }

    /**
     * Enum for priority levels.
     */
    public enum Priority {
        LOW, MEDIUM, HIGH, CRITICAL
    }

    @Override
    public String toString() {
        return "Mission{" +
                "id=" + getId() +
                ", missionCode='" + missionCode + '\'' +
                ", missionName='" + missionName + '\'' +
                ", missionType=" + missionType +
                ", status=" + status +
                ", priority=" + priority +
                ", startTime=" + startTime +
                ", completionPercentage=" + completionPercentage +
                '}';
    }
}
