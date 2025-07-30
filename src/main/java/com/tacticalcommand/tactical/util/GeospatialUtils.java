package com.tacticalcommand.tactical.util;

/**
 * Utility class for geospatial calculations and military grid operations.
 * Provides coordinate system conversions and distance calculations.
 */
public final class GeospatialUtils {

    // Earth's radius in kilometers
    private static final double EARTH_RADIUS_KM = 6371.0;
    
    // Conversion factors
    private static final double DEGREES_TO_RADIANS = Math.PI / 180.0;
    private static final double KM_TO_MILES = 0.621371;
    private static final double KM_TO_NAUTICAL_MILES = 0.539957;

    private GeospatialUtils() {
        // Utility class - prevent instantiation
    }

    /**
     * Calculates the distance between two points using the Haversine formula.
     * 
     * @param lat1 latitude of first point in degrees
     * @param lon1 longitude of first point in degrees
     * @param lat2 latitude of second point in degrees
     * @param lon2 longitude of second point in degrees
     * @return distance in kilometers
     */
    public static double calculateDistanceKm(double lat1, double lon1, double lat2, double lon2) {
        // Convert latitude and longitude from degrees to radians
        double lat1Rad = lat1 * DEGREES_TO_RADIANS;
        double lon1Rad = lon1 * DEGREES_TO_RADIANS;
        double lat2Rad = lat2 * DEGREES_TO_RADIANS;
        double lon2Rad = lon2 * DEGREES_TO_RADIANS;

        // Haversine formula
        double deltaLat = lat2Rad - lat1Rad;
        double deltaLon = lon2Rad - lon1Rad;

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }

    /**
     * Calculates the distance between two points in miles.
     * 
     * @param lat1 latitude of first point in degrees
     * @param lon1 longitude of first point in degrees
     * @param lat2 latitude of second point in degrees
     * @param lon2 longitude of second point in degrees
     * @return distance in miles
     */
    public static double calculateDistanceMiles(double lat1, double lon1, double lat2, double lon2) {
        return calculateDistanceKm(lat1, lon1, lat2, lon2) * KM_TO_MILES;
    }

    /**
     * Calculates the distance between two points in nautical miles.
     * 
     * @param lat1 latitude of first point in degrees
     * @param lon1 longitude of first point in degrees
     * @param lat2 latitude of second point in degrees
     * @param lon2 longitude of second point in degrees
     * @return distance in nautical miles
     */
    public static double calculateDistanceNauticalMiles(double lat1, double lon1, double lat2, double lon2) {
        return calculateDistanceKm(lat1, lon1, lat2, lon2) * KM_TO_NAUTICAL_MILES;
    }

    /**
     * Calculates the bearing from one point to another.
     * 
     * @param lat1 latitude of starting point in degrees
     * @param lon1 longitude of starting point in degrees
     * @param lat2 latitude of destination point in degrees
     * @param lon2 longitude of destination point in degrees
     * @return bearing in degrees (0-360)
     */
    public static double calculateBearing(double lat1, double lon1, double lat2, double lon2) {
        double lat1Rad = lat1 * DEGREES_TO_RADIANS;
        double lat2Rad = lat2 * DEGREES_TO_RADIANS;
        double deltaLonRad = (lon2 - lon1) * DEGREES_TO_RADIANS;

        double y = Math.sin(deltaLonRad) * Math.cos(lat2Rad);
        double x = Math.cos(lat1Rad) * Math.sin(lat2Rad) - 
                   Math.sin(lat1Rad) * Math.cos(lat2Rad) * Math.cos(deltaLonRad);

        double bearingRad = Math.atan2(y, x);
        double bearingDeg = bearingRad / DEGREES_TO_RADIANS;

        // Normalize to 0-360 degrees
        return (bearingDeg + 360) % 360;
    }

    /**
     * Validates if coordinates are within valid ranges.
     * 
     * @param latitude latitude in degrees
     * @param longitude longitude in degrees
     * @return true if coordinates are valid
     */
    public static boolean isValidCoordinates(double latitude, double longitude) {
        return latitude >= -90.0 && latitude <= 90.0 && 
               longitude >= -180.0 && longitude <= 180.0;
    }

