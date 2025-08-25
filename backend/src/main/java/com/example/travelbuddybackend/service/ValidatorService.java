package com.example.travelbuddybackend.service;

import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

/**
 * Centralized validation service for travel booking system
 *
 * Handles validation for:
 * - Date format validation (YYYY-MM-DD)
 * - Time format validation (HH:MM)
 * - Duration format validation (e.g., "2h 30m", "45m", "1h")
 *
 * This service follows SpringBoot best practices by:
 * - Using @Service annotation for dependency injection
 * - Providing consistent error logging
 * - Centralizing validation logic to avoid duplication
 */
@Service
public class ValidatorService {

    // Date format pattern - matches your Angular component format: YYYY-MM-DD
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);

    // Time format pattern - standard 24-hour format: HH:MM
    private static final String TIME_FORMAT = "HH:mm";
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern(TIME_FORMAT);

    // Duration pattern - matches formats like "2h 30m", "1h", "45m"
    private static final Pattern DURATION_PATTERN = Pattern.compile("^(?:(\\d+)h)?\\s*(?:(\\d+)m)?$");

    /**
     * Validates date string format from Angular date dropdown
     * Expected format: YYYY-MM-DD (e.g., "2024-12-25")
     *
     * @param dateString The date string to validate
     * @return true if valid format and represents a real date, false otherwise
     */
    public boolean isValidDate(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            System.out.println("✗ Validator: Date cannot be null or empty");
            return false;
        }

        try {
            // Parse the date to ensure it's valid (e.g., 2024-02-30 would fail)
            LocalDate.parse(dateString.trim(), DATE_FORMATTER);
            System.out.println("✓ Validator: Valid date format: " + dateString);
            return true;
        } catch (DateTimeParseException e) {
            System.out.println("✗ Validator: Invalid date format '" + dateString + "'. Expected: " + DATE_FORMAT);
            return false;
        }
    }

    /**
     * Validates time string format
     * Expected format: HH:MM in 24-hour format (e.g., "14:30", "09:15")
     *
     * @param timeString The time string to validate
     * @return true if valid format, false otherwise
     */
    public boolean isValidTime(String timeString) {
        if (timeString == null || timeString.trim().isEmpty()) {
            System.out.println("✗ Validator: Time cannot be null or empty");
            return false;
        }

        try {
            LocalTime.parse(timeString.trim(), TIME_FORMATTER);
            System.out.println("✓ Validator: Valid time format: " + timeString);
            return true;
        } catch (DateTimeParseException e) {
            System.out.println("✗ Validator: Invalid time format '" + timeString + "'. Expected: " + TIME_FORMAT);
            return false;
        }
    }

    /**
     * Validates duration string format for travel times
     * Expected formats: "2h 30m", "1h", "45m", "3h 0m"
     *
     * @param durationString The duration string to validate
     * @return true if valid format, false otherwise
     */
    public boolean isValidDuration(String durationString) {
        if (durationString == null || durationString.trim().isEmpty()) {
            System.out.println("✗ Validator: Duration cannot be null or empty");
            return false;
        }

        String trimmedDuration = durationString.trim();

        if (!DURATION_PATTERN.matcher(trimmedDuration).matches()) {
            System.out.println("✗ Validator: Invalid duration format '" + durationString + "'. Expected: '2h 30m', '1h', or '45m'");
            return false;
        }

        // Additional validation: must have at least hours OR minutes
        if (!trimmedDuration.contains("h") && !trimmedDuration.contains("m")) {
            System.out.println("✗ Validator: Duration must include hours (h) and/or minutes (m)");
            return false;
        }

        System.out.println("✓ Validator: Valid duration format: " + durationString);
        return true;
    }

    /**
     * Validates that a departure date is not in the past
     * Useful for booking validation
     *
     * @param dateString Date string in YYYY-MM-DD format
     * @return true if date is today or future, false if past or invalid
     */
    public boolean isValidFutureDate(String dateString) {
        if (!isValidDate(dateString)) {
            return false; // Already logged in isValidDate()
        }

        try {
            LocalDate inputDate = LocalDate.parse(dateString.trim(), DATE_FORMATTER);
            LocalDate today = LocalDate.now();

            if (inputDate.isBefore(today)) {
                System.out.println("✗ Validator: Date " + dateString + " is in the past");
                return false;
            }

            System.out.println("✓ Validator: Future date validation passed: " + dateString);
            return true;
        } catch (Exception e) {
            System.out.println("✗ Validator: Error validating future date: " + e.getMessage());
            return false;
        }
    }

    /**
     * Comprehensive validation for booking date fields
     * Validates both format and business rules
     *
     * @param departureDate Departure date string
     * @param arrivalDate Arrival date string (can be null for same-day travel)
     * @return true if both dates are valid, false otherwise
     */
    public boolean validateBookingDates(String departureDate, String arrivalDate) {
        // Validate departure date format and future date rule
        if (!isValidFutureDate(departureDate)) {
            return false; // Error already logged
        }

        // If arrival date is provided, validate it
        if (arrivalDate != null && !arrivalDate.trim().isEmpty()) {
            if (!isValidDate(arrivalDate)) {
                return false; // Error already logged
            }

            try {
                LocalDate depDate = LocalDate.parse(departureDate.trim(), DATE_FORMATTER);
                LocalDate arrDate = LocalDate.parse(arrivalDate.trim(), DATE_FORMATTER);

                if (arrDate.isBefore(depDate)) {
                    System.out.println("✗ Validator: Arrival date cannot be before departure date");
                    return false;
                }
            } catch (Exception e) {
                System.out.println("✗ Validator: Error comparing dates: " + e.getMessage());
                return false;
            }
        }

        System.out.println("✓ Validator: Booking dates validation passed");
        return true;
    }
}