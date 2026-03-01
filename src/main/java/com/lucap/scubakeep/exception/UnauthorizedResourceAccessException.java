package com.lucap.scubakeep.exception;

/**
 * Thrown when a user tries to modify, delete or view a resource
 * they do not own and are not authorized to access.
 */
public class UnauthorizedResourceAccessException extends RuntimeException {
    public UnauthorizedResourceAccessException() {
        super("User not allowed to access this resource.");
    }
}