    /**
     * Formats coordinates to military grid reference system format.
     * This is a simplified implementation - full MGRS requires more complex grid calculations.
     * 
     * @param latitude latitude in degrees
     * @param longitude longitude in degrees
     * @return formatted coordinate string
     */
    public static String formatToMilitaryGrid(double latitude, double longitude) {
        if (!isValidCoordinates(latitude, longitude)) {
            throw new IllegalArgumentException("Invalid coordinates");
        }

        // Convert to degrees, minutes, seconds format
        int latDegrees = (int) Math.abs(latitude);
        int latMinutes = (int) ((Math.abs(latitude) - latDegrees) * 60);
        double latSeconds = ((Math.abs(latitude) - latDegrees) * 60 - latMinutes) * 60;
        
        int lonDegrees = (int) Math.abs(longitude);
        int lonMinutes = (int) ((Math.abs(longitude) - lonDegrees) * 60);
        double lonSeconds = ((Math.abs(longitude) - lonDegrees) * 60 - lonMinutes) * 60;

        String latHemisphere = latitude >= 0 ? "N" : "S";
        String lonHemisphere = longitude >= 0 ? "E" : "W";

        return String.format("%02d°%02d'%05.2f\"%s %03d°%02d'%05.2f\"%s",
                latDegrees, latMinutes, latSeconds, latHemisphere,
                lonDegrees, lonMinutes, lonSeconds, lonHemisphere);
    }

    /**
     * Checks if a point is within a circular area of operation.
     * 
     * @param pointLat point latitude
     * @param pointLon point longitude
     * @param centerLat center latitude of area
     * @param centerLon center longitude of area
     * @param radiusKm radius in kilometers
     * @return true if point is within the area
     */
    public static boolean isWithinOperationalArea(double pointLat, double pointLon, 
                                                  double centerLat, double centerLon, 
                                                  double radiusKm) {
        double distance = calculateDistanceKm(pointLat, pointLon, centerLat, centerLon);
        return distance <= radiusKm;
    }

    /**
     * Calculates the midpoint between two coordinates.
     * 
     * @param lat1 latitude of first point
     * @param lon1 longitude of first point
     * @param lat2 latitude of second point
     * @param lon2 longitude of second point
     * @return array containing [latitude, longitude] of midpoint
     */
    public static double[] calculateMidpoint(double lat1, double lon1, double lat2, double lon2) {
        double lat1Rad = lat1 * DEGREES_TO_RADIANS;
        double lon1Rad = lon1 * DEGREES_TO_RADIANS;
        double lat2Rad = lat2 * DEGREES_TO_RADIANS;
        double deltaLonRad = (lon2 - lon1) * DEGREES_TO_RADIANS;

        double bx = Math.cos(lat2Rad) * Math.cos(deltaLonRad);
        double by = Math.cos(lat2Rad) * Math.sin(deltaLonRad);

        double midLatRad = Math.atan2(Math.sin(lat1Rad) + Math.sin(lat2Rad),
                Math.sqrt((Math.cos(lat1Rad) + bx) * (Math.cos(lat1Rad) + bx) + by * by));
        double midLonRad = lon1Rad + Math.atan2(by, Math.cos(lat1Rad) + bx);

        double midLat = midLatRad / DEGREES_TO_RADIANS;
        double midLon = midLonRad / DEGREES_TO_RADIANS;

        return new double[]{midLat, midLon};
    }

    /**
     * Calculates a new coordinate by offsetting from a base coordinate.
     * 
     * @param baseLat base latitude in degrees
     * @param baseLon base longitude in degrees
     * @param distanceKm distance to offset in kilometers
     * @param bearingDegrees bearing in degrees (0-360)
     * @return array containing [latitude, longitude] of offset point
     */
    public static double[] calculateOffset(double baseLat, double baseLon, double distanceKm, double bearingDegrees) {
        double baseLatRad = baseLat * DEGREES_TO_RADIANS;
        double baseLonRad = baseLon * DEGREES_TO_RADIANS;
        double bearingRad = bearingDegrees * DEGREES_TO_RADIANS;
        double angularDistance = distanceKm / EARTH_RADIUS_KM;

        double newLatRad = Math.asin(Math.sin(baseLatRad) * Math.cos(angularDistance) +
                Math.cos(baseLatRad) * Math.sin(angularDistance) * Math.cos(bearingRad));

        double newLonRad = baseLonRad + Math.atan2(
                Math.sin(bearingRad) * Math.sin(angularDistance) * Math.cos(baseLatRad),
                Math.cos(angularDistance) - Math.sin(baseLatRad) * Math.sin(newLatRad));

        double newLat = newLatRad / DEGREES_TO_RADIANS;
        double newLon = newLonRad / DEGREES_TO_RADIANS;

        return new double[]{newLat, newLon};
    }
}
