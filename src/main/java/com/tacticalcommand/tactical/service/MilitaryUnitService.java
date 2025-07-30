package com.tacticalcommand.tactical.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tacticalcommand.tactical.domain.MilitaryUnit;
import com.tacticalcommand.tactical.domain.UnitStatusHistory;
import com.tacticalcommand.tactical.repository.MilitaryUnitRepository;

/**
 * Service class for Military Unit operations.
 * 
 * Provides business logic for military unit management including CRUD operations, position
 * tracking, status management, and operational queries.
 * 
 * @author Tactical Command Hub Team
 * @version 1.0.0
 * @since 2025-07-29
 */
@Service
@Transactional
public class MilitaryUnitService {

  private final MilitaryUnitRepository militaryUnitRepository;

  @Autowired
  public MilitaryUnitService(MilitaryUnitRepository militaryUnitRepository) {
    this.militaryUnitRepository = militaryUnitRepository;
  }

  /**
   * Creates a new military unit.
   *
   * @param unit
   *          the military unit to create
   * @return the created military unit
   * @throws IllegalArgumentException
   *           if callsign already exists
   */
  public MilitaryUnit createUnit(MilitaryUnit unit) {
    if (militaryUnitRepository.existsByCallsign(unit.getCallsign())) {
      throw new IllegalArgumentException(
          "Unit with callsign '" + unit.getCallsign() + "' already exists");
    }

    // Set default values if not provided
    if (unit.getStatus() == null) {
      unit.setStatus(MilitaryUnit.UnitStatus.INACTIVE);
    }
    if (unit.getReadinessLevel() == null) {
      unit.setReadinessLevel(MilitaryUnit.ReadinessLevel.C4);
    }
    if (unit.getCommunicationStatus() == null) {
      unit.setCommunicationStatus(MilitaryUnit.CommunicationStatus.UNKNOWN);
    }
    if (unit.getThreatLevel() == null) {
      unit.setThreatLevel(MilitaryUnit.ThreatLevel.LOW);
    }

    return militaryUnitRepository.save(unit);
  }

  /**
   * Updates an existing military unit.
   *
   * @param id
   *          the unit ID
   * @param updatedUnit
   *          the updated unit data
   * @return the updated military unit
   * @throws RuntimeException
   *           if unit not found
   */
  public MilitaryUnit updateUnit(Long id, MilitaryUnit updatedUnit) {
    MilitaryUnit existingUnit = militaryUnitRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Military unit not found with id: " + id));

    // Check if callsign is being changed and if new callsign already exists
    if (!existingUnit.getCallsign().equals(updatedUnit.getCallsign())
        && militaryUnitRepository.existsByCallsign(updatedUnit.getCallsign())) {
      throw new IllegalArgumentException(
          "Unit with callsign '" + updatedUnit.getCallsign() + "' already exists");
    }

    // Update fields
    existingUnit.setCallsign(updatedUnit.getCallsign());
    existingUnit.setUnitName(updatedUnit.getUnitName());
    existingUnit.setUnitType(updatedUnit.getUnitType());
    existingUnit.setDomain(updatedUnit.getDomain());
    existingUnit.setPersonnelCount(updatedUnit.getPersonnelCount());
    existingUnit.setEquipment(updatedUnit.getEquipment());
    existingUnit.setNotes(updatedUnit.getNotes());

    return militaryUnitRepository.save(existingUnit);
  }

  /**
   * Updates unit status and creates status history record.
   *
   * @param id
   *          the unit ID
   * @param newStatus
   *          the new status
   * @param changeReason
   *          the reason for status change
   * @param changedBy
   *          the user making the change
   * @return the updated military unit
   */
  public MilitaryUnit updateUnitStatus(Long id, MilitaryUnit.UnitStatus newStatus,
      String changeReason, String changedBy) {
    MilitaryUnit unit = militaryUnitRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Military unit not found with id: " + id));

    MilitaryUnit.UnitStatus previousStatus = unit.getStatus();

    if (!previousStatus.equals(newStatus)) {
      // Create status history record
      UnitStatusHistory statusHistory = new UnitStatusHistory(unit, previousStatus, newStatus,
          changedBy);
      statusHistory.setChangeReason(changeReason);
      unit.getStatusHistory().add(statusHistory);

      // Update unit status
      unit.setStatus(newStatus);
    }

    return militaryUnitRepository.save(unit);
  }

