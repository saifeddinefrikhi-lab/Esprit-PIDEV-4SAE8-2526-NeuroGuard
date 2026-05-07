package com.esprit.microservice.pharmacyservice.utils;

/**
 * Central constants for Pharmacy service to reduce code duplication and magic strings.
 */
public class Constants {
    private Constants() {
        // Utility class, no instantiation
    }

    // Roles
    public static final String ROLE_PATIENT = "PATIENT";
    public static final String ROLE_PROVIDER = "PROVIDER";
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_PHARMACIST = "PHARMACIST";

    // Statuses
    public static final String STATUS_AVAILABLE = "AVAILABLE";
    public static final String STATUS_UNAVAILABLE = "UNAVAILABLE";
    public static final String STATUS_PENDING = "PENDING";

    // Error Messages
    public static final String MEDICATION_NOT_FOUND = "Medication not found";
    public static final String PATIENT_NOT_FOUND = "Patient not found";
    public static final String UNAUTHORIZED_ACCESS = "Access denied";

    // Log prefixes
    public static final String LOG_PREFIX = "[PHARMACY]";

    // WebSocket topics
    public static final String WEBSOCKET_MEDICATIONS_TOPIC = "/topic/medications/";
}
