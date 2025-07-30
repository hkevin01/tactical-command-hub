package com.tacticalcommand.tactical.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tacticalcommand.tactical.domain.MilitaryUnit;
import com.tacticalcommand.tactical.domain.Mission;
import com.tacticalcommand.tactical.domain.Mission.MissionStatus;
import com.tacticalcommand.tactical.domain.Mission.Priority;
import com.tacticalcommand.tactical.domain.MissionReport;
import com.tacticalcommand.tactical.domain.MissionWaypoint;
import com.tacticalcommand.tactical.repository.MilitaryUnitRepository;
import com.tacticalcommand.tactical.repository.MissionReportRepository;
import com.tacticalcommand.tactical.repository.MissionRepository;
import com.tacticalcommand.tactical.repository.MissionWaypointRepository;
import com.tacticalcommand.tactical.util.DateTimeUtils;
import com.tacticalcommand.tactical.util.GeospatialUtils;

/**
 * Service class for managing Mission entities and mission-related operations.
 * Provides comprehensive mission management capabilities including planning,
 * execution, monitoring, and reporting.
 */
@Service
@Transactional
public class MissionService {

    @Autowired
    private MissionRepository missionRepository;

    @Autowired
    private MissionWaypointRepository waypointRepository;

    @Autowired
    private MissionReportRepository reportRepository;

    @Autowired
    private MilitaryUnitRepository unitRepository;

    /**
     * Creates a new mission with automatic code generation.
     *
     * @param mission the mission to create
     * @return the created mission
     */
    public Mission createMission(Mission mission) {
        // Generate mission code if not provided
        if (mission.getMissionCode() == null || mission.getMissionCode().trim().isEmpty()) {
            mission.setMissionCode(generateMissionCode(mission));
        }

        // Set default values
        if (mission.getStatus() == null) {
            mission.setStatus(MissionStatus.PLANNING);
        }
        if (mission.getPriority() == null) {
            mission.setPriority(Priority.MEDIUM);
        }
        if (mission.getCompletionPercentage() == null) {
            mission.setCompletionPercentage(0);
        }

        return missionRepository.save(mission);
    }

    /**
     * Updates an existing mission.
     *
     * @param missionId the mission ID
     * @param updatedMission the updated mission data
     * @return the updated mission
     */
    public Mission updateMission(Long missionId, Mission updatedMission) {
        Mission existingMission = missionRepository.findById(missionId)
                .orElseThrow(() -> new RuntimeException("Mission not found with id: " + missionId));

        // Update fields
        existingMission.setMissionName(updatedMission.getMissionName());
        existingMission.setMissionType(updatedMission.getMissionType());
        existingMission.setObjective(updatedMission.getObjective());
        existingMission.setDescription(updatedMission.getDescription());
        existingMission.setStartTime(updatedMission.getStartTime());
        existingMission.setEndTime(updatedMission.getEndTime());
        existingMission.setEstimatedDurationHours(updatedMission.getEstimatedDurationHours());
        existingMission.setTargetLocation(updatedMission.getTargetLocation());
        existingMission.setCommander(updatedMission.getCommander());
        existingMission.setPriority(updatedMission.getPriority());

        // Update coordinates if provided
        if (updatedMission.getTargetLatitude() != null && updatedMission.getTargetLongitude() != null) {
            existingMission.setTargetCoordinates(
                updatedMission.getTargetLatitude(), 
                updatedMission.getTargetLongitude()
            );
        }

        return missionRepository.save(existingMission);
    }

    /**
     * Retrieves all missions.
     *
     * @return list of all missions
     */
    public List<Mission> getAllMissions() {
        return missionRepository.findAll();
    }

    /**
     * Retrieves missions by status with pagination.
     *
     * @param status the mission status
     * @param pageable pagination information
     * @return page of missions with the specified status
     */
    public Page<Mission> getMissionsByStatus(MissionStatus status, Pageable pageable) {
        return missionRepository.findByStatus(status, pageable);
    }

    /**
     * Retrieves missions by priority.
     *
     * @param priority the mission priority
     * @return list of missions with the specified priority
     */
    public List<Mission> getMissionsByPriority(Priority priority) {
        return missionRepository.findByPriority(priority);
    }

