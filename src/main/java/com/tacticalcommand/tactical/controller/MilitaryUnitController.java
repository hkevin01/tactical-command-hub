package com.caribouthunder.tactical.controller;

import com.caribouthunder.tactical.domain.MilitaryUnit;
import com.caribouthunder.tactical.service.MilitaryUnitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST Controller for Military Unit operations.
 * 
 * Provides endpoints for military unit management including CRUD operations,
 * position tracking, status management, and operational queries.
 * 
 * @author Tactical Command Hub Team
 * @version 1.0.0
 * @since 2025-07-29
 */
@RestController
@RequestMapping("/units")
@Tag(name = "Military Units", description = "Military unit management operations")
@CrossOrigin(origins = "*", maxAge = 3600)
public class MilitaryUnitController {

    private final MilitaryUnitService militaryUnitService;

    @Autowired
    public MilitaryUnitController(MilitaryUnitService militaryUnitService) {
        this.militaryUnitService = militaryUnitService;
    }

    @Operation(summary = "Create a new military unit", description = "Creates a new military unit with the provided details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Unit created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid unit data or callsign already exists"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('COMMANDER')")
    public ResponseEntity<MilitaryUnit> createUnit(@Valid @RequestBody MilitaryUnit unit) {
        try {
            MilitaryUnit createdUnit = militaryUnitService.createUnit(unit);
            return new ResponseEntity<>(createdUnit, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Get unit by ID", description = "Retrieves a military unit by its unique identifier")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Unit found"),
        @ApiResponse(responseCode = "404", description = "Unit not found")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('COMMANDER') or hasRole('ADMIN')")
    public ResponseEntity<MilitaryUnit> getUnitById(
            @Parameter(description = "Unit ID") @PathVariable Long id) {
        Optional<MilitaryUnit> unit = militaryUnitService.getUnitById(id);
        return unit.map(u -> ResponseEntity.ok().body(u))
                  .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get unit by callsign", description = "Retrieves a military unit by its callsign")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Unit found"),
        @ApiResponse(responseCode = "404", description = "Unit not found")
    })
    @GetMapping("/callsign/{callsign}")
    @PreAuthorize("hasRole('USER') or hasRole('COMMANDER') or hasRole('ADMIN')")
    public ResponseEntity<MilitaryUnit> getUnitByCallsign(
            @Parameter(description = "Unit callsign") @PathVariable String callsign) {
        Optional<MilitaryUnit> unit = militaryUnitService.getUnitByCallsign(callsign);
        return unit.map(u -> ResponseEntity.ok().body(u))
                  .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get all units", description = "Retrieves all military units with pagination")
    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('COMMANDER') or hasRole('ADMIN')")
    public ResponseEntity<Page<MilitaryUnit>> getAllUnits(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "callsign") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<MilitaryUnit> units = militaryUnitService.getAllUnits(pageable);
        return ResponseEntity.ok(units);
    }

    @Operation(summary = "Get active units", description = "Retrieves all active military units")
    @GetMapping("/active")
    @PreAuthorize("hasRole('USER') or hasRole('COMMANDER') or hasRole('ADMIN')")
    public ResponseEntity<List<MilitaryUnit>> getActiveUnits() {
        List<MilitaryUnit> units = militaryUnitService.getActiveUnits();
        return ResponseEntity.ok(units);
    }

    @Operation(summary = "Get units by status", description = "Retrieves military units by their current status")
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('USER') or hasRole('COMMANDER') or hasRole('ADMIN')")
    public ResponseEntity<List<MilitaryUnit>> getUnitsByStatus(
            @Parameter(description = "Unit status") @PathVariable MilitaryUnit.UnitStatus status) {
        List<MilitaryUnit> units = militaryUnitService.getUnitsByStatus(status);
        return ResponseEntity.ok(units);
    }

    @Operation(summary = "Get units by domain", description = "Retrieves military units by operational domain")
    @GetMapping("/domain/{domain}")
    @PreAuthorize("hasRole('USER') or hasRole('COMMANDER') or hasRole('ADMIN')")
    public ResponseEntity<List<MilitaryUnit>> getUnitsByDomain(
            @Parameter(description = "Operational domain") @PathVariable MilitaryUnit.OperationalDomain domain) {
        List<MilitaryUnit> units = militaryUnitService.getUnitsByDomain(domain);
        return ResponseEntity.ok(units);
    }

    @Operation(summary = "Get mission capable units", description = "Retrieves units with C1 or C2 readiness levels")
    @GetMapping("/mission-capable")
    @PreAuthorize("hasRole('USER') or hasRole('COMMANDER') or hasRole('ADMIN')")
    public ResponseEntity<List<MilitaryUnit>> getMissionCapableUnits() {
        List<MilitaryUnit> units = militaryUnitService.getMissionCapableUnits();
        return ResponseEntity.ok(units);
    }

    @Operation(summary = "Get units within radius", description = "Retrieves units within a geographical radius")
    @GetMapping("/radius")
    @PreAuthorize("hasRole('USER') or hasRole('COMMANDER') or hasRole('ADMIN')")
    public ResponseEntity<List<MilitaryUnit>> getUnitsWithinRadius(
            @Parameter(description = "Center latitude") @RequestParam BigDecimal lat,
            @Parameter(description = "Center longitude") @RequestParam BigDecimal lon,
            @Parameter(description = "Radius in kilometers") @RequestParam Double radius) {
        List<MilitaryUnit> units = militaryUnitService.getUnitsWithinRadius(lat, lon, radius);
        return ResponseEntity.ok(units);
    }

    @Operation(summary = "Search units", description = "Search units by callsign or name")
    @GetMapping("/search")
    @PreAuthorize("hasRole('USER') or hasRole('COMMANDER') or hasRole('ADMIN')")
    public ResponseEntity<Page<MilitaryUnit>> searchUnits(
            @Parameter(description = "Search term") @RequestParam String q,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<MilitaryUnit> units = militaryUnitService.searchUnits(q, pageable);
        return ResponseEntity.ok(units);
    }

    @Operation(summary = "Update unit", description = "Updates an existing military unit")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Unit updated successfully"),
        @ApiResponse(responseCode = "404", description = "Unit not found"),
        @ApiResponse(responseCode = "400", description = "Invalid unit data")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COMMANDER')")
    public ResponseEntity<MilitaryUnit> updateUnit(
            @Parameter(description = "Unit ID") @PathVariable Long id,
            @Valid @RequestBody MilitaryUnit unit) {
        try {
            MilitaryUnit updatedUnit = militaryUnitService.updateUnit(id, unit);
            return ResponseEntity.ok(updatedUnit);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Update unit status", description = "Updates the status of a military unit")
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COMMANDER')")
    public ResponseEntity<MilitaryUnit> updateUnitStatus(
            @Parameter(description = "Unit ID") @PathVariable Long id,
            @Parameter(description = "New status") @RequestParam MilitaryUnit.UnitStatus status,
            @Parameter(description = "Change reason") @RequestParam(required = false) String reason,
            @Parameter(description = "Changed by") @RequestParam String changedBy) {
        try {
            MilitaryUnit updatedUnit = militaryUnitService.updateUnitStatus(id, status, reason, changedBy);
            return ResponseEntity.ok(updatedUnit);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Update unit position", description = "Updates the position of a military unit")
    @PutMapping("/{id}/position")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COMMANDER') or hasRole('OPERATOR')")
    public ResponseEntity<MilitaryUnit> updateUnitPosition(
            @Parameter(description = "Unit ID") @PathVariable Long id,
            @Parameter(description = "Latitude") @RequestParam BigDecimal lat,
            @Parameter(description = "Longitude") @RequestParam BigDecimal lon,
            @Parameter(description = "Altitude") @RequestParam(required = false) BigDecimal alt) {
        try {
            MilitaryUnit updatedUnit = militaryUnitService.updateUnitPosition(id, lat, lon, alt);
            return ResponseEntity.ok(updatedUnit);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Update unit movement", description = "Updates the movement data of a military unit")
    @PutMapping("/{id}/movement")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COMMANDER') or hasRole('OPERATOR')")
    public ResponseEntity<MilitaryUnit> updateUnitMovement(
            @Parameter(description = "Unit ID") @PathVariable Long id,
            @Parameter(description = "Heading in degrees") @RequestParam BigDecimal heading,
            @Parameter(description = "Speed") @RequestParam BigDecimal speed) {
        try {
            MilitaryUnit updatedUnit = militaryUnitService.updateUnitMovement(id, heading, speed);
            return ResponseEntity.ok(updatedUnit);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Update readiness level", description = "Updates the readiness level of a military unit")
    @PutMapping("/{id}/readiness")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COMMANDER')")
    public ResponseEntity<MilitaryUnit> updateReadinessLevel(
            @Parameter(description = "Unit ID") @PathVariable Long id,
            @Parameter(description = "Readiness level") @RequestParam MilitaryUnit.ReadinessLevel readiness) {
        try {
            MilitaryUnit updatedUnit = militaryUnitService.updateReadinessLevel(id, readiness);
            return ResponseEntity.ok(updatedUnit);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Update threat level", description = "Updates the threat level of a military unit")
    @PutMapping("/{id}/threat")
    @PreAuthorize("hasRole('ADMIN') or hasRole('COMMANDER')")
    public ResponseEntity<MilitaryUnit> updateThreatLevel(
            @Parameter(description = "Unit ID") @PathVariable Long id,
            @Parameter(description = "Threat level") @RequestParam MilitaryUnit.ThreatLevel threat) {
        try {
            MilitaryUnit updatedUnit = militaryUnitService.updateThreatLevel(id, threat);
            return ResponseEntity.ok(updatedUnit);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Delete unit", description = "Deletes a military unit")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Unit deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Unit not found")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUnit(@Parameter(description = "Unit ID") @PathVariable Long id) {
        try {
            militaryUnitService.deleteUnit(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Get unit statistics", description = "Retrieves unit statistics by various categories")
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('USER') or hasRole('COMMANDER') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getUnitStatistics() {
        Map<String, Object> statistics = new java.util.HashMap<>();
        statistics.put("byStatus", militaryUnitService.getUnitStatusStatistics());
        statistics.put("byDomain", militaryUnitService.getUnitDomainStatistics());
        statistics.put("byReadiness", militaryUnitService.getUnitReadinessStatistics());
        return ResponseEntity.ok(statistics);
    }

    @Operation(summary = "Get units with no recent contact", description = "Retrieves units that haven't made contact within specified hours")
    @GetMapping("/no-contact")
    @PreAuthorize("hasRole('USER') or hasRole('COMMANDER') or hasRole('ADMIN')")
    public ResponseEntity<List<MilitaryUnit>> getUnitsWithNoRecentContact(
            @Parameter(description = "Hours since last contact") @RequestParam(defaultValue = "24") int hours) {
        List<MilitaryUnit> units = militaryUnitService.getUnitsWithNoRecentContact(hours);
        return ResponseEntity.ok(units);
    }
}
