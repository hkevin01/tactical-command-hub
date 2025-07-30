package com.tacticalcommand.tactical.repository;

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
import com.tacticalcommand.tactical.domain.Mission;

/**
 * Repository interface for Mission entity operations.
 * 
 * Provides comprehensive data access methods for mission management, including mission planning,
 * execution tracking, and operational queries.
 * 
 * @author Tactical Command Hub Team
 * @version 1.0.0
 * @since 2025-07-29
 */
@Repository
public interface MissionRepository
    extends JpaRepository<Mission, Long>, JpaSpecificationExecutor<Mission> {

  /**
   * Finds a mission by its unique mission code.
   *
   * @param missionCode
   *          the mission code
   * @return optional containing the mission if found
   */
  Optional<Mission> findByMissionCode(String missionCode);

  /**
   * Finds all missions with the specified status.
   *
   * @param status
   *          the mission status
   * @return list of missions with the given status
   */
  List<Mission> findByStatus(Mission.MissionStatus status);

  /**
   * Finds all missions with the specified priority.
   *
   * @param priority
   *          the mission priority
   * @return list of missions with the given priority
   */
  List<Mission> findByPriority(Mission.Priority priority);

  /**
   * Finds all missions of the specified type.
   *
   * @param missionType
   *          the mission type
   * @return list of missions of the given type
   */
  List<Mission> findByMissionType(Mission.MissionType missionType);

  /**
   * Finds all missions assigned to a specific unit.
   *
   * @param assignedUnit
   *          the assigned unit
   * @return list of missions assigned to the unit
   */
  List<Mission> findByAssignedUnit(MilitaryUnit assignedUnit);

  /**
   * Finds all missions assigned to a specific unit by unit ID.
   *
   * @param unitId
   *          the unit ID
   * @return list of missions assigned to the unit
   */
  List<Mission> findByAssignedUnitId(Long unitId);

  /**
   * Finds all missions commanded by a specific commander.
   *
   * @param commander
   *          the commander name
   * @return list of missions commanded by the specified commander
   */
  List<Mission> findByCommander(String commander);

  /**
   * Finds all active missions (status: ACTIVE).
   *
   * @return list of active missions
   */
  @Query("SELECT m FROM Mission m WHERE m.status = 'ACTIVE'")
  List<Mission> findActiveMissions();

  /**
   * Finds all missions starting within a specified time range.
   *
   * @param startTime
   *          the start of the time range
   * @param endTime
   *          the end of the time range
   * @return list of missions starting within the time range
   */
  @Query("SELECT m FROM Mission m WHERE m.startTime BETWEEN :startTime AND :endTime")
  List<Mission> findMissionsStartingBetween(@Param("startTime") LocalDateTime startTime,
      @Param("endTime") LocalDateTime endTime);

  /**
   * Finds all missions scheduled for today.
   *
   * @param startOfDay
   *          start of today
   * @param endOfDay
   *          end of today
   * @return list of missions scheduled for today
   */
  List<Mission> findByStartTimeBetween(LocalDateTime startOfDay, LocalDateTime endOfDay);

  /**
   * Finds all overdue missions (past end time but not completed).
   *
   * @param currentTime
   *          the current time
   * @return list of overdue missions
   */
  @Query("SELECT m FROM Mission m WHERE m.endTime < :currentTime AND m.status NOT IN ('COMPLETED', 'CANCELLED', 'ABORTED')")
  List<Mission> findOverdueMissions(@Param("currentTime") LocalDateTime currentTime);

  /**
   * Finds all missions that are approaching their start time.
   *
   * @param currentTime
   *          the current time
   * @param upcomingTime
   *          the upcoming time threshold
   * @return list of upcoming missions
   */
  @Query("SELECT m FROM Mission m WHERE m.startTime BETWEEN :currentTime AND :upcomingTime AND m.status = 'APPROVED'")
  List<Mission> findUpcomingMissions(@Param("currentTime") LocalDateTime currentTime,
      @Param("upcomingTime") LocalDateTime upcomingTime);

  /**
   * Finds missions by status with pagination.
   *
   * @param status
   *          the mission status
   * @param pageable
   *          pagination parameters
   * @return page of missions with the given status
   */
  Page<Mission> findByStatus(Mission.MissionStatus status, Pageable pageable);

  /**
   * Finds missions by priority with pagination.
   *
   * @param priority
   *          the mission priority
   * @param pageable
   *          pagination parameters
   * @return page of missions with the given priority
   */
  Page<Mission> findByPriority(Mission.Priority priority, Pageable pageable);

  /**
   * Counts missions by status.
   *
   * @param status
   *          the mission status
   * @return count of missions with the given status
   */
  long countByStatus(Mission.MissionStatus status);

  /**
   * Counts missions by priority.
   *
   * @param priority
   *          the mission priority
   * @return count of missions with the given priority
   */
  long countByPriority(Mission.Priority priority);

  /**
   * Counts missions by type.
   *
   * @param missionType
   *          the mission type
   * @return count of missions of the given type
   */
  long countByMissionType(Mission.MissionType missionType);

  /**
   * Finds high priority active missions.
   *
   * @return list of high priority active missions
   */
  @Query("SELECT m FROM Mission m WHERE m.status = 'ACTIVE' AND m.priority IN ('HIGH', 'CRITICAL')")
  List<Mission> findHighPriorityActiveMissions();

  /**
   * Finds missions with low completion percentage (less than specified threshold).
   *
   * @param threshold
   *          the completion threshold
   * @return list of missions with low completion
   */
  @Query("SELECT m FROM Mission m WHERE m.completionPercentage < :threshold AND m.status = 'ACTIVE'")
  List<Mission> findMissionsWithLowCompletion(@Param("threshold") Integer threshold);

  /**
   * Searches missions by mission code or name containing the search term.
   *
   * @param searchTerm
   *          the search term
   * @param pageable
   *          pagination parameters
   * @return page of matching missions
   */
  @Query("SELECT m FROM Mission m WHERE "
      + "LOWER(m.missionCode) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR "
      + "LOWER(m.missionName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
  Page<Mission> searchByCodeOrName(@Param("searchTerm") String searchTerm, Pageable pageable);

  /**
   * Finds missions within a completion percentage range.
   *
   * @param minCompletion
   *          minimum completion percentage
   * @param maxCompletion
   *          maximum completion percentage
   * @return list of missions within the completion range
   */
  @Query("SELECT m FROM Mission m WHERE m.completionPercentage BETWEEN :minCompletion AND :maxCompletion")
  List<Mission> findMissionsByCompletionRange(@Param("minCompletion") Integer minCompletion,
      @Param("maxCompletion") Integer maxCompletion);

  /**
   * Checks if a mission code is already in use.
   *
   * @param missionCode
   *          the mission code to check
   * @return true if mission code exists, false otherwise
   */
  boolean existsByMissionCode(String missionCode);

  /**
   * Finds the most recent missions ordered by creation date.
   *
   * @param pageable
   *          pagination parameters
   * @return page of recent missions
   */
  @Query("SELECT m FROM Mission m ORDER BY m.createdAt DESC")
  Page<Mission> findRecentMissions(Pageable pageable);
}
