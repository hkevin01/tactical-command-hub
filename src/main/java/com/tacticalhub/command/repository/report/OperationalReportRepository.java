package com.tacticalhub.command.repository.report;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tacticalhub.command.domain.report.OperationalReport;
import com.tacticalhub.command.domain.report.OperationalReport.ReportStatus;
import com.tacticalhub.command.domain.report.OperationalReport.ReportType;

/**
 * Repository for OperationalReport entities
 */
@Repository
public interface OperationalReportRepository extends JpaRepository<OperationalReport, Long> {
    
    Optional<OperationalReport> findByReportNumber(String reportNumber);
    
    List<OperationalReport> findByAuthorId(Long authorId);
    
    List<OperationalReport> findByReportType(ReportType reportType);
    
    List<OperationalReport> findByStatus(ReportStatus status);
    
    @Query("SELECT r FROM OperationalReport r WHERE r.periodStart >= :startDate AND r.periodEnd <= :endDate")
    List<OperationalReport> findByPeriodRange(@Param("startDate") LocalDateTime startDate, 
                                            @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT r FROM OperationalReport r WHERE r.periodStart >= :startDate AND r.periodEnd <= :endDate AND r.reportType = :type")
    List<OperationalReport> findByPeriodRangeAndType(@Param("startDate") LocalDateTime startDate,
                                                    @Param("endDate") LocalDateTime endDate,
                                                    @Param("type") ReportType type);
    
    @Query("SELECT r FROM OperationalReport r WHERE r.status IN :statuses ORDER BY r.createdAt DESC")
    List<OperationalReport> findByStatusInOrderByCreatedAtDesc(@Param("statuses") List<ReportStatus> statuses);
    
    @Query("SELECT r FROM OperationalReport r WHERE r.authorId = :authorId AND r.status = :status")
    List<OperationalReport> findByAuthorIdAndStatus(@Param("authorId") Long authorId, 
                                                   @Param("status") ReportStatus status);
    
    @Query("SELECT DISTINCT r.reportType, COUNT(r) FROM OperationalReport r GROUP BY r.reportType")
    List<Object[]> getReportTypeStatistics();
    
    @Query("SELECT DISTINCT r.status, COUNT(r) FROM OperationalReport r GROUP BY r.status")
    List<Object[]> getReportStatusStatistics();
    
    @Query("SELECT r FROM OperationalReport r WHERE r.reviewedBy IS NULL AND r.status = 'UNDER_REVIEW'")
    List<OperationalReport> findPendingReview();
    
    @Query("SELECT r FROM OperationalReport r WHERE r.approvedBy IS NULL AND r.status = 'REVIEWED'")
    List<OperationalReport> findPendingApproval();
    
    @Query("SELECT COUNT(r) FROM OperationalReport r WHERE r.createdAt >= :startDate AND r.createdAt <= :endDate")
    Long countReportsInDateRange(@Param("startDate") LocalDateTime startDate, 
                                @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT r FROM OperationalReport r WHERE " +
           "(LOWER(r.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(r.executiveSummary) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(r.keyFindings) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "ORDER BY r.createdAt DESC")
    List<OperationalReport> searchReports(@Param("searchTerm") String searchTerm);
}
