package com.twine.service.impl;

import com.twine.constants.ErrorConstants;
import com.twine.dto.AuthenticationRequest;
import com.twine.dto.AuthenticationResponse;
import com.twine.dto.InitiateRegistrationRequest;
import com.twine.dto.RegisterRequest;
import com.twine.entity.AuthUser;
import com.twine.entity.Role;
import com.twine.exception.AuthenticationException;
import com.twine.exception.ResourceAlreadyExistsException;
import com.twine.exception.ResourceNotFoundException;
import com.twine.repository.AuthUserRepository;
import com.twine.security.JwtService;
import com.twine.service.IAuthenticationService;
import com.twine.service.IOtpService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation for user authentication and registration operations.
 */
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements IAuthenticationService {

    private final AuthUserRepository authUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final IOtpService otpService;

    /**
     * Initiates the registration process for a new user by validating the email and
     * sending an OTP.
     *
     * @param request the registration initiation request
     */
    @Override
    @Transactional
    public void initiateRegistration(InitiateRegistrationRequest request) {
        validateEmailNotExists(request.getEmail());
        otpService.generateAndSendOtp(request.getEmail());
    }

    /**
     * Completes the registration process by validating the OTP and creating the
     * user.
     *
     * @param request the registration completion request
     * @return the authentication response after successful registration
     */
    @Override
    @Transactional
    public AuthenticationResponse completeRegistration(RegisterRequest request) {
        validateEmailNotExists(request.getEmail());
        otpService.validateOtp(request.getEmail(), request.getOtp());

        AuthUser authUser = createUser(request);
        String jwtToken = jwtService.generateToken(authUser);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    /**
     * Authenticates a user based on the provided credentials and returns a JWT
     * token.
     *
     * @param request the authentication request
     * @return the authentication response after successful authentication
     */
    @Override
    @Transactional
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticateUser(request);
        AuthUser authUser = findUserByEmail(request.getEmail());
        String jwtToken = jwtService.generateToken(authUser);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    private void validateEmailNotExists(String email) {
        if (authUserRepository.existsByEmail(email)) {
            throw new ResourceAlreadyExistsException(ErrorConstants.EMAIL_ALREADY_REGISTERED);
        }
    }

    private AuthUser createUser(RegisterRequest request) {
        return authUserRepository.save(
                AuthUser.builder()
                        .email(request.getEmail())
                        .password(passwordEncoder.encode(request.getPassword()))
                        .role(Role.USER)
                        .enabled(true)
                        .build());
    }

    private void authenticateUser(AuthenticationRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()));
        } catch (Exception e) {
            throw new AuthenticationException(ErrorConstants.INVALID_EMAIL_PASSWORD);
        }
    }

    private AuthUser findUserByEmail(String email) {
        return authUserRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorConstants.USER_NOT_FOUND));
    }
}