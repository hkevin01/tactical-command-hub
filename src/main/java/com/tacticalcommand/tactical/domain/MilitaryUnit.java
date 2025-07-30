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
 * Military Unit entity representing tactical units in the command and control system.
 * 
 * This entity models military units with their positioning, status, and operational
 * characteristics. Supports multi-domain operations across land, air, sea, and cyber domains.
 * 
 * @author Tactical Command Hub Team
 * @version 1.0.0
 * @since 2025-07-29
 */
@Entity
@Table(name = "military_units",
    indexes = {@Index(name = "idx_unit_callsign", columnList = "callsign"),
        @Index(name = "idx_unit_status", columnList = "status"),
        @Index(name = "idx_unit_domain", columnList = "domain"),
        @Index(name = "idx_unit_position", columnList = "latitude, longitude")})
public class MilitaryUnit extends BaseEntity {

  @NotBlank(message = "Unit callsign is required")
  @Size(max = 50, message = "Callsign must not exceed 50 characters")
  @Column(name = "callsign", nullable = false, unique = true, length = 50)
  private String callsign;

  @NotBlank(message = "Unit name is required")
  @Size(max = 100, message = "Unit name must not exceed 100 characters")
  @Column(name = "unit_name", nullable = false, length = 100)
  private String unitName;

  @NotNull(message = "Unit type is required")
  @Enumerated(EnumType.STRING)
  @Column(name = "unit_type", nullable = false, length = 20)
  private UnitType unitType;

  @NotNull(message = "Operational domain is required")
  @Enumerated(EnumType.STRING)
  @Column(name = "domain", nullable = false, length = 10)
  private OperationalDomain domain;

