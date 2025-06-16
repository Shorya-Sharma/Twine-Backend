package com.twine.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenFactory {
    private final JwtService jwtService;

    public JwtTokenFactory(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    public String createToken(UserDetails userDetails) {
        return createToken(new HashMap<>(), userDetails);
    }

    public String createToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return jwtService.generateToken(extraClaims, userDetails);
    }
} 