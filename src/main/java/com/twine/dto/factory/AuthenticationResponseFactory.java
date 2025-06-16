package com.twine.dto.factory;

import com.twine.dto.AuthenticationResponse;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationResponseFactory {
    
    public AuthenticationResponse createResponse(String token) {
        return AuthenticationResponse.builder()
                .token(token)
                .build();
    }
} 