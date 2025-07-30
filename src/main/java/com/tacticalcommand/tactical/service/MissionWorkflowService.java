package com.tacticalcommand.tactical.service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tacticalcommand.tactical.domain.MilitaryUnit;
import com.tacticalcommand.tactical.domain.Mission;
import com.tacticalcommand.tactical.domain.Mission.MissionStatus;
import com.tacticalcommand.tactical.repository.MilitaryUnitRepository;
import com.tacticalcommand.tactical.repository.MissionRepository;

/**
 * Enhanced Mission Workflow Engine Service.
 * Provides advanced workflow management, collaborative planning,
 * and real-time monitoring capabilities for missions.
 */
@Service
@Transactional
public class MissionWorkflowService {

    @Autowired
    private MissionRepository missionRepository;

    @Autowired
    private MilitaryUnitRepository unitRepository;

    @Autowired
    private MissionService missionService;

    // Collaborative Planning Session Management
    private final Map<Long, MissionPlanningSession> activePlanningSessionsMap = new ConcurrentHashMap<>();
    
    // Mission Workflow State Engine
    private final Map<Long, MissionWorkflowState> workflowStatesMap = new ConcurrentHashMap<>();

    /**
     * Enhanced Mission Planning Session for collaborative editing.
     */
    public static class MissionPlanningSession {
        private Long missionId;
        private String sessionId;
        private List<String> activeUsers;
        private LocalDateTime lastActivity;
        private Map<String, Object> collaborativeData;
        private Integer versionNumber;
        
        public MissionPlanningSession(Long missionId, String sessionId) {
            this.missionId = missionId;
            this.sessionId = sessionId;
            this.activeUsers = new java.util.ArrayList<>();
            this.lastActivity = LocalDateTime.now();
            this.collaborativeData = new HashMap<>();
            this.versionNumber = 1;
        }
        
        // Getters and setters
        public Long getMissionId() { return missionId; }
        public String getSessionId() { return sessionId; }
        public List<String> getActiveUsers() { return activeUsers; }
        public LocalDateTime getLastActivity() { return lastActivity; }
        public Map<String, Object> getCollaborativeData() { return collaborativeData; }
        public Integer getVersionNumber() { return versionNumber; }
        public void setVersionNumber(Integer versionNumber) { this.versionNumber = versionNumber; }
        public void updateActivity() { this.lastActivity = LocalDateTime.now(); }
    }

    /**
     * Mission Workflow State for complex mission lifecycle management.
     */
    public static class MissionWorkflowState {
        private Long missionId;
        private MissionStatus currentStatus;
        private List<String> availableActions;
        private Map<String, Object> workflowData;
        private LocalDateTime lastStateChange;
        private String currentAssignee;
        
        public MissionWorkflowState(Long missionId, MissionStatus status) {
            this.missionId = missionId;
            this.currentStatus = status;
            this.availableActions = new java.util.ArrayList<>();
            this.workflowData = new HashMap<>();
            this.lastStateChange = LocalDateTime.now();
        }
        
        // Getters and setters
        public Long getMissionId() { return missionId; }
        public MissionStatus getCurrentStatus() { return currentStatus; }
        public void setCurrentStatus(MissionStatus status) { 
            this.currentStatus = status; 
            this.lastStateChange = LocalDateTime.now();
        }
        public List<String> getAvailableActions() { return availableActions; }
        public Map<String, Object> getWorkflowData() { return workflowData; }
        public LocalDateTime getLastStateChange() { return lastStateChange; }
        public String getCurrentAssignee() { return currentAssignee; }
        public void setCurrentAssignee(String assignee) { this.currentAssignee = assignee; }
    }

    /**
     * Mission Resource Allocation Result.
     */
    public static class ResourceAllocationResult {
        private Long missionId;
        private List<Long> recommendedUnits;
        private Map<String, Integer> resourceRequirements;
        private Double allocationScore;
        private String allocationRationale;
        
        public ResourceAllocationResult(Long missionId) {
            this.missionId = missionId;
            this.recommendedUnits = new java.util.ArrayList<>();
            this.resourceRequirements = new HashMap<>();
        }
        
