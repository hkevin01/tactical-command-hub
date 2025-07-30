package com.caribouthunder.tactical.service.mission;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.caribouthunder.tactical.service.unit.UnitManagementService;
import com.tacticalcommand.tactical.domain.Mission;
import com.tacticalcommand.tactical.domain.Mission.MissionStatus;
import com.tacticalcommand.tactical.repository.MissionRepository;

/**
 * Mission Planning Service with Spring State Machine Integration.
 * 
 * Implements comprehensive mission planning and coordination capabilities with
 * state machine-driven workflow management. Provides mission lifecycle control,
 * resource allocation coordination, and planning session management.
 * 
 * State Machine Flow: PLANNING → APPROVED → EXECUTING → COMPLETED → CANCELLED
 * 
 * @author Tactical Command Hub - Phase 3 Implementation
 * @version 1.0.0
 * @since 2025-01-27
 */
@Service
@Transactional
public class MissionPlanningService {

    @Autowired
    private MissionRepository missionRepository;

    @Autowired
    private UnitManagementService unitManagementService;

    // Mission planning sessions with internal state management (replaces StateMachine)
    private final Map<Long, MissionPlanningSession> planningSessionsMap = 
        new ConcurrentHashMap<>();

    /**
     * Mission Planning State Enumeration.
     * Defines the possible states in the mission planning workflow.
     */
    public enum MissionPlanState {
        PLANNING,       // Initial planning phase
        APPROVED,       // Mission approved and ready for execution
        EXECUTING,      // Mission is being executed
        COMPLETED,      // Mission successfully completed
        CANCELLED       // Mission cancelled at any stage
    }

    /**
     * Mission Planning Event Enumeration.
     * Defines the events that trigger state transitions.
     */
    public enum MissionPlanEvent {
        APPROVE,        // Approve the mission plan
        START_EXECUTION, // Begin mission execution
        COMPLETE,       // Complete the mission
        CANCEL,         // Cancel the mission
        SUSPEND,        // Suspend execution (returns to APPROVED)
        RESUME          // Resume from suspension
    }

    /**
     * Mission Planning Session.
     * Represents an active collaborative planning session for a mission.
     */
    public static class MissionPlanningSession {
        private Long missionId;
        private String sessionId;
        private MissionPlanState currentState;
        private LocalDateTime createdAt;
        private LocalDateTime lastModified;
        private List<String> participants;
        private Map<String, Object> planningData;
        private Map<String, Object> resourceRequirements;
        private List<String> approvalComments;
        private String assignedCommander;

        public MissionPlanningSession(Long missionId) {
            this.missionId = missionId;
            this.sessionId = UUID.randomUUID().toString();
            this.currentState = MissionPlanState.PLANNING;
            this.createdAt = LocalDateTime.now();
            this.lastModified = LocalDateTime.now();
            this.participants = new ArrayList<>();
            this.planningData = new HashMap<>();
            this.resourceRequirements = new HashMap<>();
            this.approvalComments = new ArrayList<>();
        }

        // Getters and setters
        public Long getMissionId() { return missionId; }
        public String getSessionId() { return sessionId; }
        public MissionPlanState getCurrentState() { return currentState; }
        public void setCurrentState(MissionPlanState currentState) { 
            this.currentState = currentState; 
            this.lastModified = LocalDateTime.now();
        }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public LocalDateTime getLastModified() { return lastModified; }
        public List<String> getParticipants() { return participants; }
        public Map<String, Object> getPlanningData() { return planningData; }
        public Map<String, Object> getResourceRequirements() { return resourceRequirements; }
        public List<String> getApprovalComments() { return approvalComments; }
        public String getAssignedCommander() { return assignedCommander; }
        public void setAssignedCommander(String assignedCommander) { this.assignedCommander = assignedCommander; }
        
        public void updateLastModified() {
            this.lastModified = LocalDateTime.now();
        }
    }

    /**
     * Mission State Transition Result.
     * Contains the result of a state machine transition attempt.
     */
    public static class StateTransitionResult {
        private boolean successful;
        private MissionPlanState previousState;
        private MissionPlanState newState;
        private String errorMessage;
        private Map<String, Object> transitionData;

        public StateTransitionResult(boolean successful, MissionPlanState previousState, 
                                   MissionPlanState newState) {
            this.successful = successful;
            this.previousState = previousState;
            this.newState = newState;
            this.transitionData = new HashMap<>();
        }

