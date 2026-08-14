package com.example.resourceservice.exception;

public class InvalidMp3FileException extends RuntimeException {
    public InvalidMp3FileException(String message) {
        super(message);
    }
    public InvalidMp3FileException(String message, Throwable cause) {
        super(message, cause);
    }
}