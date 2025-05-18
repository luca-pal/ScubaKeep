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
 * Global exception handler for the Dive Log application.
 * <p>
 * Converts uncaught exceptions into standardized HTTP error responses.
 * Handles validation errors and resource-not-found scenarios.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles {@link DiverNotFoundException} and returns HTTP 404 with an error message.
     *
     * @param ex the exception thrown when a diver is not found
     * @return a {@link ResponseEntity} with status 404 and error details
     */
    @ExceptionHandler(DiverNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleDiverNotFound(DiverNotFoundException ex) {
        logger.warn("Diver not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", ex.getMessage()));
    }

    /**
     * Handles validation failures from @Valid annotations on request DTOs.
     * Returns a map of field-specific error messages with status 400.
     *
     * @param ex the validation exception thrown by Spring
     * @return a {@link ResponseEntity} with validation errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException ex) {
        logger.info("Validation failed: {} errors", ex.getBindingResult().getFieldErrors().size());
        Map<String, String> validationErrors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(fieldError -> {
            validationErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(validationErrors);
    }

    /**
     * Handles {@link DiveLogNotFoundException} and returns HTTP 404 with an error message.
     *
     * @param ex the exception thrown when a dive log is not found
     * @return a {@link ResponseEntity} with status 404 and error details
     */
    @ExceptionHandler(DiveLogNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleDiveLogNotFound(DiveLogNotFoundException ex) {
        logger.warn("Dive log not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", ex.getMessage()));
    }
}
