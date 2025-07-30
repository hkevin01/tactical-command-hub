package com.tacticalcommand.tactical.domain;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Mission Report entity for tracking mission progress and status updates.
 * 
 * This entity captures mission reports and status updates from field commanders, providing
 * real-time visibility into mission execution and progress.
 * 
 * @author Tactical Command Hub Team
 * @version 1.0.0
 * @since 2025-07-29
 */
@Entity
@Table(name = "mission_reports",
    indexes = {@Index(name = "idx_report_mission", columnList = "mission_id"),
        @Index(name = "idx_report_timestamp", columnList = "report_timestamp"),
        @Index(name = "idx_report_type", columnList = "report_type")})
public class MissionReport extends BaseEntity {

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "mission_id", nullable = false)
  private Mission mission;

  @NotNull(message = "Report type is required")
  @Enumerated(EnumType.STRING)
  @Column(name = "report_type", nullable = false, length = 20)
  private ReportType reportType;

  @NotNull
  @Column(name = "report_timestamp", nullable = false)
  private LocalDateTime reportTimestamp;

  @NotBlank(message = "Reporter name is required")
  @Size(max = 100, message = "Reporter name must not exceed 100 characters")
  @Column(name = "reporter", nullable = false, length = 100)
  private String reporter;

  @Size(max = 100, message = "Reporter rank must not exceed 100 characters")
  @Column(name = "reporter_rank", length = 100)
  private String reporterRank;

  @NotBlank(message = "Report content is required")
  @Size(max = 2000, message = "Report content must not exceed 2000 characters")
  @Column(name = "report_content", nullable = false, length = 2000)
  private String reportContent;

  @Enumerated(EnumType.STRING)
  @Column(name = "status_update", length = 20)
  private Mission.MissionStatus statusUpdate;

  @Column(name = "completion_percentage")
  private Integer completionPercentage;

  @Column(name = "casualties_friendly")
  private Integer casualtiesFriendly;

  @Column(name = "casualties_enemy")
  private Integer casualtiesEnemy;

  @Column(name = "equipment_status", length = 500)
  private String equipmentStatus;

  @Column(name = "resource_consumption", length = 500)
  private String resourceConsumption;

  @Enumerated(EnumType.STRING)
  @Column(name = "threat_level", length = 10)
  private MilitaryUnit.ThreatLevel threatLevel;

  @Column(name = "weather_conditions", length = 200)
  private String weatherConditions;

  @Column(name = "next_checkpoint_eta")
  private LocalDateTime nextCheckpointEta;

  @Column(name = "requires_support")
  private Boolean requiresSupport = false;

  @Column(name = "support_requested", length = 500)
  private String supportRequested;

  /**
   * Default constructor.
   */
  public MissionReport() {
    super();
    this.reportTimestamp = LocalDateTime.now();
    this.requiresSupport = false;
  }

  /**
   * Constructor with required fields.
   */
  public MissionReport(Mission mission, ReportType reportType, String reporter,
      String reportContent) {
    this();
    this.mission = mission;
    this.reportType = reportType;
    this.reporter = reporter;
    this.reportContent = reportContent;
  }

  // Getters and Setters

  public Mission getMission() {
    return mission;
  }

  public void setMission(Mission mission) {
    this.mission = mission;
  }

  public ReportType getReportType() {
    return reportType;
  }

  public void setReportType(ReportType reportType) {
    this.reportType = reportType;
  }

  public LocalDateTime getReportTimestamp() {
    return reportTimestamp;
  }

  public void setReportTimestamp(LocalDateTime reportTimestamp) {
    this.reportTimestamp = reportTimestamp;
  }

  public String getReporter() {
    return reporter;
  }

  public void setReporter(String reporter) {
    this.reporter = reporter;
  }

  public String getReporterRank() {
    return reporterRank;
  }

  public void setReporterRank(String reporterRank) {
    this.reporterRank = reporterRank;
  }

  public String getReportContent() {
    return reportContent;
  }

  public void setReportContent(String reportContent) {
    this.reportContent = reportContent;
  }

  public Mission.MissionStatus getStatusUpdate() {
    return statusUpdate;
  }

  public void setStatusUpdate(Mission.MissionStatus statusUpdate) {
    this.statusUpdate = statusUpdate;
  }

  public Integer getCompletionPercentage() {
    return completionPercentage;
  }

  public void setCompletionPercentage(Integer completionPercentage) {
    this.completionPercentage = completionPercentage;
  }

  public Integer getCasualtiesFriendly() {
    return casualtiesFriendly;
  }

  public void setCasualtiesFriendly(Integer casualtiesFriendly) {
    this.casualtiesFriendly = casualtiesFriendly;
  }

  public Integer getCasualtiesEnemy() {
    return casualtiesEnemy;
  }

  public void setCasualtiesEnemy(Integer casualtiesEnemy) {
    this.casualtiesEnemy = casualtiesEnemy;
  }

  public String getEquipmentStatus() {
    return equipmentStatus;
  }

  public void setEquipmentStatus(String equipmentStatus) {
    this.equipmentStatus = equipmentStatus;
  }

  public String getResourceConsumption() {
    return resourceConsumption;
  }

  public void setResourceConsumption(String resourceConsumption) {
    this.resourceConsumption = resourceConsumption;
  }

  public MilitaryUnit.ThreatLevel getThreatLevel() {
    return threatLevel;
  }

  public void setThreatLevel(MilitaryUnit.ThreatLevel threatLevel) {
    this.threatLevel = threatLevel;
  }

  public String getWeatherConditions() {
    return weatherConditions;
  }

  public void setWeatherConditions(String weatherConditions) {
    this.weatherConditions = weatherConditions;
  }

  public LocalDateTime getNextCheckpointEta() {
    return nextCheckpointEta;
  }

  public void setNextCheckpointEta(LocalDateTime nextCheckpointEta) {
    this.nextCheckpointEta = nextCheckpointEta;
  }

  public Boolean getRequiresSupport() {
    return requiresSupport;
  }

  public void setRequiresSupport(Boolean requiresSupport) {
    this.requiresSupport = requiresSupport;
  }

  public String getSupportRequested() {
    return supportRequested;
  }

  public void setSupportRequested(String supportRequested) {
    this.supportRequested = supportRequested;
  }

  /**
   * Enum for report types.
   */
  public enum ReportType {
    SITREP, // Situation Report
    PROGRESS, // Progress Update
    CONTACT, // Enemy Contact
    CASUALTY, // Casualty Report
    EQUIPMENT, // Equipment Status
    RESUPPLY, // Resupply Request
    WEATHER, // Weather Update
    COMPLETION // Mission Completion
  }

  @Override
  public String toString() {
    return "MissionReport{" + "id=" + getId() + ", missionCode="
        + (mission != null ? mission.getMissionCode() : "null") + ", reportType=" + reportType
        + ", reportTimestamp=" + reportTimestamp + ", reporter='" + reporter + '\''
        + ", completionPercentage=" + completionPercentage + '}';
  }
}