  /**
   * Updates unit position with coordinates.
   *
   * @param id
   *          the unit ID
   * @param latitude
   *          the latitude
   * @param longitude
   *          the longitude
   * @param altitude
   *          the altitude (optional)
   * @return the updated military unit
   */
  public MilitaryUnit updateUnitPosition(Long id, BigDecimal latitude, BigDecimal longitude,
      BigDecimal altitude) {
    MilitaryUnit unit = militaryUnitRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Military unit not found with id: " + id));

    unit.updatePosition(latitude, longitude, altitude);
    return militaryUnitRepository.save(unit);
  }

  /**
   * Updates unit movement data (heading and speed).
   *
   * @param id
   *          the unit ID
   * @param heading
   *          the heading in degrees
   * @param speed
   *          the speed
   * @return the updated military unit
   */
  public MilitaryUnit updateUnitMovement(Long id, BigDecimal heading, BigDecimal speed) {
    MilitaryUnit unit = militaryUnitRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Military unit not found with id: " + id));

    unit.updateMovement(heading, speed);
    return militaryUnitRepository.save(unit);
  }

  /**
   * Updates unit readiness level.
   *
   * @param id
   *          the unit ID
   * @param readinessLevel
   *          the new readiness level
   * @return the updated military unit
   */
  public MilitaryUnit updateReadinessLevel(Long id, MilitaryUnit.ReadinessLevel readinessLevel) {
    MilitaryUnit unit = militaryUnitRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Military unit not found with id: " + id));

    unit.setReadinessLevel(readinessLevel);
    return militaryUnitRepository.save(unit);
  }

  /**
   * Updates unit threat level.
   *
   * @param id
   *          the unit ID
   * @param threatLevel
   *          the new threat level
   * @return the updated military unit
   */
  public MilitaryUnit updateThreatLevel(Long id, MilitaryUnit.ThreatLevel threatLevel) {
    MilitaryUnit unit = militaryUnitRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Military unit not found with id: " + id));

    unit.setThreatLevel(threatLevel);
    return militaryUnitRepository.save(unit);
  }

  /**
   * Retrieves a military unit by ID.
   *
   * @param id
   *          the unit ID
   * @return the military unit
   */
  @Transactional(readOnly = true)
  public Optional<MilitaryUnit> getUnitById(Long id) {
    return militaryUnitRepository.findById(id);
  }

  /**
   * Retrieves a military unit by callsign.
   *
   * @param callsign
   *          the unit callsign
   * @return the military unit
   */
  @Transactional(readOnly = true)
  public Optional<MilitaryUnit> getUnitByCallsign(String callsign) {
    return militaryUnitRepository.findByCallsign(callsign);
  }

  /**
   * Retrieves all military units with pagination.
   *
   * @param pageable
   *          pagination parameters
   * @return page of military units
   */
  @Transactional(readOnly = true)
  public Page<MilitaryUnit> getAllUnits(Pageable pageable) {
    return militaryUnitRepository.findAll(pageable);
  }

  /**
   * Retrieves all active military units.
   *
   * @return list of active units
   */
  @Transactional(readOnly = true)
  public List<MilitaryUnit> getActiveUnits() {
    return militaryUnitRepository.findActiveUnits();
  }

  /**
   * Retrieves units by status.
   *
   * @param status
   *          the unit status
   * @return list of units with the specified status
   */
  @Transactional(readOnly = true)
  public List<MilitaryUnit> getUnitsByStatus(MilitaryUnit.UnitStatus status) {
    return militaryUnitRepository.findByStatus(status);
  }

