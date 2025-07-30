package com.tacticalcommand.tactical.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tacticalcommand.tactical.domain.IntelligenceReport;
import com.tacticalcommand.tactical.domain.User;

/**
 * Repository for managing intelligence reports with security and search capabilities.
 */
@Repository
public interface IntelligenceReportRepository extends JpaRepository<IntelligenceReport, Long> {

    /**
     * Find intelligence report by report number.
     *
     * @param reportNumber Unique report identifier
     * @return Optional containing the report if found
     */
    Optional<IntelligenceReport> findByReportNumber(String reportNumber);

    /**
     * Find reports by classification level ordered by creation time.
     *
     * @param classification Security classification level
     * @param pageable Pagination parameters
     * @return Page of reports with specified classification
     */
    Page<IntelligenceReport> findByClassificationOrderByCreatedAtDesc(String classification, Pageable pageable);

    /**
     * Find reports by threat level ordered by creation time.
     *
     * @param threatLevel Threat severity level
     * @param pageable Pagination parameters
     * @return Page of reports with specified threat level
     */
    Page<IntelligenceReport> findByThreatLevelOrderByCreatedAtDesc(String threatLevel, Pageable pageable);

    /**
     * Find reports by author ordered by creation time.
     *
     * @param author Report author
     * @param pageable Pagination parameters
     * @return Page of reports by the specified author
     */
    Page<IntelligenceReport> findByAuthorOrderByCreatedAtDesc(User author, Pageable pageable);

    /**
     * Find reports by review status.
     *
     * @param reviewStatus Review status (PENDING, APPROVED, REJECTED)
     * @param pageable Pagination parameters
     * @return Page of reports with specified review status
     */
    Page<IntelligenceReport> findByReviewStatusOrderByCreatedAtDesc(String reviewStatus, Pageable pageable);

    /**
     * Find reports within a time range.
     *
     * @param startTime Start of time range
     * @param endTime End of time range
     * @param pageable Pagination parameters
     * @return Page of reports within time range
     */
    Page<IntelligenceReport> findByCreatedAtBetweenOrderByCreatedAtDesc(
            LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);

    /**
     * Find reports by geographic area.
     *
     * @param geographicArea Geographic region
     * @param pageable Pagination parameters
     * @return Page of reports for specified geographic area
     */
    Page<IntelligenceReport> findByGeographicAreaContainingIgnoreCaseOrderByCreatedAtDesc(
            String geographicArea, Pageable pageable);

    /**
     * Find reports within geographic radius of a point.
     *
     * @param latitude Center latitude
     * @param longitude Center longitude
     * @param radiusKm Search radius in kilometers
     * @param pageable Pagination parameters
     * @return Page of reports within geographic radius
     */
    @Query("SELECT ir FROM IntelligenceReport ir WHERE " +
           "ir.latitude IS NOT NULL AND ir.longitude IS NOT NULL AND " +
           "SQRT(POWER((ir.latitude - :latitude) * 111.0, 2) + " +
           "POWER((ir.longitude - :longitude) * 111.0 * COS(RADIANS(ir.latitude)), 2)) <= :radiusKm " +
           "ORDER BY ir.createdAt DESC")
    Page<IntelligenceReport> findByGeographicProximity(
            @Param("latitude") Double latitude,
            @Param("longitude") Double longitude,
            @Param("radiusKm") Double radiusKm,
            Pageable pageable);

    /**
     * Find reports by threat type and target type.
     *
     * @param threatType Type of threat
     * @param targetType Type of target
     * @param pageable Pagination parameters
     * @return Page of reports matching threat and target types
     */
    Page<IntelligenceReport> findByThreatTypeAndTargetTypeOrderByCreatedAtDesc(
            String threatType, String targetType, Pageable pageable);

    /**
     * Find reports by source type.
     *
     * @param sourceType Intelligence source type (HUMINT, SIGINT, etc.)
     * @param pageable Pagination parameters
     * @return Page of reports from specified source type
     */
    Page<IntelligenceReport> findBySourceTypeOrderByCreatedAtDesc(String sourceType, Pageable pageable);

    /**
     * Find reports by time sensitivity.
     *
     * @param timeSensitivity Time sensitivity level
     * @param pageable Pagination parameters
     * @return Page of reports with specified time sensitivity
     */
    Page<IntelligenceReport> findByTimeSensitivityOrderByCreatedAtDesc(String timeSensitivity, Pageable pageable);

    /**
     * Find expired reports for cleanup.
     *
     * @param currentTime Current timestamp
     * @return List of expired reports
     */
    List<IntelligenceReport> findByExpiresAtBefore(LocalDateTime currentTime);

    /**
     * Find immediate priority reports that are unread.
     *
     * @return List of immediate priority reports
     */
    @Query("SELECT ir FROM IntelligenceReport ir WHERE " +
           "ir.timeSensitivity = 'IMMEDIATE' AND ir.reviewStatus = 'PENDING' " +
           "ORDER BY ir.createdAt ASC")
    List<IntelligenceReport> findImmediatePriorityReports();

    /**
     * Search reports by content and title.
     *
     * @param searchTerm Search term to match
     * @param pageable Pagination parameters
     * @return Page of matching reports
     */
    @Query("SELECT ir FROM IntelligenceReport ir WHERE " +
           "LOWER(ir.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(ir.content) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(ir.summary) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "ORDER BY ir.createdAt DESC")
    Page<IntelligenceReport> searchReports(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Find reports by multiple threat levels (for dashboard queries).
     *
     * @param threatLevels List of threat levels to match
     * @param pageable Pagination parameters
     * @return Page of reports matching any of the threat levels
     */
    Page<IntelligenceReport> findByThreatLevelInOrderByCreatedAtDesc(List<String> threatLevels, Pageable pageable);

    /**
     * Count reports by classification for statistics.
     *
     * @param classification Security classification
     * @return Number of reports with specified classification
     */
    long countByClassification(String classification);

    /**
     * Count reports by threat level for statistics.
     *
     * @param threatLevel Threat severity level
     * @return Number of reports with specified threat level
     */
    long countByThreatLevel(String threatLevel);

    /**
     * Count reports by review status for workflow management.
     *
     * @param reviewStatus Review status
     * @return Number of reports with specified review status
     */
    long countByReviewStatus(String reviewStatus);

    /**
     * Find recent reports within specified hours for activity monitoring.
     *
     * @param hoursAgo Number of hours back to search
     * @return List of recent reports
     */
    @Query("SELECT ir FROM IntelligenceReport ir WHERE ir.createdAt >= :sinceTime " +
           "ORDER BY ir.createdAt DESC")
    List<IntelligenceReport> findRecentReports(@Param("sinceTime") LocalDateTime sinceTime);

    /**
     * Find reports by classification and author for user-specific queries.
     *
     * @param classification Security classification
     * @param author Report author
     * @param pageable Pagination parameters
     * @return Page of reports matching classification and author
     */
    Page<IntelligenceReport> findByClassificationAndAuthorOrderByCreatedAtDesc(
            String classification, User author, Pageable pageable);
}
