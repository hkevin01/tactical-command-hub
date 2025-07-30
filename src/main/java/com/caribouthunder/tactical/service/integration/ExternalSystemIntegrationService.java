package com.caribouthunder.tactical.service.integration;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.caribouthunder.tactical.client.WeatherServiceClient;

/**
 * External System Integration Service.
 * 
 * Provides comprehensive integration capabilities with external tactical systems,
 * including weather services, intelligence feeds, communication networks,
 * and coordination with allied forces.
 * 
 * Implements circuit breaker patterns, retry mechanisms, and resilient
 * communication protocols for reliable external system connectivity.
 * 
 * @author Tactical Command Hub - Phase 3 Integration Layer
 * @version 1.0.0
 * @since 2025-01-27
 */
@Service
@Transactional
public class ExternalSystemIntegrationService {

    @Autowired
    private WeatherServiceClient weatherServiceClient;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${tactical.integration.intelligence.url:http://intelligence-api:8080}")
    private String intelligenceServiceUrl;

    @Value("${tactical.integration.communication.url:http://comms-api:8080}")
    private String communicationServiceUrl;

    @Value("${tactical.integration.logistics.url:http://logistics-api:8080}")
    private String logisticsServiceUrl;

    @Value("${tactical.integration.allied.url:http://allied-forces-api:8080}")
    private String alliedForcesUrl;

    // Async executor for non-blocking integrations
    private final Executor integrationExecutor = Executors.newFixedThreadPool(10);

    // Integration status cache
    private final Map<String, SystemIntegrationStatus> systemStatusCache = new ConcurrentHashMap<>();

    // Active integration sessions
    private final Map<String, ExternalIntegrationSession> activeIntegrations = new ConcurrentHashMap<>();

    /**
     * External System Integration Status.
     * Tracks the health and connectivity status of external systems.
     */
    public static class SystemIntegrationStatus {
        private String systemName;
        private boolean isAvailable;
        private LocalDateTime lastChecked;
        private LocalDateTime lastSuccessfulConnection;
        private int consecutiveFailures;
        private String lastError;
        private Map<String, Object> systemMetrics;

        public SystemIntegrationStatus(String systemName) {
            this.systemName = systemName;
            this.isAvailable = false;
            this.lastChecked = LocalDateTime.now();
            this.consecutiveFailures = 0;
            this.systemMetrics = new HashMap<>();
        }

        // Getters and setters
        public String getSystemName() { return systemName; }
        public boolean isAvailable() { return isAvailable; }
        public void setAvailable(boolean available) { 
            this.isAvailable = available; 
            if (available) {
                this.consecutiveFailures = 0;
                this.lastSuccessfulConnection = LocalDateTime.now();
            } else {
                this.consecutiveFailures++;
            }
            this.lastChecked = LocalDateTime.now();
        }
        public LocalDateTime getLastChecked() { return lastChecked; }
        public LocalDateTime getLastSuccessfulConnection() { return lastSuccessfulConnection; }
        public int getConsecutiveFailures() { return consecutiveFailures; }
        public String getLastError() { return lastError; }
        public void setLastError(String lastError) { this.lastError = lastError; }
        public Map<String, Object> getSystemMetrics() { return systemMetrics; }
    }

    /**
     * External Integration Session.
     * Manages active integration sessions with external systems.
     */
    public static class ExternalIntegrationSession {
        private String sessionId;
        private String systemName;
        private LocalDateTime startTime;
        private LocalDateTime lastActivity;
        private Map<String, Object> sessionData;
        private List<String> requestLog;
        private boolean isActive;

        public ExternalIntegrationSession(String systemName) {
            this.sessionId = UUID.randomUUID().toString();
            this.systemName = systemName;
            this.startTime = LocalDateTime.now();
            this.lastActivity = LocalDateTime.now();
            this.sessionData = new HashMap<>();
            this.requestLog = new ArrayList<>();
            this.isActive = true;
        }

        // Getters and setters
        public String getSessionId() { return sessionId; }
        public String getSystemName() { return systemName; }
        public LocalDateTime getStartTime() { return startTime; }
        public LocalDateTime getLastActivity() { return lastActivity; }
        public void updateActivity() { this.lastActivity = LocalDateTime.now(); }
        public Map<String, Object> getSessionData() { return sessionData; }
        public List<String> getRequestLog() { return requestLog; }
        public boolean isActive() { return isActive; }
        public void setActive(boolean active) { this.isActive = active; }
    }

    /**
     * Weather Integration Data.
     * Encapsulates weather information for tactical planning.
     */
    public static class WeatherIntegrationData {
        private String location;
        private LocalDateTime timestamp;
        private double temperature;
        private double humidity;
        private double windSpeed;
        private String windDirection;
        private String conditions;
        private double visibility;
        private List<String> alerts;
        private Map<String, Object> tacticalImpact;

        public WeatherIntegrationData(String location) {
            this.location = location;
            this.timestamp = LocalDateTime.now();
            this.alerts = new ArrayList<>();
            this.tacticalImpact = new HashMap<>();
        }

        // Getters and setters
        public String getLocation() { return location; }
        public LocalDateTime getTimestamp() { return timestamp; }
        public double getTemperature() { return temperature; }
        public void setTemperature(double temperature) { this.temperature = temperature; }
        public double getHumidity() { return humidity; }
        public void setHumidity(double humidity) { this.humidity = humidity; }
        public double getWindSpeed() { return windSpeed; }
        public void setWindSpeed(double windSpeed) { this.windSpeed = windSpeed; }
        public String getWindDirection() { return windDirection; }
        public void setWindDirection(String windDirection) { this.windDirection = windDirection; }
        public String getConditions() { return conditions; }
        public void setConditions(String conditions) { this.conditions = conditions; }
        public double getVisibility() { return visibility; }
        public void setVisibility(double visibility) { this.visibility = visibility; }
        public List<String> getAlerts() { return alerts; }
        public Map<String, Object> getTacticalImpact() { return tacticalImpact; }
    }

    /**
     * Intelligence Feed Data.
     * Encapsulates intelligence information from external sources.
     */
    public static class IntelligenceFeedData {
        private String sourceId;
        private LocalDateTime timestamp;
        private String classification;
        private String threatLevel;
        private Map<String, Object> intelligenceData;
        private List<String> affectedAreas;
        private String reliability;

        public IntelligenceFeedData(String sourceId) {
            this.sourceId = sourceId;
            this.timestamp = LocalDateTime.now();
            this.intelligenceData = new HashMap<>();
            this.affectedAreas = new ArrayList<>();
        }

        // Getters and setters
        public String getSourceId() { return sourceId; }
        public LocalDateTime getTimestamp() { return timestamp; }
        public String getClassification() { return classification; }
        public void setClassification(String classification) { this.classification = classification; }
        public String getThreatLevel() { return threatLevel; }
        public void setThreatLevel(String threatLevel) { this.threatLevel = threatLevel; }
        public Map<String, Object> getIntelligenceData() { return intelligenceData; }
        public List<String> getAffectedAreas() { return affectedAreas; }
        public String getReliability() { return reliability; }
        public void setReliability(String reliability) { this.reliability = reliability; }
    }

    // ==== EXTERNAL SYSTEM INTEGRATION METHODS ====

    /**
     * Integrates weather data for tactical planning.
     * 
     * @param location the target location
     * @param missionId optional mission ID for context
     * @return weather integration data
     */
    public CompletableFuture<WeatherIntegrationData> integrateWeatherData(String location, Long missionId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                ExternalIntegrationSession session = createIntegrationSession("WEATHER_SERVICE");
                session.getSessionData().put("location", location);
                if (missionId != null) {
                    session.getSessionData().put("missionId", missionId);
                }

                // Use weather service client with circuit breaker
                Map<String, Object> weatherData = weatherServiceClient.getCurrentWeather(location);
                
                WeatherIntegrationData integrationData = new WeatherIntegrationData(location);
                processWeatherData(integrationData, weatherData);
                
                // Calculate tactical impact
                calculateWeatherTacticalImpact(integrationData);
                
                // Update system status
                updateSystemStatus("WEATHER_SERVICE", true, null);
                session.getRequestLog().add("Weather data retrieved successfully for " + location);
                
                return integrationData;
                
            } catch (Exception e) {
                updateSystemStatus("WEATHER_SERVICE", false, e.getMessage());
                throw new RuntimeException("Weather integration failed: " + e.getMessage(), e);
            }
        }, integrationExecutor);
    }

    /**
     * Integrates intelligence feeds from external sources.
     * 
     * @param targetArea the area of interest
     * @param classification the required classification level
     * @return intelligence feed data
     */
    public CompletableFuture<List<IntelligenceFeedData>> integrateIntelligenceFeeds(String targetArea, 
                                                                                   String classification) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                ExternalIntegrationSession session = createIntegrationSession("INTELLIGENCE_SERVICE");
                session.getSessionData().put("targetArea", targetArea);
                session.getSessionData().put("classification", classification);

                List<IntelligenceFeedData> intelligenceFeeds = new ArrayList<>();
                
                // Fetch intelligence data from external service
                String url = intelligenceServiceUrl + "/feeds?area=" + targetArea + "&classification=" + classification;
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> feedsData = restTemplate.getForObject(url, List.class);
                
                if (feedsData != null) {
                    for (Map<String, Object> feedData : feedsData) {
                        IntelligenceFeedData intelligence = processIntelligenceFeed(feedData);
                        intelligenceFeeds.add(intelligence);
                    }
                }
                
                updateSystemStatus("INTELLIGENCE_SERVICE", true, null);
                session.getRequestLog().add("Intelligence feeds retrieved: " + intelligenceFeeds.size());
                
                return intelligenceFeeds;
                
            } catch (RestClientException e) {
                updateSystemStatus("INTELLIGENCE_SERVICE", false, e.getMessage());
                // Return cached/default intelligence if available
                return getCachedIntelligenceFeeds(targetArea);
            } catch (Exception e) {
                updateSystemStatus("INTELLIGENCE_SERVICE", false, e.getMessage());
                throw new RuntimeException("Intelligence integration failed: " + e.getMessage(), e);
            }
        }, integrationExecutor);
    }

    /**
     * Establishes communication link with allied forces.
     * 
     * @param alliedUnitId the allied unit identifier
     * @param communicationType the type of communication (VOICE, DATA, VIDEO)
     * @return communication session details
     */
    public CompletableFuture<Map<String, Object>> establishAlliedCommunication(String alliedUnitId, 
                                                                              String communicationType) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                ExternalIntegrationSession session = createIntegrationSession("ALLIED_FORCES");
                session.getSessionData().put("alliedUnitId", alliedUnitId);
                session.getSessionData().put("communicationType", communicationType);

                Map<String, Object> commRequest = new HashMap<>();
                commRequest.put("unitId", alliedUnitId);
                commRequest.put("type", communicationType);
                commRequest.put("timestamp", LocalDateTime.now());
                commRequest.put("requestId", session.getSessionId());

                String url = alliedForcesUrl + "/communication/establish";
                @SuppressWarnings("unchecked")
                Map<String, Object> commResponse = restTemplate.postForObject(url, commRequest, Map.class);
                
                updateSystemStatus("ALLIED_FORCES", true, null);
                session.getRequestLog().add("Communication established with " + alliedUnitId);
                
                return commResponse != null ? commResponse : new HashMap<>();
                
            } catch (RestClientException e) {
                updateSystemStatus("ALLIED_FORCES", false, e.getMessage());
                // Return fallback communication method
                return createFallbackCommunication(alliedUnitId, communicationType);
            } catch (Exception e) {
                updateSystemStatus("ALLIED_FORCES", false, e.getMessage());
                throw new RuntimeException("Allied communication failed: " + e.getMessage(), e);
            }
        }, integrationExecutor);
    }

    /**
     * Synchronizes logistics data with external supply chain systems.
     * 
     * @param unitId the unit requiring logistics support
     * @param resourceRequirements the required resources
     * @return logistics coordination data
     */
    public CompletableFuture<Map<String, Object>> synchronizeLogistics(Long unitId, 
                                                                      Map<String, Integer> resourceRequirements) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                ExternalIntegrationSession session = createIntegrationSession("LOGISTICS_SERVICE");
                session.getSessionData().put("unitId", unitId);
                session.getSessionData().put("resourceRequirements", resourceRequirements);

                Map<String, Object> logisticsRequest = new HashMap<>();
                logisticsRequest.put("unitId", unitId);
                logisticsRequest.put("requirements", resourceRequirements);
                logisticsRequest.put("priority", "TACTICAL");
                logisticsRequest.put("requestTimestamp", LocalDateTime.now());

                String url = logisticsServiceUrl + "/supply/coordinate";
                @SuppressWarnings("unchecked")
                Map<String, Object> logisticsResponse = restTemplate.postForObject(url, logisticsRequest, Map.class);
                
                updateSystemStatus("LOGISTICS_SERVICE", true, null);
                session.getRequestLog().add("Logistics synchronized for unit " + unitId);
                
                return logisticsResponse != null ? logisticsResponse : new HashMap<>();
                
            } catch (RestClientException e) {
                updateSystemStatus("LOGISTICS_SERVICE", false, e.getMessage());
                // Return local logistics estimation
                return estimateLocalLogistics(unitId, resourceRequirements);
            } catch (Exception e) {
                updateSystemStatus("LOGISTICS_SERVICE", false, e.getMessage());
                throw new RuntimeException("Logistics synchronization failed: " + e.getMessage(), e);
            }
        }, integrationExecutor);
    }

    /**
     * Gets the integration status of all external systems.
     * 
     * @return map of system integration statuses
     */
    public Map<String, SystemIntegrationStatus> getSystemIntegrationStatus() {
        return new HashMap<>(systemStatusCache);
    }

    /**
     * Performs health check on all integrated systems.
     * 
     * @return overall integration health report
     */
    public CompletableFuture<Map<String, Object>> performSystemHealthCheck() {
        return CompletableFuture.supplyAsync(() -> {
            Map<String, Object> healthReport = new HashMap<>();
            List<CompletableFuture<Void>> healthChecks = new ArrayList<>();

            // Weather service health check
            healthChecks.add(CompletableFuture.runAsync(() -> {
                try {
                    weatherServiceClient.healthCheck();
                    updateSystemStatus("WEATHER_SERVICE", true, null);
                } catch (Exception e) {
                    updateSystemStatus("WEATHER_SERVICE", false, e.getMessage());
                }
            }, integrationExecutor));

            // Intelligence service health check
            healthChecks.add(CompletableFuture.runAsync(() -> {
                try {
                    restTemplate.getForObject(intelligenceServiceUrl + "/health", String.class);
                    updateSystemStatus("INTELLIGENCE_SERVICE", true, null);
                } catch (Exception e) {
                    updateSystemStatus("INTELLIGENCE_SERVICE", false, e.getMessage());
                }
            }, integrationExecutor));

            // Allied forces health check
            healthChecks.add(CompletableFuture.runAsync(() -> {
                try {
                    restTemplate.getForObject(alliedForcesUrl + "/health", String.class);
                    updateSystemStatus("ALLIED_FORCES", true, null);
                } catch (Exception e) {
                    updateSystemStatus("ALLIED_FORCES", false, e.getMessage());
                }
            }, integrationExecutor));

            // Logistics service health check
            healthChecks.add(CompletableFuture.runAsync(() -> {
                try {
                    restTemplate.getForObject(logisticsServiceUrl + "/health", String.class);
                    updateSystemStatus("LOGISTICS_SERVICE", true, null);
                } catch (Exception e) {
                    updateSystemStatus("LOGISTICS_SERVICE", false, e.getMessage());
                }
            }, integrationExecutor));

            // Wait for all health checks to complete
            CompletableFuture.allOf(healthChecks.toArray(new CompletableFuture[0])).join();

            // Compile health report
            int totalSystems = systemStatusCache.size();
            long availableSystems = systemStatusCache.values().stream()
                .mapToLong(status -> status.isAvailable() ? 1 : 0)
                .sum();

            healthReport.put("totalSystems", totalSystems);
            healthReport.put("availableSystems", availableSystems);
            healthReport.put("healthPercentage", totalSystems > 0 ? (double) availableSystems / totalSystems * 100 : 0);
            healthReport.put("lastCheckTime", LocalDateTime.now());
            healthReport.put("systemDetails", new HashMap<>(systemStatusCache));

            return healthReport;
        }, integrationExecutor);
    }

    /**
     * Gets active integration sessions.
     * 
     * @return map of active integration sessions
     */
    public Map<String, ExternalIntegrationSession> getActiveIntegrationSessions() {
        return activeIntegrations.entrySet().stream()
            .filter(entry -> entry.getValue().isActive())
            .collect(HashMap::new, (map, entry) -> map.put(entry.getKey(), entry.getValue()), HashMap::putAll);
    }

    // ==== PRIVATE HELPER METHODS ====

    /**
     * Creates a new integration session for tracking external system interactions.
     */
    private ExternalIntegrationSession createIntegrationSession(String systemName) {
        ExternalIntegrationSession session = new ExternalIntegrationSession(systemName);
        activeIntegrations.put(session.getSessionId(), session);
        return session;
    }

    /**
     * Updates the status of an external system.
     */
    private void updateSystemStatus(String systemName, boolean isAvailable, String errorMessage) {
        SystemIntegrationStatus status = systemStatusCache.computeIfAbsent(systemName, 
            SystemIntegrationStatus::new);
        status.setAvailable(isAvailable);
        if (errorMessage != null) {
            status.setLastError(errorMessage);
        }
    }

    /**
     * Processes raw weather data into tactical integration format.
     */
    private void processWeatherData(WeatherIntegrationData integrationData, Map<String, Object> weatherData) {
        if (weatherData != null) {
            integrationData.setTemperature(getDoubleValue(weatherData, "temperature", 20.0));
            integrationData.setHumidity(getDoubleValue(weatherData, "humidity", 50.0));
            integrationData.setWindSpeed(getDoubleValue(weatherData, "windSpeed", 5.0));
            integrationData.setWindDirection(getString(weatherData, "windDirection", "N"));
            integrationData.setConditions(getString(weatherData, "conditions", "Clear"));
            integrationData.setVisibility(getDoubleValue(weatherData, "visibility", 10.0));

            // Process weather alerts
            @SuppressWarnings("unchecked")
            List<String> alerts = (List<String>) weatherData.get("alerts");
            if (alerts != null) {
                integrationData.getAlerts().addAll(alerts);
            }
        }
    }

    /**
     * Calculates tactical impact of weather conditions.
     */
    private void calculateWeatherTacticalImpact(WeatherIntegrationData weatherData) {
        Map<String, Object> tacticalImpact = weatherData.getTacticalImpact();

        // Visibility impact
        if (weatherData.getVisibility() < 1.0) {
            tacticalImpact.put("visibilityImpact", "SEVERE");
            tacticalImpact.put("recommendedAction", "Consider postponing visual operations");
        } else if (weatherData.getVisibility() < 5.0) {
            tacticalImpact.put("visibilityImpact", "MODERATE");
            tacticalImpact.put("recommendedAction", "Enhanced surveillance equipment recommended");
        } else {
            tacticalImpact.put("visibilityImpact", "MINIMAL");
        }

        // Wind impact
        if (weatherData.getWindSpeed() > 20.0) {
            tacticalImpact.put("windImpact", "HIGH");
            tacticalImpact.put("aircraftOperations", "RESTRICTED");
        } else if (weatherData.getWindSpeed() > 10.0) {
            tacticalImpact.put("windImpact", "MODERATE");
            tacticalImpact.put("aircraftOperations", "CAUTION");
        } else {
            tacticalImpact.put("windImpact", "LOW");
            tacticalImpact.put("aircraftOperations", "NORMAL");
        }

        // Temperature impact
        if (weatherData.getTemperature() < -10.0 || weatherData.getTemperature() > 40.0) {
            tacticalImpact.put("temperatureImpact", "EXTREME");
            tacticalImpact.put("personnelEndurance", "LIMITED");
        } else {
            tacticalImpact.put("temperatureImpact", "ACCEPTABLE");
            tacticalImpact.put("personnelEndurance", "NORMAL");
        }
    }

    /**
     * Processes intelligence feed data from external source.
     */
    private IntelligenceFeedData processIntelligenceFeed(Map<String, Object> feedData) {
        String sourceId = getString(feedData, "sourceId", "UNKNOWN");
        IntelligenceFeedData intelligence = new IntelligenceFeedData(sourceId);
        
        intelligence.setClassification(getString(feedData, "classification", "UNCLASSIFIED"));
        intelligence.setThreatLevel(getString(feedData, "threatLevel", "LOW"));
        intelligence.setReliability(getString(feedData, "reliability", "MODERATE"));
        
        @SuppressWarnings("unchecked")
        Map<String, Object> intelData = (Map<String, Object>) feedData.get("data");
        if (intelData != null) {
            intelligence.getIntelligenceData().putAll(intelData);
        }
        
        @SuppressWarnings("unchecked")
        List<String> areas = (List<String>) feedData.get("affectedAreas");
        if (areas != null) {
            intelligence.getAffectedAreas().addAll(areas);
        }
        
        return intelligence;
    }

    /**
     * Returns cached intelligence feeds when external service is unavailable.
     */
    private List<IntelligenceFeedData> getCachedIntelligenceFeeds(String targetArea) {
        // In a real implementation, this would return cached data
        List<IntelligenceFeedData> cachedFeeds = new ArrayList<>();
        IntelligenceFeedData cachedFeed = new IntelligenceFeedData("CACHED_SOURCE");
        cachedFeed.setClassification("UNCLASSIFIED");
        cachedFeed.setThreatLevel("UNKNOWN");
        cachedFeed.setReliability("LOW");
        cachedFeed.getIntelligenceData().put("status", "CACHED_DATA");
        cachedFeed.getIntelligenceData().put("area", targetArea);
        cachedFeeds.add(cachedFeed);
        return cachedFeeds;
    }

    /**
     * Creates fallback communication method when primary fails.
     */
    private Map<String, Object> createFallbackCommunication(String alliedUnitId, String communicationType) {
        Map<String, Object> fallback = new HashMap<>();
        fallback.put("status", "FALLBACK");
        fallback.put("method", "RADIO_BACKUP");
        fallback.put("alliedUnitId", alliedUnitId);
        fallback.put("originalType", communicationType);
        fallback.put("instructions", "Use backup radio frequency for communication");
        return fallback;
    }

    /**
     * Estimates local logistics when external service is unavailable.
     */
    private Map<String, Object> estimateLocalLogistics(Long unitId, Map<String, Integer> requirements) {
        Map<String, Object> estimation = new HashMap<>();
        estimation.put("status", "LOCAL_ESTIMATION");
        estimation.put("unitId", unitId);
        estimation.put("requirements", requirements);
        estimation.put("estimatedDelivery", LocalDateTime.now().plusHours(24));
        estimation.put("confidence", "MEDIUM");
        return estimation;
    }

    /**
     * Helper method to safely extract double values from maps.
     */
    private double getDoubleValue(Map<String, Object> map, String key, double defaultValue) {
        Object value = map.get(key);
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return defaultValue;
    }

    /**
     * Helper method to safely extract string values from maps.
     */
    private String getString(Map<String, Object> map, String key, String defaultValue) {
        Object value = map.get(key);
        return value != null ? value.toString() : defaultValue;
    }
}