        // Getters and setters
        public Long getMissionId() { return missionId; }
        public List<Long> getRecommendedUnits() { return recommendedUnits; }
        public Map<String, Integer> getResourceRequirements() { return resourceRequirements; }
        public Double getAllocationScore() { return allocationScore; }
        public void setAllocationScore(Double score) { this.allocationScore = score; }
        public String getAllocationRationale() { return allocationRationale; }
        public void setAllocationRationale(String rationale) { this.allocationRationale = rationale; }
    }

    // ==== ENHANCED MISSION WORKFLOW ENGINE ====

    /**
     * Initiates mission planning workflow with collaborative session.
     */
    public MissionPlanningSession initiateMissionPlanning(Long missionId, String userId) {
        Optional<Mission> missionOpt = missionRepository.findById(missionId);
        if (!missionOpt.isPresent()) {
            throw new IllegalArgumentException("Mission not found: " + missionId);
        }
        Mission mission = missionOpt.get();
        
        // Create collaborative planning session
        String sessionId = UUID.randomUUID().toString();
        MissionPlanningSession session = new MissionPlanningSession(missionId, sessionId);
        session.getActiveUsers().add(userId);
        
        // Initialize workflow state
        MissionWorkflowState workflowState = new MissionWorkflowState(missionId, mission.getStatus());
        updateWorkflowAvailableActions(workflowState);
        workflowState.setCurrentAssignee(userId);
        
        // Store sessions
        activePlanningSessionsMap.put(missionId, session);
        workflowStatesMap.put(missionId, workflowState);
        
        return session;
    }

    /**
     * Joins collaborative planning session.
     */
    public MissionPlanningSession joinPlanningSession(Long missionId, String userId) {
        MissionPlanningSession session = activePlanningSessionsMap.get(missionId);
        if (session == null) {
            throw new IllegalStateException("No active planning session for mission: " + missionId);
        }
        
        if (!session.getActiveUsers().contains(userId)) {
            session.getActiveUsers().add(userId);
            session.updateActivity();
        }
        
        return session;
    }

    /**
     * Updates mission collaboratively with version control.
     */
    public Mission updateMissionCollaboratively(Long missionId, Mission updatedMission, String userId) {
        MissionPlanningSession session = activePlanningSessionsMap.get(missionId);
        if (session == null) {
            throw new IllegalStateException("No active collaborative session for mission: " + missionId);
        }
        
        if (!session.getActiveUsers().contains(userId)) {
            throw new IllegalArgumentException("User not part of collaborative session: " + userId);
        }
        
        // Update mission using existing service
        Mission updated = missionService.updateMission(missionId, updatedMission);
        
        // Increment version
        session.setVersionNumber(session.getVersionNumber() + 1);
        session.updateActivity();
        
        // Update workflow state
        MissionWorkflowState workflowState = workflowStatesMap.get(missionId);
        if (workflowState != null) {
            updateWorkflowAvailableActions(workflowState);
        }
        
        return updated;
    }

    /**
     * Performs automated resource allocation for mission.
     */
    public ResourceAllocationResult performResourceAllocation(Long missionId) {
        Optional<Mission> missionOpt = missionRepository.findById(missionId);
        if (!missionOpt.isPresent()) {
            throw new IllegalArgumentException("Mission not found: " + missionId);
        }
        Mission mission = missionOpt.get();
        
        ResourceAllocationResult result = new ResourceAllocationResult(missionId);
        
        // Get available units (simplified - assumes these methods exist)
        List<MilitaryUnit> availableUnits = unitRepository.findAll().stream()
            .filter(unit -> isUnitSuitableForMission(unit, mission))
            .limit(calculateRequiredUnitCount(mission))
            .collect(Collectors.toList());
        
        result.getRecommendedUnits().addAll(
            availableUnits.stream().map(MilitaryUnit::getId).collect(Collectors.toList())
        );
        
        // Calculate resource requirements
        calculateResourceRequirements(mission, result);
        
        // Calculate allocation score
        double score = calculateAllocationScore(mission, availableUnits);
        result.setAllocationScore(score);
        result.setAllocationRationale(generateAllocationRationale(mission, availableUnits, score));
        
        return result;
    }

