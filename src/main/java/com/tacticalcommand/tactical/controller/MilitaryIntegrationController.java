package com.tacticalcommand.tactical.controller;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.tacticalcommand.tactical.domain.MilitaryUnit;
import com.tacticalcommand.tactical.service.MilitaryIntegrationService;
import com.tacticalcommand.tactical.service.MilitaryIntegrationService.*;

/**
 * REST Controller for military integration operations providing external system connectivity.
 * Implements GCCS-J style integration patterns with NATO ADatP-3, USMTF, and Link 16 support.
 * 
 * This controller provides endpoints for:
 * - External system connection management (GCCS-J, NATO systems)
 * - Military message format transmission (USMTF, ADatP-3)
 * - Common Operational Picture (COP) data retrieval
 * - Unit status synchronization across systems
 * - System connectivity monitoring and health checks
 * 
 * Security: Requires COMMANDER or ADMIN role for most operations due to sensitivity
 * of external military system integration.
 * 
 * @author Tactical Command Hub Team
 * @version 1.0.0
 * @since 2025-01-29
 */
@RestController
@RequestMapping("/api/integration")
public class MilitaryIntegrationController {

    @Autowired
    private MilitaryIntegrationService integrationService;

    /**
     * Establish connection to external GCCS-J system.
     * Requires ADMIN privileges due to critical system integration nature.
     * 
     * @param request Connection request containing system ID and credentials
     * @return Connection result indicating success/failure
     */
    @PostMapping("/gccs-j/connect")
    @PreAuthorize("hasRole('ADMIN')")
    public CompletableFuture<ResponseEntity<ExternalSystemConnection>> connectToGCCSJ(
            @RequestBody GCCSJConnectionRequest request) {
        
        return integrationService.connectToGCCSJ(request.systemId, request.credentials)
            .thenApply(connection -> {
                if (connection.connected) {
                    return ResponseEntity.ok(connection);
                } else {
                    return ResponseEntity.badRequest().body(connection);
                }
            });
    }

