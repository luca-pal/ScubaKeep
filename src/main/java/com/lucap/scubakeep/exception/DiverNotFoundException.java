package com.lucap.scubakeep.exception;

import java.util.UUID;

/**
 * Exception thrown when a diver with the specified ID is not found in the system.
 */
public class DiverNotFoundException extends RuntimeException {

    /**
     * Constructs a new exception with a message referencing the missing diver ID.
     *
     * @param id the ID of the diver that was not found
     */
    public DiverNotFoundException(UUID id) {
        super("Diver with id " + id + " not found");
    }
}
