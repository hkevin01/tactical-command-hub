package com.tacticalhub.command.repository.analytics;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tacticalhub.command.domain.analytics.AnalyticsMetric;
import com.tacticalhub.command.domain.analytics.AnalyticsMetric.MetricCategory;
import com.tacticalhub.command.domain.analytics.AnalyticsMetric.MetricType;

/**
 * Repository for AnalyticsMetric entities
 */
@Repository
public interface AnalyticsMetricRepository extends JpaRepository<AnalyticsMetric, Long> {
    
    List<AnalyticsMetric> findByMetricName(String metricName);
    
    List<AnalyticsMetric> findByMetricType(MetricType metricType);
    
    List<AnalyticsMetric> findByCategory(MetricCategory category);
    
    List<AnalyticsMetric> findByUnitId(Long unitId);
    
    List<AnalyticsMetric> findByMissionId(Long missionId);
    
    @Query("SELECT m FROM AnalyticsMetric m WHERE m.timestamp >= :startTime AND m.timestamp <= :endTime ORDER BY m.timestamp")
    List<AnalyticsMetric> findByTimeRange(@Param("startTime") LocalDateTime startTime,
                                         @Param("endTime") LocalDateTime endTime);
    
    @Query("SELECT m FROM AnalyticsMetric m WHERE m.metricName = :metricName AND " +
           "m.timestamp >= :startTime AND m.timestamp <= :endTime ORDER BY m.timestamp")
    List<AnalyticsMetric> findByMetricNameAndTimeRange(@Param("metricName") String metricName,
                                                      @Param("startTime") LocalDateTime startTime,
                                                      @Param("endTime") LocalDateTime endTime);
    
    @Query("SELECT m FROM AnalyticsMetric m WHERE m.category = :category AND " +
           "m.timestamp >= :startTime AND m.timestamp <= :endTime ORDER BY m.timestamp")
    List<AnalyticsMetric> findByCategoryAndTimeRange(@Param("category") MetricCategory category,
                                                    @Param("startTime") LocalDateTime startTime,
                                                    @Param("endTime") LocalDateTime endTime);
    
    @Query("SELECT m FROM AnalyticsMetric m WHERE m.unitId = :unitId AND " +
           "m.timestamp >= :startTime AND m.timestamp <= :endTime ORDER BY m.timestamp")
    List<AnalyticsMetric> findByUnitIdAndTimeRange(@Param("unitId") Long unitId,
                                                  @Param("startTime") LocalDateTime startTime,
                                                  @Param("endTime") LocalDateTime endTime);
    
    @Query("SELECT AVG(m.value) FROM AnalyticsMetric m WHERE m.metricName = :metricName AND " +
           "m.timestamp >= :startTime AND m.timestamp <= :endTime")
    Double getAverageValue(@Param("metricName") String metricName,
                          @Param("startTime") LocalDateTime startTime,
                          @Param("endTime") LocalDateTime endTime);
    
    @Query("SELECT MAX(m.value) FROM AnalyticsMetric m WHERE m.metricName = :metricName AND " +
           "m.timestamp >= :startTime AND m.timestamp <= :endTime")
    Double getMaxValue(@Param("metricName") String metricName,
                      @Param("startTime") LocalDateTime startTime,
                      @Param("endTime") LocalDateTime endTime);
    
    @Query("SELECT MIN(m.value) FROM AnalyticsMetric m WHERE m.metricName = :metricName AND " +
           "m.timestamp >= :startTime AND m.timestamp <= :endTime")
    Double getMinValue(@Param("metricName") String metricName,
                      @Param("startTime") LocalDateTime startTime,
                      @Param("endTime") LocalDateTime endTime);
    
    @Query("SELECT COUNT(m) FROM AnalyticsMetric m WHERE m.metricName = :metricName AND " +
           "m.timestamp >= :startTime AND m.timestamp <= :endTime")
    Long getMetricCount(@Param("metricName") String metricName,
                       @Param("startTime") LocalDateTime startTime,
                       @Param("endTime") LocalDateTime endTime);
    
    @Query("SELECT DISTINCT m.metricName FROM AnalyticsMetric m WHERE m.category = :category")
    List<String> getMetricNamesByCategory(@Param("category") MetricCategory category);
    
    @Query("SELECT m.category, COUNT(m) FROM AnalyticsMetric m GROUP BY m.category")
    List<Object[]> getMetricCountByCategory();
    
    @Query("SELECT m FROM AnalyticsMetric m WHERE " +
           "EXISTS (SELECT 1 FROM m.tags t WHERE t = :tag)")
    List<AnalyticsMetric> findByTag(@Param("tag") String tag);
}
