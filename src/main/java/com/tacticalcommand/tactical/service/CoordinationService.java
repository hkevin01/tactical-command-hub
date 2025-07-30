package com.tacticalcommand.tactical.service;

import com.tacticalcommand.tactical.domain.Mission;
import com.tacticalcommand.tactical.domain.Mission.MissionStatus;
import com.tacticalcommand.tactical.domain.MilitaryUnit;
import com.tacticalcommand.tactical.domain.MilitaryUnit.UnitStatus;
import com.tacticalcommand.tactical.repository.MissionRepository;
import com.tacticalcommand.tactical.repository.MilitaryUnitRepository;
import com.tacticalcommand.tactical.util.GeospatialUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class for coordinating multi-unit military operations.
 * Provides resource allocation, unit coordination, and operational
 * planning capabilities for complex missions.
 */
@Service
@Transactional
public class CoordinationService {

    @Autowired
    private MissionRepository missionRepository;

    @Autowired
    private MilitaryUnitRepository unitRepository;

    @Autowired
    private TacticalEventService eventService;

    /**
     * Coordinates multiple units for a joint mission.
     *
     * @param missionId the mission ID
     * @param unitIds list of unit IDs to coordinate
     * @return coordination result
     */
    public CoordinationResult coordinateUnitsForMission(Long missionId, List<Long> unitIds) {
        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new RuntimeException("Mission not found with id: " + missionId));

        List<MilitaryUnit> units = unitRepository.findAllById(unitIds);
        if (units.size() != unitIds.size()) {
            throw new RuntimeException("Some units not found");
        }

        CoordinationResult result = new CoordinationResult();
        result.setMissionId(missionId);
        result.setRequestedUnits(unitIds);

        // Validate units availability
        List<Long> availableUnits = new ArrayList<>();
        List<Long> unavailableUnits = new ArrayList<>();
        List<String> conflicts = new ArrayList<>();

        for (MilitaryUnit unit : units) {
            if (isUnitAvailableForMission(unit, mission)) {
                availableUnits.add(unit.getId());
            } else {
                unavailableUnits.add(unit.getId());
                conflicts.add("Unit " + unit.getUnitName() + " is not available: " + getUnavailabilityReason(unit));
            }
        }

        result.setAvailableUnits(availableUnits);
        result.setUnavailableUnits(unavailableUnits);
        result.setConflicts(conflicts);

        // Calculate coordination requirements
        if (!availableUnits.isEmpty()) {
            CoordinationPlan plan = generateCoordinationPlan(mission, 
                units.stream().filter(u -> availableUnits.contains(u.getId())).collect(Collectors.toList()));
            result.setCoordinationPlan(plan);
            result.setSuccess(true);
        } else {
            result.setSuccess(false);
            result.setMessage("No units available for coordination");
        }

        // Log coordination event
        eventService.createMissionEvent(missionId, "COORDINATION_REQUEST", 
            "Multi-unit coordination requested for " + unitIds.size() + " units", "MEDIUM");

