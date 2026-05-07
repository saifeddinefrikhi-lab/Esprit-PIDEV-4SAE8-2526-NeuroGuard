package com.esprit.microservice.careplanservice.utils;

/**
 * Central constants for CarePlan service to reduce code duplication and magic strings.
 */
public class Constants {
    private Constants() {
        // Utility class, no instantiation
    }

    // Roles
    public static final String ROLE_PATIENT = "PATIENT";
    public static final String ROLE_PROVIDER = "PROVIDER";
    public static final String ROLE_ADMIN = "ADMIN";

    // Statuses
    public static final String STATUS_TODO = "TODO";
    public static final String DEFAULT_PRIORITY = "MEDIUM";

    // Messages
    public static final String PATIENT_NOT_FOUND = "Patient not found or not a patient";
    public static final String CARE_PLAN_NOT_FOUND = "Care plan not found";
    public static final String UNAUTHORIZED_PROVIDER = "You are not the creator of this care plan";

    // Log prefixes
    public static final String LOG_MAIL_PREFIX = "[MAIL]";
    public static final String LOG_SMS_PREFIX = "[SMS]";

    // WebSocket topics
    public static final String WEBSOCKET_CARE_PLANS_TOPIC = "/topic/care-plans/";
    public static final String WEBSOCKET_MESSAGES_TOPIC = "/topic/messages/";

    // Default provider label
    public static final String DEFAULT_PROVIDER_LABEL = "votre provider";
}
