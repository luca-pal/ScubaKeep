package com.lucap.scubakeep.exception;

/**
 * Exception thrown when a diver with the specified ID is not found in the system.
 * <p>
 * Typically used in service or controller layers to trigger a 404 Not Found response.
 */
public class DiverNotFoundException extends RuntimeException {

    /**
     * Constructs a new exception with a message referencing the missing diver ID.
     *
     * @param id the ID of the diver that was not found
     */
    public DiverNotFoundException(Long id) {
        super("Diver with id " + id + " not found");
    }
}
