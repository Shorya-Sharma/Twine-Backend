package com.twine.constants;

public final class ErrorConstants {
    private ErrorConstants() {
        // Private constructor to prevent instantiation
    }

    public static final String EMAIL_ALREADY_REGISTERED = "Email already registered";
    public static final String USER_NOT_FOUND = "User not found";
    public static final String INVALID_EMAIL_PASSWORD = "Invalid email or password";
    public static final String NO_VALID_OTP = "No valid OTP found for this email";
    public static final String OTP_EXPIRED = "OTP has expired";
    public static final String INVALID_OTP = "Invalid OTP";
    public static final String FAILED_TO_SEND_OTP_EMAIL = "Failed to send OTP email: %s";
} 