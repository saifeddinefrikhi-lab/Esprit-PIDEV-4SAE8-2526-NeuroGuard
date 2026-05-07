package com.esprit.microservice.careplanservice.utils;

import com.esprit.microservice.careplanservice.dto.UserDto;
import lombok.extern.slf4j.Slf4j;

/**
 * Utility methods for common operations across services.
 */
@Slf4j
public class ServiceUtils {
    private ServiceUtils() {
        // Utility class, no instantiation
    }

    /**
     * Extracts full name from UserDto, handling null values gracefully.
     * @param user the user DTO
     * @return full name or null if user is null
     */
    public static String extractFullName(UserDto user) {
        if (user == null) {
            return null;
        }
        String firstName = user.getFirstName() != null ? user.getFirstName() : "";
        String lastName = user.getLastName() != null ? user.getLastName() : "";
        return (firstName + " " + lastName).trim();
    }

    /**
     * Safely gets a string value, returning empty string if null.
     * @param value the value to check
     * @return the value or empty string if null
     */
    public static String safeString(String value) {
        return value != null ? value : "";
    }

    /**
     * Gets provider label for display, using provider name or default label.
     * @param provider the provider user DTO
     * @return provider name or default label
     */
    public static String getProviderLabel(UserDto provider) {
        if (provider == null) {
            return Constants.DEFAULT_PROVIDER_LABEL;
        }
        String providerName = extractFullName(provider);
        return !providerName.isBlank() ? providerName : Constants.DEFAULT_PROVIDER_LABEL;
    }

    /**
     * Converts enum name to display format, with default fallback.
     * @param enumValue the enum value
     * @param defaultValue the default if null
     * @return enum name or default value
     */
    public static String enumNameOrDefault(Enum<?> enumValue, String defaultValue) {
        return enumValue != null ? enumValue.name() : defaultValue;
    }
}
