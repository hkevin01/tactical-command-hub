package com.tacticalcommand.tactical.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tacticalcommand.tactical.domain.TacticalEvent;

/**
 * Repository interface for TacticalEvent entities.
 * Provides CRUD operations and custom queries for tactical event management.
 */
@Repository
public interface TacticalEventRepository extends JpaRepository<TacticalEvent, Long> {

    /**
     * Find an event by its unique event ID.
     *
     * @param eventId the event ID
     * @return optional containing the event if found
     */
    Optional<TacticalEvent> findByEventId(String eventId);

    /**
     * Find all events ordered by event time (newest first).
     *
     * @param pageable pagination information
     * @return page of events ordered by event time
     */
    Page<TacticalEvent> findAllByOrderByEventTimeDesc(Pageable pageable);

    /**
     * Find events by severity level.
     *
     * @param severity the severity level
     * @param pageable pagination information
     * @return page of events with specified severity
     */
    Page<TacticalEvent> findBySeverityOrderByEventTimeDesc(String severity, Pageable pageable);

    /**
     * Find events by type.
     *
     * @param eventType the event type
     * @param pageable pagination information
     * @return page of events with specified type
     */
    Page<TacticalEvent> findByEventTypeOrderByEventTimeDesc(String eventType, Pageable pageable);

    /**
     * Find events by status.
     *
     * @param status the event status
     * @param pageable pagination information
     * @return page of events with specified status
     */
    Page<TacticalEvent> findByStatusOrderByEventTimeDesc(String status, Pageable pageable);

    /**
     * Find events for a specific mission.
     *
     * @param missionId the mission ID
     * @return list of events for the mission
     */
    List<TacticalEvent> findByMissionIdOrderByEventTimeDesc(Long missionId);

    /**
     * Find events for a specific unit.
     *
     * @param unitId the unit ID
     * @return list of events for the unit
     */
    List<TacticalEvent> findByReportingUnitIdOrderByEventTimeDesc(Long unitId);

    /**
     * Find events within a time range.
     *
     * @param startTime the start time
     * @param endTime the end time
     * @return list of events within the time range
     */
    List<TacticalEvent> findByEventTimeBetweenOrderByEventTimeDesc(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * Find events after a specific time.
     *
     * @param eventTime the event time
     * @return list of events after the specified time
     */
    List<TacticalEvent> findByEventTimeAfterOrderByEventTimeDesc(LocalDateTime eventTime);

    /**
     * Find events with severities in the given list.
     *
     * @param severities list of severity levels
     * @return list of events with specified severities
     */
    List<TacticalEvent> findBySeverityInOrderByEventTimeDesc(List<String> severities);

    /**
     * Count events after a specific time.
     *
     * @param eventTime the event time
     * @return number of events after the specified time
     */
    Long countByEventTimeAfter(LocalDateTime eventTime);

    /**
     * Count events with severities in the given list.
     *
     * @param severities list of severity levels
     * @return number of events with specified severities
     */
    Long countBySeverityIn(List<String> severities);

    /**
     * Count events with status not equal to the specified status.
     *
     * @param status the status to exclude
     * @return number of events not having the specified status
     */
    Long countByStatusNot(String status);

    /**
     * Find unresolved events for a mission.
     *
     * @param missionId the mission ID
     * @return list of unresolved events for the mission
     */
    @Query("SELECT e FROM TacticalEvent e WHERE e.mission.id = :missionId AND e.status != 'RESOLVED' ORDER BY e.eventTime DESC")
    List<TacticalEvent> findUnresolvedEventsByMissionId(@Param("missionId") Long missionId);

    /**
     * Find unresolved events for a unit.
     *
     * @param unitId the unit ID
     * @return list of unresolved events for the unit
     */
    @Query("SELECT e FROM TacticalEvent e WHERE e.reportingUnit.id = :unitId AND e.status != 'RESOLVED' ORDER BY e.eventTime DESC")
    List<TacticalEvent> findUnresolvedEventsByUnitId(@Param("unitId") Long unitId);

    /**
     * Find critical events (HIGH and CRITICAL severity).
     *
     * @return list of critical events
     */
    @Query("SELECT e FROM TacticalEvent e WHERE e.severity IN ('HIGH', 'CRITICAL') ORDER BY e.eventTime DESC")
    List<TacticalEvent> findCriticalEvents();

    /**
     * Find events by type and severity.
     *
     * @param eventType the event type
     * @param severity the severity level
     * @return list of events matching the criteria
     */
    List<TacticalEvent> findByEventTypeAndSeverityOrderByEventTimeDesc(String eventType, String severity);

    /**
     * Find recent critical events within specified hours.
     *
     * @param hours the number of hours to look back
     * @return list of recent critical events
     */
    @Query("SELECT e FROM TacticalEvent e WHERE e.eventTime >= :since AND e.severity IN ('HIGH', 'CRITICAL') ORDER BY e.eventTime DESC")
    List<TacticalEvent> findRecentCriticalEvents(@Param("since") LocalDateTime since);

    /**
     * Delete events older than the specified date.
     *
     * @param cutoffDate the cutoff date
     */
    void deleteByEventTimeBefore(LocalDateTime cutoffDate);
}
