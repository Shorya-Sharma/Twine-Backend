package com.twine.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Application-wide configuration.
 */
@Configuration
@EnableJpaAuditing
public class ApplicationConfig {
}