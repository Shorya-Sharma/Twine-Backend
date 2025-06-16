package com.twine.constants;

public final class OtpConstants {
    private OtpConstants() {
        // Private constructor to prevent instantiation
    }

    public static final int OTP_LENGTH = 6;
    public static final int OTP_VALIDITY_MINUTES = 5;
    public static final String OTP_CHARS = "0123456789";
    public static final String OTP_EMAIL_TEMPLATE = "otp-email";
    public static final String EMAIL_SUBJECT = "Your Twine Verification Code";
} 