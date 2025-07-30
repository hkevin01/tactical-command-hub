package com.tacticalcommand.tactical.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tacticalcommand.tactical.domain.TacticalEvent;
import com.tacticalcommand.tactical.service.TacticalEventService;

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
     * @return events for the mission
     */
    @GetMapping("/mission/{missionId}")
    public ResponseEntity<List<TacticalEvent>> getEventsByMission(@PathVariable Long missionId) {
        List<TacticalEvent> events = eventService.getEventsByMission(missionId);
        return ResponseEntity.ok(events);
    }

    /**
     * Gets events by unit.
     *
     * @param unitId the unit ID
     * @return events for the unit
     */
    @GetMapping("/unit/{unitId}")
    public ResponseEntity<List<TacticalEvent>> getEventsByUnit(@PathVariable Long unitId) {
        List<TacticalEvent> events = eventService.getEventsByUnit(unitId);
        return ResponseEntity.ok(events);
    }

    /**
     * Gets events by severity.
     *
     * @param severity the event severity (LOW, MEDIUM, HIGH, CRITICAL)
     * @param pageable pagination parameters
     * @return events with specified severity
     */
    @GetMapping("/severity/{severity}")
    public ResponseEntity<Page<TacticalEvent>> getEventsBySeverity(
            @PathVariable String severity,
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
     * Gets events within time range.
     *
     * @param startTime start time
     * @param endTime end time
     * @return events within time range
     */
    @GetMapping("/time-range")
    public ResponseEntity<List<TacticalEvent>> getEventsInTimeRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        List<TacticalEvent> events = eventService.getEventsByTimeRange(startTime, endTime);
        return ResponseEntity.ok(events);
    }

    /**
     * Gets critical events.
     *
     * @return critical events
     */
    @GetMapping("/critical")
    public ResponseEntity<List<TacticalEvent>> getCriticalEvents() {
        List<TacticalEvent> events = eventService.getCriticalEvents();
        return ResponseEntity.ok(events);
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
     * Updates an event.
     *
     * @param id the event ID
     * @param event the updated event data
     * @return updated event
     */
    @PutMapping("/{id}")
    public ResponseEntity<TacticalEvent> updateEvent(@PathVariable Long id, @RequestBody TacticalEvent event) {
        try {
            TacticalEvent updatedEvent = eventService.updateEvent(id, event);
            return ResponseEntity.ok(updatedEvent);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Acknowledges an event.
     *
     * @param id the event ID
     * @param acknowledgedBy who acknowledged the event
     * @return updated event
     */
    @PostMapping("/{id}/acknowledge")
    public ResponseEntity<TacticalEvent> acknowledgeEvent(
            @PathVariable Long id,
            @RequestParam String acknowledgedBy) {
        try {
            TacticalEvent event = eventService.acknowledgeEvent(id, acknowledgedBy);
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
     * @param resolution the resolution description
     * @return updated event
     */
    @PostMapping("/{id}/resolve")
    public ResponseEntity<TacticalEvent> resolveEvent(
            @PathVariable Long id,
            @RequestParam String resolution) {
        try {
            TacticalEvent event = eventService.resolveEvent(id, resolution);
            return ResponseEntity.ok(event);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
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