    /**
     * Advances mission workflow to next stage.
     */
    public Mission advanceMissionWorkflow(Long missionId, String action, String userId) {
        MissionWorkflowState workflowState = workflowStatesMap.get(missionId);
        if (workflowState == null) {
            throw new IllegalStateException("No workflow state found for mission: " + missionId);
        }
        
        if (!workflowState.getAvailableActions().contains(action)) {
            throw new IllegalArgumentException("Action not available in current workflow state: " + action);
        }
        
        Optional<Mission> missionOpt = missionRepository.findById(missionId);
        if (!missionOpt.isPresent()) {
            throw new IllegalArgumentException("Mission not found: " + missionId);
        }
        Mission mission = missionOpt.get();
        
        MissionStatus newStatus = executeWorkflowAction(action, mission.getStatus());
        
        // Update mission status
        mission.setStatus(newStatus);
        mission = missionRepository.save(mission);
        
        // Update workflow state
        workflowState.setCurrentStatus(newStatus);
        workflowState.setCurrentAssignee(determineNextAssignee(newStatus, userId));
        updateWorkflowAvailableActions(workflowState);
        
        return mission;
    }

    /**
     * Generates mission risk assessment.
     */
    public Map<String, Object> generateMissionRiskAssessment(Long missionId) {
        Optional<Mission> missionOpt = missionRepository.findById(missionId);
        if (!missionOpt.isPresent()) {
            throw new IllegalArgumentException("Mission not found: " + missionId);
        }
        Mission mission = missionOpt.get();
        
        Map<String, Object> riskAssessment = new HashMap<>();
        
        // Timeline risk analysis
        double timelineRisk = calculateTimelineRisk(mission);
        riskAssessment.put("timelineRisk", timelineRisk);
        
        // Resource availability risk
        double resourceRisk = calculateResourceRisk(mission);
        riskAssessment.put("resourceRisk", resourceRisk);
        
        // Environmental risk factors
        double environmentalRisk = calculateEnvironmentalRisk(mission);
        riskAssessment.put("environmentalRisk", environmentalRisk);
        
        // Overall risk score
        double overallRisk = (timelineRisk + resourceRisk + environmentalRisk) / 3.0;
        riskAssessment.put("overallRisk", overallRisk);
        
        // Risk mitigation recommendations
        List<String> mitigationRecommendations = generateRiskMitigations(riskAssessment);
        riskAssessment.put("mitigationRecommendations", mitigationRecommendations);
        
        return riskAssessment;
    }

    /**
     * Retrieves workflow actions available for mission in current state.
     */
    public List<String> getAvailableWorkflowActions(Long missionId) {
        MissionWorkflowState workflowState = workflowStatesMap.get(missionId);
        if (workflowState == null) {
            throw new IllegalStateException("No workflow state found for mission: " + missionId);
        }
        
        return new java.util.ArrayList<>(workflowState.getAvailableActions());
    }

    /**
     * Retrieves active planning session information.
     */
    public MissionPlanningSession getPlanningSessionInfo(Long missionId) {
        MissionPlanningSession session = activePlanningSessionsMap.get(missionId);
        if (session == null) {
            throw new IllegalStateException("No active planning session for mission: " + missionId);
        }
        
        return session;
    }

    /**
     * Closes collaborative planning session.
     */
    public void closePlanningSession(Long missionId, String userId) {
        MissionPlanningSession session = activePlanningSessionsMap.get(missionId);
        if (session == null) {
            throw new IllegalStateException("No active planning session for mission: " + missionId);
        }
        
        // Remove session and workflow state
        activePlanningSessionsMap.remove(missionId);
        workflowStatesMap.remove(missionId);
    }

    // ==== PRIVATE HELPER METHODS ====

    private void updateWorkflowAvailableActions(MissionWorkflowState workflowState) {
        List<String> actions = workflowState.getAvailableActions();
        actions.clear();
        
        switch (workflowState.getCurrentStatus()) {
            case PLANNING:
                actions.add("SUBMIT_FOR_APPROVAL");
                actions.add("CANCEL");
                break;
            case ACTIVE:
                actions.add("SUSPEND");
                actions.add("COMPLETE");
                actions.add("ABORT");
                break;
            case SUSPENDED:
                actions.add("RESUME");
                actions.add("CANCEL");
                break;
            default:
                // No actions available for terminal states
                break;
        }
    }

