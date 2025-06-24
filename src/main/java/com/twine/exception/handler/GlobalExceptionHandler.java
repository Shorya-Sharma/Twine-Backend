package com.twine.exception.handler;

import com.twine.dto.ErrorResponse;
import com.twine.exception.BaseException;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for REST controllers. Handles application-specific
 * and generic exceptions,
 * providing consistent error responses for clients.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles custom BaseException and returns a structured error response.
     *
     * @param ex      the BaseException thrown
     * @param request the HTTP request
     * @return a ResponseEntity containing the error response and status
     */
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBaseException(BaseException ex, HttpServletRequest request) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(ex.getStatus().value())
                .error(ex.getStatus().getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(errorResponse, ex.getStatus());
    }

    /**
     * Handles authentication failures due to bad credentials.
     *
     * @param ex      the BadCredentialsException thrown
     * @param request the HTTP request
     * @return a ResponseEntity containing the error response and 401 status
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException ex,
            HttpServletRequest request) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(401)
                .error("Unauthorized")
                .message("Invalid email or password")
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(errorResponse, org.springframework.http.HttpStatus.UNAUTHORIZED);
    }

    /**
     * Handles validation errors for method arguments and returns a map of field
     * errors.
     *
     * @param ex the MethodArgumentNotValidException thrown
     * @return a ResponseEntity containing a map of field names to error messages
     *         and 400 status
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, org.springframework.http.HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles all uncaught exceptions and returns a generic error response.
     *
     * @param ex      the Exception thrown
     * @param request the HTTP request
     * @return a ResponseEntity containing the error response and 500 status
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex, HttpServletRequest request) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(500)
                .error("Internal Server Error")
                .message("An unexpected error occurred")
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(errorResponse, org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR);
    }
}