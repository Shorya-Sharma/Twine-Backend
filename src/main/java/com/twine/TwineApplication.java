package com.twine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Twine Spring Boot application.
 * <p>
 * This class bootstraps the application, enabling auto-configuration and
 * component scanning.
 * </p>
 */
@SpringBootApplication
public class TwineApplication {
    /**
     * Starts the Twine Spring Boot application.
     *
     * @param args command-line arguments passed to the application
     */
    public static void main(String[] args) {
        SpringApplication.run(TwineApplication.class, args);
    }
}
