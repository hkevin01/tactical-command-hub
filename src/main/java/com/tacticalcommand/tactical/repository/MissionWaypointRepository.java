package com.tacticalcommand.tactical.repository;

import com.tacticalcommand.tactical.domain.MissionWaypoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for MissionWaypoint entities.
 * Provides CRUD operations and custom queries for mission waypoints.
 */
@Repository
public interface MissionWaypointRepository extends JpaRepository<MissionWaypoint, Long> {

    /**
     * Find all waypoints for a specific mission.
     *
     * @param missionId the mission ID
     * @return list of waypoints for the mission
     */
    List<MissionWaypoint> findByMissionIdOrderBySequenceNumber(Long missionId);

    /**
     * Find waypoints by mission ID and status.
     *
     * @param missionId the mission ID
     * @param status the waypoint status
     * @return list of waypoints matching the criteria
     */
    @Query("SELECT w FROM MissionWaypoint w WHERE w.mission.id = :missionId AND w.status = :status")
    List<MissionWaypoint> findByMissionIdAndStatus(@Param("missionId") Long missionId, @Param("status") String status);

    /**
     * Count completed waypoints for a mission.
     *
     * @param missionId the mission ID
     * @return number of completed waypoints
     */
    @Query("SELECT COUNT(w) FROM MissionWaypoint w WHERE w.mission.id = :missionId AND w.status = 'COMPLETED'")
    Long countCompletedWaypointsByMissionId(@Param("missionId") Long missionId);

    /**
     * Count total waypoints for a mission.
     *
     * @param missionId the mission ID
     * @return total number of waypoints
     */
    Long countByMissionId(Long missionId);

    /**
     * Delete all waypoints for a specific mission.
     *
     * @param missionId the mission ID
     */
    void deleteByMissionId(Long missionId);
}
