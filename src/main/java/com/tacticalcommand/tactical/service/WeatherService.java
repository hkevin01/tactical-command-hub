package com.tacticalcommand.tactical.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.tacticalcommand.tactical.domain.MilitaryUnit;
import com.tacticalcommand.tactical.repository.MilitaryUnitRepository;
import com.tacticalcommand.tactical.util.GeospatialUtils;

/**
 * Service for integrating with National Weather Service API
 * to provide tactical weather intelligence for military operations.
 * 
 * Supports weather data correlation with unit positions and
 * mission areas for enhanced tactical decision making.
 */
@Service
public class WeatherService {
    
    private static final Logger logger = LoggerFactory.getLogger(WeatherService.class);
    
    private static final String NWS_BASE_URL = "https://api.weather.gov";
    private static final String USER_AGENT = "TacticalCommandHub/1.0 (tactical-command-hub@defense.mil)";
    
    @Autowired
    private MilitaryUnitRepository unitRepository;
    
    private final RestTemplate restTemplate;
    
    public WeatherService() {
        this.restTemplate = new RestTemplate();
    }
    
    /**
     * Gets current weather conditions for a specific latitude/longitude.
     * 
     * @param latitude Latitude in decimal degrees
     * @param longitude Longitude in decimal degrees
     * @return WeatherConditions object or null if unavailable
     */
    public WeatherConditions getCurrentWeather(double latitude, double longitude) {
        try {
            // Validate coordinates
            if (!GeospatialUtils.isValidCoordinates(latitude, longitude)) {
                logger.warn("Invalid coordinates provided: {}, {}", latitude, longitude);
                return null;
            }
            
            // Step 1: Get grid point information
            String pointUrl = String.format("%s/points/%.4f,%.4f", NWS_BASE_URL, latitude, longitude);
            HttpHeaders headers = createHeaders();
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            ResponseEntity<PointResponse> pointResponse = restTemplate.exchange(
                pointUrl, HttpMethod.GET, entity, PointResponse.class
            );
            
            PointResponse pointResponseBody = pointResponse.getBody();
            if (pointResponseBody == null || pointResponseBody.properties == null) {
                logger.warn("No grid point data available for coordinates: {}, {}", latitude, longitude);
                return null;
            }
            
            // Step 2: Get latest observation from nearby station
            String stationsUrl = pointResponseBody.properties.observationStations;
            if (stationsUrl != null) {
                ResponseEntity<StationCollectionResponse> stationsResponse = restTemplate.exchange(
                    stationsUrl, HttpMethod.GET, entity, StationCollectionResponse.class
                );
                
                StationCollectionResponse stationsResponseBody = stationsResponse.getBody();
                if (stationsResponseBody != null && 
                    stationsResponseBody.observationStations != null &&
                    !stationsResponseBody.observationStations.isEmpty()) {
                    
                    // Get latest observation from first available station
                    String stationUrl = stationsResponseBody.observationStations.get(0);
                    String latestObsUrl = stationUrl + "/observations/latest";
                    
                    ResponseEntity<ObservationResponse> obsResponse = restTemplate.exchange(
                        latestObsUrl, HttpMethod.GET, entity, ObservationResponse.class
                    );
                    
                    ObservationResponse obsResponseBody = obsResponse.getBody();
                    if (obsResponseBody != null && obsResponseBody.properties != null) {
                        return mapToWeatherConditions(obsResponseBody.properties, latitude, longitude);
                    }
                }
            }
            
            logger.warn("No observation data available for coordinates: {}, {}", latitude, longitude);
            return null;
            
        } catch (RestClientException e) {
            logger.error("Error retrieving weather data for coordinates {}, {}: {}", 
                latitude, longitude, e.getMessage());
            return null;
        }
    }
    
    /**
     * Gets weather forecast for a specific location.
     * 
     * @param latitude Latitude in decimal degrees
     * @param longitude Longitude in decimal degrees
     * @return WeatherForecast object or null if unavailable
     */
    public WeatherForecast getForecast(double latitude, double longitude) {
        try {
            // Validate coordinates
            if (!GeospatialUtils.isValidCoordinates(latitude, longitude)) {
                logger.warn("Invalid coordinates provided: {}, {}", latitude, longitude);
                return null;
            }
            
            // Step 1: Get grid point information
            String pointUrl = String.format("%s/points/%.4f,%.4f", NWS_BASE_URL, latitude, longitude);
            HttpHeaders headers = createHeaders();
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            ResponseEntity<PointResponse> pointResponse = restTemplate.exchange(
                pointUrl, HttpMethod.GET, entity, PointResponse.class
            );
            
            PointResponse pointResponseBody = pointResponse.getBody();
            if (pointResponseBody == null || pointResponseBody.properties == null) {
                logger.warn("No grid point data available for coordinates: {}, {}", latitude, longitude);
                return null;
            }
            
            // Step 2: Get forecast
            String forecastUrl = pointResponseBody.properties.forecast;
            if (forecastUrl != null) {
                ResponseEntity<ForecastResponse> forecastResponse = restTemplate.exchange(
                    forecastUrl, HttpMethod.GET, entity, ForecastResponse.class
                );
                
                ForecastResponse forecastResponseBody = forecastResponse.getBody();
                if (forecastResponseBody != null && forecastResponseBody.properties != null) {
                    return mapToWeatherForecast(forecastResponseBody.properties, latitude, longitude);
                }
            }
            
            logger.warn("No forecast data available for coordinates: {}, {}", latitude, longitude);
            return null;
            
        } catch (RestClientException e) {
            logger.error("Error retrieving forecast data for coordinates {}, {}: {}", 
                latitude, longitude, e.getMessage());
            return null;
        }
    }
    
