package com.twine.controller;

import com.twine.dto.AuthenticationRequest;
import com.twine.dto.AuthenticationResponse;
import com.twine.dto.InitiateRegistrationRequest;
import com.twine.dto.RegisterRequest;
import com.twine.service.interfaces.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register/initiate")
    public ResponseEntity<Void> initiateRegistration(
            @Valid @RequestBody InitiateRegistrationRequest request
    ) {
        authenticationService.initiateRegistration(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/register/complete")
    public ResponseEntity<AuthenticationResponse> completeRegistration(
            @Valid @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(authenticationService.completeRegistration(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @Valid @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }
} 