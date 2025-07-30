package com.tacticalcommand.tactical.controller;

import com.tacticalcommand.tactical.domain.TacticalEvent;
import com.tacticalcommand.tactical.service.TacticalEventService;
import com.tacticalcommand.tactical.service.TacticalEventService.EventStats;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for tactical event management.
 * Provides endpoints for event tracking, acknowledgment, and resolution.
 */
@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "*")
public class TacticalEventController {

    @Autowired
    private TacticalEventService eventService;

    /**
     * Creates a new tactical event.
     *
     * @param event the event to create
     * @return created event
     */
    @PostMapping
    public ResponseEntity<TacticalEvent> createEvent(@RequestBody TacticalEvent event) {
        try {
            TacticalEvent createdEvent = eventService.createEvent(event);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdEvent);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Creates a mission-related event.
     *
     * @param missionId the mission ID
     * @param eventType the event type
     * @param description the event description
     * @param severity the event severity
     * @return created event
     */
    @PostMapping("/mission/{missionId}")
    public ResponseEntity<TacticalEvent> createMissionEvent(
            @PathVariable Long missionId,
            @RequestParam String eventType,
            @RequestParam String description,
            @RequestParam String severity) {
        try {
            TacticalEvent event = eventService.createMissionEvent(missionId, eventType, description, severity);
            return ResponseEntity.status(HttpStatus.CREATED).body(event);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Creates a unit-related event.
     *
     * @param unitId the unit ID
     * @param eventType the event type
     * @param description the event description
     * @param severity the event severity
     * @return created event
     */
    @PostMapping("/unit/{unitId}")
    public ResponseEntity<TacticalEvent> createUnitEvent(
            @PathVariable Long unitId,
            @RequestParam String eventType,
            @RequestParam String description,
            @RequestParam String severity) {
        try {
            TacticalEvent event = eventService.createUnitEvent(unitId, eventType, description, severity);
            return ResponseEntity.status(HttpStatus.CREATED).body(event);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Gets all events with pagination.
     *
     * @param pageable pagination parameters
     * @return paginated list of events
     */
    @GetMapping
    public ResponseEntity<Page<TacticalEvent>> getAllEvents(Pageable pageable) {
        Page<TacticalEvent> events = eventService.getAllEvents(pageable);
        return ResponseEntity.ok(events);
    }

    /**
     * Gets an event by ID.
     *
     * @param id the event ID
     * @return event details
     */
    @GetMapping("/{id}")
    public ResponseEntity<TacticalEvent> getEvent(@PathVariable Long id) {
        Optional<TacticalEvent> event = eventService.getEventById(id);
        return event.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    /**
     * Gets events by mission.
     *
     * @param missionId the mission ID
     * @param pageable pagination parameters
     * @return events for the mission
     */
    @GetMapping("/mission/{missionId}")
    public ResponseEntity<Page<TacticalEvent>> getEventsByMission(@PathVariable Long missionId, Pageable pageable) {
        Page<TacticalEvent> events = eventService.getEventsByMission(missionId, pageable);
        return ResponseEntity.ok(events);
    }

    /**
     * Gets events by unit.
     *
     * @param unitId the unit ID
     * @param pageable pagination parameters
     * @return events for the unit
     */
    @GetMapping("/unit/{unitId}")
    public ResponseEntity<Page<TacticalEvent>> getEventsByUnit(@PathVariable Long unitId, Pageable pageable) {
        Page<TacticalEvent> events = eventService.getEventsByUnit(unitId, pageable);
        return ResponseEntity.ok(events);
    }

    /**
     * Gets events by severity.
     *
     * @param severity the event severity
     * @param pageable pagination parameters
     * @return events with specified severity
     */
    @GetMapping("/severity/{severity}")
    public ResponseEntity<Page<TacticalEvent>> getEventsBySeverity(
            @PathVariable TacticalEvent.EventSeverity severity,
            Pageable pageable) {
        Page<TacticalEvent> events = eventService.getEventsBySeverity(severity, pageable);
        return ResponseEntity.ok(events);
    }

    /**
     * Gets events by type.
     *
     * @param eventType the event type
     * @param pageable pagination parameters
     * @return events of specified type
     */
    @GetMapping("/type/{eventType}")
    public ResponseEntity<Page<TacticalEvent>> getEventsByType(@PathVariable String eventType, Pageable pageable) {
        Page<TacticalEvent> events = eventService.getEventsByType(eventType, pageable);
        return ResponseEntity.ok(events);
    }

    /**
     * Gets events by status.
     *
     * @param status the event status
     * @param pageable pagination parameters
     * @return events with specified status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<Page<TacticalEvent>> getEventsByStatus(
            @PathVariable TacticalEvent.EventStatus status,
            Pageable pageable) {
        Page<TacticalEvent> events = eventService.getEventsByStatus(status, pageable);
        return ResponseEntity.ok(events);
    }

    /**
     * Gets events within time range.
     *
     * @param startTime start time
     * @param endTime end time
     * @param pageable pagination parameters
     * @return events within time range
     */
    @GetMapping("/time-range")
    public ResponseEntity<Page<TacticalEvent>> getEventsInTimeRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            Pageable pageable) {
        Page<TacticalEvent> events = eventService.getEventsInTimeRange(startTime, endTime, pageable);
        return ResponseEntity.ok(events);
    }

    /**
     * Gets critical events.
     *
     * @param pageable pagination parameters
     * @return critical events
     */
    @GetMapping("/critical")
    public ResponseEntity<Page<TacticalEvent>> getCriticalEvents(Pageable pageable) {
        Page<TacticalEvent> events = eventService.getCriticalEvents(pageable);
        return ResponseEntity.ok(events);
    }

    /**
     * Gets unacknowledged events.
     *
     * @param pageable pagination parameters
     * @return unacknowledged events
     */
    @GetMapping("/unacknowledged")
    public ResponseEntity<Page<TacticalEvent>> getUnacknowledgedEvents(Pageable pageable) {
        Page<TacticalEvent> events = eventService.getUnacknowledgedEvents(pageable);
        return ResponseEntity.ok(events);
    }

    /**
     * Gets unresolved events.
     *
     * @param pageable pagination parameters
     * @return unresolved events
     */
    @GetMapping("/unresolved")
    public ResponseEntity<Page<TacticalEvent>> getUnresolvedEvents(Pageable pageable) {
        Page<TacticalEvent> events = eventService.getUnresolvedEvents(pageable);
        return ResponseEntity.ok(events);
    }

    /**
     * Acknowledges an event.
     *
     * @param id the event ID
     * @param acknowledgedBy who acknowledged the event
     * @param notes optional acknowledgment notes
     * @return updated event
     */
    @PostMapping("/{id}/acknowledge")
    public ResponseEntity<TacticalEvent> acknowledgeEvent(
            @PathVariable Long id,
            @RequestParam String acknowledgedBy,
            @RequestParam(required = false) String notes) {
        try {
            TacticalEvent event = eventService.acknowledgeEvent(id, acknowledgedBy, notes);
            return ResponseEntity.ok(event);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Resolves an event.
     *
     * @param id the event ID
     * @param resolvedBy who resolved the event
     * @param resolution the resolution description
     * @return updated event
     */
    @PostMapping("/{id}/resolve")
    public ResponseEntity<TacticalEvent> resolveEvent(
            @PathVariable Long id,
            @RequestParam String resolvedBy,
            @RequestParam String resolution) {
        try {
            TacticalEvent event = eventService.resolveEvent(id, resolvedBy, resolution);
            return ResponseEntity.ok(event);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Updates event location.
     *
     * @param id the event ID
     * @param latitude the latitude
     * @param longitude the longitude
     * @return updated event
     */
    @PostMapping("/{id}/location")
    public ResponseEntity<TacticalEvent> updateEventLocation(
            @PathVariable Long id,
            @RequestParam Double latitude,
            @RequestParam Double longitude) {
        try {
            TacticalEvent event = eventService.updateEventLocation(id, latitude, longitude);
            return ResponseEntity.ok(event);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Escalates an event.
     *
     * @param id the event ID
     * @param newSeverity the new severity level
     * @param escalatedBy who escalated the event
     * @param reason escalation reason
     * @return updated event
     */
    @PostMapping("/{id}/escalate")
    public ResponseEntity<TacticalEvent> escalateEvent(
            @PathVariable Long id,
            @RequestParam TacticalEvent.EventSeverity newSeverity,
            @RequestParam String escalatedBy,
            @RequestParam String reason) {
        try {
            TacticalEvent event = eventService.escalateEvent(id, newSeverity, escalatedBy, reason);
            return ResponseEntity.ok(event);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Gets event statistics.
     *
     * @return event statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<EventStats> getEventStatistics() {
        EventStats stats = eventService.getEventStatistics();
        return ResponseEntity.ok(stats);
    }

    /**
     * Gets recent events.
     *
     * @param hours number of hours to look back
     * @return recent events
     */
    @GetMapping("/recent")
    public ResponseEntity<List<TacticalEvent>> getRecentEvents(@RequestParam(defaultValue = "24") int hours) {
        List<TacticalEvent> events = eventService.getRecentEvents(hours);
        return ResponseEntity.ok(events);
    }

    /**
     * Gets events requiring attention.
     *
     * @return events that need attention
     */
    @GetMapping("/attention-required")
    public ResponseEntity<List<TacticalEvent>> getEventsRequiringAttention() {
        List<TacticalEvent> events = eventService.getEventsRequiringAttention();
        return ResponseEntity.ok(events);
    }

    /**
     * Deletes an event.
     *
     * @param id the event ID
     * @return no content response
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        try {
            eventService.deleteEvent(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