        // Getters and setters
        public boolean isSuccessful() { return successful; }
        public MissionPlanState getPreviousState() { return previousState; }
        public MissionPlanState getNewState() { return newState; }
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
        public Map<String, Object> getTransitionData() { return transitionData; }
    }

    /**
     * Resource Allocation Assessment.
     * Evaluates and tracks resource requirements for mission planning.
     */
    public static class ResourceAllocationAssessment {
        private Long missionId;
        private List<Long> requiredUnitIds;
        private Map<String, Integer> equipmentRequirements;
        private Map<String, Object> logisticsRequirements;
        private double readinessScore;
        private List<String> riskFactors;
        private List<String> recommendations;

        public ResourceAllocationAssessment(Long missionId) {
            this.missionId = missionId;
            this.requiredUnitIds = new ArrayList<>();
            this.equipmentRequirements = new HashMap<>();
            this.logisticsRequirements = new HashMap<>();
            this.riskFactors = new ArrayList<>();
            this.recommendations = new ArrayList<>();
            this.readinessScore = 0.0;
        }

        // Getters and setters
        public Long getMissionId() { return missionId; }
        public List<Long> getRequiredUnitIds() { return requiredUnitIds; }
        public Map<String, Integer> getEquipmentRequirements() { return equipmentRequirements; }
        public Map<String, Object> getLogisticsRequirements() { return logisticsRequirements; }
        public double getReadinessScore() { return readinessScore; }
        public void setReadinessScore(double readinessScore) { this.readinessScore = readinessScore; }
        public List<String> getRiskFactors() { return riskFactors; }
        public List<String> getRecommendations() { return recommendations; }
    }

    // ==== MISSION PLANNING WORKFLOW METHODS ====

    /**
     * Initiates a new mission planning session.
     * Creates a new planning session and initializes the state machine.
     * 
     * @param missionId the mission ID to plan
     * @param initiatorId the user ID initiating the planning
     * @return the created planning session
     */
    public MissionPlanningSession initiateMissionPlanning(Long missionId, String initiatorId) {
        Optional<Mission> missionOpt = missionRepository.findById(missionId);
        if (!missionOpt.isPresent()) {
            throw new IllegalArgumentException("Mission not found: " + missionId);
        }

        Mission mission = missionOpt.get();
        
        // Validate that mission is in a plannable state
        if (mission.getStatus() != MissionStatus.PLANNING) {
            throw new IllegalStateException("Mission is not in PLANNING state: " + mission.getStatus());
        }

        // Create planning session
        MissionPlanningSession session = new MissionPlanningSession(missionId);
        session.getParticipants().add(initiatorId);
        session.setAssignedCommander(initiatorId);

        // Initialize resource requirements
        initializeResourceRequirements(session, mission);

        // Store session
        planningSessionsMap.put(missionId, session);

        return session;
    }

    /**
     * Adds a participant to an active planning session.
     * 
     * @param missionId the mission ID
     * @param participantId the participant user ID
     * @return the updated planning session
     */
    public MissionPlanningSession addPlanningParticipant(Long missionId, String participantId) {
        MissionPlanningSession session = planningSessionsMap.get(missionId);
        if (session == null) {
            throw new IllegalStateException("No active planning session for mission: " + missionId);
        }

        if (!session.getParticipants().contains(participantId)) {
            session.getParticipants().add(participantId);
            session.updateLastModified();
        }

        return session;
    }

    /**
     * Updates planning data for a mission.
     * 
     * @param missionId the mission ID
     * @param planningUpdates the planning data updates
     * @param updatedBy the user making the updates
     * @return the updated planning session
     */
    public MissionPlanningSession updateMissionPlanningData(Long missionId, 
                                                           Map<String, Object> planningUpdates, 
                                                           String updatedBy) {
        MissionPlanningSession session = planningSessionsMap.get(missionId);
        if (session == null) {
            throw new IllegalStateException("No active planning session for mission: " + missionId);
        }

        if (!session.getParticipants().contains(updatedBy)) {
            throw new IllegalArgumentException("User not authorized to update planning data: " + updatedBy);
        }

        // Update planning data
        session.getPlanningData().putAll(planningUpdates);
        session.updateLastModified();

        // Update resource requirements if provided
        if (planningUpdates.containsKey("resourceRequirements")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> resourceUpdates = (Map<String, Object>) planningUpdates.get("resourceRequirements");
            session.getResourceRequirements().putAll(resourceUpdates);
        }

        return session;
    }

