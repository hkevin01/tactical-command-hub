package com.tacticalcommand.tactical.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.tacticalcommand.tactical.domain.Mission;
import com.tacticalcommand.tactical.service.MissionWorkflowService;
import com.tacticalcommand.tactical.service.MissionWorkflowService.MissionPlanningSession;
import com.tacticalcommand.tactical.service.MissionWorkflowService.ResourceAllocationResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * REST Controller for Mission Workflow Management.
 * Provides endpoints for collaborative mission planning, workflow advancement,
 * resource allocation, and risk assessment capabilities.
 */
@RestController
@RequestMapping("/api/v1/missions/workflow")
@Tag(name = "Mission Workflow", description = "Advanced mission workflow and collaboration operations")
@SecurityRequirement(name = "bearerAuth")
public class MissionWorkflowController {

    @Autowired
    private MissionWorkflowService missionWorkflowService;

    /**
     * Initiates collaborative mission planning session.
     */
    @PostMapping("/{missionId}/planning/initiate")
    @PreAuthorize("hasRole('USER')")
    @Operation(
        summary = "Initiate Mission Planning Session",
        description = "Creates a new collaborative planning session for the specified mission"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Planning session created successfully"),
        @ApiResponse(responseCode = "404", description = "Mission not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<MissionPlanningSession> initiateMissionPlanning(
            @Parameter(description = "Mission ID") @PathVariable Long missionId,
            @Parameter(description = "User ID") @RequestParam String userId) {
        
        try {
            MissionPlanningSession session = missionWorkflowService.initiateMissionPlanning(missionId, userId);
            return new ResponseEntity<>(session, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Joins existing collaborative planning session.
     */
    @PostMapping("/{missionId}/planning/join")
    @PreAuthorize("hasRole('USER')")
    @Operation(
        summary = "Join Planning Session",
        description = "Allows a user to join an existing collaborative planning session"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully joined planning session"),
        @ApiResponse(responseCode = "404", description = "Planning session not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<MissionPlanningSession> joinPlanningSession(
            @Parameter(description = "Mission ID") @PathVariable Long missionId,
            @Parameter(description = "User ID") @RequestParam String userId) {
        
        try {
            MissionPlanningSession session = missionWorkflowService.joinPlanningSession(missionId, userId);
            return ResponseEntity.ok(session);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Updates mission collaboratively with version control.
     */
    @PutMapping("/{missionId}/planning/update")
    @PreAuthorize("hasRole('USER')")
    @Operation(
        summary = "Collaborative Mission Update",
        description = "Updates mission data through collaborative planning session with version control"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Mission updated successfully"),
        @ApiResponse(responseCode = "400", description = "User not part of collaborative session"),
        @ApiResponse(responseCode = "404", description = "Mission or session not found")
    })
    public ResponseEntity<Mission> updateMissionCollaboratively(
            @Parameter(description = "Mission ID") @PathVariable Long missionId,
            @Parameter(description = "Updated mission data") @RequestBody Mission updatedMission,
            @Parameter(description = "User ID") @RequestParam String userId) {
        
        try {
            Mission updated = missionWorkflowService.updateMissionCollaboratively(missionId, updatedMission, userId);
            return ResponseEntity.ok(updated);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Performs automated resource allocation for mission.
     */
    @PostMapping("/{missionId}/resource-allocation")
    @PreAuthorize("hasRole('COMMANDER')")
    @Operation(
        summary = "Perform Resource Allocation",
        description = "Executes automated resource allocation algorithm for the mission"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Resource allocation completed"),
        @ApiResponse(responseCode = "404", description = "Mission not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - requires COMMANDER role")
    })
    public ResponseEntity<ResourceAllocationResult> performResourceAllocation(
            @Parameter(description = "Mission ID") @PathVariable Long missionId) {
        
        try {
            ResourceAllocationResult result = missionWorkflowService.performResourceAllocation(missionId);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Advances mission workflow to next stage.
     */
    @PostMapping("/{missionId}/workflow/advance")
    @PreAuthorize("hasRole('COMMANDER')")
    @Operation(
        summary = "Advance Mission Workflow",
        description = "Moves mission to next workflow stage based on available actions"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Workflow advanced successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid workflow action"),
        @ApiResponse(responseCode = "404", description = "Mission or workflow state not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - requires COMMANDER role")
    })
    public ResponseEntity<Mission> advanceMissionWorkflow(
            @Parameter(description = "Mission ID") @PathVariable Long missionId,
            @Parameter(description = "Workflow action to execute") @RequestParam String action,
            @Parameter(description = "User ID") @RequestParam String userId) {
        
        try {
            Mission updatedMission = missionWorkflowService.advanceMissionWorkflow(missionId, action, userId);
            return ResponseEntity.ok(updatedMission);
        } catch (IllegalStateException | IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Generates comprehensive mission risk assessment.
     */
    @GetMapping("/{missionId}/risk-assessment")
    @PreAuthorize("hasRole('USER')")
    @Operation(
        summary = "Generate Risk Assessment",
        description = "Produces comprehensive risk analysis for the mission including timeline, resource, and environmental factors"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Risk assessment generated successfully"),
        @ApiResponse(responseCode = "404", description = "Mission not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<Map<String, Object>> generateMissionRiskAssessment(
            @Parameter(description = "Mission ID") @PathVariable Long missionId) {
        
        try {
            Map<String, Object> riskAssessment = missionWorkflowService.generateMissionRiskAssessment(missionId);
            return ResponseEntity.ok(riskAssessment);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves workflow actions available for mission in current state.
     */
    @GetMapping("/{missionId}/workflow/available-actions")
    @PreAuthorize("hasRole('USER')")
    @Operation(
        summary = "Get Available Workflow Actions",
        description = "Returns list of workflow actions available for mission in its current state"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Available actions retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Mission workflow state not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<List<String>> getAvailableWorkflowActions(
            @Parameter(description = "Mission ID") @PathVariable Long missionId) {
        
        try {
            // This would need to be implemented in the service layer
            List<String> availableActions = missionWorkflowService.getAvailableWorkflowActions(missionId);
            return ResponseEntity.ok(availableActions);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieves active planning session information.
     */
    @GetMapping("/{missionId}/planning/session")
    @PreAuthorize("hasRole('USER')")
    @Operation(
        summary = "Get Planning Session Info",
        description = "Retrieves information about active collaborative planning session"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Session information retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "No active planning session found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<MissionPlanningSession> getPlanningSessionInfo(
            @Parameter(description = "Mission ID") @PathVariable Long missionId) {
        
        try {
            MissionPlanningSession session = missionWorkflowService.getPlanningSessionInfo(missionId);
            return ResponseEntity.ok(session);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Closes collaborative planning session.
     */
    @DeleteMapping("/{missionId}/planning/session")
    @PreAuthorize("hasRole('COMMANDER')")
    @Operation(
        summary = "Close Planning Session",
        description = "Terminates active collaborative planning session"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Planning session closed successfully"),
        @ApiResponse(responseCode = "404", description = "No active planning session found"),
        @ApiResponse(responseCode = "403", description = "Access denied - requires COMMANDER role")
    })
    public ResponseEntity<Void> closePlanningSession(
            @Parameter(description = "Mission ID") @PathVariable Long missionId,
            @Parameter(description = "User ID") @RequestParam String userId) {
        
        try {
            missionWorkflowService.closePlanningSession(missionId, userId);
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