    /**
     * Gets weather conditions for all military units.
     * 
     * @return List of UnitWeatherReport objects
     */
    public List<UnitWeatherReport> getUnitWeatherReports() {
        List<MilitaryUnit> units = unitRepository.findAll();
        return units.stream()
            .filter(unit -> unit.getLatitude() != null && unit.getLongitude() != null)
            .map(this::createUnitWeatherReport)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .toList();
    }
    
    /**
     * Gets weather conditions for a specific military unit.
     * 
     * @param unitId The unit ID
     * @return UnitWeatherReport or null if unit not found or no weather data
     */
    public UnitWeatherReport getUnitWeatherReport(Long unitId) {
        Optional<MilitaryUnit> unitOpt = unitRepository.findById(unitId);
        if (unitOpt.isPresent()) {
            return createUnitWeatherReport(unitOpt.get()).orElse(null);
        }
        return null;
    }
    
    /**
     * Checks for severe weather alerts in a specific area.
     * 
     * @param latitude Latitude in decimal degrees
     * @param longitude Longitude in decimal degrees
     * @param radiusKm Radius in kilometers to check around the point
     * @return List of active weather alerts
     */
    public List<WeatherAlert> getActiveAlerts(double latitude, double longitude, double radiusKm) {
        try {
            String alertsUrl = String.format("%s/alerts/active?point=%.4f,%.4f", 
                NWS_BASE_URL, latitude, longitude);
            
            HttpHeaders headers = createHeaders();
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            ResponseEntity<AlertCollectionResponse> alertResponse = restTemplate.exchange(
                alertsUrl, HttpMethod.GET, entity, AlertCollectionResponse.class
            );
            
            AlertCollectionResponse alertResponseBody = alertResponse.getBody();
            if (alertResponseBody != null && alertResponseBody.features != null) {
                return alertResponseBody.features.stream()
                    .map(feature -> mapToWeatherAlert(feature.properties))
                    .toList();
            }
            
            return List.of();
            
        } catch (RestClientException e) {
            logger.error("Error retrieving weather alerts for coordinates {}, {}: {}", 
                latitude, longitude, e.getMessage());
            return List.of();
        }
    }
    
    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", USER_AGENT);
        headers.set("Accept", "application/geo+json");
        return headers;
    }
    
    private Optional<UnitWeatherReport> createUnitWeatherReport(MilitaryUnit unit) {
        if (unit.getLatitude() == null || unit.getLongitude() == null) {
            return Optional.empty();
        }
        
        double lat = unit.getLatitude().doubleValue();
        double lon = unit.getLongitude().doubleValue();
        
        WeatherConditions conditions = getCurrentWeather(lat, lon);
        if (conditions == null) {
            return Optional.empty();
        }
        
        WeatherForecast forecast = getForecast(lat, lon);
        List<WeatherAlert> alerts = getActiveAlerts(lat, lon, 50.0); // 50km radius
        
        UnitWeatherReport report = new UnitWeatherReport();
        report.unitId = unit.getId();
        report.unitName = unit.getUnitName();
        report.latitude = lat;
        report.longitude = lon;
        report.currentConditions = conditions;
        report.forecast = forecast;
        report.activeAlerts = alerts;
        report.reportTime = LocalDateTime.now();
        
        return Optional.of(report);
    }
    
    private WeatherConditions mapToWeatherConditions(ObservationProperties props, double lat, double lon) {
        WeatherConditions conditions = new WeatherConditions();
        conditions.latitude = lat;
        conditions.longitude = lon;
        conditions.observationTime = props.timestamp;
        conditions.temperature = extractTemperature(props.temperature);
        conditions.humidity = extractHumidity(props.relativeHumidity);
        conditions.windSpeed = extractWindSpeed(props.windSpeed);
        conditions.windDirection = extractWindDirection(props.windDirection);
        conditions.visibility = extractVisibility(props.visibility);
        conditions.conditions = props.textDescription;
        conditions.pressure = extractPressure(props.barometricPressure);
        
        return conditions;
    }
    
    private WeatherForecast mapToWeatherForecast(ForecastProperties props, double lat, double lon) {
        WeatherForecast forecast = new WeatherForecast();
        forecast.latitude = lat;
        forecast.longitude = lon;
        forecast.generatedAt = props.generatedAt;
        forecast.updateTime = props.updateTime;
        
        if (props.periods != null && !props.periods.isEmpty()) {
            forecast.periods = props.periods.stream()
                .map(this::mapToForecastPeriod)
                .toList();
        }
        
        return forecast;
    }
    
    private ForecastPeriod mapToForecastPeriod(ForecastPeriodData period) {
        ForecastPeriod fp = new ForecastPeriod();
        fp.number = period.number;
        fp.name = period.name;
        fp.startTime = period.startTime;
        fp.endTime = period.endTime;
        fp.isDaytime = period.isDaytime;
        fp.temperature = period.temperature;
        fp.temperatureUnit = period.temperatureUnit;
        fp.windSpeed = period.windSpeed;
        fp.windDirection = period.windDirection;
        fp.shortForecast = period.shortForecast;
        fp.detailedForecast = period.detailedForecast;
        
        return fp;
    }
    
    private WeatherAlert mapToWeatherAlert(AlertProperties props) {
        WeatherAlert alert = new WeatherAlert();
        alert.id = props.id;
        alert.event = props.event;
        alert.headline = props.headline;
        alert.description = props.description;
        alert.severity = props.severity;
        alert.urgency = props.urgency;
        alert.certainty = props.certainty;
        alert.effective = props.effective;
        alert.expires = props.expires;
        alert.areaDesc = props.areaDesc;
        
        return alert;
    }
    
    // Helper methods to safely extract numeric values
    private Double extractTemperature(QuantitativeValue qv) {
        return qv != null ? qv.value : null;
    }
    
    private Double extractHumidity(QuantitativeValue qv) {
        return qv != null ? qv.value : null;
    }
    
    private Double extractWindSpeed(QuantitativeValue qv) {
        return qv != null ? qv.value : null;
    }
    
    private String extractWindDirection(QuantitativeValue qv) {
        if (qv != null && qv.value != null) {
            double degrees = qv.value;
            String[] directions = {"N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE", 
                                 "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW"};
            int index = (int) Math.round(degrees / 22.5) % 16;
            return directions[index];
        }
        return null;
    }
    
    private Double extractVisibility(QuantitativeValue qv) {
        return qv != null ? qv.value : null;
    }
    
    private Double extractPressure(QuantitativeValue qv) {
        return qv != null ? qv.value : null;
    }
    
    // Data Transfer Objects for NWS API responses
    public static class PointResponse {
        public PointProperties properties;
    }
    
    public static class PointProperties {
        public String forecast;
        public String forecastHourly;
        public String observationStations;
    }
    
    public static class StationCollectionResponse {
        public List<String> observationStations;
    }
    
    public static class ObservationResponse {
        public ObservationProperties properties;
    }
    
    public static class ObservationProperties {
        public String timestamp;
        public QuantitativeValue temperature;
        public QuantitativeValue relativeHumidity;
        public QuantitativeValue windSpeed;
        public QuantitativeValue windDirection;
        public QuantitativeValue visibility;
        public QuantitativeValue barometricPressure;
        public String textDescription;
    }
    
    public static class ForecastResponse {
        public ForecastProperties properties;
    }
    
    public static class ForecastProperties {
        public String generatedAt;
        public String updateTime;
        public List<ForecastPeriodData> periods;
    }
    
    public static class ForecastPeriodData {
        public Integer number;
        public String name;
        public String startTime;
        public String endTime;
        public Boolean isDaytime;
        public Integer temperature;
        public String temperatureUnit;
        public String windSpeed;
        public String windDirection;
        public String shortForecast;
        public String detailedForecast;
    }
    
    public static class AlertCollectionResponse {
        public List<AlertFeature> features;
    }
    
    public static class AlertFeature {
        public AlertProperties properties;
    }
    
    public static class AlertProperties {
        public String id;
        public String event;
        public String headline;
        public String description;
        public String severity;
        public String urgency;
        public String certainty;
        public String effective;
        public String expires;
        public String areaDesc;
    }
    
    public static class QuantitativeValue {
        public Double value;
        public String unitCode;
    }
    
    // Domain Objects for Weather Data
    public static class WeatherConditions {
        public double latitude;
        public double longitude;
        public String observationTime;
        public Double temperature; // Celsius
        public Double humidity; // %
        public Double windSpeed; // m/s
        public String windDirection;
        public Double visibility; // meters
        public String conditions;
        public Double pressure; // Pa
    }
    
    public static class WeatherForecast {
        public double latitude;
        public double longitude;
        public String generatedAt;
        public String updateTime;
        public List<ForecastPeriod> periods;
    }
    
    public static class ForecastPeriod {
        public Integer number;
        public String name;
        public String startTime;
        public String endTime;
        public Boolean isDaytime;
        public Integer temperature;
        public String temperatureUnit;
        public String windSpeed;
        public String windDirection;
        public String shortForecast;
        public String detailedForecast;
    }
    
    public static class WeatherAlert {
        public String id;
        public String event;
        public String headline;
        public String description;
        public String severity;
        public String urgency;
        public String certainty;
        public String effective;
        public String expires;
        public String areaDesc;
    }
    
    public static class UnitWeatherReport {
        public Long unitId;
        public String unitName;
        public double latitude;
        public double longitude;
        public WeatherConditions currentConditions;
        public WeatherForecast forecast;
        public List<WeatherAlert> activeAlerts;
        public LocalDateTime reportTime;
    }
}
