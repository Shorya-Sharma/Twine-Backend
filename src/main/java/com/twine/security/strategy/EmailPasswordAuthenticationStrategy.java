package com.twine.security.strategy;

import com.twine.dto.AuthenticationRequest;
import com.twine.dto.AuthenticationResponse;
import com.twine.entity.AuthUser;
import com.twine.exception.AuthenticationException;
import com.twine.exception.ResourceNotFoundException;
import com.twine.repository.AuthUserRepository;
import com.twine.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

/**
 * Authentication strategy for email and password-based authentication.
 * Implements the AuthenticationStrategy interface to provide authentication
 * logic
 * and support checks for email/password requests.
 */
@Component
@RequiredArgsConstructor
public class EmailPasswordAuthenticationStrategy implements AuthenticationStrategy {

    private final AuthenticationManager authenticationManager;
    private final AuthUserRepository authUserRepository;
    private final JwtService jwtService;

    /**
     * Authenticates a user using email and password credentials.
     *
     * @param request the authentication request containing email and password
     * @return the authentication response with a JWT token if successful
     * @throws AuthenticationException if authentication fails
     */
    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()));
        } catch (Exception e) {
            throw new AuthenticationException("Invalid email or password");
        }

        AuthUser authUser = authUserRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String jwtToken = jwtService.generateToken(authUser);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    /**
     * Checks if this strategy supports the given authentication request (requires
     * email and password).
     *
     * @param request the authentication request
     * @return true if both email and password are present, false otherwise
     */
    @Override
    public boolean supports(AuthenticationRequest request) {
        return request.getEmail() != null && request.getPassword() != null;
    }
}