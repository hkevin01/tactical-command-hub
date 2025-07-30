package com.tacticalhub.command.service.analytics;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tacticalhub.command.domain.analytics.AnalyticsMetric;
import com.tacticalhub.command.domain.analytics.AnalyticsMetric.MetricCategory;
import com.tacticalhub.command.domain.analytics.AnalyticsMetric.MetricType;
import com.tacticalhub.command.repository.analytics.AnalyticsMetricRepository;

/**
 * Service for managing analytics metrics and generating insights
 */
@Service
@Transactional
public class AnalyticsService {
    
    private final AnalyticsMetricRepository analyticsRepository;
    
    @Autowired
    public AnalyticsService(AnalyticsMetricRepository analyticsRepository) {
        this.analyticsRepository = analyticsRepository;
    }
    
    /**
     * Record a new metric
     */
    public AnalyticsMetric recordMetric(String metricName, MetricType metricType, 
                                       MetricCategory category, Double value) {
        return recordMetric(metricName, metricType, category, value, LocalDateTime.now(), null, null, null);
    }
    
    /**
     * Record a new metric with additional context
     */
    public AnalyticsMetric recordMetric(String metricName, MetricType metricType, MetricCategory category,
                                       Double value, LocalDateTime timestamp, Long unitId, Long missionId,
                                       Map<String, String> dimensions) {
        AnalyticsMetric metric = new AnalyticsMetric(metricName, metricType, category, value, timestamp);
        metric.setUnitId(unitId);
        metric.setMissionId(missionId);
        metric.setDimensions(dimensions);
        
        return analyticsRepository.save(metric);
    }
    
    /**
     * Get metrics by name and time range
     */
    @Transactional(readOnly = true)
    public List<AnalyticsMetric> getMetrics(String metricName, LocalDateTime startTime, LocalDateTime endTime) {
        return analyticsRepository.findByMetricNameAndTimeRange(metricName, startTime, endTime);
    }
    
    /**
     * Get metrics by category and time range
     */
    @Transactional(readOnly = true)
    public List<AnalyticsMetric> getMetricsByCategory(MetricCategory category, LocalDateTime startTime, LocalDateTime endTime) {
        return analyticsRepository.findByCategoryAndTimeRange(category, startTime, endTime);
    }
    
    /**
     * Get metrics for a specific unit
     */
    @Transactional(readOnly = true)
    public List<AnalyticsMetric> getUnitMetrics(Long unitId, LocalDateTime startTime, LocalDateTime endTime) {
        return analyticsRepository.findByUnitIdAndTimeRange(unitId, startTime, endTime);
    }
    
    /**
     * Calculate metric statistics
     */
    @Transactional(readOnly = true)
    public MetricStatistics getMetricStatistics(String metricName, LocalDateTime startTime, LocalDateTime endTime) {
        Double average = analyticsRepository.getAverageValue(metricName, startTime, endTime);
        Double max = analyticsRepository.getMaxValue(metricName, startTime, endTime);
        Double min = analyticsRepository.getMinValue(metricName, startTime, endTime);
        Long count = analyticsRepository.getMetricCount(metricName, startTime, endTime);
        
        return new MetricStatistics(metricName, average, max, min, count, startTime, endTime);
    }
    
    /**
     * Get operational dashboard data
     */
    @Transactional(readOnly = true)
    public OperationalDashboard getOperationalDashboard() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime dayStart = now.truncatedTo(ChronoUnit.DAYS);
        LocalDateTime weekStart = now.minusDays(7);
        
        // Get key metrics for the dashboard
        List<AnalyticsMetric> todayMetrics = analyticsRepository.findByTimeRange(dayStart, now);
        List<AnalyticsMetric> weekMetrics = analyticsRepository.findByTimeRange(weekStart, now);
        
        // Calculate operational metrics
        Map<MetricCategory, Long> categoryBreakdown = analyticsRepository.getMetricCountByCategory()
            .stream()
            .collect(Collectors.toMap(
                row -> (MetricCategory) row[0],
                row -> (Long) row[1]
            ));
        
        // Mission effectiveness metrics
        List<AnalyticsMetric> missionMetrics = analyticsRepository.findByCategory(MetricCategory.OPERATIONAL);
        double missionEffectiveness = calculateMissionEffectiveness(missionMetrics);
        
        // Communication efficiency
        List<AnalyticsMetric> commMetrics = analyticsRepository.findByCategory(MetricCategory.COMMUNICATION);
        double communicationEfficiency = calculateCommunicationEfficiency(commMetrics);
        
