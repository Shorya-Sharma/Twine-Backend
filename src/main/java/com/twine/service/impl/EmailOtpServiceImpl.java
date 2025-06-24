package com.twine.service.impl;

import com.twine.constants.ErrorConstants;
import com.twine.constants.OtpConstants;
import com.twine.entity.Otp;
import com.twine.exception.AuthenticationException;
import com.twine.repository.OtpRepository;
import com.twine.service.IOtpService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.security.SecureRandom;
import java.time.LocalDateTime;

/**
 * Service implementation for handling OTP generation, validation, and email
 * delivery.
 * <p>
 * This service is responsible for generating OTP codes, saving and validating
 * them,
 * and sending OTP emails to recipients using a template engine and mail sender.
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailOtpServiceImpl implements IOtpService {
    private final OtpRepository otpRepository;
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final SecureRandom secureRandom = new SecureRandom();

    /**
     * Generates an OTP, saves it, and sends it to the recipient via email.
     *
     * @param recipientEmail the recipient's email address
     * @throws AuthenticationException if sending the OTP email fails
     */
    @Override
    @Transactional
    public void generateAndSendOtp(String recipientEmail) {
        String otpValue = generateRandomOtp();
        saveOtpForRecipient(recipientEmail, otpValue);
        try {
            sendOtpEmail(recipientEmail, otpValue);
        } catch (MessagingException e) {
            log.error("Failed to send OTP email to: {}", recipientEmail, e);
            throw new AuthenticationException(String.format(ErrorConstants.FAILED_TO_SEND_OTP_EMAIL, e.getMessage()));
        }
    }

    /**
     * Validates the provided OTP for the recipient and marks it as used if valid.
     *
     * @param recipientEmail the recipient's email address
     * @param otpValue       the OTP code to validate
     * @throws AuthenticationException if the OTP is invalid, expired, or not found
     */
    @Override
    @Transactional
    public void validateOtp(String recipientEmail, String otpValue) {
        Otp otpRecord = findValidOtpForRecipient(recipientEmail);
        validateOtpNotExpired(otpRecord);
        validateOtpValue(otpRecord, otpValue);
        markOtpAsUsed(otpRecord);
    }

    /**
     * Sends an OTP email to the specified recipient.
     *
     * @param recipientEmail the recipient's email address
     * @param otpValue       the OTP code to send
     * @throws MessagingException if email sending fails
     */
    public void sendOtpEmail(String recipientEmail, String otpValue) throws MessagingException {
        try {
            Context emailContext = createEmailContext(otpValue);
            String emailContent = templateEngine.process(OtpConstants.OTP_EMAIL_TEMPLATE, emailContext);
            MimeMessage message = createMimeMessage(recipientEmail, emailContent);
            mailSender.send(message);
            log.info("OTP email sent successfully to: {}", recipientEmail);
        } catch (MessagingException e) {
            log.error("Failed to send OTP email to: {}", recipientEmail, e);
            throw new MessagingException("Failed to send OTP email: " + e.getMessage());
        }
    }

    // --- Private Helper Methods ---

    /**
     * Generates a random OTP code using the configured length and character set.
     *
     * @return the generated OTP code
     */
    private String generateRandomOtp() {
        StringBuilder otpBuilder = new StringBuilder(OtpConstants.OTP_LENGTH);
        for (int i = 0; i < OtpConstants.OTP_LENGTH; i++) {
            otpBuilder.append(OtpConstants.OTP_CHARS.charAt(secureRandom.nextInt(OtpConstants.OTP_CHARS.length())));
        }
        return otpBuilder.toString();
    }

    /**
     * Saves the generated OTP for the recipient in the repository.
     *
     * @param recipientEmail the recipient's email address
     * @param otpValue       the OTP code to save
     */
    private void saveOtpForRecipient(String recipientEmail, String otpValue) {
        Otp otp = Otp.builder()
                .email(recipientEmail)
                .otpCode(otpValue)
                .expiryTime(LocalDateTime.now().plusMinutes(OtpConstants.OTP_VALIDITY_MINUTES))
                .used(false)
                .createdAt(LocalDateTime.now())
                .build();
        otpRepository.save(otp);
        log.info("OTP saved for email: {}", recipientEmail);
    }

    /**
     * Finds a valid (unused and unexpired) OTP for the recipient.
     *
     * @param recipientEmail the recipient's email address
     * @return the valid OTP entity
     * @throws AuthenticationException if no valid OTP is found
     */
    private Otp findValidOtpForRecipient(String recipientEmail) {
        return otpRepository.findByEmailAndUsedFalseOrderByExpiryTimeDesc(recipientEmail)
                .orElseThrow(() -> new AuthenticationException(ErrorConstants.NO_VALID_OTP));
    }

    /**
     * Validates that the OTP has not expired.
     *
     * @param otpRecord the OTP entity to check
     * @throws AuthenticationException if the OTP is expired
     */
    private void validateOtpNotExpired(Otp otpRecord) {
        if (otpRecord.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new AuthenticationException(ErrorConstants.OTP_EXPIRED);
        }
    }

    /**
     * Validates that the provided OTP code matches the stored OTP.
     *
     * @param otpRecord the OTP entity
     * @param otpValue  the OTP code to validate
     * @throws AuthenticationException if the OTP code does not match
     */
    private void validateOtpValue(Otp otpRecord, String otpValue) {
        if (!otpRecord.getOtpCode().equals(otpValue)) {
            throw new AuthenticationException(ErrorConstants.INVALID_OTP);
        }
    }

    /**
     * Marks the OTP as used in the repository.
     *
     * @param otpRecord the OTP entity to mark as used
     */
    private void markOtpAsUsed(Otp otpRecord) {
        otpRecord.setUsed(true);
        otpRepository.save(otpRecord);
        log.info("OTP marked as used for email: {}", otpRecord.getEmail());
    }

    /**
     * Creates the Thymeleaf context for the OTP email template.
     *
     * @param otpValue the OTP code to include in the email
     * @return the Thymeleaf context with the OTP variable set
     */
    private Context createEmailContext(String otpValue) {
        Context context = new Context();
        context.setVariable("otp", otpValue);
        return context;
    }

    /**
     * Creates a MIME email message with the given content.
     *
     * @param recipientEmail the recipient's email address
     * @param emailContent   the HTML content of the email
     * @return the constructed MimeMessage
     * @throws MessagingException if message creation fails
     */
    private MimeMessage createMimeMessage(String recipientEmail, String emailContent) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(recipientEmail);
        helper.setSubject(OtpConstants.EMAIL_SUBJECT);
        helper.setText(emailContent, true);
        return message;
    }
}