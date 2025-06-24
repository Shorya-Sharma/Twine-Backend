package com.twine.config;

import com.twine.repository.AuthUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuration class for authentication-related beans in the Twine
 * application.
 * <p>
 * This class defines and provides beans required for authentication and
 * security,
 * including:
 * <ul>
 * <li>{@link org.springframework.security.core.userdetails.UserDetailsService}
 * - Loads user-specific data.</li>
 * <li>{@link org.springframework.security.authentication.AuthenticationProvider}
 * - Handles authentication logic.</li>
 * <li>{@link org.springframework.security.authentication.AuthenticationManager}
 * - Manages authentication processes.</li>
 * <li>{@link org.springframework.security.crypto.password.PasswordEncoder} -
 * Encodes and verifies passwords.</li>
 * </ul>
 * These beans are essential for Spring Security integration and are used
 * throughout
 * the authentication and authorization flow of the application.
 * </p>
 */
@Configuration
@RequiredArgsConstructor
public class AuthConfig {

    private final AuthUserRepository authUserRepository;

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> authUserRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}