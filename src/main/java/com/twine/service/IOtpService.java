package com.twine.service;

/**
 * Service interface for handling OTP (One-Time Password) operations such as
 * generation, delivery, and validation.
 */
public interface IOtpService {
    /**
     * Generates an OTP for the recipient and sends it (e.g., via email).
     *
     * @param recipient the recipient's identifier (e.g., email address)
     */
    void generateAndSendOtp(String recipient);

    /**
     * Validates the provided OTP for the recipient.
     *
     * @param recipient the recipient's identifier (e.g., email address)
     * @param otpCode   the OTP code to validate
     */
    void validateOtp(String recipient, String otpCode);
}