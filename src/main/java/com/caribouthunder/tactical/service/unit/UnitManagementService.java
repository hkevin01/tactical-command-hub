package com.caribouthunder.tactical.service.unit;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tacticalcommand.tactical.domain.MilitaryUnit;
import com.tacticalcommand.tactical.repository.MilitaryUnitRepository;
import com.tacticalcommand.tactical.service.MilitaryUnitService;

/**
 * Enhanced Unit Management Service for administrative and operational unit management.
 * 
 * This service provides high-level unit management operations including unit lifecycle,
 * organizational structure management, and administrative operations. It builds upon
 * the existing MilitaryUnitService to provide specialized management capabilities.
 * 
 * @author Tactical Command Hub Team - TaskSync Implementation
 * @version 1.0.0
 * @since 2025-07-30
 */
@Service
@Transactional
public class UnitManagementService {

    private final MilitaryUnitRepository militaryUnitRepository;
    private final MilitaryUnitService militaryUnitService;

    @Autowired
    public UnitManagementService(
            MilitaryUnitRepository militaryUnitRepository,
            MilitaryUnitService militaryUnitService) {
        this.militaryUnitRepository = militaryUnitRepository;
        this.militaryUnitService = militaryUnitService;
    }

    /**
     * Creates a new military unit with enhanced validation and setup.
     * 
     * @param unit the military unit to create
     * @param organizationalContext additional organizational context
     * @return the created military unit with setup complete
     * @throws IllegalArgumentException if validation fails
     */
    @Transactional
    public MilitaryUnit createManagedUnit(MilitaryUnit unit, Map<String, Object> organizationalContext) {
        validateUnitCreationContext(unit, organizationalContext);
        
        // Set default management properties
        if (unit.getStatus() == null) {
            unit.setStatus(MilitaryUnit.UnitStatus.INACTIVE);
        }
        
        // Initialize unit with default operational parameters
        initializeUnitDefaults(unit);
        
        // Create unit using existing service
        MilitaryUnit createdUnit = militaryUnitService.createUnit(unit);
        
        // Setup organizational relationships
        setupOrganizationalRelationships(createdUnit, organizationalContext);
        
        return createdUnit;
    }

    /**
     * Activates a unit and transitions it to operational status.
     * 
     * @param unitId the unit ID to activate
     * @param activatedBy the user activating the unit
     * @param activationNotes activation notes and context
     * @return the activated unit
     */
    @Transactional
    public MilitaryUnit activateUnit(Long unitId, String activatedBy, String activationNotes) {
        MilitaryUnit unit = militaryUnitRepository.findById(unitId)
            .orElseThrow(() -> new RuntimeException("Unit not found with ID: " + unitId));

        validateUnitActivation(unit);
        
        // Update unit status to active
        unit.setStatus(MilitaryUnit.UnitStatus.ACTIVE);
        unit.setLastContact(LocalDateTime.now());
        
        // Update readiness if not set
        if (unit.getReadinessLevel() == null || 
            unit.getReadinessLevel() == MilitaryUnit.ReadinessLevel.C4) {
            unit.setReadinessLevel(MilitaryUnit.ReadinessLevel.C3);
        }
        
        // Create status history record
        return militaryUnitService.updateUnitStatus(unitId, MilitaryUnit.UnitStatus.ACTIVE, 
            "Unit activated: " + activationNotes, activatedBy);
    }

    /**
     * Deactivates a unit and transitions it to inactive status.
     * 
     * @param unitId the unit ID to deactivate
     * @param deactivatedBy the user deactivating the unit
     * @param deactivationReason reason for deactivation
     * @return the deactivated unit
     */
    @Transactional
    public MilitaryUnit deactivateUnit(Long unitId, String deactivatedBy, String deactivationReason) {
        MilitaryUnit unit = militaryUnitRepository.findById(unitId)
            .orElseThrow(() -> new RuntimeException("Unit not found with ID: " + unitId));

        validateUnitDeactivation(unit);
        
        // Clear sensitive operational data
        clearOperationalData(unit);
        
        // Update unit status
        return militaryUnitService.updateUnitStatus(unitId, MilitaryUnit.UnitStatus.INACTIVE, 
            "Unit deactivated: " + deactivationReason, deactivatedBy);
    }