  @NotNull(message = "Unit status is required")
  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 20)
  private UnitStatus status;

  @NotNull(message = "Readiness level is required")
  @Enumerated(EnumType.STRING)
  @Column(name = "readiness_level", nullable = false, length = 20)
  private ReadinessLevel readinessLevel;

  @Column(name = "latitude", precision = 10, scale = 8)
  private BigDecimal latitude;

  @Column(name = "longitude", precision = 11, scale = 8)
  private BigDecimal longitude;

  @Column(name = "altitude", precision = 8, scale = 2)
  private BigDecimal altitude;

  @Column(name = "heading", precision = 5, scale = 2)
  private BigDecimal heading;

  @Column(name = "speed", precision = 7, scale = 2)
  private BigDecimal speed;

  @Column(name = "personnel_count")
  private Integer personnelCount;

  @Size(max = 500, message = "Equipment description must not exceed 500 characters")
  @Column(name = "equipment", length = 500)
  private String equipment;

  @Column(name = "last_contact")
  private LocalDateTime lastContact;

  @Column(name = "communication_status")
  @Enumerated(EnumType.STRING)
  private CommunicationStatus communicationStatus;

  @Column(name = "threat_level")
  @Enumerated(EnumType.STRING)
  private ThreatLevel threatLevel;

  @Size(max = 1000, message = "Notes must not exceed 1000 characters")
  @Column(name = "notes", length = 1000)
  private String notes;

  @OneToMany(mappedBy = "unit", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<UnitStatusHistory> statusHistory = new ArrayList<>();

  @OneToMany(mappedBy = "assignedUnit", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<Mission> missions = new ArrayList<>();

  /**
   * Default constructor.
   */
  public MilitaryUnit() {
    super();
    this.status = UnitStatus.INACTIVE;
    this.readinessLevel = ReadinessLevel.C4;
    this.communicationStatus = CommunicationStatus.UNKNOWN;
    this.threatLevel = ThreatLevel.LOW;
  }

  /**
   * Constructor with required fields.
   */
  public MilitaryUnit(String callsign, String unitName, UnitType unitType,
      OperationalDomain domain) {
    this();
    this.callsign = callsign;
    this.unitName = unitName;
    this.unitType = unitType;
    this.domain = domain;
  }

  // Getters and Setters

  public String getCallsign() {
    return callsign;
  }

  public void setCallsign(String callsign) {
    this.callsign = callsign;
  }

  public String getUnitName() {
    return unitName;
  }

  public void setUnitName(String unitName) {
    this.unitName = unitName;
  }

  public UnitType getUnitType() {
    return unitType;
  }

  public void setUnitType(UnitType unitType) {
    this.unitType = unitType;
  }

  public OperationalDomain getDomain() {
    return domain;
  }

  public void setDomain(OperationalDomain domain) {
    this.domain = domain;
  }

  public UnitStatus getStatus() {
    return status;
  }

  public void setStatus(UnitStatus status) {
    this.status = status;
  }

  public ReadinessLevel getReadinessLevel() {
    return readinessLevel;
  }

  public void setReadinessLevel(ReadinessLevel readinessLevel) {
    this.readinessLevel = readinessLevel;
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

  public BigDecimal getHeading() {
    return heading;
  }

  public void setHeading(BigDecimal heading) {
    this.heading = heading;
  }

  public BigDecimal getSpeed() {
    return speed;
  }

  public void setSpeed(BigDecimal speed) {
    this.speed = speed;
  }

  public Integer getPersonnelCount() {
    return personnelCount;
  }

  public void setPersonnelCount(Integer personnelCount) {
    this.personnelCount = personnelCount;
  }

  public String getEquipment() {
    return equipment;
  }

  public void setEquipment(String equipment) {
    this.equipment = equipment;
  }

  public LocalDateTime getLastContact() {
    return lastContact;
  }

  public void setLastContact(LocalDateTime lastContact) {
    this.lastContact = lastContact;
  }

  public CommunicationStatus getCommunicationStatus() {
    return communicationStatus;
  }

  public void setCommunicationStatus(CommunicationStatus communicationStatus) {
    this.communicationStatus = communicationStatus;
  }

  public ThreatLevel getThreatLevel() {
    return threatLevel;
  }

  public void setThreatLevel(ThreatLevel threatLevel) {
    this.threatLevel = threatLevel;
  }

  public String getNotes() {
    return notes;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }

  public List<UnitStatusHistory> getStatusHistory() {
    return statusHistory;
  }

  public void setStatusHistory(List<UnitStatusHistory> statusHistory) {
    this.statusHistory = statusHistory;
  }

  public List<Mission> getMissions() {
    return missions;
  }

  public void setMissions(List<Mission> missions) {
    this.missions = missions;
  }

  /**
   * Updates the unit's position with new coordinates.
   */
  public void updatePosition(BigDecimal latitude, BigDecimal longitude, BigDecimal altitude) {
    this.latitude = latitude;
    this.longitude = longitude;
    this.altitude = altitude;
    this.lastContact = LocalDateTime.now();
  }

  /**
   * Updates the unit's movement with heading and speed.
   */
  public void updateMovement(BigDecimal heading, BigDecimal speed) {
    this.heading = heading;
    this.speed = speed;
    this.lastContact = LocalDateTime.now();
  }

  /**
   * Enum for unit types.
   */
  public enum UnitType {
    INFANTRY, ARMOR, ARTILLERY, AIR_DEFENSE, AVIATION, NAVAL, CYBER, LOGISTICS, COMMAND, INTELLIGENCE
  }

  /**
   * Enum for operational domains.
   */
  public enum OperationalDomain {
    LAND, AIR, SEA, CYBER, SPACE
  }

  /**
   * Enum for unit status.
   */
  public enum UnitStatus {
    ACTIVE, INACTIVE, DEPLOYED, STANDBY, MAINTENANCE, OFFLINE
  }

  /**
   * Enum for readiness levels (C1-C4 military standard).
   */
  public enum ReadinessLevel {
    C1, // Fully mission capable
    C2, // Substantially mission capable
    C3, // Marginally mission capable
    C4 // Not mission capable
  }

  /**
   * Enum for communication status.
   */
  public enum CommunicationStatus {
    OPERATIONAL, DEGRADED, OFFLINE, UNKNOWN
  }

  /**
   * Enum for threat levels.
   */
  public enum ThreatLevel {
    LOW, MODERATE, HIGH, CRITICAL
  }
}
