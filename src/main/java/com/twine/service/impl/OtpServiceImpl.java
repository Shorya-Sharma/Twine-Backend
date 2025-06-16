package com.twine.service.impl;

import com.twine.constants.ErrorConstants;
import com.twine.constants.OtpConstants;
import com.twine.entity.Otp;
import com.twine.exception.AuthenticationException;
import com.twine.repository.OtpRepository;
import com.twine.service.interfaces.EmailService;
import com.twine.service.interfaces.OtpService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private final OtpRepository otpRepository;
    private final EmailService emailService;
    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    @Transactional
    public void generateAndSendOtp(String email) {
        String otpCode = generateOtp();
        saveOtp(email, otpCode);
        sendOtpEmail(email, otpCode);
    }

    @Override
    @Transactional
    public void validateOtp(String email, String otpCode) {
        Otp otp = findValidOtp(email);
        validateOtpExpiration(otp);
        validateOtpCode(otp, otpCode);
        markOtpAsUsed(otp);
    }

    private String generateOtp() {
        StringBuilder otp = new StringBuilder(OtpConstants.OTP_LENGTH);
        for (int i = 0; i < OtpConstants.OTP_LENGTH; i++) {
            otp.append(OtpConstants.OTP_CHARS.charAt(secureRandom.nextInt(OtpConstants.OTP_CHARS.length())));
        }
        return otp.toString();
    }

    private void saveOtp(String email, String otpCode) {
        Otp otp = Otp.builder()
                .email(email)
                .otpCode(otpCode)
                .expiryTime(LocalDateTime.now().plusMinutes(OtpConstants.OTP_VALIDITY_MINUTES))
                .used(false)
                .build();
        
        otpRepository.save(otp);
        log.info("OTP saved for email: {}", email);
    }

    private void sendOtpEmail(String email, String otpCode) {
        try {
            emailService.sendOtpEmail(email, otpCode);
            log.info("OTP email sent to: {}", email);
        } catch (MessagingException e) {
            log.error("Failed to send OTP email to: {}", email, e);
            throw new AuthenticationException(String.format(ErrorConstants.FAILED_TO_SEND_OTP_EMAIL, e.getMessage()));
        }
    }

    private Otp findValidOtp(String email) {
        return otpRepository.findByEmailAndUsedFalseOrderByExpiryTimeDesc(email)
                .orElseThrow(() -> new AuthenticationException(ErrorConstants.NO_VALID_OTP));
    }

    private void validateOtpExpiration(Otp otp) {
        if (otp.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new AuthenticationException(ErrorConstants.OTP_EXPIRED);
        }
    }

    private void validateOtpCode(Otp otp, String otpCode) {
        if (!otp.getOtpCode().equals(otpCode)) {
            throw new AuthenticationException(ErrorConstants.INVALID_OTP);
        }
    }

    private void markOtpAsUsed(Otp otp) {
        otp.setUsed(true);
        otpRepository.save(otp);
        log.info("OTP marked as used for email: {}", otp.getEmail());
    }
} 