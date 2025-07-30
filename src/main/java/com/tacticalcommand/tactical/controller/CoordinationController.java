package com.tacticalcommand.tactical.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tacticalcommand.tactical.service.CoordinationService;
import com.tacticalcommand.tactical.service.CoordinationService.*;

/**
 * REST controller for multi-unit coordination operations.
 * Provides endpoints for resource allocation, unit positioning, and operational synchronization.
 */
@RestController
@RequestMapping("/api/coordination")
@CrossOrigin(origins = "*")
public class CoordinationController {

    @Autowired
    private CoordinationService coordinationService;

    /**
     * Coordinates multiple units for a joint mission.
     *
     * @param missionId the mission ID
     * @param unitIds list of unit IDs to coordinate
     * @return coordination result
     */
    @PostMapping("/mission/{missionId}/coordinate")
    public ResponseEntity<CoordinationResult> coordinateUnitsForMission(
            @PathVariable Long missionId,
            @RequestBody List<Long> unitIds) {
        try {
            CoordinationResult result = coordinationService.coordinateUnitsForMission(missionId, unitIds);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Allocates resources across multiple units for optimal mission execution.
     *
     * @param missionId the mission ID
     * @param resourceRequirements map of resource types to quantities needed
     * @return resource allocation result
     */
    @PostMapping("/mission/{missionId}/allocate-resources")
    public ResponseEntity<ResourceAllocationResult> allocateResources(
            @PathVariable Long missionId,
            @RequestBody Map<String, Integer> resourceRequirements) {
        try {
            ResourceAllocationResult result = coordinationService.allocateResources(missionId, resourceRequirements);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Finds optimal unit positioning for mission objectives.
     *
     * @param missionId the mission ID
     * @return positioning recommendations
     */
    @PostMapping("/mission/{missionId}/optimize-positioning")
    public ResponseEntity<UnitPositioningResult> optimizeUnitPositioning(@PathVariable Long missionId) {
        try {
            UnitPositioningResult result = coordinationService.optimizeUnitPositioning(missionId);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Synchronizes operations across multiple units.
     *
     * @param missionId the mission ID
     * @param synchronizationPoint the time point for synchronization
     * @return synchronization result
     */
    @PostMapping("/mission/{missionId}/synchronize")
    public ResponseEntity<SynchronizationResult> synchronizeOperations(
            @PathVariable Long missionId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime synchronizationPoint) {
        try {
            SynchronizationResult result = coordinationService.synchronizeOperations(missionId, synchronizationPoint);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Gets communication matrix between units.
     *
     * @param unitIds list of unit IDs
     * @return communication matrix
     */
    @PostMapping("/communication-matrix")
    public ResponseEntity<CommunicationMatrix> getCommunicationMatrix(@RequestBody List<Long> unitIds) {
        try {
            CommunicationMatrix matrix = coordinationService.getCommunicationMatrix(unitIds);
            return ResponseEntity.ok(matrix);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Gets available resource types and their typical quantities.
     *
     * @return resource information
     */
    @GetMapping("/resources/types")
    public ResponseEntity<Map<String, String>> getResourceTypes() {
        Map<String, String> resourceTypes = Map.of(
            "personnel", "Available personnel for deployment",
            "vehicles", "Transport and combat vehicles",
            "equipment", "Military equipment and weapons",
            "supplies", "Food, ammunition, and other supplies"
        );
        return ResponseEntity.ok(resourceTypes);
    }

    /**
     * Gets coordination status for a mission.
     *
     * @param missionId the mission ID
     * @return coordination status summary
     */
    @GetMapping("/mission/{missionId}/status")
    public ResponseEntity<Map<String, Object>> getCoordinationStatus(@PathVariable Long missionId) {
        try {
            // Basic status - could be expanded with more detailed coordination tracking
            Map<String, Object> status = Map.of(
                "missionId", missionId,
                "message", "Coordination status endpoint - would contain real-time coordination details",
                "timestamp", LocalDateTime.now()
            );
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Gets recommended coordination protocols based on mission type.
     *
     * @param missionType the type of mission
     * @return recommended protocols
     */
    @GetMapping("/protocols/{missionType}")
    public ResponseEntity<List<String>> getCoordinationProtocols(@PathVariable String missionType) {
        List<String> protocols = switch (missionType.toLowerCase()) {
            case "reconnaissance" -> List.of(
                "Establish secure communication channels",
                "Coordinate observation posts",
                "Plan extraction routes",
                "Set up regular reporting intervals"
            );
            case "assault" -> List.of(
                "Synchronize assault timing",
                "Coordinate fire support",
                "Establish casualty evacuation procedures",
                "Plan ammunition resupply"
            );
            case "defense" -> List.of(
                "Position defensive units",
                "Coordinate overlapping fields of fire",
                "Establish fallback positions",
                "Plan reinforcement deployment"
            );
            case "patrol" -> List.of(
                "Define patrol routes",
                "Set communication check-in times",
                "Coordinate with adjacent units",
                "Plan emergency response procedures"
            );
            default -> List.of(
                "Establish command and control",
                "Coordinate unit movements",
                "Plan communication protocols",
                "Set up logistics support"
            );
        };
        return ResponseEntity.ok(protocols);
    }

    /**
     * Validates coordination parameters before execution.
     *
     * @param missionId the mission ID
     * @param unitIds list of unit IDs
     * @return validation result
     */
    @PostMapping("/mission/{missionId}/validate")
    public ResponseEntity<Map<String, Object>> validateCoordination(
            @PathVariable Long missionId,
            @RequestBody List<Long> unitIds) {
        try {
            // Basic validation - in a real system would check unit availability,
            // compatibility, geographic constraints, etc.
            boolean isValid = unitIds != null && !unitIds.isEmpty() && missionId != null;
            
            Map<String, Object> result = Map.of(
                "valid", isValid,
                "missionId", missionId,
                "unitCount", unitIds != null ? unitIds.size() : 0,
                "message", isValid ? "Coordination parameters are valid" : "Invalid coordination parameters",
                "timestamp", LocalDateTime.now()
            );
            
            HttpStatus status = isValid ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
            return ResponseEntity.status(status).body(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
