package com.tacticalcommand.tactical.controller;

import com.tacticalcommand.tactical.domain.Mission;
import com.tacticalcommand.tactical.domain.Mission.MissionStatus;
import com.tacticalcommand.tactical.domain.Mission.Priority;
import com.tacticalcommand.tactical.domain.MissionWaypoint;
import com.tacticalcommand.tactical.domain.MissionReport;
import com.tacticalcommand.tactical.service.MissionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for mission management operations.
 * Provides endpoints for CRUD operations and mission lifecycle management.
 */
@RestController
@RequestMapping("/api/missions")
@CrossOrigin(origins = "*")
public class MissionController {

    @Autowired
    private MissionService missionService;

    /**
     * Creates a new mission.
     *
     * @param mission the mission to create
     * @return created mission
     */
    @PostMapping
    public ResponseEntity<Mission> createMission(@RequestBody Mission mission) {
        try {
            Mission createdMission = missionService.createMission(mission);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdMission);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Updates an existing mission.
     *
     * @param id the mission ID
     * @param mission the mission data to update
     * @return updated mission
     */
    @PutMapping("/{id}")
    public ResponseEntity<Mission> updateMission(@PathVariable Long id, @RequestBody Mission mission) {
        try {
            Mission updatedMission = missionService.updateMission(id, mission);
            return ResponseEntity.ok(updatedMission);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Gets a mission by ID.
     *
     * @param id the mission ID
     * @return mission details
     */
    @GetMapping("/{id}")
    public ResponseEntity<Mission> getMission(@PathVariable Long id) {
        Optional<Mission> mission = missionService.getMissionById(id);
        return mission.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    /**
     * Gets all missions.
     *
     * @return list of all missions
     */
    @GetMapping
    public ResponseEntity<List<Mission>> getAllMissions() {
        List<Mission> missions = missionService.getAllMissions();
        return ResponseEntity.ok(missions);
    }

    /**
     * Gets missions by status with pagination.
     *
     * @param status the mission status
     * @param pageable pagination parameters
     * @return missions with specified status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<Page<Mission>> getMissionsByStatus(
            @PathVariable MissionStatus status,
            Pageable pageable) {
        
        Page<Mission> missions = missionService.getMissionsByStatus(status, pageable);
        return ResponseEntity.ok(missions);
    }

    /**
     * Gets missions by priority.
     *
     * @param priority the mission priority
     * @return missions with specified priority
     */
    @GetMapping("/priority/{priority}")
    public ResponseEntity<List<Mission>> getMissionsByPriority(@PathVariable Priority priority) {
        List<Mission> missions = missionService.getMissionsByPriority(priority);
        return ResponseEntity.ok(missions);
    }

    /**
     * Gets missions by unit ID.
     *
     * @param unitId the unit ID
     * @return missions assigned to the unit
     */
    @GetMapping("/unit/{unitId}")
    public ResponseEntity<List<Mission>> getMissionsByUnit(@PathVariable Long unitId) {
        try {
            List<Mission> missions = missionService.getMissionsByUnit(unitId);
            return ResponseEntity.ok(missions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Assigns a unit to a mission.
     *
     * @param missionId the mission ID
     * @param unitId the unit ID
     * @return updated mission
     */
    @PostMapping("/{missionId}/assign-unit/{unitId}")
    public ResponseEntity<Mission> assignUnit(@PathVariable Long missionId, @PathVariable Long unitId) {
        try {
            Mission mission = missionService.assignUnit(missionId, unitId);
            return ResponseEntity.ok(mission);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Updates mission status.
     *
     * @param id the mission ID
     * @param status the new status
     * @return updated mission
     */
    @PostMapping("/{id}/status")
    public ResponseEntity<Mission> updateMissionStatus(@PathVariable Long id, @RequestParam MissionStatus status) {
        try {
            Mission mission = missionService.updateMissionStatus(id, status);
            return ResponseEntity.ok(mission);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Adds a waypoint to a mission.
     *
     * @param missionId the mission ID
     * @param waypoint the waypoint to add
     * @return created waypoint
     */
    @PostMapping("/{missionId}/waypoints")
    public ResponseEntity<MissionWaypoint> addWaypoint(@PathVariable Long missionId, @RequestBody MissionWaypoint waypoint) {
        try {
            MissionWaypoint createdWaypoint = missionService.addWaypoint(missionId, waypoint);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdWaypoint);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Gets waypoints for a mission.
     *
     * @param missionId the mission ID
     * @return list of waypoints
     */
    @GetMapping("/{missionId}/waypoints")
    public ResponseEntity<List<MissionWaypoint>> getMissionWaypoints(@PathVariable Long missionId) {
        try {
            List<MissionWaypoint> waypoints = missionService.getMissionWaypoints(missionId);
            return ResponseEntity.ok(waypoints);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Adds a report to a mission.
     *
     * @param missionId the mission ID
     * @param report the report to add
     * @return created report
     */
    @PostMapping("/{missionId}/reports")
    public ResponseEntity<MissionReport> addReport(@PathVariable Long missionId, @RequestBody MissionReport report) {
        try {
            MissionReport createdReport = missionService.addReport(missionId, report);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdReport);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Gets reports for a mission.
     *
     * @param missionId the mission ID
     * @return list of reports
     */
    @GetMapping("/{missionId}/reports")
    public ResponseEntity<List<MissionReport>> getMissionReports(@PathVariable Long missionId) {
        try {
            List<MissionReport> reports = missionService.getMissionReports(missionId);
            return ResponseEntity.ok(reports);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Gets mission progress calculation.
     *
     * @param missionId the mission ID
     * @return progress percentage
     */
    @GetMapping("/{missionId}/progress")
    public ResponseEntity<Integer> getMissionProgress(@PathVariable Long missionId) {
        try {
            int progress = missionService.calculateMissionProgress(missionId);
            return ResponseEntity.ok(progress);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * Deletes a mission.
     *
     * @param id the mission ID
     * @return no content response
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMission(@PathVariable Long id) {
        try {
            missionService.deleteMission(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
