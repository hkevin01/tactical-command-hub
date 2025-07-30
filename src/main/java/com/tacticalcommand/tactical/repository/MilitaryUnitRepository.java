package com.tacticalcommand.tactical.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tacticalcommand.tactical.domain.MilitaryUnit;

/**
 * Repository interface for MilitaryUnit entity operations.
 * 
 * Provides comprehensive data access methods for military unit management, including position
 * tracking, status monitoring, and operational queries.
 * 
 * @author Tactical Command Hub Team
 * @version 1.0.0
 * @since 2025-07-29
 */
@Repository
public interface MilitaryUnitRepository
    extends JpaRepository<MilitaryUnit, Long>, JpaSpecificationExecutor<MilitaryUnit> {

  /**
   * Finds a military unit by its unique callsign.
   *
   * @param callsign
   *          the unit callsign
   * @return optional containing the unit if found
   */
  Optional<MilitaryUnit> findByCallsign(String callsign);

  /**
   * Finds all units with the specified status.
   *
   * @param status
   *          the unit status
   * @return list of units with the given status
   */
  List<MilitaryUnit> findByStatus(MilitaryUnit.UnitStatus status);

  /**
   * Finds all units in the specified operational domain.
   *
   * @param domain
   *          the operational domain
   * @return list of units in the domain
   */
  List<MilitaryUnit> findByDomain(MilitaryUnit.OperationalDomain domain);

  /**
   * Finds all units of the specified type.
   *
   * @param unitType
   *          the unit type
   * @return list of units of the given type
   */
  List<MilitaryUnit> findByUnitType(MilitaryUnit.UnitType unitType);

  /**
   * Finds all units with the specified readiness level.
   *
   * @param readinessLevel
   *          the readiness level
   * @return list of units with the given readiness level
   */
  List<MilitaryUnit> findByReadinessLevel(MilitaryUnit.ReadinessLevel readinessLevel);

  /**
   * Finds all units with the specified threat level.
   *
   * @param threatLevel
   *          the threat level
   * @return list of units with the given threat level
   */
  List<MilitaryUnit> findByThreatLevel(MilitaryUnit.ThreatLevel threatLevel);

  /**
   * Finds all units that have not made contact within the specified time period.
   *
   * @param cutoffTime
   *          the cutoff time for last contact
   * @return list of units with no recent contact
   */
  @Query("SELECT u FROM MilitaryUnit u WHERE u.lastContact < :cutoffTime OR u.lastContact IS NULL")
  List<MilitaryUnit> findUnitsWithNoRecentContact(@Param("cutoffTime") LocalDateTime cutoffTime);

  /**
   * Finds all active units (not inactive or offline).
   *
   * @return list of active units
   */
  @Query("SELECT u FROM MilitaryUnit u WHERE u.status NOT IN ('INACTIVE', 'OFFLINE')")
  List<MilitaryUnit> findActiveUnits();

  /**
   * Finds units within a geographical radius of specified coordinates.
   *
   * @param centerLat
   *          center latitude
   * @param centerLon
   *          center longitude
   * @param radiusKm
   *          radius in kilometers
   * @return list of units within the specified area
   */
  @Query(value = """
      SELECT * FROM military_units u
      WHERE u.latitude IS NOT NULL AND u.longitude IS NOT NULL
      AND (6371 * acos(cos(radians(:centerLat)) * cos(radians(u.latitude)) *
           cos(radians(u.longitude) - radians(:centerLon)) +
           sin(radians(:centerLat)) * sin(radians(u.latitude)))) <= :radiusKm
      """, nativeQuery = true)
  List<MilitaryUnit> findUnitsWithinRadius(@Param("centerLat") BigDecimal centerLat,
      @Param("centerLon") BigDecimal centerLon, @Param("radiusKm") Double radiusKm);

  /**
   * Finds units by status with pagination.
   *
   * @param status
   *          the unit status
   * @param pageable
   *          pagination parameters
   * @return page of units with the given status
   */
  Page<MilitaryUnit> findByStatus(MilitaryUnit.UnitStatus status, Pageable pageable);

  /**
   * Finds units by domain with pagination.
   *
   * @param domain
   *          the operational domain
   * @param pageable
   *          pagination parameters
   * @return page of units in the domain
   */
  Page<MilitaryUnit> findByDomain(MilitaryUnit.OperationalDomain domain, Pageable pageable);

  /**
   * Counts units by status.
   *
   * @param status
   *          the unit status
   * @return count of units with the given status
   */
  long countByStatus(MilitaryUnit.UnitStatus status);

  /**
   * Counts units by domain.
   *
   * @param domain
   *          the operational domain
   * @return count of units in the domain
   */
  long countByDomain(MilitaryUnit.OperationalDomain domain);

  /**
   * Counts units by readiness level.
   *
   * @param readinessLevel
   *          the readiness level
   * @return count of units with the given readiness level
   */
  long countByReadinessLevel(MilitaryUnit.ReadinessLevel readinessLevel);

  /**
   * Finds units that have positions (not null coordinates).
   *
   * @return list of units with known positions
   */
  @Query("SELECT u FROM MilitaryUnit u WHERE u.latitude IS NOT NULL AND u.longitude IS NOT NULL")
  List<MilitaryUnit> findUnitsWithKnownPositions();

  /**
   * Finds units by communication status.
   *
   * @param communicationStatus
   *          the communication status
   * @return list of units with the given communication status
   */
  List<MilitaryUnit> findByCommunicationStatus(
      MilitaryUnit.CommunicationStatus communicationStatus);

  /**
   * Finds units that are mission capable (readiness level C1 or C2).
   *
   * @return list of mission capable units
   */
  @Query("SELECT u FROM MilitaryUnit u WHERE u.readinessLevel IN ('C1', 'C2')")
  List<MilitaryUnit> findMissionCapableUnits();

  /**
   * Searches units by callsign or unit name containing the search term.
   *
   * @param searchTerm
   *          the search term
   * @param pageable
   *          pagination parameters
   * @return page of matching units
   */
  @Query("SELECT u FROM MilitaryUnit u WHERE "
      + "LOWER(u.callsign) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR "
      + "LOWER(u.unitName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
  Page<MilitaryUnit> searchByCallsignOrName(@Param("searchTerm") String searchTerm,
      Pageable pageable);

  /**
   * Checks if a callsign is already in use.
   *
   * @param callsign
   *          the callsign to check
   * @return true if callsign exists, false otherwise
   */
  boolean existsByCallsign(String callsign);
}
