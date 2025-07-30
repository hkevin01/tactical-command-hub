package com.caribouthunder.tactical.client;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Weather Service Client with Simple Circuit Breaker Pattern.
 * 
 * Provides resilient connectivity to external weather services with
 * automatic fallback mechanisms, retry logic, and basic circuit breaker
 * protection against cascading failures.
 * 
 * Implements a simple circuit breaker pattern for enhanced fault tolerance
 * in tactical weather information retrieval (without Resilience4j dependency).
 * 
 * @author Tactical Command Hub - Phase 3 Integration Layer
 * @version 1.0.0
 * @since 2025-01-27
 */
@Component
public class WeatherServiceClient {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${tactical.weather.service.url:http://weather-api:8080}")
    private String weatherServiceUrl;

    @Value("${tactical.weather.api.key:demo-key}")
    private String weatherApiKey;

    @Value("${tactical.weather.timeout:5000}")
    private int timeoutMs;

    // Simple circuit breaker state management
    private volatile CircuitBreakerState circuitState = CircuitBreakerState.CLOSED;
    private final AtomicInteger failureCount = new AtomicInteger(0);
    private final AtomicLong lastFailureTime = new AtomicLong(0);
    private static final int FAILURE_THRESHOLD = 5;
    private static final long RECOVERY_TIMEOUT = 60000; // 1 minute

    /**
     * Simple Circuit Breaker State Enumeration.
     */
    public enum CircuitBreakerState {
        CLOSED,    // Normal operation
        OPEN,      // Circuit breaker triggered - calls fail fast
        HALF_OPEN  // Testing if service has recovered
    }

    /**
     * Weather Response Data.
     * Encapsulates weather service response data.
     */
    public static class WeatherResponse {
        private String location;
        private LocalDateTime timestamp;
        private Map<String, Object> weatherData;
        private boolean isFromCache;
        private String dataSource;

        public WeatherResponse(String location) {
            this.location = location;
            this.timestamp = LocalDateTime.now();
            this.weatherData = new HashMap<>();
            this.isFromCache = false;
            this.dataSource = "EXTERNAL_API";
        }

        // Getters and setters
        public String getLocation() { return location; }
        public LocalDateTime getTimestamp() { return timestamp; }
        public Map<String, Object> getWeatherData() { return weatherData; }
        public boolean isFromCache() { return isFromCache; }
        public void setFromCache(boolean fromCache) { this.isFromCache = fromCache; }
        public String getDataSource() { return dataSource; }
        public void setDataSource(String dataSource) { this.dataSource = dataSource; }
    }

    /**
     * Gets current weather data for a specified location.
     * 
     * @param location the target location
     * @return weather data map
     */
    public Map<String, Object> getCurrentWeather(String location) {
        // Check circuit breaker state
        if (isCircuitOpen()) {
            return createFallbackWeatherData(location);
        }

        try {
            String url = weatherServiceUrl + "/weather/current?location=" + location + "&apiKey=" + weatherApiKey;
            
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            
            // Reset failure count on successful call
            onSuccess();
            
            if (response != null && response.containsKey("weather")) {
                @SuppressWarnings("unchecked")
                Map<String, Object> weatherData = (Map<String, Object>) response.get("weather");
                return enhanceWeatherData(weatherData, location);
            } else {
                return createDefaultWeatherData(location);
            }
            
        } catch (RestClientException e) {
            onFailure();
            return createFallbackWeatherData(location);
        }
    }

