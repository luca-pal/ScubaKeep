package com.lucap.scubakeep.exception;

/**
 * Thrown when authentication succeeds but the corresponding user cannot be found.
 * This indicates an inconsistent application state.
 */
public class AuthenticatedUserNotFoundException extends RuntimeException {

    public AuthenticatedUserNotFoundException(String username) {
        super("Authenticated user not found in DB: " + username);
    }
}
