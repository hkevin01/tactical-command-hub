package com.tacticalcommand.tactical.domain;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Unit Status History entity for tracking status changes of military units.
 * 
 * This entity maintains an audit trail of all status changes for military units, providing
 * comprehensive tracking for operational analysis and reporting.
 * 
 * @author Tactical Command Hub Team
 * @version 1.0.0
 * @since 2025-07-29
 */
@Entity
@Table(name = "unit_status_history",
    indexes = {@Index(name = "idx_status_history_unit", columnList = "unit_id"),
        @Index(name = "idx_status_history_timestamp", columnList = "status_changed_at")})
public class UnitStatusHistory extends BaseEntity {

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "unit_id", nullable = false)
  private MilitaryUnit unit;

  @NotNull(message = "Previous status is required")
  @Enumerated(EnumType.STRING)
  @Column(name = "previous_status", nullable = false, length = 20)
  private MilitaryUnit.UnitStatus previousStatus;

  @NotNull(message = "New status is required")
  @Enumerated(EnumType.STRING)
  @Column(name = "new_status", nullable = false, length = 20)
  private MilitaryUnit.UnitStatus newStatus;

  @NotNull
  @Column(name = "status_changed_at", nullable = false)
  private LocalDateTime statusChangedAt;

  @Size(max = 255, message = "Change reason must not exceed 255 characters")
  @Column(name = "change_reason", length = 255)
  private String changeReason;

  @Size(max = 100, message = "Changed by must not exceed 100 characters")
  @Column(name = "changed_by", nullable = false, length = 100)
  private String changedBy;

  /**
   * Default constructor.
   */
  public UnitStatusHistory() {
    super();
    this.statusChangedAt = LocalDateTime.now();
  }

  /**
   * Constructor with required fields.
   */
  public UnitStatusHistory(MilitaryUnit unit, MilitaryUnit.UnitStatus previousStatus,
      MilitaryUnit.UnitStatus newStatus, String changedBy) {
    this();
    this.unit = unit;
    this.previousStatus = previousStatus;
    this.newStatus = newStatus;
    this.changedBy = changedBy;
  }

  // Getters and Setters

  public MilitaryUnit getUnit() {
    return unit;
  }

  public void setUnit(MilitaryUnit unit) {
    this.unit = unit;
  }

  public MilitaryUnit.UnitStatus getPreviousStatus() {
    return previousStatus;
  }

  public void setPreviousStatus(MilitaryUnit.UnitStatus previousStatus) {
    this.previousStatus = previousStatus;
  }

  public MilitaryUnit.UnitStatus getNewStatus() {
    return newStatus;
  }

  public void setNewStatus(MilitaryUnit.UnitStatus newStatus) {
    this.newStatus = newStatus;
  }

  public LocalDateTime getStatusChangedAt() {
    return statusChangedAt;
  }

  public void setStatusChangedAt(LocalDateTime statusChangedAt) {
    this.statusChangedAt = statusChangedAt;
  }

  public String getChangeReason() {
    return changeReason;
  }

  public void setChangeReason(String changeReason) {
    this.changeReason = changeReason;
  }

  public String getChangedBy() {
    return changedBy;
  }

  public void setChangedBy(String changedBy) {
    this.changedBy = changedBy;
  }

  @Override
  public String toString() {
    return "UnitStatusHistory{" + "id=" + getId() + ", unitCallsign="
        + (unit != null ? unit.getCallsign() : "null") + ", previousStatus=" + previousStatus
        + ", newStatus=" + newStatus + ", statusChangedAt=" + statusChangedAt + ", changedBy='"
        + changedBy + '\'' + '}';
  }
}
