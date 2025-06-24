package com.twine.repository;

import com.twine.entity.Otp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing OTP (One-Time Password) entities in the
 * database.
 */
@Repository
public interface OtpRepository extends JpaRepository<Otp, Long> {
    /**
     * Finds an unused OTP by email and OTP code.
     *
     * @param email   the recipient's email address
     * @param otpCode the OTP code
     * @return an Optional containing the OTP if found, or empty otherwise
     */
    Optional<Otp> findByEmailAndOtpCodeAndUsedFalse(String email, String otpCode);

    /**
     * Finds the most recent unused OTP for the given email, ordered by creation
     * time descending.
     *
     * @param email the recipient's email address
     * @return an Optional containing the latest unused OTP if found, or empty
     *         otherwise
     */
    Optional<Otp> findTopByEmailAndUsedFalseOrderByCreatedAtDesc(String email);

    /**
     * Finds an unused OTP for the given email, ordered by expiry time descending.
     *
     * @param email the recipient's email address
     * @return an Optional containing the unused OTP if found, or empty otherwise
     */
    Optional<Otp> findByEmailAndUsedFalseOrderByExpiryTimeDesc(String email);
}