        return result;
    }

    /**
     * Allocates resources across multiple units for optimal mission execution.
     *
     * @param missionId the mission ID
     * @param resourceRequirements map of resource types to quantities needed
     * @return resource allocation result
     */
    public ResourceAllocationResult allocateResources(Long missionId, Map<String, Integer> resourceRequirements) {
        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new RuntimeException("Mission not found with id: " + missionId));

        // Get all available units for the mission
        List<MilitaryUnit> availableUnits = getAvailableUnitsForMission(mission);

        ResourceAllocationResult result = new ResourceAllocationResult();
        result.setMissionId(missionId);
        result.setResourceRequirements(resourceRequirements);

        Map<String, Integer> allocatedResources = new HashMap<>();
        Map<String, List<Long>> resourceSources = new HashMap<>();
        List<String> shortfalls = new ArrayList<>();

        // Allocate resources based on unit capabilities and availability
        for (Map.Entry<String, Integer> requirement : resourceRequirements.entrySet()) {
            String resourceType = requirement.getKey();
            Integer requiredQuantity = requirement.getValue();

            ResourceAllocation allocation = allocateResourceType(availableUnits, resourceType, requiredQuantity);
            
            if (allocation.getAllocatedQuantity() >= requiredQuantity) {
                allocatedResources.put(resourceType, allocation.getAllocatedQuantity());
                resourceSources.put(resourceType, allocation.getSourceUnits());
            } else {
                allocatedResources.put(resourceType, allocation.getAllocatedQuantity());
                resourceSources.put(resourceType, allocation.getSourceUnits());
                shortfalls.add(resourceType + ": need " + requiredQuantity + ", available " + allocation.getAllocatedQuantity());
            }
        }

        result.setAllocatedResources(allocatedResources);
        result.setResourceSources(resourceSources);
        result.setShortfalls(shortfalls);
        result.setSuccess(shortfalls.isEmpty());

        return result;
    }

    /**
     * Finds optimal unit positioning for mission objectives.
     *
     * @param missionId the mission ID
     * @return positioning recommendations
     */
    public UnitPositioningResult optimizeUnitPositioning(Long missionId) {
        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new RuntimeException("Mission not found with id: " + missionId));

        List<MilitaryUnit> assignedUnits = getUnitsAssignedToMission(missionId);
        UnitPositioningResult result = new UnitPositioningResult();
        result.setMissionId(missionId);

        if (mission.getTargetLatitude() == null || mission.getTargetLongitude() == null) {
            result.setSuccess(false);
            result.setMessage("Mission target coordinates not specified");
            return result;
        }

        List<UnitPosition> recommendations = new ArrayList<>();

        // Calculate optimal positions based on mission type and unit capabilities
        for (MilitaryUnit unit : assignedUnits) {
            UnitPosition position = calculateOptimalPosition(unit, mission);
            recommendations.add(position);
        }

        result.setPositionRecommendations(recommendations);
        result.setSuccess(true);

        return result;
    }

    /**
     * Synchronizes operations across multiple units.
     *
     * @param missionId the mission ID
     * @param synchronizationPoint the time point for synchronization
     * @return synchronization result
     */
    public SynchronizationResult synchronizeOperations(Long missionId, LocalDateTime synchronizationPoint) {
        // Verify mission exists
        if (!missionRepository.existsById(missionId)) {
            throw new RuntimeException("Mission not found with id: " + missionId);
        }

        List<MilitaryUnit> assignedUnits = getUnitsAssignedToMission(missionId);
        SynchronizationResult result = new SynchronizationResult();
        result.setMissionId(missionId);
        result.setSynchronizationPoint(synchronizationPoint);

        List<UnitSyncStatus> unitStatuses = new ArrayList<>();
        boolean allUnitsReady = true;

        for (MilitaryUnit unit : assignedUnits) {
            UnitSyncStatus status = assessUnitSynchronizationStatus(unit, synchronizationPoint);
            unitStatuses.add(status);
            if (!status.isReady()) {
                allUnitsReady = false;
            }
        }

        result.setUnitStatuses(unitStatuses);
        result.setAllUnitsReady(allUnitsReady);

        if (allUnitsReady) {
            result.setMessage("All units synchronized and ready for operation");
            eventService.createMissionEvent(missionId, "SYNC_COMPLETE", 
                "All units synchronized for coordinated operation", "LOW");
        } else {
            result.setMessage("Some units not ready for synchronization");
            eventService.createMissionEvent(missionId, "SYNC_PENDING", 
                "Unit synchronization pending - some units not ready", "MEDIUM");
        }

        return result;
    }

    /**
     * Gets communication matrix between units.
     *
     * @param unitIds list of unit IDs
     * @return communication matrix
     */
    public CommunicationMatrix getCommunicationMatrix(List<Long> unitIds) {
        List<MilitaryUnit> units = unitRepository.findAllById(unitIds);
        CommunicationMatrix matrix = new CommunicationMatrix();
        matrix.setUnits(unitIds);

        Map<String, List<String>> communicationPaths = new HashMap<>();
        
        // Build communication matrix based on unit types and capabilities
        for (MilitaryUnit unit1 : units) {
            List<String> canCommunicateWith = new ArrayList<>();
            for (MilitaryUnit unit2 : units) {
                if (!unit1.getId().equals(unit2.getId()) && 
                    canUnitsDirectlyCommunicate(unit1, unit2)) {
                    canCommunicateWith.add(unit2.getUnitName());
                }
            }
            communicationPaths.put(unit1.getUnitName(), canCommunicateWith);
        }

        matrix.setCommunicationPaths(communicationPaths);
        return matrix;
    }

    // Private helper methods

    private boolean isUnitAvailableForMission(MilitaryUnit unit, Mission mission) {
        // Check unit status
        if (unit.getStatus() != UnitStatus.ACTIVE) {
            return false;
        }

        // Check if unit is already assigned to conflicting missions
        List<Mission> activeMissions = missionRepository.findByAssignedUnitAndStatusIn(
            unit, List.of(MissionStatus.PLANNING, MissionStatus.APPROVED, MissionStatus.ACTIVE));

        for (Mission activeMission : activeMissions) {
            if (!activeMission.getId().equals(mission.getId()) && 
                missionsHaveTimeConflict(mission, activeMission)) {
                return false;
            }
        }

        return true;
    }

    private String getUnavailabilityReason(MilitaryUnit unit) {
        if (unit.getStatus() != UnitStatus.ACTIVE) {
            return "Unit status: " + unit.getStatus();
        }
        return "Unit has conflicting mission assignments";
    }

    private List<MilitaryUnit> getAvailableUnitsForMission(Mission mission) {
        List<MilitaryUnit> allUnits = unitRepository.findByStatus(UnitStatus.ACTIVE);
        return allUnits.stream()
                .filter(unit -> isUnitAvailableForMission(unit, mission))
                .collect(Collectors.toList());
    }

    private List<MilitaryUnit> getUnitsAssignedToMission(Long missionId) {
        List<Mission> missions = missionRepository.findAllById(List.of(missionId));
        if (missions.isEmpty()) {
            return new ArrayList<>();
        }
        
        Mission mission = missions.get(0);
        if (mission.getAssignedUnit() != null) {
            return List.of(mission.getAssignedUnit());
        }
        
        // For multiple units, we would need a many-to-many relationship
        // For now, return single assigned unit or empty list
        return new ArrayList<>();
    }

    private CoordinationPlan generateCoordinationPlan(Mission mission, List<MilitaryUnit> units) {
        CoordinationPlan plan = new CoordinationPlan();
        plan.setMissionId(mission.getId());
        plan.setParticipatingUnits(units.stream().map(MilitaryUnit::getId).collect(Collectors.toList()));
        plan.setEstimatedStartTime(mission.getStartTime());
        plan.setCoordinationInstructions(generateCoordinationInstructions(mission, units));
        return plan;
    }

    private List<String> generateCoordinationInstructions(Mission mission, List<MilitaryUnit> units) {
        List<String> instructions = new ArrayList<>();
        instructions.add("Mission: " + mission.getMissionName());
        instructions.add("Participating units: " + units.size());
        instructions.add("Primary objective: " + mission.getObjective());
        
        for (int i = 0; i < units.size(); i++) {
            MilitaryUnit unit = units.get(i);
            instructions.add("Unit " + (i + 1) + ": " + unit.getUnitName() + " - " + unit.getUnitType());
        }
        
        return instructions;
    }

    private ResourceAllocation allocateResourceType(List<MilitaryUnit> units, String resourceType, Integer required) {
        ResourceAllocation allocation = new ResourceAllocation();
        allocation.setResourceType(resourceType);
        allocation.setRequiredQuantity(required);
        allocation.setAllocatedQuantity(0);
        allocation.setSourceUnits(new ArrayList<>());

        // Simple allocation based on unit size and type
        for (MilitaryUnit unit : units) {
            int availableFromUnit = estimateResourceAvailability(unit, resourceType);
            if (availableFromUnit > 0) {
                allocation.getSourceUnits().add(unit.getId());
                allocation.setAllocatedQuantity(allocation.getAllocatedQuantity() + availableFromUnit);
                
                if (allocation.getAllocatedQuantity() >= required) {
                    break;
                }
            }
        }

        return allocation;
    }

    private int estimateResourceAvailability(MilitaryUnit unit, String resourceType) {
        // Simple estimation based on unit size and type
        int baseCapacity = unit.getPersonnelCount() != null ? unit.getPersonnelCount() : 100;
        
        return switch (resourceType.toLowerCase()) {
            case "personnel" -> Math.max(0, baseCapacity - 10); // Reserve 10 for unit operations
            case "vehicles" -> baseCapacity / 20; // Rough estimate
            case "equipment" -> baseCapacity / 10;
            case "supplies" -> baseCapacity / 5;
            default -> 0;
        };
    }

    private UnitPosition calculateOptimalPosition(MilitaryUnit unit, Mission mission) {
        UnitPosition position = new UnitPosition();
        position.setUnitId(unit.getId());
        position.setUnitName(unit.getUnitName());

        // Calculate optimal position based on mission target and unit type
        BigDecimal targetLat = mission.getTargetLatitude();
        BigDecimal targetLon = mission.getTargetLongitude();

        // Simple positioning logic - units positioned around target based on type
        double offsetKm = getPositionOffsetForUnitType(unit.getUnitType().name());
        double[] coordinates = GeospatialUtils.calculateOffset(
            targetLat.doubleValue(), targetLon.doubleValue(), offsetKm, 0);

        position.setRecommendedLatitude(BigDecimal.valueOf(coordinates[0]));
        position.setRecommendedLongitude(BigDecimal.valueOf(coordinates[1]));
        position.setReasoning("Positioned " + offsetKm + "km from target based on unit type");

        return position;
    }

    private double getPositionOffsetForUnitType(String unitType) {
        return switch (unitType.toLowerCase()) {
            case "infantry" -> 1.0; // Close to target
            case "armor" -> 2.0; // Medium distance
            case "artillery" -> 5.0; // Further back
            case "support" -> 3.0; // Safe distance
            default -> 2.0; // Default distance
        };
    }

    private boolean missionsHaveTimeConflict(Mission mission1, Mission mission2) {
        LocalDateTime start1 = mission1.getStartTime();
        LocalDateTime end1 = mission1.getEndTime() != null ? mission1.getEndTime() : start1.plusHours(24);
        LocalDateTime start2 = mission2.getStartTime();
        LocalDateTime end2 = mission2.getEndTime() != null ? mission2.getEndTime() : start2.plusHours(24);

        return start1.isBefore(end2) && start2.isBefore(end1);
    }

    private UnitSyncStatus assessUnitSynchronizationStatus(MilitaryUnit unit, LocalDateTime syncPoint) {
        UnitSyncStatus status = new UnitSyncStatus();
        status.setUnitId(unit.getId());
        status.setUnitName(unit.getUnitName());
        status.setSynchronizationPoint(syncPoint);

        // Simple readiness assessment
        boolean isReady = unit.getStatus() == UnitStatus.ACTIVE && 
                         unit.getReadinessLevel().name().matches("READY|COMBAT_READY");
        
        status.setReady(isReady);
        status.setReadinessLevel(unit.getReadinessLevel().name());
        status.setTimeToReadiness(isReady ? 0 : estimateTimeToReadiness(unit));

        return status;
    }

    private int estimateTimeToReadiness(MilitaryUnit unit) {
        // Simple estimation based on readiness level
        return switch (unit.getReadinessLevel().name()) {
            case "TRAINING" -> 240; // 4 hours
            case "MAINTENANCE" -> 120; // 2 hours
            case "STANDBY" -> 60; // 1 hour
            default -> 30; // 30 minutes
        };
    }

    private boolean canUnitsDirectlyCommunicate(MilitaryUnit unit1, MilitaryUnit unit2) {
        // Simple communication matrix based on unit types
        // In reality, this would be based on communication equipment and protocols
        return true; // Assume all units can communicate for now
    }

    // Inner classes for coordination results

    public static class CoordinationResult {
        private Long missionId;
        private List<Long> requestedUnits;
        private List<Long> availableUnits;
        private List<Long> unavailableUnits;
        private List<String> conflicts;
        private CoordinationPlan coordinationPlan;
        private boolean success;
        private String message;

        // Getters and setters
        public Long getMissionId() { return missionId; }
        public void setMissionId(Long missionId) { this.missionId = missionId; }

        public List<Long> getRequestedUnits() { return requestedUnits; }
        public void setRequestedUnits(List<Long> requestedUnits) { this.requestedUnits = requestedUnits; }

        public List<Long> getAvailableUnits() { return availableUnits; }
        public void setAvailableUnits(List<Long> availableUnits) { this.availableUnits = availableUnits; }

        public List<Long> getUnavailableUnits() { return unavailableUnits; }
        public void setUnavailableUnits(List<Long> unavailableUnits) { this.unavailableUnits = unavailableUnits; }

        public List<String> getConflicts() { return conflicts; }
        public void setConflicts(List<String> conflicts) { this.conflicts = conflicts; }

        public CoordinationPlan getCoordinationPlan() { return coordinationPlan; }
        public void setCoordinationPlan(CoordinationPlan coordinationPlan) { this.coordinationPlan = coordinationPlan; }

        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    public static class CoordinationPlan {
        private Long missionId;
        private List<Long> participatingUnits;
        private LocalDateTime estimatedStartTime;
        private List<String> coordinationInstructions;

        // Getters and setters
        public Long getMissionId() { return missionId; }
        public void setMissionId(Long missionId) { this.missionId = missionId; }

        public List<Long> getParticipatingUnits() { return participatingUnits; }
        public void setParticipatingUnits(List<Long> participatingUnits) { this.participatingUnits = participatingUnits; }

        public LocalDateTime getEstimatedStartTime() { return estimatedStartTime; }
        public void setEstimatedStartTime(LocalDateTime estimatedStartTime) { this.estimatedStartTime = estimatedStartTime; }

        public List<String> getCoordinationInstructions() { return coordinationInstructions; }
        public void setCoordinationInstructions(List<String> coordinationInstructions) { this.coordinationInstructions = coordinationInstructions; }
    }

    public static class ResourceAllocationResult {
        private Long missionId;
        private Map<String, Integer> resourceRequirements;
        private Map<String, Integer> allocatedResources;
        private Map<String, List<Long>> resourceSources;
        private List<String> shortfalls;
        private boolean success;

        // Getters and setters
        public Long getMissionId() { return missionId; }
        public void setMissionId(Long missionId) { this.missionId = missionId; }

        public Map<String, Integer> getResourceRequirements() { return resourceRequirements; }
        public void setResourceRequirements(Map<String, Integer> resourceRequirements) { this.resourceRequirements = resourceRequirements; }

        public Map<String, Integer> getAllocatedResources() { return allocatedResources; }
        public void setAllocatedResources(Map<String, Integer> allocatedResources) { this.allocatedResources = allocatedResources; }

        public Map<String, List<Long>> getResourceSources() { return resourceSources; }
        public void setResourceSources(Map<String, List<Long>> resourceSources) { this.resourceSources = resourceSources; }

        public List<String> getShortfalls() { return shortfalls; }
        public void setShortfalls(List<String> shortfalls) { this.shortfalls = shortfalls; }

        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
    }

    public static class ResourceAllocation {
        private String resourceType;
        private Integer requiredQuantity;
        private Integer allocatedQuantity;
        private List<Long> sourceUnits;

        // Getters and setters
        public String getResourceType() { return resourceType; }
        public void setResourceType(String resourceType) { this.resourceType = resourceType; }

        public Integer getRequiredQuantity() { return requiredQuantity; }
        public void setRequiredQuantity(Integer requiredQuantity) { this.requiredQuantity = requiredQuantity; }

        public Integer getAllocatedQuantity() { return allocatedQuantity; }
        public void setAllocatedQuantity(Integer allocatedQuantity) { this.allocatedQuantity = allocatedQuantity; }

        public List<Long> getSourceUnits() { return sourceUnits; }
        public void setSourceUnits(List<Long> sourceUnits) { this.sourceUnits = sourceUnits; }
    }

    public static class UnitPositioningResult {
        private Long missionId;
        private List<UnitPosition> positionRecommendations;
        private boolean success;
        private String message;

        // Getters and setters
        public Long getMissionId() { return missionId; }
        public void setMissionId(Long missionId) { this.missionId = missionId; }

        public List<UnitPosition> getPositionRecommendations() { return positionRecommendations; }
        public void setPositionRecommendations(List<UnitPosition> positionRecommendations) { this.positionRecommendations = positionRecommendations; }

        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    public static class UnitPosition {
        private Long unitId;
        private String unitName;
        private BigDecimal recommendedLatitude;
        private BigDecimal recommendedLongitude;
        private String reasoning;

        // Getters and setters
        public Long getUnitId() { return unitId; }
        public void setUnitId(Long unitId) { this.unitId = unitId; }

        public String getUnitName() { return unitName; }
        public void setUnitName(String unitName) { this.unitName = unitName; }

        public BigDecimal getRecommendedLatitude() { return recommendedLatitude; }
        public void setRecommendedLatitude(BigDecimal recommendedLatitude) { this.recommendedLatitude = recommendedLatitude; }

        public BigDecimal getRecommendedLongitude() { return recommendedLongitude; }
        public void setRecommendedLongitude(BigDecimal recommendedLongitude) { this.recommendedLongitude = recommendedLongitude; }

        public String getReasoning() { return reasoning; }
        public void setReasoning(String reasoning) { this.reasoning = reasoning; }
    }

    public static class SynchronizationResult {
        private Long missionId;
        private LocalDateTime synchronizationPoint;
        private List<UnitSyncStatus> unitStatuses;
        private boolean allUnitsReady;
        private String message;

        // Getters and setters
        public Long getMissionId() { return missionId; }
        public void setMissionId(Long missionId) { this.missionId = missionId; }

        public LocalDateTime getSynchronizationPoint() { return synchronizationPoint; }
        public void setSynchronizationPoint(LocalDateTime synchronizationPoint) { this.synchronizationPoint = synchronizationPoint; }

        public List<UnitSyncStatus> getUnitStatuses() { return unitStatuses; }
        public void setUnitStatuses(List<UnitSyncStatus> unitStatuses) { this.unitStatuses = unitStatuses; }

        public boolean isAllUnitsReady() { return allUnitsReady; }
        public void setAllUnitsReady(boolean allUnitsReady) { this.allUnitsReady = allUnitsReady; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    public static class UnitSyncStatus {
        private Long unitId;
        private String unitName;
        private LocalDateTime synchronizationPoint;
        private boolean ready;
        private String readinessLevel;
        private int timeToReadiness; // in minutes

        // Getters and setters
        public Long getUnitId() { return unitId; }
        public void setUnitId(Long unitId) { this.unitId = unitId; }

        public String getUnitName() { return unitName; }
        public void setUnitName(String unitName) { this.unitName = unitName; }

        public LocalDateTime getSynchronizationPoint() { return synchronizationPoint; }
        public void setSynchronizationPoint(LocalDateTime synchronizationPoint) { this.synchronizationPoint = synchronizationPoint; }

        public boolean isReady() { return ready; }
        public void setReady(boolean ready) { this.ready = ready; }

        public String getReadinessLevel() { return readinessLevel; }
        public void setReadinessLevel(String readinessLevel) { this.readinessLevel = readinessLevel; }

        public int getTimeToReadiness() { return timeToReadiness; }
        public void setTimeToReadiness(int timeToReadiness) { this.timeToReadiness = timeToReadiness; }
    }

    public static class CommunicationMatrix {
        private List<Long> units;
        private Map<String, List<String>> communicationPaths;

        // Getters and setters
        public List<Long> getUnits() { return units; }
        public void setUnits(List<Long> units) { this.units = units; }

        public Map<String, List<String>> getCommunicationPaths() { return communicationPaths; }
        public void setCommunicationPaths(Map<String, List<String>> communicationPaths) { this.communicationPaths = communicationPaths; }
    }
}