    /**
     * Performs resource allocation assessment for mission planning.
     * 
     * @param missionId the mission ID
     * @return resource allocation assessment
     */
    public ResourceAllocationAssessment performResourceAllocation(Long missionId) {
        Optional<Mission> missionOpt = missionRepository.findById(missionId);
        if (!missionOpt.isPresent()) {
            throw new IllegalArgumentException("Mission not found: " + missionId);
        }

        Mission mission = missionOpt.get();
        ResourceAllocationAssessment assessment = new ResourceAllocationAssessment(missionId);

        // Get planning session for additional context
        MissionPlanningSession session = planningSessionsMap.get(missionId);
        
        // Calculate unit requirements based on mission type and priority
        calculateUnitRequirements(assessment, mission, session);
        
        // Calculate equipment requirements
        calculateEquipmentRequirements(assessment, mission, session);
        
        // Calculate logistics requirements
        calculateLogisticsRequirements(assessment, mission, session);
        
        // Assess readiness and risks
        assessReadinessAndRisks(assessment, mission);

        return assessment;
    }

    /**
     * Attempts to approve a mission plan.
     * Transitions the state machine from PLANNING to APPROVED.
     * 
     * @param missionId the mission ID
     * @param approverId the user ID of the approver
     * @param approvalComments optional approval comments
     * @return state transition result
     */
    public StateTransitionResult approveMissionPlan(Long missionId, String approverId, 
                                                   String approvalComments) {
        MissionPlanningSession session = planningSessionsMap.get(missionId);
        if (session == null) {
            throw new IllegalStateException("No active planning session for mission: " + missionId);
        }

        if (session.getCurrentState() != MissionPlanState.PLANNING) {
            throw new IllegalStateException("Mission is not in PLANNING state: " + session.getCurrentState());
        }

        // Validate resource allocation
        ResourceAllocationAssessment assessment = performResourceAllocation(missionId);
        if (assessment.getReadinessScore() < 0.7) {
            StateTransitionResult result = new StateTransitionResult(false, 
                session.getCurrentState(), session.getCurrentState());
            result.setErrorMessage("Insufficient readiness score for approval: " + assessment.getReadinessScore());
            return result;
        }

        // Perform state transition
        MissionPlanState previousState = session.getCurrentState();
        session.setCurrentState(MissionPlanState.APPROVED);
        
        // Add approval comments
        if (approvalComments != null && !approvalComments.trim().isEmpty()) {
            session.getApprovalComments().add(approverId + ": " + approvalComments);
        }

        // Update mission status in database
        updateMissionStatus(missionId, MissionStatus.APPROVED);

        StateTransitionResult result = new StateTransitionResult(true, previousState, MissionPlanState.APPROVED);
        result.getTransitionData().put("approverId", approverId);
        result.getTransitionData().put("approvalTime", LocalDateTime.now());
        result.getTransitionData().put("readinessScore", assessment.getReadinessScore());

        return result;
    }

    /**
     * Starts mission execution.
     * Transitions from APPROVED to EXECUTING state.
     * 
     * @param missionId the mission ID
     * @param commanderId the commanding officer ID
     * @return state transition result
     */
    public StateTransitionResult startMissionExecution(Long missionId, String commanderId) {
        MissionPlanningSession session = planningSessionsMap.get(missionId);
        if (session == null) {
            throw new IllegalStateException("No active planning session for mission: " + missionId);
        }

        if (session.getCurrentState() != MissionPlanState.APPROVED) {
            throw new IllegalStateException("Mission is not in APPROVED state: " + session.getCurrentState());
        }

        // Validate commander assignment
        if (!session.getParticipants().contains(commanderId)) {
            throw new IllegalArgumentException("Commander not authorized for this mission: " + commanderId);
        }

        // Perform state transition
        MissionPlanState previousState = session.getCurrentState();
        session.setCurrentState(MissionPlanState.EXECUTING);
        session.setAssignedCommander(commanderId);

        // Update mission status and start time
        updateMissionStatus(missionId, MissionStatus.ACTIVE);
        updateMissionStartTime(missionId);

        StateTransitionResult result = new StateTransitionResult(true, previousState, MissionPlanState.EXECUTING);
        result.getTransitionData().put("commanderId", commanderId);
        result.getTransitionData().put("executionStartTime", LocalDateTime.now());

        return result;
    }