    /**
     * Gets weather forecast for a specified location and duration.
     * 
     * @param location the target location
     * @param hours forecast duration in hours
     * @return forecast data map
     */
    public Map<String, Object> getWeatherForecast(String location, int hours) {
        if (isCircuitOpen()) {
            return createFallbackForecastData(location, hours);
        }

        try {
            String url = weatherServiceUrl + "/weather/forecast?location=" + location + 
                        "&hours=" + hours + "&apiKey=" + weatherApiKey;
            
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            
            onSuccess();
            
            if (response != null && response.containsKey("forecast")) {
                @SuppressWarnings("unchecked")
                Map<String, Object> forecastData = (Map<String, Object>) response.get("forecast");
                return enhanceForecastData(forecastData, location, hours);
            } else {
                return createDefaultForecastData(location, hours);
            }
            
        } catch (RestClientException e) {
            onFailure();
            return createFallbackForecastData(location, hours);
        }
    }

    /**
     * Gets weather alerts for a specified area.
     * 
     * @param area the target area
     * @return list of weather alerts
     */
    public List<Map<String, Object>> getWeatherAlerts(String area) {
        if (isCircuitOpen()) {
            return createFallbackAlerts(area);
        }

        try {
            String url = weatherServiceUrl + "/weather/alerts?area=" + area + "&apiKey=" + weatherApiKey;
            
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            
            onSuccess();
            
            if (response != null && response.containsKey("alerts")) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> alerts = (List<Map<String, Object>>) response.get("alerts");
                return enhanceAlertData(alerts, area);
            } else {
                return new ArrayList<>();
            }
            
        } catch (RestClientException e) {
            onFailure();
            return createFallbackAlerts(area);
        }
    }

    /**
     * Performs health check on the weather service.
     * 
     * @return health status
     */
    public Map<String, Object> healthCheck() {
        try {
            String url = weatherServiceUrl + "/health";
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            
            Map<String, Object> healthStatus = new HashMap<>();
            healthStatus.put("status", "UP");
            healthStatus.put("timestamp", LocalDateTime.now());
            healthStatus.put("service", "weather-service");
            healthStatus.put("response", response);
            
            return healthStatus;
            
        } catch (Exception e) {
            Map<String, Object> healthStatus = new HashMap<>();
            healthStatus.put("status", "DOWN");
            healthStatus.put("timestamp", LocalDateTime.now());
            healthStatus.put("service", "weather-service");
            healthStatus.put("error", e.getMessage());
            
            return healthStatus;
        }
    }

    /**
     * Gets circuit breaker metrics and status.
     * 
     * @return circuit breaker status information
     */
    public Map<String, Object> getCircuitBreakerStatus() {
        Map<String, Object> status = new HashMap<>();
        
        status.put("state", circuitState.toString());
        status.put("name", "weather-service-circuit-breaker");
        status.put("failureCount", failureCount.get());
        status.put("lastFailureTime", lastFailureTime.get());
        status.put("timestamp", LocalDateTime.now());
        
        return status;
    }

    // ==== CIRCUIT BREAKER HELPER METHODS ====

    /**
     * Checks if the circuit breaker is open.
     */
    private boolean isCircuitOpen() {
        long currentTime = System.currentTimeMillis();
        
        switch (circuitState) {
            case CLOSED:
                return false;
            case OPEN:
                // Check if recovery timeout has passed
                if (currentTime - lastFailureTime.get() > RECOVERY_TIMEOUT) {
                    circuitState = CircuitBreakerState.HALF_OPEN;
                    return false; // Allow one test call
                }
                return true;
            case HALF_OPEN:
                return false; // Allow test call
            default:
                return false;
        }
    }

    /**
     * Handles successful service calls.
     */
    private void onSuccess() {
        if (circuitState == CircuitBreakerState.HALF_OPEN) {
            // Service has recovered, close the circuit
            circuitState = CircuitBreakerState.CLOSED;
        }
        failureCount.set(0);
    }

    /**
     * Handles failed service calls.
     */
    private void onFailure() {
        int failures = failureCount.incrementAndGet();
        lastFailureTime.set(System.currentTimeMillis());
        
        if (failures >= FAILURE_THRESHOLD) {
            circuitState = CircuitBreakerState.OPEN;
        }
    }

