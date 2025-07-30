package com.caribouthunder.tactical.service.unit;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tacticalcommand.tactical.domain.MilitaryUnit;
import com.tacticalcommand.tactical.repository.MilitaryUnitRepository;
import com.tacticalcommand.tactical.service.MilitaryUnitService;

/**
 * Unit Tracking Service for real-time position and status tracking.
 * 
 * This service provides specialized tracking capabilities including real-time position updates,
 * movement monitoring, communication status tracking, and operational alerting. It works
 * alongside the existing MilitaryUnitService to provide enhanced tracking functionality.
 * 
 * @author Tactical Command Hub Team - TaskSync Implementation
 * @version 1.0.0
 * @since 2025-07-30
 */
@Service
@Transactional
public class UnitTrackingService {

    private final MilitaryUnitRepository militaryUnitRepository;
    private final MilitaryUnitService militaryUnitService;
    
    // Real-time tracking cache for active units
    private final Map<Long, UnitTrackingData> trackingCache = new ConcurrentHashMap<>();

    @Autowired
    public UnitTrackingService(
            MilitaryUnitRepository militaryUnitRepository,
            MilitaryUnitService militaryUnitService) {
        this.militaryUnitRepository = militaryUnitRepository;
        this.militaryUnitService = militaryUnitService;
    }

    /**
     * Updates unit position with enhanced tracking capabilities.
     * 
     * @param unitId the unit ID to update
     * @param latitude the new latitude
     * @param longitude the new longitude
     * @param altitude the new altitude (optional)
     * @param trackingMetadata additional tracking metadata
     * @return the updated unit with tracking information
     */
    @Transactional
    public MilitaryUnit updateUnitPosition(Long unitId, BigDecimal latitude, BigDecimal longitude, 
                                         BigDecimal altitude, Map<String, Object> trackingMetadata) {
        // Store previous position for movement calculation
        UnitTrackingData previousTracking = trackingCache.get(unitId);
        
        // Update position using existing service (this will validate unit exists)
        MilitaryUnit updatedUnit = militaryUnitService.updateUnitPosition(unitId, latitude, longitude, altitude);
        
        // Calculate movement data
        MovementData movementData = calculateMovementData(previousTracking, latitude, longitude, altitude);
        
        // Update tracking cache
        UnitTrackingData newTracking = UnitTrackingData.builder()
            .unitId(unitId)
            .currentLatitude(latitude)
            .currentLongitude(longitude)
            .currentAltitude(altitude)
            .lastUpdateTime(LocalDateTime.now())
            .movementData(movementData)
            .trackingMetadata(trackingMetadata)
            .build();
        
        trackingCache.put(unitId, newTracking);
        
        // Check for tracking alerts using the updated unit
        checkTrackingAlerts(updatedUnit, newTracking, previousTracking);
        
        return updatedUnit;
    }

    /**
     * Updates unit communication status with tracking integration.
     * 
     * @param unitId the unit ID to update
     * @param communicationStatus the new communication status
     * @param statusDetails additional status details
     * @return the updated unit
     */
    @Transactional
    public MilitaryUnit updateCommunicationStatus(Long unitId, 
                                                MilitaryUnit.CommunicationStatus communicationStatus,
                                                String statusDetails) {
        MilitaryUnit unit = militaryUnitRepository.findById(unitId)
            .orElseThrow(() -> new RuntimeException("Unit not found with ID: " + unitId));

        MilitaryUnit.CommunicationStatus previousStatus = unit.getCommunicationStatus();
        
        // Update communication status
        unit.setCommunicationStatus(communicationStatus);
        unit.setLastContact(LocalDateTime.now());
        
        MilitaryUnit updatedUnit = militaryUnitRepository.save(unit);
        
        // Update tracking cache with communication status
        UnitTrackingData tracking = trackingCache.get(unitId);
        if (tracking != null) {
            tracking.setCommunicationStatus(communicationStatus);
            tracking.setLastCommunicationUpdate(LocalDateTime.now());
            tracking.setCommunicationDetails(statusDetails);
        }
        
        // Check for communication alerts
        checkCommunicationAlerts(updatedUnit, communicationStatus, previousStatus);
        
        return updatedUnit;
    }

    /**
     * Gets real-time tracking data for a unit.
     * 
     * @param unitId the unit ID
     * @return tracking data if available
     */
    @Transactional(readOnly = true)
    public UnitTrackingData getUnitTrackingData(Long unitId) {
        return trackingCache.get(unitId);
    }

