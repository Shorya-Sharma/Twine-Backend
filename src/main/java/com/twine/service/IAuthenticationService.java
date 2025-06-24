package com.twine.service;

import com.twine.dto.AuthenticationRequest;
import com.twine.dto.AuthenticationResponse;
import com.twine.dto.InitiateRegistrationRequest;
import com.twine.dto.RegisterRequest;

/**
 * Service interface for user authentication and registration operations.
 */
public interface IAuthenticationService {
    /**
     * Initiates the registration process for a new user.
     *
     * @param request the registration initiation request
     */
    void initiateRegistration(InitiateRegistrationRequest request);

    /**
     * Completes the registration process for a new user.
     *
     * @param request the registration completion request
     * @return the authentication response after successful registration
     */
    AuthenticationResponse completeRegistration(RegisterRequest request);

    /**
     * Authenticates a user based on the provided credentials.
     *
     * @param request the authentication request
     * @return the authentication response after successful authentication
     */
    AuthenticationResponse authenticate(AuthenticationRequest request);
}