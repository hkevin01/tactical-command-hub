package com.tacticalcommand.tactical.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.tacticalcommand.tactical.domain.MilitaryUnit;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for MilitaryUnit creation requests.
 * 
 * @author Tactical Command Hub Team
 * @version 1.0.0
 * @since 2025-07-29
 */
public class MilitaryUnitCreateDto {

    @NotBlank(message = "Unit callsign is required")
    @Size(max = 50, message = "Callsign must not exceed 50 characters")
    private String callsign;

    @NotBlank(message = "Unit name is required")
    @Size(max = 100, message = "Unit name must not exceed 100 characters")
    private String unitName;

    @NotNull(message = "Unit type is required")
    private MilitaryUnit.UnitType unitType;

    @NotNull(message = "Operational domain is required")
    private MilitaryUnit.OperationalDomain domain;

    private MilitaryUnit.UnitStatus status;
    private MilitaryUnit.ReadinessLevel readinessLevel;
    
    private BigDecimal latitude;
    private BigDecimal longitude;
    private BigDecimal altitude;
    private BigDecimal heading;
    private BigDecimal speed;
    
    private Integer personnelCount;
    
    @Size(max = 500, message = "Equipment description must not exceed 500 characters")
    private String equipment;
    
    private MilitaryUnit.CommunicationStatus communicationStatus;
    private MilitaryUnit.ThreatLevel threatLevel;
    
    @Size(max = 1000, message = "Notes must not exceed 1000 characters")
    private String notes;

    // Constructors
    public MilitaryUnitCreateDto() {}

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

    public MilitaryUnit.UnitType getUnitType() {
        return unitType;
    }

    public void setUnitType(MilitaryUnit.UnitType unitType) {
        this.unitType = unitType;
    }

    public MilitaryUnit.OperationalDomain getDomain() {
        return domain;
    }

    public void setDomain(MilitaryUnit.OperationalDomain domain) {
        this.domain = domain;
    }

    public MilitaryUnit.UnitStatus getStatus() {
        return status;
    }

    public void setStatus(MilitaryUnit.UnitStatus status) {
        this.status = status;
    }

    public MilitaryUnit.ReadinessLevel getReadinessLevel() {
        return readinessLevel;
    }

    public void setReadinessLevel(MilitaryUnit.ReadinessLevel readinessLevel) {
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

    public MilitaryUnit.CommunicationStatus getCommunicationStatus() {
        return communicationStatus;
    }

    public void setCommunicationStatus(MilitaryUnit.CommunicationStatus communicationStatus) {
        this.communicationStatus = communicationStatus;
    }

    public MilitaryUnit.ThreatLevel getThreatLevel() {
        return threatLevel;
    }

    public void setThreatLevel(MilitaryUnit.ThreatLevel threatLevel) {
        this.threatLevel = threatLevel;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}

/**
 * Data Transfer Object for MilitaryUnit response data.
 */
class MilitaryUnitResponseDto {
    private Long id;
    private String callsign;
    private String unitName;
    private MilitaryUnit.UnitType unitType;
    private MilitaryUnit.OperationalDomain domain;
    private MilitaryUnit.UnitStatus status;
    private MilitaryUnit.ReadinessLevel readinessLevel;
    
    private BigDecimal latitude;
    private BigDecimal longitude;
    private BigDecimal altitude;
    private BigDecimal heading;
    private BigDecimal speed;
    
    private Integer personnelCount;
    private String equipment;
    private LocalDateTime lastContact;
    private MilitaryUnit.CommunicationStatus communicationStatus;
    private MilitaryUnit.ThreatLevel threatLevel;
    private String notes;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    // Constructors
    public MilitaryUnitResponseDto() {}

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public MilitaryUnit.UnitType getUnitType() {
        return unitType;
    }

    public void setUnitType(MilitaryUnit.UnitType unitType) {
        this.unitType = unitType;
    }

    public MilitaryUnit.OperationalDomain getDomain() {
        return domain;
    }

    public void setDomain(MilitaryUnit.OperationalDomain domain) {
        this.domain = domain;
    }

    public MilitaryUnit.UnitStatus getStatus() {
        return status;
    }

    public void setStatus(MilitaryUnit.UnitStatus status) {
        this.status = status;
    }

    public MilitaryUnit.ReadinessLevel getReadinessLevel() {
        return readinessLevel;
    }

    public void setReadinessLevel(MilitaryUnit.ReadinessLevel readinessLevel) {
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

    public MilitaryUnit.CommunicationStatus getCommunicationStatus() {
        return communicationStatus;
    }

    public void setCommunicationStatus(MilitaryUnit.CommunicationStatus communicationStatus) {
        this.communicationStatus = communicationStatus;
    }

    public MilitaryUnit.ThreatLevel getThreatLevel() {
        return threatLevel;
    }

    public void setThreatLevel(MilitaryUnit.ThreatLevel threatLevel) {
        this.threatLevel = threatLevel;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }
}