    /**
     * Gets real-time tracking data for all active units.
     * 
     * @return map of unit ID to tracking data
     */
    @Transactional(readOnly = true)
    public Map<Long, UnitTrackingData> getAllActiveTrackingData() {
        return new ConcurrentHashMap<>(trackingCache);
    }

    /**
     * Gets units within a geographical area with enhanced tracking.
     * 
     * @param centerLatitude center point latitude
     * @param centerLongitude center point longitude
     * @param radiusKm radius in kilometers
     * @return list of units within the area with tracking data
     */
    @Transactional(readOnly = true)
    public List<UnitWithTrackingData> getUnitsInArea(BigDecimal centerLatitude, 
                                                   BigDecimal centerLongitude, 
                                                   double radiusKm) {
        List<MilitaryUnit> unitsInArea = militaryUnitRepository.findUnitsWithinRadius(
            centerLatitude, centerLongitude, radiusKm);
            
        return unitsInArea.stream()
            .map(unit -> {
                UnitTrackingData tracking = trackingCache.get(unit.getId());
                return new UnitWithTrackingData(unit, tracking);
            })
            .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Gets units that have not reported position updates within specified time.
     * 
     * @param hours hours since last position update
     * @return list of units with stale position data
     */
    @Transactional(readOnly = true)
    public List<UnitTrackingData> getUnitsWithStaleTracking(int hours) {
        LocalDateTime cutoffTime = LocalDateTime.now().minusHours(hours);
        
        return trackingCache.values().stream()
            .filter(tracking -> tracking.getLastUpdateTime().isBefore(cutoffTime))
            .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Starts tracking for a unit.
     * 
     * @param unitId the unit ID to start tracking
     * @return true if tracking started successfully
     */
    public boolean startTracking(Long unitId) {
        MilitaryUnit unit = militaryUnitRepository.findById(unitId).orElse(null);
        if (unit == null) {
            return false;
        }
        
        UnitTrackingData tracking = UnitTrackingData.builder()
            .unitId(unitId)
            .trackingStartTime(LocalDateTime.now())
            .trackingStatus(TrackingStatus.ACTIVE)
            .currentLatitude(unit.getLatitude())
            .currentLongitude(unit.getLongitude())
            .currentAltitude(unit.getAltitude())
            .communicationStatus(unit.getCommunicationStatus())
            .build();
            
        trackingCache.put(unitId, tracking);
        return true;
    }

    /**
     * Stops tracking for a unit.
     * 
     * @param unitId the unit ID to stop tracking
     * @return true if tracking stopped successfully
     */
    public boolean stopTracking(Long unitId) {
        UnitTrackingData tracking = trackingCache.remove(unitId);
        return tracking != null;
    }

    /**
     * Gets tracking statistics for operational overview.
     * 
     * @return tracking statistics
     */
    @Transactional(readOnly = true)
    public TrackingStatistics getTrackingStatistics() {
        long totalTracked = trackingCache.size();
        long activeUnits = trackingCache.values().stream()
            .filter(t -> t.getTrackingStatus() == TrackingStatus.ACTIVE)
            .count();
        long unitsWithStaleData = getUnitsWithStaleTracking(1).size();
        
        Map<MilitaryUnit.CommunicationStatus, Long> commStatusCount = trackingCache.values().stream()
            .filter(t -> t.getCommunicationStatus() != null)
            .collect(java.util.stream.Collectors.groupingBy(
                UnitTrackingData::getCommunicationStatus,
                java.util.stream.Collectors.counting()));
        
        return TrackingStatistics.builder()
            .totalTrackedUnits(totalTracked)
            .activeTrackedUnits(activeUnits)
            .unitsWithStaleData(unitsWithStaleData)
            .communicationStatusBreakdown(commStatusCount)
            .lastUpdated(LocalDateTime.now())
            .build();
    }

    // Private helper methods

    private MovementData calculateMovementData(UnitTrackingData previousTracking, 
                                             BigDecimal newLat, BigDecimal newLon, BigDecimal newAlt) {
        if (previousTracking == null || 
            previousTracking.getCurrentLatitude() == null || 
            previousTracking.getCurrentLongitude() == null) {
            return MovementData.builder()
                .distanceMoved(BigDecimal.ZERO)
                .speed(BigDecimal.ZERO)
                .heading(BigDecimal.ZERO)
                .build();
        }
        
        // Calculate distance using Haversine formula
        double distance = calculateDistance(
            previousTracking.getCurrentLatitude().doubleValue(),
            previousTracking.getCurrentLongitude().doubleValue(),
            newLat.doubleValue(),
            newLon.doubleValue()
        );
        
        // Calculate time difference in hours
        double hoursElapsed = java.time.Duration.between(
            previousTracking.getLastUpdateTime(), LocalDateTime.now()).toMinutes() / 60.0;
        
        // Calculate speed (km/h)
        double speed = hoursElapsed > 0 ? distance / hoursElapsed : 0.0;
        
        // Calculate heading
        double heading = calculateHeading(
            previousTracking.getCurrentLatitude().doubleValue(),
            previousTracking.getCurrentLongitude().doubleValue(),
            newLat.doubleValue(),
            newLon.doubleValue()
        );
        
        return MovementData.builder()
            .distanceMoved(BigDecimal.valueOf(distance))
            .speed(BigDecimal.valueOf(speed))
            .heading(BigDecimal.valueOf(heading))
            .calculatedAt(LocalDateTime.now())
            .build();
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius of the earth in km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    private double calculateHeading(double lat1, double lon1, double lat2, double lon2) {
        double dLon = Math.toRadians(lon2 - lon1);
        double lat1Rad = Math.toRadians(lat1);
        double lat2Rad = Math.toRadians(lat2);
        
        double y = Math.sin(dLon) * Math.cos(lat2Rad);
        double x = Math.cos(lat1Rad) * Math.sin(lat2Rad) - 
                  Math.sin(lat1Rad) * Math.cos(lat2Rad) * Math.cos(dLon);
        
        double heading = Math.toDegrees(Math.atan2(y, x));
        return (heading + 360) % 360; // Normalize to 0-360 degrees
    }

    private void checkTrackingAlerts(MilitaryUnit unit, UnitTrackingData newTracking, 
                                   UnitTrackingData previousTracking) {
        // Check for rapid movement alerts
        if (previousTracking != null && newTracking.getMovementData() != null) {
            BigDecimal speed = newTracking.getMovementData().getSpeed();
            if (speed != null && speed.compareTo(BigDecimal.valueOf(100)) > 0) {
                // Alert for units moving > 100 km/h
                generateTrackingAlert(unit, TrackingAlertType.HIGH_SPEED, 
                    "Unit moving at " + speed + " km/h");
            }
        }
    }

    private void checkCommunicationAlerts(MilitaryUnit unit, 
                                        MilitaryUnit.CommunicationStatus newStatus,
                                        MilitaryUnit.CommunicationStatus previousStatus) {
        if (previousStatus != null && 
            previousStatus == MilitaryUnit.CommunicationStatus.OPERATIONAL &&
            newStatus == MilitaryUnit.CommunicationStatus.OFFLINE) {
            generateTrackingAlert(unit, TrackingAlertType.COMMUNICATION_LOST, 
                "Unit communication status changed to OFFLINE");
        }
    }

    private void generateTrackingAlert(MilitaryUnit unit, TrackingAlertType alertType, String message) {
        // In a real implementation, this would integrate with an alerting system
        System.out.println("TRACKING ALERT [" + alertType + "] for unit " + 
                          unit.getCallsign() + ": " + message);
    }

    // Inner classes for return types

    public static class UnitTrackingData {
        private Long unitId;
        private BigDecimal currentLatitude;
        private BigDecimal currentLongitude;
        private BigDecimal currentAltitude;
        private LocalDateTime lastUpdateTime;
        private LocalDateTime trackingStartTime;
        private TrackingStatus trackingStatus;
        private MovementData movementData;
        private MilitaryUnit.CommunicationStatus communicationStatus;
        private LocalDateTime lastCommunicationUpdate;
        private String communicationDetails;
        private Map<String, Object> trackingMetadata;

        public static UnitTrackingDataBuilder builder() {
            return new UnitTrackingDataBuilder();
        }

        // Getters and setters
        public Long getUnitId() { return unitId; }
        public void setUnitId(Long unitId) { this.unitId = unitId; }
        
        public BigDecimal getCurrentLatitude() { return currentLatitude; }
        public void setCurrentLatitude(BigDecimal currentLatitude) { this.currentLatitude = currentLatitude; }
        
        public BigDecimal getCurrentLongitude() { return currentLongitude; }
        public void setCurrentLongitude(BigDecimal currentLongitude) { this.currentLongitude = currentLongitude; }
        
        public BigDecimal getCurrentAltitude() { return currentAltitude; }
        public void setCurrentAltitude(BigDecimal currentAltitude) { this.currentAltitude = currentAltitude; }
        
        public LocalDateTime getLastUpdateTime() { return lastUpdateTime; }
        public void setLastUpdateTime(LocalDateTime lastUpdateTime) { this.lastUpdateTime = lastUpdateTime; }
        
        public LocalDateTime getTrackingStartTime() { return trackingStartTime; }
        public void setTrackingStartTime(LocalDateTime trackingStartTime) { this.trackingStartTime = trackingStartTime; }
        
        public TrackingStatus getTrackingStatus() { return trackingStatus; }
        public void setTrackingStatus(TrackingStatus trackingStatus) { this.trackingStatus = trackingStatus; }
        
        public MovementData getMovementData() { return movementData; }
        public void setMovementData(MovementData movementData) { this.movementData = movementData; }
        
        public MilitaryUnit.CommunicationStatus getCommunicationStatus() { return communicationStatus; }
        public void setCommunicationStatus(MilitaryUnit.CommunicationStatus communicationStatus) { this.communicationStatus = communicationStatus; }
        
        public LocalDateTime getLastCommunicationUpdate() { return lastCommunicationUpdate; }
        public void setLastCommunicationUpdate(LocalDateTime lastCommunicationUpdate) { this.lastCommunicationUpdate = lastCommunicationUpdate; }
        
        public String getCommunicationDetails() { return communicationDetails; }
        public void setCommunicationDetails(String communicationDetails) { this.communicationDetails = communicationDetails; }
        
        public Map<String, Object> getTrackingMetadata() { return trackingMetadata; }
        public void setTrackingMetadata(Map<String, Object> trackingMetadata) { this.trackingMetadata = trackingMetadata; }
    }

    public static class UnitTrackingDataBuilder {
        private UnitTrackingData data = new UnitTrackingData();

        public UnitTrackingDataBuilder unitId(Long unitId) {
            data.setUnitId(unitId);
            return this;
        }

        public UnitTrackingDataBuilder currentLatitude(BigDecimal latitude) {
            data.setCurrentLatitude(latitude);
            return this;
        }

        public UnitTrackingDataBuilder currentLongitude(BigDecimal longitude) {
            data.setCurrentLongitude(longitude);
            return this;
        }

        public UnitTrackingDataBuilder currentAltitude(BigDecimal altitude) {
            data.setCurrentAltitude(altitude);
            return this;
        }

        public UnitTrackingDataBuilder lastUpdateTime(LocalDateTime time) {
            data.setLastUpdateTime(time);
            return this;
        }

        public UnitTrackingDataBuilder trackingStartTime(LocalDateTime time) {
            data.setTrackingStartTime(time);
            return this;
        }

        public UnitTrackingDataBuilder trackingStatus(TrackingStatus status) {
            data.setTrackingStatus(status);
            return this;
        }

        public UnitTrackingDataBuilder movementData(MovementData movement) {
            data.setMovementData(movement);
            return this;
        }

        public UnitTrackingDataBuilder communicationStatus(MilitaryUnit.CommunicationStatus status) {
            data.setCommunicationStatus(status);
            return this;
        }

        public UnitTrackingDataBuilder trackingMetadata(Map<String, Object> metadata) {
            data.setTrackingMetadata(metadata);
            return this;
        }

        public UnitTrackingData build() {
            return data;
        }
    }

    public static class MovementData {
        private BigDecimal distanceMoved;
        private BigDecimal speed;
        private BigDecimal heading;
        private LocalDateTime calculatedAt;

        public static MovementDataBuilder builder() {
            return new MovementDataBuilder();
        }

        // Getters and setters
        public BigDecimal getDistanceMoved() { return distanceMoved; }
        public void setDistanceMoved(BigDecimal distanceMoved) { this.distanceMoved = distanceMoved; }
        
        public BigDecimal getSpeed() { return speed; }
        public void setSpeed(BigDecimal speed) { this.speed = speed; }
        
        public BigDecimal getHeading() { return heading; }
        public void setHeading(BigDecimal heading) { this.heading = heading; }
        
        public LocalDateTime getCalculatedAt() { return calculatedAt; }
        public void setCalculatedAt(LocalDateTime calculatedAt) { this.calculatedAt = calculatedAt; }
    }

    public static class MovementDataBuilder {
        private MovementData data = new MovementData();

        public MovementDataBuilder distanceMoved(BigDecimal distance) {
            data.setDistanceMoved(distance);
            return this;
        }

        public MovementDataBuilder speed(BigDecimal speed) {
            data.setSpeed(speed);
            return this;
        }

        public MovementDataBuilder heading(BigDecimal heading) {
            data.setHeading(heading);
            return this;
        }

        public MovementDataBuilder calculatedAt(LocalDateTime time) {
            data.setCalculatedAt(time);
            return this;
        }

        public MovementData build() {
            return data;
        }
    }

    public static class UnitWithTrackingData {
        private MilitaryUnit unit;
        private UnitTrackingData trackingData;

        public UnitWithTrackingData(MilitaryUnit unit, UnitTrackingData trackingData) {
            this.unit = unit;
            this.trackingData = trackingData;
        }

        // Getters and setters
        public MilitaryUnit getUnit() { return unit; }
        public void setUnit(MilitaryUnit unit) { this.unit = unit; }
        
        public UnitTrackingData getTrackingData() { return trackingData; }
        public void setTrackingData(UnitTrackingData trackingData) { this.trackingData = trackingData; }
    }

    public static class TrackingStatistics {
        private long totalTrackedUnits;
        private long activeTrackedUnits;
        private long unitsWithStaleData;
        private Map<MilitaryUnit.CommunicationStatus, Long> communicationStatusBreakdown;
        private LocalDateTime lastUpdated;

        public static TrackingStatisticsBuilder builder() {
            return new TrackingStatisticsBuilder();
        }

        // Getters and setters
        public long getTotalTrackedUnits() { return totalTrackedUnits; }
        public void setTotalTrackedUnits(long totalTrackedUnits) { this.totalTrackedUnits = totalTrackedUnits; }
        
        public long getActiveTrackedUnits() { return activeTrackedUnits; }
        public void setActiveTrackedUnits(long activeTrackedUnits) { this.activeTrackedUnits = activeTrackedUnits; }
        
        public long getUnitsWithStaleData() { return unitsWithStaleData; }
        public void setUnitsWithStaleData(long unitsWithStaleData) { this.unitsWithStaleData = unitsWithStaleData; }
        
        public Map<MilitaryUnit.CommunicationStatus, Long> getCommunicationStatusBreakdown() { return communicationStatusBreakdown; }
        public void setCommunicationStatusBreakdown(Map<MilitaryUnit.CommunicationStatus, Long> communicationStatusBreakdown) { this.communicationStatusBreakdown = communicationStatusBreakdown; }
        
        public LocalDateTime getLastUpdated() { return lastUpdated; }
        public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
    }

    public static class TrackingStatisticsBuilder {
        private TrackingStatistics stats = new TrackingStatistics();

        public TrackingStatisticsBuilder totalTrackedUnits(long total) {
            stats.setTotalTrackedUnits(total);
            return this;
        }

        public TrackingStatisticsBuilder activeTrackedUnits(long active) {
            stats.setActiveTrackedUnits(active);
            return this;
        }

        public TrackingStatisticsBuilder unitsWithStaleData(long stale) {
            stats.setUnitsWithStaleData(stale);
            return this;
        }

        public TrackingStatisticsBuilder communicationStatusBreakdown(Map<MilitaryUnit.CommunicationStatus, Long> breakdown) {
            stats.setCommunicationStatusBreakdown(breakdown);
            return this;
        }

        public TrackingStatisticsBuilder lastUpdated(LocalDateTime time) {
            stats.setLastUpdated(time);
            return this;
        }

        public TrackingStatistics build() {
            return stats;
        }
    }

    public enum TrackingStatus {
        ACTIVE,
        INACTIVE,
        PAUSED,
        ERROR
    }

    public enum TrackingAlertType {
        HIGH_SPEED,
        COMMUNICATION_LOST,
        POSITION_STALE,
        BOUNDARY_VIOLATION,
        EMERGENCY_BEACON
    }
}
