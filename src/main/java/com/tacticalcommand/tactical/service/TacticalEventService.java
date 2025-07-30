package com.tacticalcommand.tactical.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tacticalcommand.tactical.domain.MilitaryUnit;
import com.tacticalcommand.tactical.domain.Mission;
import com.tacticalcommand.tactical.domain.TacticalEvent;
import com.tacticalcommand.tactical.repository.MilitaryUnitRepository;
import com.tacticalcommand.tactical.repository.MissionRepository;
import com.tacticalcommand.tactical.repository.TacticalEventRepository;
import com.tacticalcommand.tactical.util.DateTimeUtils;

/**
 * Service class for managing tactical events and notifications.
 * Provides event tracking, alerting, and coordination capabilities
 * for military operations.
 */
@Service
@Transactional
public class TacticalEventService {

    @Autowired
    private TacticalEventRepository eventRepository;

    @Autowired
    private MissionRepository missionRepository;

    @Autowired
    private MilitaryUnitRepository unitRepository;

    /**
     * Creates a new tactical event.
     *
     * @param event the event to create
     * @return the created event
     */
    public TacticalEvent createEvent(TacticalEvent event) {
        // Set creation timestamp
        if (event.getEventTime() == null) {
            event.setEventTime(LocalDateTime.now());
        }

        // Generate event ID if not provided
        if (event.getEventId() == null || event.getEventId().trim().isEmpty()) {
            event.setEventId(generateEventId(event));
        }

        return eventRepository.save(event);
    }

    /**
     * Creates a mission-related tactical event.
     *
     * @param missionId the mission ID
     * @param eventType the type of event
     * @param description the event description
     * @param severity the event severity
     * @return the created event
     */
    public TacticalEvent createMissionEvent(Long missionId, String eventType, 
                                          String description, String severity) {
        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new RuntimeException("Mission not found with id: " + missionId));

        TacticalEvent event = new TacticalEvent();
        event.setEventType(eventType);
        event.setDescription(description);
        event.setSeverity(severity);
        event.setMission(mission);
        event.setEventTime(LocalDateTime.now());
        event.setEventId(generateEventId(event));

        // Link to mission's assigned unit if available
        if (mission.getAssignedUnit() != null) {
            event.setReportingUnit(mission.getAssignedUnit());
        }

