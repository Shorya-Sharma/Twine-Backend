package com.twine.exception.handler;

import com.twine.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

public abstract class BaseExceptionHandler {
    
    protected ResponseEntity<ErrorResponse> createErrorResponse(
            String message,
            int status,
            String error,
            HttpServletRequest request
    ) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status)
                .error(error)
                .message(message)
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(errorResponse, org.springframework.http.HttpStatus.valueOf(status));
    }
} 