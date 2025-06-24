package com.twine.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Factory class for creating JWT tokens for user authentication.
 */
@Component
public class JwtTokenFactory {
    private final JwtService jwtService;

    /**
     * Constructs a JwtTokenFactory with the given JwtService.
     *
     * @param jwtService the JwtService used for token generation
     */
    public JwtTokenFactory(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    /**
     * Creates a JWT token for the given user details.
     *
     * @param userDetails the user details
     * @return the generated JWT token
     */
    public String createToken(UserDetails userDetails) {
        return createToken(new HashMap<>(), userDetails);
    }

    /**
     * Creates a JWT token with extra claims for the given user details.
     *
     * @param extraClaims additional claims to include in the token
     * @param userDetails the user details
     * @return the generated JWT token
     */
    public String createToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return jwtService.generateToken(extraClaims, userDetails);
    }
}