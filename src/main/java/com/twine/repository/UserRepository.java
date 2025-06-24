package com.twine.repository;

import com.twine.entity.AuthUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository interface for managing user entities in the database.
 */
public interface UserRepository extends JpaRepository<AuthUser, Long> {
    /**
     * Finds a user by their email address.
     *
     * @param email the user's email address
     * @return an Optional containing the user if found, or empty otherwise
     */
    Optional<AuthUser> findByEmail(String email);

    /**
     * Checks if a user exists with the given email address.
     *
     * @param email the user's email address
     * @return true if a user exists, false otherwise
     */
    boolean existsByEmail(String email);
}