package com.example.resourceservice.exception;

public class StorageServiceIntegrationException extends RuntimeException {
    public StorageServiceIntegrationException(String message, Throwable cause) {
        super(message, cause);
    }
}