  /**
   * Retrieves units by operational domain.
   *
   * @param domain
   *          the operational domain
   * @return list of units in the specified domain
   */
  @Transactional(readOnly = true)
  public List<MilitaryUnit> getUnitsByDomain(MilitaryUnit.OperationalDomain domain) {
    return militaryUnitRepository.findByDomain(domain);
  }

  /**
   * Retrieves mission-capable units (C1 or C2 readiness).
   *
   * @return list of mission-capable units
   */
  @Transactional(readOnly = true)
  public List<MilitaryUnit> getMissionCapableUnits() {
    return militaryUnitRepository.findMissionCapableUnits();
  }

  /**
   * Retrieves units within a geographical radius.
   *
   * @param centerLat
   *          center latitude
   * @param centerLon
   *          center longitude
   * @param radiusKm
   *          radius in kilometers
   * @return list of units within the specified area
   */
  @Transactional(readOnly = true)
  public List<MilitaryUnit> getUnitsWithinRadius(BigDecimal centerLat, BigDecimal centerLon,
      Double radiusKm) {
    return militaryUnitRepository.findUnitsWithinRadius(centerLat, centerLon, radiusKm);
  }

  /**
   * Retrieves units with no recent contact.
   *
   * @param hours
   *          the number of hours to look back
   * @return list of units with no recent contact
   */
  @Transactional(readOnly = true)
  public List<MilitaryUnit> getUnitsWithNoRecentContact(int hours) {
    LocalDateTime cutoffTime = LocalDateTime.now().minusHours(hours);
    return militaryUnitRepository.findUnitsWithNoRecentContact(cutoffTime);
  }

  /**
   * Searches units by callsign or name.
   *
   * @param searchTerm
   *          the search term
   * @param pageable
   *          pagination parameters
   * @return page of matching units
   */
  @Transactional(readOnly = true)
  public Page<MilitaryUnit> searchUnits(String searchTerm, Pageable pageable) {
    return militaryUnitRepository.searchByCallsignOrName(searchTerm, pageable);
  }

  /**
   * Deletes a military unit.
   *
   * @param id
   *          the unit ID
   * @throws RuntimeException
   *           if unit not found
   */
  public void deleteUnit(Long id) {
    if (!militaryUnitRepository.existsById(id)) {
      throw new RuntimeException("Military unit not found with id: " + id);
    }
    militaryUnitRepository.deleteById(id);
  }

  /**
   * Gets unit statistics by status.
   *
   * @return statistics map
   */
  @Transactional(readOnly = true)
  public java.util.Map<MilitaryUnit.UnitStatus, Long> getUnitStatusStatistics() {
    java.util.Map<MilitaryUnit.UnitStatus, Long> stats = new java.util.HashMap<>();
    for (MilitaryUnit.UnitStatus status : MilitaryUnit.UnitStatus.values()) {
      stats.put(status, militaryUnitRepository.countByStatus(status));
    }
    return stats;
  }

  /**
   * Gets unit statistics by domain.
   *
   * @return statistics map
   */
  @Transactional(readOnly = true)
  public java.util.Map<MilitaryUnit.OperationalDomain, Long> getUnitDomainStatistics() {
    java.util.Map<MilitaryUnit.OperationalDomain, Long> stats = new java.util.HashMap<>();
    for (MilitaryUnit.OperationalDomain domain : MilitaryUnit.OperationalDomain.values()) {
      stats.put(domain, militaryUnitRepository.countByDomain(domain));
    }
    return stats;
  }