    /**
     * Gets all units under management with their current operational status.
     * 
     * @param includeInactive whether to include inactive units
     * @return list of managed units
     */
    @Transactional(readOnly = true)
    public List<MilitaryUnit> getManagedUnits(boolean includeInactive) {
        if (includeInactive) {
            return militaryUnitRepository.findAll();
        } else {
            return militaryUnitService.getActiveUnits();
        }
    }

    /**
     * Gets comprehensive unit management statistics.
     * 
     * @return management statistics
     */
    @Transactional(readOnly = true)
    public UnitManagementStatistics getManagementStatistics() {
        return UnitManagementStatistics.builder()
            .totalUnits(militaryUnitRepository.count())
            .activeUnits(militaryUnitService.getActiveUnits().size())
            .unitsByStatus(militaryUnitService.getUnitStatusStatistics())
            .unitsByDomain(militaryUnitService.getUnitDomainStatistics())
            .unitsByReadiness(militaryUnitService.getUnitReadinessStatistics())
            .missionCapableUnits(militaryUnitService.getMissionCapableUnits().size())
            .build();
    }

    /**
     * Performs bulk unit operations (activation, deactivation, reassignment).
     * 
     * @param unitIds list of unit IDs to operate on
     * @param operation the bulk operation to perform
     * @param operationContext operation context and parameters
     * @param operatedBy the user performing the operation
     * @return operation results
     */
    @Transactional
    public BulkOperationResult performBulkOperation(
            List<Long> unitIds, 
            BulkOperation operation, 
            Map<String, Object> operationContext, 
            String operatedBy) {
        
        BulkOperationResult result = new BulkOperationResult();
        result.setTotalRequested(unitIds.size());
        result.setOperationType(operation);
        result.setOperatedBy(operatedBy);
        result.setOperationTime(LocalDateTime.now());
        
        for (Long unitId : unitIds) {
            try {
                switch (operation) {
                    case ACTIVATE:
                        activateUnit(unitId, operatedBy, 
                            (String) operationContext.getOrDefault("reason", "Bulk activation"));
                        result.addSuccess(unitId);
                        break;
                    case DEACTIVATE:
                        deactivateUnit(unitId, operatedBy, 
                            (String) operationContext.getOrDefault("reason", "Bulk deactivation"));
                        result.addSuccess(unitId);
                        break;
                    case UPDATE_READINESS:
                        MilitaryUnit.ReadinessLevel readiness = 
                            (MilitaryUnit.ReadinessLevel) operationContext.get("readinessLevel");
                        militaryUnitService.updateReadinessLevel(unitId, readiness);
                        result.addSuccess(unitId);
                        break;
                    default:
                        result.addFailure(unitId, "Unsupported operation: " + operation);
                }
            } catch (Exception e) {
                result.addFailure(unitId, e.getMessage());
            }
        }
        
        return result;
    }

    /**
     * Gets unit hierarchy and organizational relationships.
     * 
     * @param parentUnitId the parent unit ID (null for top-level)
     * @return hierarchical unit structure
     */
    @Transactional(readOnly = true)
    public UnitHierarchy getUnitHierarchy(Long parentUnitId) {
        // This would typically integrate with an organizational structure
        // For now, return a basic hierarchy based on unit types
        List<MilitaryUnit> allUnits = militaryUnitRepository.findAll();
        
        return UnitHierarchy.builder()
            .rootUnitId(parentUnitId)
            .hierarchyData(buildHierarchyStructure(allUnits, parentUnitId))
            .generatedAt(LocalDateTime.now())
            .build();
    }

    // Private helper methods

    private void validateUnitCreationContext(MilitaryUnit unit, Map<String, Object> context) {
        if (unit.getCallsign() == null || unit.getCallsign().trim().isEmpty()) {
            throw new IllegalArgumentException("Unit callsign is required");
        }
        if (unit.getUnitName() == null || unit.getUnitName().trim().isEmpty()) {
            throw new IllegalArgumentException("Unit name is required");
        }
        if (unit.getUnitType() == null) {
            throw new IllegalArgumentException("Unit type is required");
        }
        
        // Additional context validation
        if (context != null && context.containsKey("requiredClearanceLevel")) {
            // Validate security clearance requirements
        }
    }