    /**
     * Completes a mission.
     * Transitions from EXECUTING to COMPLETED state.
     * 
     * @param missionId the mission ID
     * @param completedBy the user completing the mission
     * @param completionNotes optional completion notes
     * @return state transition result
     */
    public StateTransitionResult completeMission(Long missionId, String completedBy, 
                                                String completionNotes) {
        MissionPlanningSession session = planningSessionsMap.get(missionId);
        if (session == null) {
            throw new IllegalStateException("No active planning session for mission: " + missionId);
        }

        if (session.getCurrentState() != MissionPlanState.EXECUTING) {
            throw new IllegalStateException("Mission is not in EXECUTING state: " + session.getCurrentState());
        }

        // Perform state transition
        MissionPlanState previousState = session.getCurrentState();
        session.setCurrentState(MissionPlanState.COMPLETED);

        // Update mission status and completion
        updateMissionStatus(missionId, MissionStatus.COMPLETED);
        updateMissionCompletion(missionId, completionNotes);

        StateTransitionResult result = new StateTransitionResult(true, previousState, MissionPlanState.COMPLETED);
        result.getTransitionData().put("completedBy", completedBy);
        result.getTransitionData().put("completionTime", LocalDateTime.now());
        if (completionNotes != null) {
            result.getTransitionData().put("completionNotes", completionNotes);
        }

        // Clean up planning session for completed missions
        planningSessionsMap.remove(missionId);

        return result;
    }

    /**
     * Cancels a mission at any stage.
     * Transitions to CANCELLED state from any other state.
     * 
     * @param missionId the mission ID
     * @param cancelledBy the user cancelling the mission
     * @param cancellationReason the reason for cancellation
     * @return state transition result
     */
    public StateTransitionResult cancelMission(Long missionId, String cancelledBy, 
                                              String cancellationReason) {
        MissionPlanningSession session = planningSessionsMap.get(missionId);
        if (session == null) {
            throw new IllegalStateException("No active planning session for mission: " + missionId);
        }

        if (session.getCurrentState() == MissionPlanState.CANCELLED || 
            session.getCurrentState() == MissionPlanState.COMPLETED) {
            throw new IllegalStateException("Mission is already in terminal state: " + session.getCurrentState());
        }

        // Perform state transition
        MissionPlanState previousState = session.getCurrentState();
        session.setCurrentState(MissionPlanState.CANCELLED);

        // Update mission status
        updateMissionStatus(missionId, MissionStatus.CANCELLED);

        StateTransitionResult result = new StateTransitionResult(true, previousState, MissionPlanState.CANCELLED);
        result.getTransitionData().put("cancelledBy", cancelledBy);
        result.getTransitionData().put("cancellationTime", LocalDateTime.now());
        result.getTransitionData().put("cancellationReason", cancellationReason);

        // Clean up planning session for cancelled missions
        planningSessionsMap.remove(missionId);

        return result;
    }

    /**
     * Gets the current planning session for a mission.
     * 
     * @param missionId the mission ID
     * @return the planning session
     */
    public MissionPlanningSession getPlanningSession(Long missionId) {
        MissionPlanningSession session = planningSessionsMap.get(missionId);
        if (session == null) {
            throw new IllegalStateException("No active planning session for mission: " + missionId);
        }
        return session;
    }

    /**
     * Gets all available state transitions for a mission's current state.
     * 
     * @param missionId the mission ID
     * @return list of available transition events
     */
    public List<MissionPlanEvent> getAvailableTransitions(Long missionId) {
        MissionPlanningSession session = planningSessionsMap.get(missionId);
        if (session == null) {
            return Arrays.asList(); // Empty list if no session
        }

        return switch (session.getCurrentState()) {
            case PLANNING -> Arrays.asList(MissionPlanEvent.APPROVE, MissionPlanEvent.CANCEL);
            case APPROVED -> Arrays.asList(MissionPlanEvent.START_EXECUTION, MissionPlanEvent.CANCEL);
            case EXECUTING -> Arrays.asList(MissionPlanEvent.COMPLETE, MissionPlanEvent.CANCEL, MissionPlanEvent.SUSPEND);
            case COMPLETED, CANCELLED -> Arrays.asList(); // Terminal states
        };
    }

    // ==== PRIVATE HELPER METHODS ====

