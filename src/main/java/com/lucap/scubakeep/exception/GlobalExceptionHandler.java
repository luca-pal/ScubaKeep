package com.lucap.scubakeep.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Centralized exception handler for REST controllers.
 * Maps domain-specific exceptions and validation errors to clean HTTP responses.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles cases where a requested resource (diver, dive log) is not found.
     * <p>
     * Returns: 404 Not Found
     */
    @ExceptionHandler({
            DiverNotFoundException.class,
            DiveLogNotFoundException.class
    })
    public ResponseEntity<Map<String, String>> handleNotFound(RuntimeException ex) {
        logger.warn("Resource not found: {}", ex.getMessage());
        Map<String, String> body = new HashMap<>();
        body.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    /**
     * Handles validation errors triggered by @Valid on request bodies.
     * <p>
     * Returns: 400 Bad Request with a map of field names to validation messages.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        logger.warn("Validation failed for {} fields: {}",
                errors.size(), errors.keySet());

        return ResponseEntity.badRequest().body(errors);
    }

    /**
     * Handles conflicts caused by duplicate user attributes such as email or username.
     * <p>
     * Returns: 409 Conflict
     */
    @ExceptionHandler({
            EmailAlreadyExistsException.class,
            UsernameAlreadyExistsException.class
    })
    public ResponseEntity<Map<String, String>> handleConflict(RuntimeException ex) {

        logger.warn("Conflict for duplicate user attributes detected: {}", ex.getMessage());

        Map<String, String> body = new HashMap<>();
        body.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }
}
