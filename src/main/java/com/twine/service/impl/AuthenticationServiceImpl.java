package com.twine.service.impl;

import com.twine.constants.ErrorConstants;
import com.twine.dto.AuthenticationRequest;
import com.twine.dto.AuthenticationResponse;
import com.twine.dto.RegisterRequest;
import com.twine.entity.AuthUser;
import com.twine.entity.Role;
import com.twine.exception.AuthenticationException;
import com.twine.exception.ResourceAlreadyExistsException;
import com.twine.exception.ResourceNotFoundException;
import com.twine.repository.AuthUserRepository;
import com.twine.security.JwtService;
import com.twine.service.interfaces.AuthenticationService;
import com.twine.service.interfaces.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthUserRepository authUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final OtpService otpService;

    @Override
    @Transactional
    public void initiateRegistration(RegisterRequest request) {
        validateEmailNotExists(request.getEmail());
        otpService.generateAndSendOtp(request.getEmail());
    }

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
        AuthUser authUser = new AuthUser();
        authUser.setEmail(request.getEmail());
        authUser.setPassword(passwordEncoder.encode(request.getPassword()));
        authUser.setRole(Role.USER);
        return authUserRepository.save(authUser);
    }

    private void authenticateUser(AuthenticationRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (Exception e) {
            throw new AuthenticationException(ErrorConstants.INVALID_EMAIL_PASSWORD);
        }
    }

    private AuthUser findUserByEmail(String email) {
        return authUserRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorConstants.USER_NOT_FOUND));
    }
} 