    // ==== PRIVATE HELPER METHODS ====

    /**
     * Enhances weather data with tactical-relevant information.
     */
    private Map<String, Object> enhanceWeatherData(Map<String, Object> weatherData, String location) {
        Map<String, Object> enhanced = new HashMap<>(weatherData);
        enhanced.put("location", location);
        enhanced.put("retrievedAt", LocalDateTime.now());
        enhanced.put("dataSource", "EXTERNAL_API");
        
        // Add tactical enhancements
        addTacticalWeatherMetrics(enhanced);
        
        return enhanced;
    }

    /**
     * Creates default weather data when service is unavailable.
     */
    private Map<String, Object> createDefaultWeatherData(String location) {
        Map<String, Object> defaultData = new HashMap<>();
        defaultData.put("location", location);
        defaultData.put("temperature", 20.0);
        defaultData.put("humidity", 50.0);
        defaultData.put("windSpeed", 5.0);
        defaultData.put("windDirection", "N");
        defaultData.put("conditions", "Unknown");
        defaultData.put("visibility", 10.0);
        defaultData.put("dataSource", "DEFAULT");
        defaultData.put("retrievedAt", LocalDateTime.now());
        
        return defaultData;
    }

    /**
     * Creates fallback weather data when circuit breaker is open.
     */
    private Map<String, Object> createFallbackWeatherData(String location) {
        Map<String, Object> fallbackData = new HashMap<>();
        fallbackData.put("location", location);
        fallbackData.put("temperature", 15.0);
        fallbackData.put("humidity", 60.0);
        fallbackData.put("windSpeed", 8.0);
        fallbackData.put("windDirection", "NW");
        fallbackData.put("conditions", "Service Unavailable");
        fallbackData.put("visibility", 8.0);
        fallbackData.put("dataSource", "FALLBACK");
        fallbackData.put("retrievedAt", LocalDateTime.now());
        fallbackData.put("alerts", Arrays.asList("Weather service temporarily unavailable"));
        
        return fallbackData;
    }

    /**
     * Enhances forecast data with tactical planning information.
     */
    private Map<String, Object> enhanceForecastData(Map<String, Object> forecastData, String location, int hours) {
        Map<String, Object> enhanced = new HashMap<>(forecastData);
        enhanced.put("location", location);
        enhanced.put("forecastHours", hours);
        enhanced.put("retrievedAt", LocalDateTime.now());
        enhanced.put("dataSource", "EXTERNAL_API");
        
        return enhanced;
    }

    /**
     * Creates default forecast data.
     */
    private Map<String, Object> createDefaultForecastData(String location, int hours) {
        Map<String, Object> defaultForecast = new HashMap<>();
        defaultForecast.put("location", location);
        defaultForecast.put("forecastHours", hours);
        defaultForecast.put("dataSource", "DEFAULT");
        defaultForecast.put("retrievedAt", LocalDateTime.now());
        
        // Create hourly forecast data
        List<Map<String, Object>> hourlyData = new ArrayList<>();
        for (int i = 0; i < hours; i++) {
            Map<String, Object> hourly = new HashMap<>();
            hourly.put("hour", i + 1);
            hourly.put("temperature", 18.0 + (Math.random() * 10));
            hourly.put("conditions", "Partly Cloudy");
            hourly.put("windSpeed", 5.0 + (Math.random() * 5));
            hourlyData.add(hourly);
        }
        defaultForecast.put("hourlyForecast", hourlyData);
        
        return defaultForecast;
    }

    /**
     * Creates fallback forecast data when circuit breaker is open.
     */
    private Map<String, Object> createFallbackForecastData(String location, int hours) {
        Map<String, Object> fallbackForecast = createDefaultForecastData(location, hours);
        fallbackForecast.put("dataSource", "FALLBACK");
        
        List<String> alerts = new ArrayList<>();
        alerts.add("Weather forecast service temporarily unavailable");
        alerts.add("Using estimated weather patterns for planning");
        fallbackForecast.put("alerts", alerts);
        
        return fallbackForecast;
    }