    /**
     * Initializes resource requirements for a new planning session.
     */
    private void initializeResourceRequirements(MissionPlanningSession session, Mission mission) {
        Map<String, Object> requirements = session.getResourceRequirements();
        
        // Basic requirements based on mission type and priority
        requirements.put("estimatedPersonnel", calculatePersonnelRequirement(mission));
        requirements.put("estimatedVehicles", calculateVehicleRequirement(mission));
        requirements.put("estimatedEquipment", calculateEquipmentRequirement(mission));
        requirements.put("estimatedDuration", calculateDurationRequirement(mission));
        
        // Initialize planning data with mission context
        session.getPlanningData().put("missionType", mission.getMissionType());
        session.getPlanningData().put("priority", mission.getPriority());
        session.getPlanningData().put("targetLocation", mission.getTargetLocation());
    }

    /**
     * Calculates unit requirements for resource allocation assessment.
     */
    private void calculateUnitRequirements(ResourceAllocationAssessment assessment, 
                                         Mission mission, MissionPlanningSession session) {
        // Use unit management service to find suitable units
        int requiredUnitCount = calculatePersonnelRequirement(mission) / 10; // Assume 10 personnel per unit
        
        try {
            // Get available units through unit management service
            // This integrates with the UnitManagementService we created earlier
            UnitManagementService.UnitManagementStatistics unitStats = unitManagementService.getManagementStatistics();
            
            if (unitStats.getActiveUnits() >= requiredUnitCount) {
                // Add mock unit IDs for now - in real implementation would query actual units
                for (int i = 1; i <= requiredUnitCount; i++) {
                    assessment.getRequiredUnitIds().add((long) i);
                }
            } else {
                assessment.getRiskFactors().add("Insufficient active units available: " + 
                    unitStats.getActiveUnits() + " available, " + requiredUnitCount + " required");
            }
        } catch (Exception e) {
            assessment.getRiskFactors().add("Unable to assess unit availability: " + e.getMessage());
        }
    }

    /**
     * Calculates equipment requirements for resource allocation assessment.
     */
    private void calculateEquipmentRequirements(ResourceAllocationAssessment assessment, 
                                              Mission mission, MissionPlanningSession session) {
        Map<String, Integer> equipment = assessment.getEquipmentRequirements();
        
        // Equipment requirements based on mission type
        switch (mission.getMissionType()) {
            case RECONNAISSANCE:
                equipment.put("surveillance_equipment", 5);
                equipment.put("communication_devices", 10);
                equipment.put("vehicles", 2);
                break;
            case ASSAULT:
                equipment.put("weapons", 20);
                equipment.put("ammunition", 1000);
                equipment.put("protective_gear", 15);
                equipment.put("vehicles", 5);
                break;
            case LOGISTICS:
                equipment.put("transport_vehicles", 10);
                equipment.put("fuel", 5000);
                equipment.put("supplies", 2000);
                break;
            default:
                equipment.put("standard_equipment", 10);
                equipment.put("communication_devices", 5);
                equipment.put("vehicles", 3);
        }
    }

    /**
     * Calculates logistics requirements for resource allocation assessment.
     */
    private void calculateLogisticsRequirements(ResourceAllocationAssessment assessment, 
                                              Mission mission, MissionPlanningSession session) {
        Map<String, Object> logistics = assessment.getLogisticsRequirements();
        
        // Logistics based on mission duration and size
        int duration = calculateDurationRequirement(mission);
        int personnel = calculatePersonnelRequirement(mission);
        
        logistics.put("food_rations", personnel * duration * 3); // 3 meals per day
        logistics.put("water_liters", personnel * duration * 4); // 4 liters per person per day
        logistics.put("fuel_liters", calculateVehicleRequirement(mission) * duration * 50); // 50L per vehicle per day
        logistics.put("medical_supplies", Math.max(1, personnel / 10)); // Medical kit per 10 personnel
        logistics.put("communication_range_km", mission.getTargetLocation() != null ? 100 : 50); // Extended range for remote locations
    }

