package com.twine.controller;

import com.twine.dto.AuthenticationRequest;
import com.twine.dto.AuthenticationResponse;
import com.twine.dto.RegisterRequest;
import com.twine.service.AuthenticationService;
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
            @RequestBody RegisterRequest request
    ) {
        authenticationService.initiateRegistration(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/register/complete")
    public ResponseEntity<AuthenticationResponse> completeRegistration(
            @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(authenticationService.completeRegistration(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }
} 