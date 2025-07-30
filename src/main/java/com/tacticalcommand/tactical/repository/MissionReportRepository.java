package com.tacticalcommand.tactical.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tacticalcommand.tactical.domain.MissionReport;

/**
 * Repository interface for MissionReport entities.
 * Provides CRUD operations and custom queries for mission reports.
 */
@Repository
public interface MissionReportRepository extends JpaRepository<MissionReport, Long> {

    /**
     * Find all reports for a specific mission.
     *
     * @param missionId the mission ID
     * @return list of reports for the mission ordered by creation time
     */
    List<MissionReport> findByMissionIdOrderByCreatedAtDesc(Long missionId);

    /**
     * Find reports by mission ID and report type.
     *
     * @param missionId the mission ID
     * @param reportType the report type
     * @return list of reports matching the criteria
     */
    List<MissionReport> findByMissionIdAndReportType(Long missionId, String reportType);

    /**
     * Find reports created within a date range.
     *
     * @param missionId the mission ID
     * @param startDate the start date
     * @param endDate the end date
     * @return list of reports within the date range
     */
    @Query("SELECT r FROM MissionReport r WHERE r.mission.id = :missionId AND r.createdAt BETWEEN :startDate AND :endDate ORDER BY r.createdAt DESC")
    List<MissionReport> findByMissionIdAndCreatedAtBetween(
            @Param("missionId") Long missionId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * Find the latest report for a mission.
     *
     * @param missionId the mission ID
     * @return the most recent report for the mission
     */
    @Query("SELECT r FROM MissionReport r WHERE r.mission.id = :missionId ORDER BY r.createdAt DESC")
    List<MissionReport> findLatestByMissionId(@Param("missionId") Long missionId);

    /**
     * Count reports for a mission.
     *
     * @param missionId the mission ID
     * @return total number of reports
     */
    Long countByMissionId(Long missionId);

    /**
     * Delete all reports for a specific mission.
     *
     * @param missionId the mission ID
     */
    void deleteByMissionId(Long missionId);
}
