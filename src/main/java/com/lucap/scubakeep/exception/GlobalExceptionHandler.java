package com.lucap.scubakeep.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

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
        LOGGER.warn("Resource not found: {}", ex.getMessage());
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
    public ResponseEntity<Map<String, String>> handleValidation(
            MethodArgumentNotValidException ex
    ) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        LOGGER.warn("Validation failed for {} fields: {}",
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

        LOGGER.warn("Conflict for duplicate user attributes detected: {}", ex.getMessage());

        Map<String, String> body = new HashMap<>();
        body.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    /**
     * Handles authentication failures such as invalid username/email or password.
     * Returns HTTP 401 Unauthorized.
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, String>> handleAuthenticationException(
            AuthenticationException ex) {

        LOGGER.warn("Authentication failed: {}", ex.getMessage());

        Map<String, String> body = new HashMap<>();
        body.put("error", "Invalid username/email or password");

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(body);
    }

    /**
     * Handles inconsistent authentication state.
     * <p>
     * This should not occur in normal application flow.
     * Returns 500 Internal Server Error.
     */
    @ExceptionHandler(AuthenticatedUserNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleAuthState(
            AuthenticatedUserNotFoundException ex) {

        LOGGER.error("Authentication state inconsistent: {}", ex.getMessage());

        Map<String, String> body = new HashMap<>();
        body.put("error", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(body);
    }

    /**
     * Handles unauthorized resource access.
     * <p>
     * Thrown when an authenticated user attempts to modify or delete
     * a resource they do not own and are not permitted to manage (admin).
     * Returns 403 Forbidden.
     */
    @ExceptionHandler(UnauthorizedResourceAccessException.class)
    public ResponseEntity<Map<String, String>> handleUnauthorizedResourceAccess(
            UnauthorizedResourceAccessException ex) {

        LOGGER.warn("Unauthorized access attempt: {}", ex.getMessage());

        Map<String, String> body = new HashMap<>();
        body.put("error", ex.getMessage());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    /**
     * Handles object storage failures (e.g. MinIO upload/download issues).
     * <p>
     * Returns: 500 Internal Server Error.
     */
    @ExceptionHandler(StorageOperationException.class)
    public ResponseEntity<Map<String, String>> handleStorageOperationException(
            StorageOperationException ex
    ) {
        LOGGER.error("Object storage operation failed: {}", ex.getMessage());

        Map<String, String> body = new HashMap<>();
        body.put("error", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(body);
    }

    /**
     * Handles invalid file type errors during file upload.
     * <p>
     * Returns: 400 Bad Request
     */
    @ExceptionHandler(InvalidFileTypeException.class)
    public ResponseEntity<Map<String, String>> handleInvalidFileType(
            InvalidFileTypeException ex
    ) {
        LOGGER.warn("Invalid file upload attempt: {}", ex.getMessage());

        Map<String, String> body = new HashMap<>();
        body.put("error", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(body);
    }
}