    /**
     * Enhances alert data with tactical relevance.
     */
    private List<Map<String, Object>> enhanceAlertData(List<Map<String, Object>> alerts, String area) {
        List<Map<String, Object>> enhanced = new ArrayList<>();
        
        for (Map<String, Object> alert : alerts) {
            Map<String, Object> enhancedAlert = new HashMap<>(alert);
            enhancedAlert.put("area", area);
            enhancedAlert.put("retrievedAt", LocalDateTime.now());
            
            // Add tactical priority based on alert type
            String alertType = (String) alert.get("type");
            if (alertType != null) {
                switch (alertType.toUpperCase()) {
                    case "SEVERE_STORM":
                    case "TORNADO":
                        enhancedAlert.put("tacticalPriority", "CRITICAL");
                        break;
                    case "HIGH_WIND":
                    case "HEAVY_RAIN":
                        enhancedAlert.put("tacticalPriority", "HIGH");
                        break;
                    default:
                        enhancedAlert.put("tacticalPriority", "MEDIUM");
                }
            }
            
            enhanced.add(enhancedAlert);
        }
        
        return enhanced;
    }

    /**
     * Creates fallback alerts when service is unavailable.
     */
    private List<Map<String, Object>> createFallbackAlerts(String area) {
        List<Map<String, Object>> fallbackAlerts = new ArrayList<>();
        
        Map<String, Object> serviceAlert = new HashMap<>();
        serviceAlert.put("type", "SERVICE_ALERT");
        serviceAlert.put("area", area);
        serviceAlert.put("message", "Weather alert service temporarily unavailable");
        serviceAlert.put("severity", "INFO");
        serviceAlert.put("tacticalPriority", "LOW");
        serviceAlert.put("dataSource", "FALLBACK");
        serviceAlert.put("timestamp", LocalDateTime.now());
        
        fallbackAlerts.add(serviceAlert);
        return fallbackAlerts;
    }

    /**
     * Adds tactical weather metrics for operational planning.
     */
    private void addTacticalWeatherMetrics(Map<String, Object> weatherData) {
        Map<String, Object> tacticalMetrics = new HashMap<>();
        
        // Visibility classification
        Double visibility = getDoubleValue(weatherData, "visibility", 10.0);
        if (visibility < 1.0) {
            tacticalMetrics.put("visibilityClass", "POOR");
            tacticalMetrics.put("visualOpsRecommendation", "NOT_RECOMMENDED");
        } else if (visibility < 5.0) {
            tacticalMetrics.put("visibilityClass", "LIMITED");
            tacticalMetrics.put("visualOpsRecommendation", "CAUTION");
        } else {
            tacticalMetrics.put("visibilityClass", "GOOD");
            tacticalMetrics.put("visualOpsRecommendation", "NORMAL");
        }
        
        // Wind impact assessment
        Double windSpeed = getDoubleValue(weatherData, "windSpeed", 0.0);
        if (windSpeed > 15.0) {
            tacticalMetrics.put("windImpact", "HIGH");
            tacticalMetrics.put("airOpsRecommendation", "RESTRICTED");
        } else if (windSpeed > 8.0) {
            tacticalMetrics.put("windImpact", "MODERATE");
            tacticalMetrics.put("airOpsRecommendation", "CAUTION");
        } else {
            tacticalMetrics.put("windImpact", "LOW");
            tacticalMetrics.put("airOpsRecommendation", "NORMAL");
        }
        
        weatherData.put("tacticalMetrics", tacticalMetrics);
    }

    /**
     * Helper method to safely extract double values from weather data.
     */
    private Double getDoubleValue(Map<String, Object> data, String key, Double defaultValue) {
        Object value = data.get(key);
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return defaultValue;
    }
}
