package com.esprit.microservice.prescriptionservice.utils;

/**
 * Utility methods for common operations across prescription service.
 */
public class ServiceUtils {
    private ServiceUtils() {
        // Utility class, no instantiation
    }

    /**
     * Safely extract full name from user DTO.
     * @param firstName first name
     * @param lastName last name
     * @return full name or empty string
     */
    public static String getFullName(String firstName, String lastName) {
        String first = firstName != null ? firstName : "";
        String last = lastName != null ? lastName : "";
        return (first + " " + last).trim();
    }

    /**
     * Check if string is valid and not empty.
     * @param value the value to check
     * @return true if value is not empty
     */
    public static boolean isValidString(String value) {
        return value != null && !value.isBlank();
    }

    /**
     * Convert enum to string with fallback.
     * @param enumValue the enum
     * @param defaultValue fallback value
     * @return enum name or default
     */
    public static String enumNameOrDefault(Enum<?> enumValue, String defaultValue) {
        return enumValue != null ? enumValue.name() : defaultValue;
    }
}
