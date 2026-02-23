package com.lucap.scubakeep.exception;

/**
 * Exception thrown when an attempt is made to create a diver with an email
 * address that already exists in the system.
 */
public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String email) {
        super("Email already in use: " + email);
    }
}