    /**
     * Assesses readiness and identifies risk factors.
     */
    private void assessReadinessAndRisks(ResourceAllocationAssessment assessment, Mission mission) {
        double readinessScore = 1.0;
        List<String> risks = assessment.getRiskFactors();
        List<String> recommendations = assessment.getRecommendations();

        // Resource availability assessment
        if (assessment.getRequiredUnitIds().isEmpty()) {
            readinessScore -= 0.4;
            risks.add("No units allocated for mission");
            recommendations.add("Allocate at least " + (calculatePersonnelRequirement(mission) / 10) + " units");
        }

        // Equipment readiness
        if (assessment.getEquipmentRequirements().isEmpty()) {
            readinessScore -= 0.2;
            risks.add("No equipment requirements calculated");
            recommendations.add("Complete equipment requirements assessment");
        }

        // Timeline assessment
        if (mission.getStartTime() != null && mission.getStartTime().isBefore(LocalDateTime.now().plusHours(24))) {
            readinessScore -= 0.1;
            risks.add("Mission scheduled to start within 24 hours");
            recommendations.add("Ensure all resources are pre-positioned");
        }

        // Mission complexity assessment
        if (mission.getPriority() != null) {
            switch (mission.getPriority()) {
                case HIGH:
                    readinessScore -= 0.1; // Higher standards for high priority
                    recommendations.add("Additional backup resources recommended for high-priority mission");
                    break;
                case LOW:
                    readinessScore += 0.1; // More tolerance for low priority
                    break;
                default:
                    // Medium priority - no adjustment
            }
        }

        // Ensure readiness score stays within bounds
        readinessScore = Math.max(0.0, Math.min(1.0, readinessScore));
        assessment.setReadinessScore(readinessScore);

        // Add general recommendations
        if (readinessScore < 0.7) {
            recommendations.add("Mission readiness below acceptable threshold - address identified risks");
        }
        recommendations.add("Conduct final readiness review 2 hours before mission start");
        recommendations.add("Establish clear communication protocols with all units");
    }

    /**
     * Calculates personnel requirement based on mission characteristics.
     */
    private int calculatePersonnelRequirement(Mission mission) {
        int basePersonnel = 10; // Base requirement
        
        // Adjust based on mission type
        if (mission.getMissionType() != null) {
            switch (mission.getMissionType()) {
                case ASSAULT: return basePersonnel * 3;
                case DEFENSE: return basePersonnel * 2;
                case RECONNAISSANCE: return basePersonnel / 2;
                case LOGISTICS: return basePersonnel;
                case SEARCH_AND_RESCUE: return basePersonnel * 2;
                default: return basePersonnel;
            }
        }
        
        return basePersonnel;
    }

    /**
     * Calculates vehicle requirement based on mission characteristics.
     */
    private int calculateVehicleRequirement(Mission mission) {
        int personnel = calculatePersonnelRequirement(mission);
        return Math.max(1, personnel / 5); // 1 vehicle per 5 personnel
    }

    /**
     * Calculates equipment requirement based on mission characteristics.
     */
    private int calculateEquipmentRequirement(Mission mission) {
        return calculatePersonnelRequirement(mission) * 2; // 2 equipment items per personnel
    }

    /**
     * Calculates duration requirement in hours.
     */
    private int calculateDurationRequirement(Mission mission) {
        if (mission.getStartTime() != null && mission.getEndTime() != null) {
            return (int) java.time.Duration.between(mission.getStartTime(), mission.getEndTime()).toHours();
        }
        
        // Default durations by mission type
        if (mission.getMissionType() != null) {
            return switch (mission.getMissionType()) {
                case RECONNAISSANCE -> 8;
                case PATROL -> 12;
                case ASSAULT -> 6;
                case DEFENSE -> 24;
                case LOGISTICS -> 4;
                case SEARCH_AND_RESCUE -> 16;
                default -> 8;
            };
        }
        
        return 8; // Default 8 hours
    }

    /**
     * Updates mission status in the database.
     */
    private void updateMissionStatus(Long missionId, MissionStatus status) {
        Optional<Mission> missionOpt = missionRepository.findById(missionId);
        if (missionOpt.isPresent()) {
            Mission mission = missionOpt.get();
            mission.setStatus(status);
            missionRepository.save(mission);
        }
    }

    /**
     * Updates mission start time when execution begins.
     */
    private void updateMissionStartTime(Long missionId) {
        Optional<Mission> missionOpt = missionRepository.findById(missionId);
        if (missionOpt.isPresent()) {
            Mission mission = missionOpt.get();
            if (mission.getStartTime() == null) {
                mission.setStartTime(LocalDateTime.now());
                missionRepository.save(mission);
            }
        }
    }

    /**
     * Updates mission completion details.
     */
    private void updateMissionCompletion(Long missionId, String completionNotes) {
        Optional<Mission> missionOpt = missionRepository.findById(missionId);
        if (missionOpt.isPresent()) {
            Mission mission = missionOpt.get();
            mission.setEndTime(LocalDateTime.now());
            mission.setCompletionPercentage(100);
            // Note: completionNotes would be stored in a mission notes field if available
            missionRepository.save(mission);
        }
    }
}
