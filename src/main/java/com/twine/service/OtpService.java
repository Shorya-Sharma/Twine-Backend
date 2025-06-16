package com.twine.service;

import com.twine.entity.Otp;
import com.twine.repository.OtpRepository;
import com.twine.exception.AuthenticationException;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OtpService {

    private final OtpRepository otpRepository;
    private final EmailService emailService;
    private static final int OTP_LENGTH = 6;
    private static final int OTP_VALIDITY_MINUTES = 5;

    @Transactional
    public void generateAndSendOtp(String email) {
        // Generate a 6-digit OTP
        String otpCode = generateOtp();
        
        // Save OTP to database
        Otp otp = new Otp();
        otp.setEmail(email);
        otp.setOtpCode(otpCode);
        otp.setExpiryTime(LocalDateTime.now().plusMinutes(OTP_VALIDITY_MINUTES));
        otp.setUsed(false);
        otpRepository.save(otp);

        try {
            // Send OTP via email
            emailService.sendOtpEmail(email, otpCode);
        } catch (MessagingException e) {
            throw new AuthenticationException("Failed to send OTP email: " + e.getMessage());
        }
    }

    @Transactional
    public void validateOtp(String email, String otpCode) {
        Otp otp = otpRepository.findByEmailAndUsedFalseOrderByExpiryTimeDesc(email)
                .orElseThrow(() -> new AuthenticationException("No valid OTP found for this email"));

        if (otp.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new AuthenticationException("OTP has expired");
        }

        if (!otp.getOtpCode().equals(otpCode)) {
            throw new AuthenticationException("Invalid OTP");
        }

        // Mark OTP as used
        otp.setUsed(true);
        otpRepository.save(otp);
    }

    private String generateOtp() {
        Random random = new Random();
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(random.nextInt(10));
        }
        return otp.toString();
    }
} 