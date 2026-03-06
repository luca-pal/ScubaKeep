package com.lucap.scubakeep.exception;

/**
 * Exception thrown when an uploaded file has an unsupported content type.
 */
public class InvalidFileTypeException extends RuntimeException {

    public InvalidFileTypeException(String contentType) {
        super("Unsupported file type: " + contentType);
    }
}