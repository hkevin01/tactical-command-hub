package com.tacticalhub.command.domain.analytics;

import java.time.LocalDateTime;
import java.util.Map;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Entity representing analytics metrics for operational performance tracking
 */
@Entity
@Table(name = "analytics_metrics", indexes = {
    @Index(name = "idx_analytics_timestamp", columnList = "timestamp"),
    @Index(name = "idx_analytics_type", columnList = "metricType"),
    @Index(name = "idx_analytics_category", columnList = "category"),
    @Index(name = "idx_analytics_unit", columnList = "unitId")
})
public class AnalyticsMetric {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Column(nullable = false)
    private String metricName;
    
    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false)
    private MetricType metricType;
    
    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false)
    private MetricCategory category;
    
    @NotNull
    @Column(nullable = false)
    private Double value;
    
    @Column
    private String unit;
    
    @Column
    private Long unitId;
    
    @Column
    private Long missionId;
    
    @ElementCollection
    @CollectionTable(name = "metric_dimensions", 
                    joinColumns = @JoinColumn(name = "metric_id"))
    @MapKeyColumn(name = "dimension_name")
    @Column(name = "dimension_value")
    private Map<String, String> dimensions;
    
    @ElementCollection
    @CollectionTable(name = "metric_tags",
                    joinColumns = @JoinColumn(name = "metric_id"))
    @Column(name = "tag_value")
    private java.util.Set<String> tags;
    
    @NotNull
    @Column(nullable = false)
    private LocalDateTime timestamp;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    // Constructors
    public AnalyticsMetric() {}
    
    public AnalyticsMetric(String metricName, MetricType metricType, MetricCategory category,
                          Double value, LocalDateTime timestamp) {
        this.metricName = metricName;
        this.metricType = metricType;
        this.category = category;
        this.value = value;
        this.timestamp = timestamp;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getMetricName() { return metricName; }
    public void setMetricName(String metricName) { this.metricName = metricName; }
    
    public MetricType getMetricType() { return metricType; }
    public void setMetricType(MetricType metricType) { this.metricType = metricType; }
    
    public MetricCategory getCategory() { return category; }
    public void setCategory(MetricCategory category) { this.category = category; }
    
    public Double getValue() { return value; }
    public void setValue(Double value) { this.value = value; }
    
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    
    public Long getUnitId() { return unitId; }
    public void setUnitId(Long unitId) { this.unitId = unitId; }
    
    public Long getMissionId() { return missionId; }
    public void setMissionId(Long missionId) { this.missionId = missionId; }
    
    public Map<String, String> getDimensions() { return dimensions; }
    public void setDimensions(Map<String, String> dimensions) { this.dimensions = dimensions; }
    
    public java.util.Set<String> getTags() { return tags; }
    public void setTags(java.util.Set<String> tags) { this.tags = tags; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public enum MetricType {
        COUNTER,          // Incrementing value
        GAUGE,            // Point-in-time value
        HISTOGRAM,        // Distribution of values
        TIMER,            // Duration measurements
        RATE              // Events per time unit
    }
    
    public enum MetricCategory {
        OPERATIONAL,      // Mission execution metrics
        PERFORMANCE,      // System/unit performance
        RESOURCE,         // Resource utilization
        COMMUNICATION,    // Communication effectiveness
        INTELLIGENCE,     // Intelligence gathering/analysis
        LOGISTICS,        // Supply and logistics
        PERSONNEL,        // Personnel metrics
        SECURITY,         // Security incidents/compliance
        TRAINING,         // Training effectiveness
        EQUIPMENT         // Equipment status/utilization
    }
}
