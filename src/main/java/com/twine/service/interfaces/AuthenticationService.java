package com.twine.service.interfaces;

import com.twine.dto.AuthenticationRequest;
import com.twine.dto.AuthenticationResponse;
import com.twine.dto.InitiateRegistrationRequest;
import com.twine.dto.RegisterRequest;

public interface AuthenticationService {
    void initiateRegistration(InitiateRegistrationRequest request);
    AuthenticationResponse completeRegistration(RegisterRequest request);
    AuthenticationResponse authenticate(AuthenticationRequest request);
} 