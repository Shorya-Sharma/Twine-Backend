package com.twine.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Base exception class for application-specific exceptions with HTTP status
 * support.
 */
@Getter
public class BaseException extends RuntimeException {
    private final HttpStatus status;
    private final String message;

    /**
     * Constructs a new BaseException with the specified detail message and HTTP
     * status.
     *
     * @param message the detail message
     * @param status  the HTTP status associated with the exception
     */
    public BaseException(String message, HttpStatus status) {
        super(message);
        this.message = message;
        this.status = status;
    }
}