    /**
     * Send USMTF (US Message Text Format) compliant message.
     * Implements MIL-STD-6040B standard for military message exchange.
     * 
     * @param request USMTF message transmission request
     * @return Message transmission result
     */
    @PostMapping("/messages/usmtf")
    @PreAuthorize("hasRole('COMMANDER') or hasRole('ADMIN')")
    public ResponseEntity<MessageTransmissionResult> sendUSMTFMessage(
            @RequestBody USMTFMessageRequest request) {

        MessageTransmissionResult result = integrationService.sendUSMTFMessage(
            request.messageType, request.recipients, request.messageData);
            
        if (result.success) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    /**
     * Send NATO ADatP-3 compliant message using APP-11 message catalogue.
     * Supports allied interoperability with over 400 NATO message types.
     * 
     * @param request NATO message transmission request
     * @return Message transmission result
     */
    @PostMapping("/messages/nato")
    @PreAuthorize("hasRole('COMMANDER') or hasRole('ADMIN')")
    public ResponseEntity<MessageTransmissionResult> sendNATOMessage(
            @RequestBody NATOMessageRequest request) {

        MessageTransmissionResult result = integrationService.sendNATOMessage(
            request.messageType, request.recipients, request.messageData, request.classification);
            
        if (result.success) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    /**
     * Retrieve Common Operational Picture (COP) data from integrated systems.
     * Consolidates tactical picture from GCCS-J, allied systems, and internal data.
     * 
     * @param minLat Minimum latitude for area of interest
     * @param maxLat Maximum latitude for area of interest
     * @param minLon Minimum longitude for area of interest
     * @param maxLon Maximum longitude for area of interest
     * @return Common Operational Picture data
     */
    @GetMapping("/cop")
    @PreAuthorize("hasRole('USER') or hasRole('COMMANDER') or hasRole('ADMIN')")
    public CompletableFuture<ResponseEntity<CommonOperationalPicture>> getCOPData(
            @RequestParam("minLat") double minLat,
            @RequestParam("maxLat") double maxLat,
            @RequestParam("minLon") double minLon,
            @RequestParam("maxLon") double maxLon) {

        GeographicBounds bounds = new GeographicBounds();
        bounds.minLatitude = minLat;
        bounds.maxLatitude = maxLat;
        bounds.minLongitude = minLon;
        bounds.maxLongitude = maxLon;

        return integrationService.retrieveCOPData(bounds)
            .thenApply(ResponseEntity::ok);
    }

    /**
     * Synchronize unit status with external command systems.
     * Ensures tactical unit data consistency across GCCS-J and allied systems.
     * 
     * @param unitId Military unit identifier to synchronize
     * @return Synchronization result
     */
    @PostMapping("/sync/unit/{unitId}")
    @PreAuthorize("hasRole('COMMANDER') or hasRole('ADMIN')")
    public ResponseEntity<SynchronizationResult> synchronizeUnitStatus(@PathVariable Long unitId) {
        
        // Note: This would typically get the unit from repository first
        // For now, creating a minimal implementation
        try {
            // Would need to fetch the actual unit from repository
            MilitaryUnit unit = new MilitaryUnit(); // Placeholder
            unit.setId(unitId);
            
            SynchronizationResult result = integrationService.synchronizeUnitStatus(unit);
            
            if (result.success) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.badRequest().body(result);
            }
        } catch (Exception e) {
            SynchronizationResult errorResult = new SynchronizationResult(unitId, false, 
                "Error: " + e.getMessage(), java.time.LocalDateTime.now());
            return ResponseEntity.internalServerError().body(errorResult);
        }
    }

    /**
     * Check connectivity status of all external integrated systems.
     * Provides health monitoring for GCCS-J, NATO systems, and other integrations.
     * 
     * @return System connectivity status report
     */
    @GetMapping("/status")
    @PreAuthorize("hasRole('COMMANDER') or hasRole('ADMIN')")
    public ResponseEntity<ExternalSystemStatus> getSystemStatus() {
        
        ExternalSystemStatus status = integrationService.checkSystemConnectivity();
        return ResponseEntity.ok(status);
    }

    /**
     * Get list of supported USMTF message types.
     * Returns available message types from MIL-STD-6040B catalogue.
     * 
     * @return List of supported USMTF message types
     */
    @GetMapping("/messages/usmtf/types")
    @PreAuthorize("hasRole('USER') or hasRole('COMMANDER') or hasRole('ADMIN')")
    public ResponseEntity<List<String>> getUSMTFMessageTypes() {
        
        // Standard USMTF message types from MIL-STD-6040B
        List<String> messageTypes = List.of(
            "ATO",      // Air Tasking Order
            "ACO",      // Airspace Control Order
            "OPSTAT",   // Operational Status
            "SITREP",   // Situation Report
            "INTSUM",   // Intelligence Summary
            "GENSTAT",  // General Status
            "OPTASK",   // Operational Task
            "MISREP",   // Mission Report
            "CASREP",   // Casualty Report
            "LOGREP",   // Logistics Report
            "MEDEVAC",  // Medical Evacuation
            "IEDREP",   // Improvised Explosive Device Report
            "CBRN",     // Chemical, Biological, Radiological, Nuclear
            "SPOTREP"   // Spot Report
        );
        
        return ResponseEntity.ok(messageTypes);
    }

    /**
     * Get list of supported NATO message types from APP-11 catalogue.
     * Returns available NATO ADatP-3 message types for allied interoperability.
     * 
     * @return List of supported NATO message types
     */
    @GetMapping("/messages/nato/types")
    @PreAuthorize("hasRole('USER') or hasRole('COMMANDER') or hasRole('ADMIN')")
    public ResponseEntity<List<String>> getNATOMessageTypes() {
        
        // NATO APP-11 message types (subset of 400+ available)
        List<String> messageTypes = List.of(
            "GENSTAT",  // General Status
            "ORBAT",    // Order of Battle
            "SITSUM",   // Situation Summary
            "OPSUM",    // Operations Summary
            "INTREP",   // Intelligence Report
            "LOGREP",   // Logistics Report
            "CASREP",   // Casualty Report
            "MARREP",   // Maritime Report
            "AIRREP",   // Air Report
            "LANDREP",  // Land Report
            "FFILINK",  // Friendly Force Information
            "TACLINK",  // Tactical Link
            "COMREP",   // Communications Report
            "ENVIRO"    // Environmental Report
        );
        
        return ResponseEntity.ok(messageTypes);
    }

    // Request DTOs for API endpoints

    public static class GCCSJConnectionRequest {
        public String systemId;
        public SystemCredentials credentials;
    }

    public static class USMTFMessageRequest {
        public String messageType;
        public List<String> recipients;
        public Map<String, Object> messageData;
    }

    public static class NATOMessageRequest {
        public String messageType;
        public List<String> recipients;
        public Map<String, Object> messageData;
        public String classification;
    }
}
