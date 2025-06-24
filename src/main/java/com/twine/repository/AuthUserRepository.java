package com.twine.repository;

import com.twine.entity.AuthUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository interface for managing AuthUser entities in the database.
 */
public interface AuthUserRepository extends JpaRepository<AuthUser, Long> {
    /**
     * Finds an AuthUser by their email address.
     *
     * @param email the user's email address
     * @return an Optional containing the AuthUser if found, or empty otherwise
     */
    Optional<AuthUser> findByEmail(String email);

    /**
     * Checks if an AuthUser exists with the given email address.
     *
     * @param email the user's email address
     * @return true if an AuthUser exists, false otherwise
     */
    boolean existsByEmail(String email);
}