package com.lucap.scubakeep.exception;

/**
 * Runtime exception thrown when an object storage operation fails.
 */
public class StorageOperationException extends RuntimeException {

    public StorageOperationException(String objectKey) {
        super("Object storage operation failed for key: " + objectKey);
    }
}
