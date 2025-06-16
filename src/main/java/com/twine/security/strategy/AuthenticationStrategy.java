package com.twine.security.strategy;

import com.twine.dto.AuthenticationRequest;
import com.twine.dto.AuthenticationResponse;

public interface AuthenticationStrategy {
    AuthenticationResponse authenticate(AuthenticationRequest request);
    boolean supports(AuthenticationRequest request);
} 