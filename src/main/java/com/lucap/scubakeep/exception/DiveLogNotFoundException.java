package com.lucap.scubakeep.exception;

/**
 * Exception thrown when a dive log with the specified ID cannot be found.
 */
public class DiveLogNotFoundException extends RuntimeException {

    /**
     * Constructs a new exception with a message referencing the missing dive log ID.
     *
     * @param id the ID of the dive log that was not found
     */
    public DiveLogNotFoundException(Long id) {
        super("Dive log with id " + id + " not found");
    }
}
