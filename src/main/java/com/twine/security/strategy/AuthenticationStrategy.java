package com.twine.security.strategy;

import com.twine.dto.AuthenticationRequest;
import com.twine.dto.AuthenticationResponse;

/**
 * Strategy interface for different authentication mechanisms.
 */
public interface AuthenticationStrategy {
    /**
     * Authenticates a user based on the provided request.
     *
     * @param request the authentication request
     * @return the authentication response
     */
    AuthenticationResponse authenticate(AuthenticationRequest request);

    /**
     * Checks if this strategy supports the given authentication request.
     *
     * @param request the authentication request
     * @return true if supported, false otherwise
     */
    boolean supports(AuthenticationRequest request);
}