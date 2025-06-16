package com.twine.constants;

public final class AuthConstants {
    private AuthConstants() {
        // Private constructor to prevent instantiation
    }

    public static final String BEARER_PREFIX = "Bearer ";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String JWT_SECRET_PROPERTY = "jwt.secret";
    public static final String JWT_EXPIRATION_PROPERTY = "jwt.expiration";
} 