package com.example.resourceservice.exception;

public class SongServiceIntegrationException extends RuntimeException {
    public SongServiceIntegrationException(String message) {
        super(message);
    }
    public SongServiceIntegrationException(String message, Throwable cause) {
        super(message, cause);
    }
}