    private MissionStatus executeWorkflowAction(String action, MissionStatus currentStatus) {
        switch (action) {
            case "SUBMIT_FOR_APPROVAL":
                return MissionStatus.ACTIVE; // Simplified - assumes immediate approval
            case "SUSPEND":
                return MissionStatus.SUSPENDED;
            case "RESUME":
                return MissionStatus.ACTIVE;
            case "COMPLETE":
                return MissionStatus.COMPLETED;
            case "CANCEL":
            case "ABORT":
                return MissionStatus.CANCELLED;
            default:
                throw new IllegalArgumentException("Unknown workflow action: " + action);
        }
    }

    private String determineNextAssignee(MissionStatus status, String currentUser) {
        // Simplified assignment logic
        switch (status) {
            case ACTIVE:
                return "field_commander";
            case SUSPENDED:
                return "operations_officer";
            default:
                return currentUser;
        }
    }

    private boolean isUnitSuitableForMission(MilitaryUnit unit, Mission mission) {
        // Simplified suitability check
        return unit.getStatus().toString().equals("ACTIVE") && 
               unit.getReadinessLevel().toString().startsWith("C");
    }

    private int calculateRequiredUnitCount(Mission mission) {
        // Simplified calculation based on mission priority
        switch (mission.getPriority()) {
            case HIGH:
                return 5;
            case MEDIUM:
                return 3;
            case LOW:
                return 1;
            default:
                return 2;
        }
    }

    private void calculateResourceRequirements(Mission mission, ResourceAllocationResult result) {
        Map<String, Integer> requirements = result.getResourceRequirements();
        
        // Basic resource calculation
        requirements.put("personnel", calculateRequiredUnitCount(mission) * 10);
        requirements.put("vehicles", calculateRequiredUnitCount(mission) * 2);
        requirements.put("equipment", calculateRequiredUnitCount(mission) * 5);
    }

    private double calculateAllocationScore(Mission mission, List<MilitaryUnit> units) {
        if (units.isEmpty()) {
            return 0.0;
        }
        
        // Simple scoring based on readiness levels
        double score = units.stream()
            .mapToDouble(unit -> {
                String readiness = unit.getReadinessLevel().toString();
                switch (readiness) {
                    case "C1": return 1.0;
                    case "C2": return 0.8;
                    case "C3": return 0.6;
                    case "C4": return 0.4;
                    default: return 0.2;
                }
            })
            .average()
            .orElse(0.0);
        
        return score;
    }

    private String generateAllocationRationale(Mission mission, List<MilitaryUnit> units, double score) {
        return String.format("Resource allocation for mission %s: %d units allocated with overall readiness score of %.2f", 
            mission.getMissionName(), units.size(), score);
    }

    private double calculateTimelineRisk(Mission mission) {
        if (mission.getStartTime() == null || mission.getEndTime() == null) {
            return 0.5; // Medium risk for undefined timeline
        }
        
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(mission.getStartTime())) {
            return 0.8; // High risk if already started
        }
        
        return 0.2; // Low risk for future missions
    }

    private double calculateResourceRisk(Mission mission) {
        // Count available units
        long availableUnits = unitRepository.findAll().stream()
            .filter(unit -> unit.getStatus().toString().equals("ACTIVE"))
            .count();
        
        int requiredUnits = calculateRequiredUnitCount(mission);
        
        if (availableUnits < requiredUnits) {
            return 0.9; // Very high risk
        } else if (availableUnits < requiredUnits * 1.5) {
            return 0.6; // Medium risk
        } else {
            return 0.2; // Low risk
        }
    }

    private double calculateEnvironmentalRisk(Mission mission) {
        // Simplified environmental risk - could integrate with weather service
        return 0.3; // Default medium-low environmental risk
    }

    private List<String> generateRiskMitigations(Map<String, Object> riskAssessment) {
        List<String> mitigations = new java.util.ArrayList<>();
        
        double overallRisk = (Double) riskAssessment.get("overallRisk");
        
        if (overallRisk > 0.7) {
            mitigations.add("Consider mission postponement");
            mitigations.add("Allocate additional resources");
        } else if (overallRisk > 0.5) {
            mitigations.add("Implement enhanced monitoring");
            mitigations.add("Prepare contingency plans");
        } else {
            mitigations.add("Standard risk monitoring");
        }
        
        return mitigations;
    }
}
