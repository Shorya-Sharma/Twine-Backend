package com.twine.controller;

import com.twine.dto.AuthenticationRequest;
import com.twine.dto.AuthenticationResponse;
import com.twine.dto.InitiateRegistrationRequest;
import com.twine.dto.RegisterRequest;
import com.twine.service.IAuthenticationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for handling user authentication and registration operations.
 * <p>
 * Provides endpoints for initiating registration (sending OTP), completing
 * registration (verifying OTP and creating user),
 * and authenticating users (login). All endpoints expect and return JSON
 * payloads.
 * </p>
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final IAuthenticationService authenticationService;

    /**
     * Initiates the user registration process by validating the request and sending
     * an OTP to the user's email.
     * <p>
     * Endpoint: <b>POST /api/v1/auth/register/initiate</b><br>
     * Request Body: {@link InitiateRegistrationRequest} (must be valid)<br>
     * Response: 200 OK (no content)
     * </p>
     *
     * @param request the registration initiation request containing the user's
     *                email
     * @return 200 OK if OTP is sent successfully
     */
    @PostMapping("/register/initiate")
    public ResponseEntity<Void> initiateRegistration(
            @Valid @RequestBody InitiateRegistrationRequest request) {
        authenticationService.initiateRegistration(request);
        return ResponseEntity.ok().build();
    }

    /**
     * Completes the user registration process by validating the OTP and creating
     * the user account.
     * <p>
     * Endpoint: <b>POST /api/v1/auth/register/complete</b><br>
     * Request Body: {@link RegisterRequest} (must be valid, includes email,
     * password, and OTP)<br>
     * Response: 200 OK with {@link AuthenticationResponse} containing a JWT token
     * </p>
     *
     * @param request the registration completion request with user details and OTP
     * @return 200 OK with authentication response containing the JWT token
     */
    @PostMapping("/register/complete")
    public ResponseEntity<AuthenticationResponse> completeRegistration(
            @Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authenticationService.completeRegistration(request));
    }

    /**
     * Authenticates a user using email and password credentials.
     * <p>
     * Endpoint: <b>POST /api/v1/auth/login</b><br>
     * Request Body: {@link AuthenticationRequest} (must be valid, includes email
     * and password)<br>
     * Response: 200 OK with {@link AuthenticationResponse} containing a JWT token
     * </p>
     *
     * @param request the authentication request with email and password
     * @return 200 OK with authentication response containing the JWT token
     */
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @Valid @RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }
}