  /**
   * Gets unit statistics by readiness level.
   *
   * @return statistics map
   */
  @Transactional(readOnly = true)
  public java.util.Map<MilitaryUnit.ReadinessLevel, Long> getUnitReadinessStatistics() {
    java.util.Map<MilitaryUnit.ReadinessLevel, Long> stats = new java.util.HashMap<>();
    for (MilitaryUnit.ReadinessLevel level : MilitaryUnit.ReadinessLevel.values()) {
      stats.put(level, militaryUnitRepository.countByReadinessLevel(level));
    }
    return stats;
  }

  /**
   * Find unit by ID.
   * 
   * @param id
   *          unit ID
   * @return Optional containing the unit if found
   */
  public Optional<MilitaryUnit> findById(Long id) {
    return militaryUnitRepository.findById(id);
  }

  /**
   * Find unit by callsign.
   * 
   * @param callsign
   *          unit callsign
   * @return Optional containing the unit if found
   */
  public Optional<MilitaryUnit> findByCallsign(String callsign) {
    return militaryUnitRepository.findByCallsign(callsign);
  }

  /**
   * Find units by status.
   * 
   * @param status
   *          unit status
   * @return list of units with the specified status
   */
  public List<MilitaryUnit> findByStatus(MilitaryUnit.UnitStatus status) {
    return militaryUnitRepository.findByStatus(status);
  }

  /**
   * Find units by domain.
   * 
   * @param domain
   *          unit domain
   * @return list of units in the specified domain
   */
  public List<MilitaryUnit> findByDomain(MilitaryUnit.OperationalDomain domain) {
    return militaryUnitRepository.findByDomain(domain);
  }

  /**
   * Find units by readiness level.
   * 
   * @param readinessLevel
   *          readiness level
   * @return list of units with the specified readiness level
   */
  public List<MilitaryUnit> findByReadinessLevel(MilitaryUnit.ReadinessLevel readinessLevel) {
    return militaryUnitRepository.findByReadinessLevel(readinessLevel);
  }

  /**
   * Find units within a geographic radius.
   * 
   * @param latitude
   *          center latitude
   * @param longitude
   *          center longitude
   * @param radiusKm
   *          radius in kilometers
   * @return list of units within the specified radius
   */
  public List<MilitaryUnit> findUnitsWithinRadius(double latitude, double longitude,
      double radiusKm) {
    return militaryUnitRepository.findUnitsWithinRadius(BigDecimal.valueOf(latitude),
        BigDecimal.valueOf(longitude), radiusKm);
  }

  /**
   * Update unit status.
   * 
   * @param id
   *          unit ID
   * @param status
   *          new status
   * @return updated unit
   */
  public MilitaryUnit updateUnitStatus(Long id, MilitaryUnit.UnitStatus status) {
    MilitaryUnit unit = militaryUnitRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Unit not found with ID: " + id));

    unit.setStatus(status);
    return militaryUnitRepository.save(unit);
  }

  /**
   * Delete a military unit.
   * 
   * @param id
   *          unit ID
   * @return true if deleted, false if not found
   */
  public boolean deleteUnit(Long id) {
    if (militaryUnitRepository.existsById(id)) {
      militaryUnitRepository.deleteById(id);
      return true;
    }
    return false;
  }

  /**
   * Get all units with pagination.
   * 
   * @param pageable
   *          pagination parameters
   * @return page of units
   */
  public Page<MilitaryUnit> getAllUnits(Pageable pageable) {
    return militaryUnitRepository.findAll(pageable);
  }

  /**
   * Validate unit data before creation.
   * 
   * @param unit
   *          unit to validate
   * @throws IllegalArgumentException
   *           if validation fails
   */
  private void validateUnitData(MilitaryUnit unit) {
    if (unit.getCallsign() == null || unit.getCallsign().trim().isEmpty()) {
      throw new IllegalArgumentException("Unit callsign is required");
    }
    if (unit.getUnitName() == null || unit.getUnitName().trim().isEmpty()) {
      throw new IllegalArgumentException("Unit name is required");
    }
    if (unit.getUnitType() == null) {
      throw new IllegalArgumentException("Unit type is required");
    }
  }
}