        return eventRepository.save(event);
    }

    /**
     * Creates a unit-related tactical event.
     *
     * @param unitId the unit ID
     * @param eventType the type of event
     * @param description the event description
     * @param severity the event severity
     * @return the created event
     */
    public TacticalEvent createUnitEvent(Long unitId, String eventType, 
                                       String description, String severity) {
        MilitaryUnit unit = unitRepository.findById(unitId)
                .orElseThrow(() -> new RuntimeException("Military unit not found with id: " + unitId));

        TacticalEvent event = new TacticalEvent();
        event.setEventType(eventType);
        event.setDescription(description);
        event.setSeverity(severity);
        event.setReportingUnit(unit);
        event.setEventTime(LocalDateTime.now());
        event.setEventId(generateEventId(event));

        return eventRepository.save(event);
    }

    /**
     * Retrieves all tactical events with pagination.
     *
     * @param pageable pagination information
     * @return page of tactical events
     */
    public Page<TacticalEvent> getAllEvents(Pageable pageable) {
        return eventRepository.findAllByOrderByEventTimeDesc(pageable);
    }

    /**
     * Retrieves events by severity level.
     *
     * @param severity the severity level
     * @param pageable pagination information
     * @return page of events with specified severity
     */
    public Page<TacticalEvent> getEventsBySeverity(String severity, Pageable pageable) {
        return eventRepository.findBySeverityOrderByEventTimeDesc(severity, pageable);
    }

    /**
     * Retrieves events by type.
     *
     * @param eventType the event type
     * @param pageable pagination information
     * @return page of events with specified type
     */
    public Page<TacticalEvent> getEventsByType(String eventType, Pageable pageable) {
        return eventRepository.findByEventTypeOrderByEventTimeDesc(eventType, pageable);
    }

    /**
     * Retrieves events for a specific mission.
     *
     * @param missionId the mission ID
     * @return list of events for the mission
     */
    public List<TacticalEvent> getEventsByMission(Long missionId) {
        return eventRepository.findByMissionIdOrderByEventTimeDesc(missionId);
    }

    /**
     * Retrieves events for a specific unit.
     *
     * @param unitId the unit ID
     * @return list of events for the unit
     */
    public List<TacticalEvent> getEventsByUnit(Long unitId) {
        return eventRepository.findByReportingUnitIdOrderByEventTimeDesc(unitId);
    }

    /**
     * Retrieves events within a time range.
     *
     * @param startTime the start time
     * @param endTime the end time
     * @return list of events within the time range
     */
    public List<TacticalEvent> getEventsByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        return eventRepository.findByEventTimeBetweenOrderByEventTimeDesc(startTime, endTime);
    }

    /**
     * Retrieves critical events (HIGH and CRITICAL severity).
     *
     * @return list of critical events
     */
    public List<TacticalEvent> getCriticalEvents() {
        return eventRepository.findBySeverityInOrderByEventTimeDesc(List.of("HIGH", "CRITICAL"));
    }

    /**
     * Retrieves recent events within the last specified hours.
     *
     * @param hours the number of hours to look back
     * @return list of recent events
     */
    public List<TacticalEvent> getRecentEvents(int hours) {
        LocalDateTime since = LocalDateTime.now().minusHours(hours);
        return eventRepository.findByEventTimeAfterOrderByEventTimeDesc(since);
    }

    /**
     * Updates an existing tactical event.
     *
     * @param eventId the event ID
     * @param updatedEvent the updated event data
     * @return the updated event
     */
    public TacticalEvent updateEvent(Long eventId, TacticalEvent updatedEvent) {
        TacticalEvent existingEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Tactical event not found with id: " + eventId));

        // Update fields
        existingEvent.setEventType(updatedEvent.getEventType());
        existingEvent.setDescription(updatedEvent.getDescription());
        existingEvent.setSeverity(updatedEvent.getSeverity());
        existingEvent.setStatus(updatedEvent.getStatus());
        existingEvent.setResolution(updatedEvent.getResolution());

        // Update coordinates if provided
        if (updatedEvent.getLatitude() != null && updatedEvent.getLongitude() != null) {
            existingEvent.setLatitude(updatedEvent.getLatitude());
            existingEvent.setLongitude(updatedEvent.getLongitude());
        }

        return eventRepository.save(existingEvent);
    }

    /**
     * Marks an event as resolved.
     *
     * @param eventId the event ID
     * @param resolution the resolution description
     * @return the updated event
     */
    public TacticalEvent resolveEvent(Long eventId, String resolution) {
        TacticalEvent event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Tactical event not found with id: " + eventId));

        event.setStatus("RESOLVED");
        event.setResolution(resolution);
        event.setResolvedTime(LocalDateTime.now());

        return eventRepository.save(event);
    }

    /**
     * Acknowledges an event.
     *
     * @param eventId the event ID
     * @param acknowledgedBy the user who acknowledged the event
     * @return the updated event
     */
    public TacticalEvent acknowledgeEvent(Long eventId, String acknowledgedBy) {
        TacticalEvent event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Tactical event not found with id: " + eventId));

        event.setStatus("ACKNOWLEDGED");
        event.setAcknowledgedBy(acknowledgedBy);
        event.setAcknowledgedTime(LocalDateTime.now());

        return eventRepository.save(event);
    }

    /**
     * Retrieves an event by ID.
     *
     * @param eventId the event ID
     * @return optional containing the event if found
     */
    public Optional<TacticalEvent> getEventById(Long eventId) {
        return eventRepository.findById(eventId);
    }

    /**
     * Deletes a tactical event.
     *
     * @param eventId the event ID
     */
    public void deleteEvent(Long eventId) {
        TacticalEvent event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Tactical event not found with id: " + eventId));

        eventRepository.delete(event);
    }

    /**
     * Gets event statistics for dashboard.
     *
     * @return event statistics
     */
    public EventStatistics getEventStatistics() {
        LocalDateTime last24Hours = LocalDateTime.now().minusHours(24);
        LocalDateTime lastWeek = LocalDateTime.now().minusWeeks(1);

        EventStatistics stats = new EventStatistics();
        stats.setTotalEvents(eventRepository.count());
        stats.setEventsLast24Hours(eventRepository.countByEventTimeAfter(last24Hours));
        stats.setEventsLastWeek(eventRepository.countByEventTimeAfter(lastWeek));
        stats.setCriticalEvents(eventRepository.countBySeverityIn(List.of("HIGH", "CRITICAL")));
        stats.setUnresolvedEvents(eventRepository.countByStatusNot("RESOLVED"));

        return stats;
    }

    /**
     * Generates a unique event ID.
     *
     * @param event the tactical event
     * @return generated event ID
     */
    private String generateEventId(TacticalEvent event) {
        String typePrefix = event.getEventType() != null ? 
            event.getEventType().substring(0, Math.min(3, event.getEventType().length())).toUpperCase() : "EVT";
        String timestamp = DateTimeUtils.formatToTacticalTime(LocalDateTime.now());
        return typePrefix + "-" + timestamp;
    }

    /**
     * Event statistics data class.
     */
    public static class EventStatistics {
        private long totalEvents;
        private long eventsLast24Hours;
        private long eventsLastWeek;
        private long criticalEvents;
        private long unresolvedEvents;

        // Getters and setters
        public long getTotalEvents() { return totalEvents; }
        public void setTotalEvents(long totalEvents) { this.totalEvents = totalEvents; }

        public long getEventsLast24Hours() { return eventsLast24Hours; }
        public void setEventsLast24Hours(long eventsLast24Hours) { this.eventsLast24Hours = eventsLast24Hours; }

        public long getEventsLastWeek() { return eventsLastWeek; }
        public void setEventsLastWeek(long eventsLastWeek) { this.eventsLastWeek = eventsLastWeek; }

        public long getCriticalEvents() { return criticalEvents; }
        public void setCriticalEvents(long criticalEvents) { this.criticalEvents = criticalEvents; }

        public long getUnresolvedEvents() { return unresolvedEvents; }
        public void setUnresolvedEvents(long unresolvedEvents) { this.unresolvedEvents = unresolvedEvents; }
    }
}