    /**
     * Retrieves missions by assigned unit.
     *
     * @param unitId the unit ID
     * @return list of missions assigned to the unit
     */
    public List<Mission> getMissionsByUnit(Long unitId) {
        return missionRepository.findByAssignedUnitId(unitId);
    }

    /**
     * Retrieves a mission by ID.
     *
     * @param missionId the mission ID
     * @return optional containing the mission if found
     */
    public Optional<Mission> getMissionById(Long missionId) {
        return missionRepository.findById(missionId);
    }

    /**
     * Assigns a military unit to a mission.
     *
     * @param missionId the mission ID
     * @param unitId the unit ID
     * @return the updated mission
     */
    public Mission assignUnit(Long missionId, Long unitId) {
        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new RuntimeException("Mission not found with id: " + missionId));

        MilitaryUnit unit = unitRepository.findById(unitId)
                .orElseThrow(() -> new RuntimeException("Military unit not found with id: " + unitId));

        // Validate unit availability
        validateUnitAvailability(unit);

        mission.setAssignedUnit(unit);
        return missionRepository.save(mission);
    }

    /**
     * Updates mission status with validation.
     *
     * @param missionId the mission ID
     * @param newStatus the new status
     * @return the updated mission
     */
    public Mission updateMissionStatus(Long missionId, MissionStatus newStatus) {
        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new RuntimeException("Mission not found with id: " + missionId));

        MissionStatus currentStatus = mission.getStatus();
        validateStatusTransition(currentStatus, newStatus);

        mission.setStatus(newStatus);

        // Update timestamps based on status
        if (newStatus == MissionStatus.ACTIVE && mission.getStartTime() == null) {
            mission.setStartTime(LocalDateTime.now());
        } else if (newStatus == MissionStatus.COMPLETED && mission.getEndTime() == null) {
            mission.setEndTime(LocalDateTime.now());
            mission.setCompletionPercentage(100);
        }

        return missionRepository.save(mission);
    }

    /**
     * Adds a waypoint to a mission.
     *
     * @param missionId the mission ID
     * @param waypoint the waypoint to add
     * @return the created waypoint
     */
    public MissionWaypoint addWaypoint(Long missionId, MissionWaypoint waypoint) {
        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new RuntimeException("Mission not found with id: " + missionId));

        waypoint.setMission(mission);

        // Validate coordinates
        if (waypoint.getLatitude() != null && waypoint.getLongitude() != null) {
            if (!GeospatialUtils.isValidCoordinates(
                waypoint.getLatitude().doubleValue(), 
                waypoint.getLongitude().doubleValue())) {
                throw new IllegalArgumentException("Invalid waypoint coordinates");
            }
        }

        return waypointRepository.save(waypoint);
    }

    /**
     * Retrieves waypoints for a mission.
     *
     * @param missionId the mission ID
     * @return list of waypoints ordered by sequence
     */
    public List<MissionWaypoint> getMissionWaypoints(Long missionId) {
        return waypointRepository.findByMissionIdOrderBySequenceNumber(missionId);
    }

    /**
     * Adds a report to a mission.
     *
     * @param missionId the mission ID
     * @param report the report to add
     * @return the created report
     */
    public MissionReport addReport(Long missionId, MissionReport report) {
        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new RuntimeException("Mission not found with id: " + missionId));

        report.setMission(mission);
        report.setCreatedAt(LocalDateTime.now());

        return reportRepository.save(report);
    }

    /**
     * Retrieves reports for a mission.
     *
     * @param missionId the mission ID
     * @return list of reports ordered by creation time (newest first)
     */
    public List<MissionReport> getMissionReports(Long missionId) {
        return reportRepository.findByMissionIdOrderByCreatedAtDesc(missionId);
    }

    /**
     * Calculates mission progress based on completed waypoints.
     *
     * @param missionId the mission ID
     * @return mission progress percentage
     */
    public int calculateMissionProgress(Long missionId) {
        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new RuntimeException("Mission not found with id: " + missionId));

        List<MissionWaypoint> waypoints = mission.getWaypoints();
        if (waypoints.isEmpty()) {
            return mission.getCompletionPercentage();
        }

        long completedWaypoints = waypoints.stream()
            .mapToLong(wp -> wp.getIsReached() != null && wp.getIsReached() ? 1 : 0)
            .sum();

        int progress = (int) ((completedWaypoints * 100) / waypoints.size());
        
        // Update mission completion percentage
        mission.setCompletionPercentage(progress);
        missionRepository.save(mission);

        return progress;
    }

    /**
     * Deletes a mission.
     *
     * @param missionId the mission ID
     */
    public void deleteMission(Long missionId) {
        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new RuntimeException("Mission not found with id: " + missionId));

        // Can only delete missions in PLANNING or CANCELLED status
        if (mission.getStatus() != MissionStatus.PLANNING && 
            mission.getStatus() != MissionStatus.CANCELLED) {
            throw new IllegalStateException("Cannot delete mission with status: " + mission.getStatus());
        }

        missionRepository.delete(mission);
    }

    /**
     * Validates mission data.
     *
     * @param mission the mission to validate
     */
    private void validateMission(Mission mission) {
        if (mission.getMissionName() == null || mission.getMissionName().trim().isEmpty()) {
            throw new IllegalArgumentException("Mission name is required");
        }

        if (mission.getMissionCode() == null || mission.getMissionCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Mission code is required");
        }

        if (mission.getMissionType() == null) {
            throw new IllegalArgumentException("Mission type is required");
        }

        if (mission.getStartTime() == null) {
            throw new IllegalArgumentException("Start time is required");
        }

        if (mission.getEndTime() != null && mission.getStartTime().isAfter(mission.getEndTime())) {
            throw new IllegalArgumentException("Start time cannot be after end time");
        }
    }

    /**
     * Generates a unique mission code.
     *
     * @param mission the mission
     * @return generated mission code
     */
    private String generateMissionCode(Mission mission) {
        String typePrefix = mission.getMissionType() != null ? 
            mission.getMissionType().name().substring(0, Math.min(3, mission.getMissionType().name().length())) : "MSN";
        String timestamp = DateTimeUtils.formatToTacticalTime(LocalDateTime.now());
        return typePrefix.toUpperCase() + "-" + timestamp;
    }

    /**
     * Validates unit availability for mission assignment.
     *
     * @param unit the military unit
     */
    private void validateUnitAvailability(MilitaryUnit unit) {
        // Check if unit is already assigned to active missions
        List<Mission> activeMissions = missionRepository.findByAssignedUnitAndStatusIn(
            unit, List.of(MissionStatus.PLANNING, MissionStatus.ACTIVE));
        
        if (!activeMissions.isEmpty()) {
            throw new IllegalStateException("Unit is already assigned to active missions");
        }

        // Additional unit readiness checks can be added here
    }

    /**
     * Validates mission status transitions.
     *
     * @param currentStatus the current status
     * @param newStatus the new status
     */
    private void validateStatusTransition(MissionStatus currentStatus, MissionStatus newStatus) {
        // Define valid status transitions
        boolean validTransition = switch (currentStatus) {
            case PLANNING -> newStatus == MissionStatus.APPROVED || newStatus == MissionStatus.CANCELLED;
            case APPROVED -> newStatus == MissionStatus.ACTIVE || newStatus == MissionStatus.CANCELLED;
            case ACTIVE -> newStatus == MissionStatus.SUSPENDED || newStatus == MissionStatus.COMPLETED || 
                          newStatus == MissionStatus.CANCELLED || newStatus == MissionStatus.ABORTED;
            case SUSPENDED -> newStatus == MissionStatus.ACTIVE || newStatus == MissionStatus.CANCELLED || 
                             newStatus == MissionStatus.ABORTED;
            case COMPLETED, CANCELLED, ABORTED -> false; // Terminal states
        };
        
        if (!validTransition) {
            throw new IllegalStateException(
                String.format("Invalid status transition from %s to %s", currentStatus, newStatus));
        }
    }
}
