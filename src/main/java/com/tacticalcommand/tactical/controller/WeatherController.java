package com.tacticalcommand.tactical.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tacticalcommand.tactical.service.WeatherService;
import com.tacticalcommand.tactical.service.WeatherService.UnitWeatherReport;
import com.tacticalcommand.tactical.service.WeatherService.WeatherAlert;
import com.tacticalcommand.tactical.service.WeatherService.WeatherConditions;
import com.tacticalcommand.tactical.service.WeatherService.WeatherForecast;

/**
 * REST Controller for weather-related operations providing tactical weather intelligence
 * for military command and control systems. Integrates with National Weather Service
 * to provide current conditions, forecasts, alerts and unit-specific weather reports.
 */
@RestController
@RequestMapping("/api/weather")
public class WeatherController {

    @Autowired
    private WeatherService weatherService;

    /**
     * Get current weather conditions for specified coordinates
     * 
     * @param latitude Geographic latitude in decimal degrees
     * @param longitude Geographic longitude in decimal degrees
     * @return Current weather conditions or 404 if not available
     */
    @GetMapping("/current")
    public ResponseEntity<WeatherConditions> getCurrentWeather(
            @RequestParam("lat") double latitude,
            @RequestParam("lon") double longitude) {
        
        WeatherConditions conditions = weatherService.getCurrentWeather(latitude, longitude);
        if (conditions != null) {
            return ResponseEntity.ok(conditions);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get weather forecast for specified coordinates
     * 
     * @param latitude Geographic latitude in decimal degrees
     * @param longitude Geographic longitude in decimal degrees
     * @return Weather forecast or 404 if not available
     */
    @GetMapping("/forecast")
    public ResponseEntity<WeatherForecast> getForecast(
            @RequestParam("lat") double latitude,
            @RequestParam("lon") double longitude) {
        
        WeatherForecast forecast = weatherService.getForecast(latitude, longitude);
        if (forecast != null) {
            return ResponseEntity.ok(forecast);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get active weather alerts for specified coordinates with default 50km radius
     * 
     * @param latitude Geographic latitude in decimal degrees
     * @param longitude Geographic longitude in decimal degrees
     * @param radius Optional radius in kilometers (default 50km)
     * @return List of active weather alerts (empty list if none)
     */
    @GetMapping("/alerts")
    public ResponseEntity<List<WeatherAlert>> getAlerts(
            @RequestParam("lat") double latitude,
            @RequestParam("lon") double longitude,
            @RequestParam(value = "radius", defaultValue = "50.0") double radius) {
        
        List<WeatherAlert> alerts = weatherService.getActiveAlerts(latitude, longitude, radius);
        return ResponseEntity.ok(alerts);
    }

    /**
     * Get weather reports for all military units
     * 
     * @return List of unit weather reports with tactical correlation
     */
    @GetMapping("/units")
    public ResponseEntity<List<UnitWeatherReport>> getUnitWeatherReports() {
        
        List<UnitWeatherReport> reports = weatherService.getUnitWeatherReports();
        return ResponseEntity.ok(reports);
    }

    /**
     * Get weather report for a specific military unit
     * 
     * @param unitId Unique identifier for the military unit
     * @return Unit weather report or 404 if unit not found
     */
    @GetMapping("/units/{unitId}")
    public ResponseEntity<UnitWeatherReport> getUnitWeatherReport(@PathVariable Long unitId) {
        
        UnitWeatherReport unitReport = weatherService.getUnitWeatherReport(unitId);
            
        if (unitReport != null) {
            return ResponseEntity.ok(unitReport);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
