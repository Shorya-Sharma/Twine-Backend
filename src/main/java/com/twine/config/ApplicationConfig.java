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
 * Configuration class for application-wide authentication and security beans.
 */
@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    private final AuthUserRepository authUserRepository;

    /**
     * Provides a UserDetailsService that loads user details from the
     * AuthUserRepository.
     *
     * @return the UserDetailsService bean
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> authUserRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    /**
     * Provides an AuthenticationProvider that uses the custom UserDetailsService
     * and password encoder.
     *
     * @return the AuthenticationProvider bean
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Provides the AuthenticationManager bean from the Spring Security
     * configuration.
     *
     * @param config the AuthenticationConfiguration
     * @return the AuthenticationManager bean
     * @throws Exception if the authentication manager cannot be created
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Provides a BCryptPasswordEncoder bean for password encoding.
     *
     * @return the PasswordEncoder bean
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}