        // Resource utilization
        List<AnalyticsMetric> resourceMetrics = analyticsRepository.findByCategory(MetricCategory.RESOURCE);
        double resourceUtilization = calculateResourceUtilization(resourceMetrics);
        
        return new OperationalDashboard(
            todayMetrics.size(),
            weekMetrics.size(),
            categoryBreakdown,
            missionEffectiveness,
            communicationEfficiency,
            resourceUtilization
        );
    }
    
    /**
     * Generate trend analysis for a metric
     */
    @Transactional(readOnly = true)
    public TrendAnalysis getTrendAnalysis(String metricName, LocalDateTime startTime, LocalDateTime endTime) {
        List<AnalyticsMetric> metrics = analyticsRepository.findByMetricNameAndTimeRange(metricName, startTime, endTime);
        
        if (metrics.isEmpty()) {
            return new TrendAnalysis(metricName, "NO_DATA", 0.0, Collections.emptyList());
        }
        
        // Calculate trend
        List<Double> values = metrics.stream().map(AnalyticsMetric::getValue).collect(Collectors.toList());
        String trend = calculateTrend(values);
        double changePercent = calculateChangePercent(values);
        
        // Group by time periods for visualization
        Map<LocalDateTime, Double> timeSeries = metrics.stream()
            .collect(Collectors.groupingBy(
                m -> m.getTimestamp().truncatedTo(ChronoUnit.HOURS),
                Collectors.averagingDouble(AnalyticsMetric::getValue)
            ));
        
        List<TimeSeriesPoint> points = timeSeries.entrySet().stream()
            .map(entry -> new TimeSeriesPoint(entry.getKey(), entry.getValue()))
            .sorted(Comparator.comparing(TimeSeriesPoint::getTimestamp))
            .collect(Collectors.toList());
        
        return new TrendAnalysis(metricName, trend, changePercent, points);
    }
    
    /**
     * Get performance alerts
     */
    @Transactional(readOnly = true)
    public List<PerformanceAlert> getPerformanceAlerts() {
        List<PerformanceAlert> alerts = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime hourAgo = now.minusHours(1);
        
        // Check for critical metrics
        List<AnalyticsMetric> recentMetrics = analyticsRepository.findByTimeRange(hourAgo, now);
        
        // Analyze metrics for anomalies
        Map<String, List<AnalyticsMetric>> metricGroups = recentMetrics.stream()
            .collect(Collectors.groupingBy(AnalyticsMetric::getMetricName));
        
        for (Map.Entry<String, List<AnalyticsMetric>> entry : metricGroups.entrySet()) {
            String metricName = entry.getKey();
            List<AnalyticsMetric> metrics = entry.getValue();
            
            // Check for anomalies
            PerformanceAlert alert = detectAnomalies(metricName, metrics);
            if (alert != null) {
                alerts.add(alert);
            }
        }
        
        return alerts;
    }
    
    // Helper methods
    
    private double calculateMissionEffectiveness(List<AnalyticsMetric> metrics) {
        return metrics.stream()
            .filter(m -> "mission_success_rate".equals(m.getMetricName()))
            .mapToDouble(AnalyticsMetric::getValue)
            .average()
            .orElse(0.0);
    }
    
    private double calculateCommunicationEfficiency(List<AnalyticsMetric> metrics) {
        return metrics.stream()
            .filter(m -> "message_delivery_rate".equals(m.getMetricName()))
            .mapToDouble(AnalyticsMetric::getValue)
            .average()
            .orElse(0.0);
    }
    
    private double calculateResourceUtilization(List<AnalyticsMetric> metrics) {
        return metrics.stream()
            .filter(m -> "resource_utilization".equals(m.getMetricName()))
            .mapToDouble(AnalyticsMetric::getValue)
            .average()
            .orElse(0.0);
    }
    
    private String calculateTrend(List<Double> values) {
        if (values.size() < 2) return "STABLE";
        
        double first = values.get(0);
        double last = values.get(values.size() - 1);
        double change = (last - first) / first * 100;
        
        if (change > 5) return "INCREASING";
        else if (change < -5) return "DECREASING";
        else return "STABLE";
    }
    
    private double calculateChangePercent(List<Double> values) {
        if (values.size() < 2) return 0.0;
        
        double first = values.get(0);
        double last = values.get(values.size() - 1);
        
        if (first == 0) return 0.0;
        return (last - first) / first * 100;
    }
    
    private PerformanceAlert detectAnomalies(String metricName, List<AnalyticsMetric> metrics) {
        if (metrics.size() < 3) return null;
        
        // Simple anomaly detection based on standard deviation
        double mean = metrics.stream().mapToDouble(AnalyticsMetric::getValue).average().orElse(0.0);
        double variance = metrics.stream()
            .mapToDouble(m -> Math.pow(m.getValue() - mean, 2))
            .average()
            .orElse(0.0);
        double stdDev = Math.sqrt(variance);
        
        // Check for values outside 2 standard deviations
        for (AnalyticsMetric metric : metrics) {
            if (Math.abs(metric.getValue() - mean) > 2 * stdDev) {
                return new PerformanceAlert(
                    metricName,
                    "ANOMALY_DETECTED",
                    String.format("Value %.2f is outside normal range (mean: %.2f, stddev: %.2f)", 
                                metric.getValue(), mean, stdDev),
                    metric.getTimestamp()
                );
            }
        }
        
        return null;
    }
    
    // Data classes
    
    public static class MetricStatistics {
        private final String metricName;
        private final Double average;
        private final Double max;
        private final Double min;
        private final Long count;
        private final LocalDateTime startTime;
        private final LocalDateTime endTime;
        
        public MetricStatistics(String metricName, Double average, Double max, Double min, 
                              Long count, LocalDateTime startTime, LocalDateTime endTime) {
            this.metricName = metricName;
            this.average = average;
            this.max = max;
            this.min = min;
            this.count = count;
            this.startTime = startTime;
            this.endTime = endTime;
        }
        
        // Getters
        public String getMetricName() { return metricName; }
        public Double getAverage() { return average; }
        public Double getMax() { return max; }
        public Double getMin() { return min; }
        public Long getCount() { return count; }
        public LocalDateTime getStartTime() { return startTime; }
        public LocalDateTime getEndTime() { return endTime; }
    }
    
    public static class OperationalDashboard {
        private final int todayMetrics;
        private final int weekMetrics;
        private final Map<MetricCategory, Long> categoryBreakdown;
        private final double missionEffectiveness;
        private final double communicationEfficiency;
        private final double resourceUtilization;
        
        public OperationalDashboard(int todayMetrics, int weekMetrics, Map<MetricCategory, Long> categoryBreakdown,
                                  double missionEffectiveness, double communicationEfficiency, double resourceUtilization) {
            this.todayMetrics = todayMetrics;
            this.weekMetrics = weekMetrics;
            this.categoryBreakdown = categoryBreakdown;
            this.missionEffectiveness = missionEffectiveness;
            this.communicationEfficiency = communicationEfficiency;
            this.resourceUtilization = resourceUtilization;
        }
        
        // Getters
        public int getTodayMetrics() { return todayMetrics; }
        public int getWeekMetrics() { return weekMetrics; }
        public Map<MetricCategory, Long> getCategoryBreakdown() { return categoryBreakdown; }
        public double getMissionEffectiveness() { return missionEffectiveness; }
        public double getCommunicationEfficiency() { return communicationEfficiency; }
        public double getResourceUtilization() { return resourceUtilization; }
    }
    
    public static class TrendAnalysis {
        private final String metricName;
        private final String trend;
        private final double changePercent;
        private final List<TimeSeriesPoint> timeSeries;
        
        public TrendAnalysis(String metricName, String trend, double changePercent, List<TimeSeriesPoint> timeSeries) {
            this.metricName = metricName;
            this.trend = trend;
            this.changePercent = changePercent;
            this.timeSeries = timeSeries;
        }
        
        // Getters
        public String getMetricName() { return metricName; }
        public String getTrend() { return trend; }
        public double getChangePercent() { return changePercent; }
        public List<TimeSeriesPoint> getTimeSeries() { return timeSeries; }
    }
    
    public static class TimeSeriesPoint {
        private final LocalDateTime timestamp;
        private final Double value;
        
        public TimeSeriesPoint(LocalDateTime timestamp, Double value) {
            this.timestamp = timestamp;
            this.value = value;
        }
        
        public LocalDateTime getTimestamp() { return timestamp; }
        public Double getValue() { return value; }
    }
    
    public static class PerformanceAlert {
        private final String metricName;
        private final String alertType;
        private final String description;
        private final LocalDateTime timestamp;
        
        public PerformanceAlert(String metricName, String alertType, String description, LocalDateTime timestamp) {
            this.metricName = metricName;
            this.alertType = alertType;
            this.description = description;
            this.timestamp = timestamp;
        }
        
        // Getters
        public String getMetricName() { return metricName; }
        public String getAlertType() { return alertType; }
        public String getDescription() { return description; }
        public LocalDateTime getTimestamp() { return timestamp; }
    }
}