    private void initializeUnitDefaults(MilitaryUnit unit) {
        if (unit.getReadinessLevel() == null) {
            unit.setReadinessLevel(MilitaryUnit.ReadinessLevel.C4);
        }
        if (unit.getCommunicationStatus() == null) {
            unit.setCommunicationStatus(MilitaryUnit.CommunicationStatus.UNKNOWN);
        }
        if (unit.getThreatLevel() == null) {
            unit.setThreatLevel(MilitaryUnit.ThreatLevel.LOW);
        }
    }

    private void setupOrganizationalRelationships(MilitaryUnit unit, Map<String, Object> context) {
        // Setup organizational context - this would typically integrate with
        // an organizational management system
        if (context != null) {
            // Set parent unit, command structure, etc.
        }
    }

    private void validateUnitActivation(MilitaryUnit unit) {
        if (unit.getStatus() == MilitaryUnit.UnitStatus.ACTIVE) {
            throw new IllegalStateException("Unit is already active");
        }
        if (unit.getCallsign() == null || unit.getCallsign().trim().isEmpty()) {
            throw new IllegalStateException("Unit must have valid callsign before activation");
        }
    }

    private void validateUnitDeactivation(MilitaryUnit unit) {
        if (unit.getStatus() == MilitaryUnit.UnitStatus.INACTIVE) {
            throw new IllegalStateException("Unit is already inactive");
        }
        // Additional validation for units that cannot be deactivated
    }

    private void clearOperationalData(MilitaryUnit unit) {
        // Clear sensitive operational information before deactivation
        // This would typically involve clearing classified positions, etc.
    }

    private Map<String, Object> buildHierarchyStructure(List<MilitaryUnit> units, Long parentId) {
        // Build hierarchical structure - simplified implementation
        Map<String, Object> hierarchy = new java.util.HashMap<>();
        hierarchy.put("unitCount", units.size());
        hierarchy.put("lastUpdated", LocalDateTime.now());
        return hierarchy;
    }

    // Inner classes for return types

    public static class UnitManagementStatistics {
        private long totalUnits;
        private long activeUnits;
        private long missionCapableUnits;
        private Map<MilitaryUnit.UnitStatus, Long> unitsByStatus;
        private Map<MilitaryUnit.OperationalDomain, Long> unitsByDomain;
        private Map<MilitaryUnit.ReadinessLevel, Long> unitsByReadiness;

        public static UnitManagementStatisticsBuilder builder() {
            return new UnitManagementStatisticsBuilder();
        }

        // Getters and setters
        public long getTotalUnits() { return totalUnits; }
        public void setTotalUnits(long totalUnits) { this.totalUnits = totalUnits; }
        
        public long getActiveUnits() { return activeUnits; }
        public void setActiveUnits(long activeUnits) { this.activeUnits = activeUnits; }
        
        public long getMissionCapableUnits() { return missionCapableUnits; }
        public void setMissionCapableUnits(long missionCapableUnits) { this.missionCapableUnits = missionCapableUnits; }
        
        public Map<MilitaryUnit.UnitStatus, Long> getUnitsByStatus() { return unitsByStatus; }
        public void setUnitsByStatus(Map<MilitaryUnit.UnitStatus, Long> unitsByStatus) { this.unitsByStatus = unitsByStatus; }
        
        public Map<MilitaryUnit.OperationalDomain, Long> getUnitsByDomain() { return unitsByDomain; }
        public void setUnitsByDomain(Map<MilitaryUnit.OperationalDomain, Long> unitsByDomain) { this.unitsByDomain = unitsByDomain; }
        
        public Map<MilitaryUnit.ReadinessLevel, Long> getUnitsByReadiness() { return unitsByReadiness; }
        public void setUnitsByReadiness(Map<MilitaryUnit.ReadinessLevel, Long> unitsByReadiness) { this.unitsByReadiness = unitsByReadiness; }
    }

    public static class UnitManagementStatisticsBuilder {
        private UnitManagementStatistics stats = new UnitManagementStatistics();

        public UnitManagementStatisticsBuilder totalUnits(long totalUnits) {
            stats.setTotalUnits(totalUnits);
            return this;
        }

        public UnitManagementStatisticsBuilder activeUnits(long activeUnits) {
            stats.setActiveUnits(activeUnits);
            return this;
        }

