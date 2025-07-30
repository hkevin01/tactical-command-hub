package com.tacticalcommand.tactical.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Mission Waypoint entity representing navigation points in mission planning.
 * 
 * This entity models waypoints that define the path and objectives for military
 * missions, supporting complex multi-point mission planning and execution.
 * 
 * @author Tactical Command Hub Team
 * @version 1.0.0
 * @since 2025-07-29
 */
@Entity
@Table(name = "mission_waypoints", indexes = {
    @Index(name = "idx_waypoint_mission", columnList = "mission_id"),
    @Index(name = "idx_waypoint_sequence", columnList = "mission_id, sequence_number")
})
public class MissionWaypoint extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_id", nullable = false)
    private Mission mission;

    @NotNull(message = "Sequence number is required")
    @Column(name = "sequence_number", nullable = false)
    private Integer sequenceNumber;

    @Size(max = 100, message = "Waypoint name must not exceed 100 characters")
    @Column(name = "waypoint_name", length = 100)
    private String waypointName;

    @NotNull(message = "Latitude is required")
    @Column(name = "latitude", nullable = false, precision = 10, scale = 8)
    private BigDecimal latitude;

    @NotNull(message = "Longitude is required")
    @Column(name = "longitude", nullable = false, precision = 11, scale = 8)
    private BigDecimal longitude;

    @Column(name = "altitude", precision = 8, scale = 2)
    private BigDecimal altitude;

    @NotNull(message = "Waypoint type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "waypoint_type", nullable = false, length = 20)
    private WaypointType waypointType;

    @Column(name = "estimated_arrival_time")
    private LocalDateTime estimatedArrivalTime;

    @Column(name = "actual_arrival_time")
    private LocalDateTime actualArrivalTime;

    @Column(name = "is_reached")
    private Boolean isReached = false;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    @Column(name = "description", length = 500)
    private String description;

    @Size(max = 300, message = "Action required must not exceed 300 characters")
    @Column(name = "action_required", length = 300)
    private String actionRequired;

    /**
     * Default constructor.
     */
    public MissionWaypoint() {
        super();
        this.isReached = false;
    }

    /**
     * Constructor with required fields.
     */
    public MissionWaypoint(Mission mission, Integer sequenceNumber, BigDecimal latitude, 
                          BigDecimal longitude, WaypointType waypointType) {
        this();
        this.mission = mission;
        this.sequenceNumber = sequenceNumber;
        this.latitude = latitude;
        this.longitude = longitude;
        this.waypointType = waypointType;
    }

    // Getters and Setters

    public Mission getMission() {
        return mission;
    }

    public void setMission(Mission mission) {
        this.mission = mission;
    }

    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public String getWaypointName() {
        return waypointName;
    }

    public void setWaypointName(String waypointName) {
        this.waypointName = waypointName;
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

    public BigDecimal getAltitude() {
        return altitude;
    }

    public void setAltitude(BigDecimal altitude) {
        this.altitude = altitude;
    }

    public WaypointType getWaypointType() {
        return waypointType;
    }

    public void setWaypointType(WaypointType waypointType) {
        this.waypointType = waypointType;
    }

    public LocalDateTime getEstimatedArrivalTime() {
        return estimatedArrivalTime;
    }

    public void setEstimatedArrivalTime(LocalDateTime estimatedArrivalTime) {
        this.estimatedArrivalTime = estimatedArrivalTime;
    }

    public LocalDateTime getActualArrivalTime() {
        return actualArrivalTime;
    }

    public void setActualArrivalTime(LocalDateTime actualArrivalTime) {
        this.actualArrivalTime = actualArrivalTime;
    }

    public Boolean getIsReached() {
        return isReached;
    }

    public void setIsReached(Boolean isReached) {
        this.isReached = isReached;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getActionRequired() {
        return actionRequired;
    }

    public void setActionRequired(String actionRequired) {
        this.actionRequired = actionRequired;
    }

    /**
     * Marks this waypoint as reached with the current timestamp.
     */
    public void markAsReached() {
        this.isReached = true;
        this.actualArrivalTime = LocalDateTime.now();
    }

    /**
     * Sets the coordinates for this waypoint.
     */
    public void setCoordinates(BigDecimal latitude, BigDecimal longitude, BigDecimal altitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
    }

    /**
     * Enum for waypoint types.
     */
    public enum WaypointType {
        START, // Mission start point
        CHECKPOINT, // Intermediate checkpoint
        OBJECTIVE, // Mission objective location
        RALLY_POINT, // Rally or regrouping point
        EXTRACTION, // Extraction point
        END // Mission end point
    }

    @Override
    public String toString() {
        return "MissionWaypoint{" +
                "id=" + getId() +
                ", missionCode=" + (mission != null ? mission.getMissionCode() : "null") +
                ", sequenceNumber=" + sequenceNumber +
                ", waypointName='" + waypointName + '\'' +
                ", waypointType=" + waypointType +
                ", isReached=" + isReached +
                '}';
    }
}
