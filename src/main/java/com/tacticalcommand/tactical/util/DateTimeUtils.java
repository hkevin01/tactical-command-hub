package com.tacticalcommand.tactical.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Utility class for date and time operations in military context.
 * Provides standardized date formatting and parsing for tactical operations.
 */
public final class DateTimeUtils {

    // Military time formats
    public static final DateTimeFormatter ZULU_TIME_FORMAT = DateTimeFormatter.ofPattern("ddHHmmZ MMM yy");
    public static final DateTimeFormatter TACTICAL_TIME_FORMAT = DateTimeFormatter.ofPattern("ddHHmm MMM yy");
    public static final DateTimeFormatter ISO_DATETIME_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    
    private DateTimeUtils() {
        // Utility class - prevent instantiation
    }

    /**
     * Formats a LocalDateTime to military Zulu time format.
     * Example: 291430Z JUL 25
     * 
     * @param dateTime the date time to format
     * @return formatted military time string
     */
    public static String formatToZuluTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(ZULU_TIME_FORMAT);
    }

    /**
     * Formats a LocalDateTime to tactical time format.
     * Example: 291430 JUL 25
     * 
     * @param dateTime the date time to format
     * @return formatted tactical time string
     */
    public static String formatToTacticalTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(TACTICAL_TIME_FORMAT);
    }

    /**
     * Parses a military time string to LocalDateTime.
     * Supports both Zulu and tactical time formats.
     * 
     * @param militaryTime the military time string
     * @return parsed LocalDateTime
     * @throws DateTimeParseException if the string cannot be parsed
     */
    public static LocalDateTime parseFromMilitaryTime(String militaryTime) {
        if (militaryTime == null || militaryTime.trim().isEmpty()) {
            return null;
        }

        try {
            // Try Zulu time format first
            return LocalDateTime.parse(militaryTime, ZULU_TIME_FORMAT);
        } catch (DateTimeParseException e) {
            // Try tactical time format
            return LocalDateTime.parse(militaryTime, TACTICAL_TIME_FORMAT);
        }
    }

    /**
     * Gets the current time in Zulu time format.
     * 
     * @return current time as Zulu time string
     */
    public static String getCurrentZuluTime() {
        return formatToZuluTime(LocalDateTime.now());
    }

    /**
     * Gets the current time in tactical time format.
     * 
     * @return current time as tactical time string
     */
    public static String getCurrentTacticalTime() {
        return formatToTacticalTime(LocalDateTime.now());
    }

    /**
     * Checks if a date time is within the last specified number of hours.
     * Useful for determining recent activity or status updates.
     * 
     * @param dateTime the date time to check
     * @param hours the number of hours to check within
     * @return true if the date time is within the specified hours
     */
    public static boolean isWithinLastHours(LocalDateTime dateTime, int hours) {
        if (dateTime == null || hours < 0) {
            return false;
        }
        
        LocalDateTime cutoff = LocalDateTime.now().minusHours(hours);
        return dateTime.isAfter(cutoff);
    }

    /**
     * Calculates the duration between two timestamps in minutes.
     * Useful for mission duration calculations.
     * 
     * @param start start time
     * @param end end time
     * @return duration in minutes
     */
    public static long calculateDurationMinutes(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            return 0;
        }
        
        return java.time.Duration.between(start, end).toMinutes();
    }
}
