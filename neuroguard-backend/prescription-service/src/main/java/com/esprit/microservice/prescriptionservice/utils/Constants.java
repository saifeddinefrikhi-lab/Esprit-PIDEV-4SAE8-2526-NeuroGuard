package com.esprit.microservice.prescriptionservice.utils;

/**
 * Central constants for Prescription service to reduce code duplication and magic strings.
 */
public class Constants {
    private Constants() {
        // Utility class, no instantiation
    }

    // Roles
    public static final String ROLE_PATIENT = "PATIENT";
    public static final String ROLE_PROVIDER = "PROVIDER";
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_CAREGIVER = "CAREGIVER";

    // Statuses
    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String STATUS_INACTIVE = "INACTIVE";
    public static final String STATUS_PENDING = "PENDING";

    // Error Messages
    public static final String PRESCRIPTION_NOT_FOUND = "Prescription not found";
    public static final String PATIENT_NOT_FOUND = "Patient not found";
    public static final String UNAUTHORIZED_ACCESS = "Access denied";

    // Log prefixes
    public static final String LOG_PREFIX = "[PRESCRIPTION]";

    // WebSocket topics
    public static final String WEBSOCKET_PRESCRIPTIONS_TOPIC = "/topic/prescriptions/";
}