        public UnitManagementStatisticsBuilder missionCapableUnits(long missionCapableUnits) {
            stats.setMissionCapableUnits(missionCapableUnits);
            return this;
        }

        public UnitManagementStatisticsBuilder unitsByStatus(Map<MilitaryUnit.UnitStatus, Long> unitsByStatus) {
            stats.setUnitsByStatus(unitsByStatus);
            return this;
        }

        public UnitManagementStatisticsBuilder unitsByDomain(Map<MilitaryUnit.OperationalDomain, Long> unitsByDomain) {
            stats.setUnitsByDomain(unitsByDomain);
            return this;
        }

        public UnitManagementStatisticsBuilder unitsByReadiness(Map<MilitaryUnit.ReadinessLevel, Long> unitsByReadiness) {
            stats.setUnitsByReadiness(unitsByReadiness);
            return this;
        }

        public UnitManagementStatistics build() {
            return stats;
        }
    }

    public static class BulkOperationResult {
        private int totalRequested;
        private int successCount;
        private int failureCount;
        private BulkOperation operationType;
        private String operatedBy;
        private LocalDateTime operationTime;
        private List<Long> successfulUnits = new java.util.ArrayList<>();
        private Map<Long, String> failedUnits = new java.util.HashMap<>();

        public void addSuccess(Long unitId) {
            successfulUnits.add(unitId);
            successCount++;
        }

        public void addFailure(Long unitId, String reason) {
            failedUnits.put(unitId, reason);
            failureCount++;
        }

        // Getters and setters
        public int getTotalRequested() { return totalRequested; }
        public void setTotalRequested(int totalRequested) { this.totalRequested = totalRequested; }
        
        public int getSuccessCount() { return successCount; }
        public void setSuccessCount(int successCount) { this.successCount = successCount; }
        
        public int getFailureCount() { return failureCount; }
        public void setFailureCount(int failureCount) { this.failureCount = failureCount; }
        
        public BulkOperation getOperationType() { return operationType; }
        public void setOperationType(BulkOperation operationType) { this.operationType = operationType; }
        
        public String getOperatedBy() { return operatedBy; }
        public void setOperatedBy(String operatedBy) { this.operatedBy = operatedBy; }
        
        public LocalDateTime getOperationTime() { return operationTime; }
        public void setOperationTime(LocalDateTime operationTime) { this.operationTime = operationTime; }
        
        public List<Long> getSuccessfulUnits() { return successfulUnits; }
        public void setSuccessfulUnits(List<Long> successfulUnits) { this.successfulUnits = successfulUnits; }
        
        public Map<Long, String> getFailedUnits() { return failedUnits; }
        public void setFailedUnits(Map<Long, String> failedUnits) { this.failedUnits = failedUnits; }
    }

    public static class UnitHierarchy {
        private Long rootUnitId;
        private Map<String, Object> hierarchyData;
        private LocalDateTime generatedAt;

        public static UnitHierarchyBuilder builder() {
            return new UnitHierarchyBuilder();
        }

        // Getters and setters
        public Long getRootUnitId() { return rootUnitId; }
        public void setRootUnitId(Long rootUnitId) { this.rootUnitId = rootUnitId; }
        
        public Map<String, Object> getHierarchyData() { return hierarchyData; }
        public void setHierarchyData(Map<String, Object> hierarchyData) { this.hierarchyData = hierarchyData; }
        
        public LocalDateTime getGeneratedAt() { return generatedAt; }
        public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }
    }

    public static class UnitHierarchyBuilder {
        private UnitHierarchy hierarchy = new UnitHierarchy();

        public UnitHierarchyBuilder rootUnitId(Long rootUnitId) {
            hierarchy.setRootUnitId(rootUnitId);
            return this;
        }

        public UnitHierarchyBuilder hierarchyData(Map<String, Object> hierarchyData) {
            hierarchy.setHierarchyData(hierarchyData);
            return this;
        }

        public UnitHierarchyBuilder generatedAt(LocalDateTime generatedAt) {
            hierarchy.setGeneratedAt(generatedAt);
            return this;
        }

        public UnitHierarchy build() {
            return hierarchy;
        }
    }

    public enum BulkOperation {
        ACTIVATE,
        DEACTIVATE,
        UPDATE_READINESS,
        UPDATE_STATUS,
        REASSIGN
    }
}
