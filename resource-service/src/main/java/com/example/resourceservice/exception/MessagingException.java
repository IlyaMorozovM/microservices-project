package com.example.resourceservice.exception;

public class MessagingException extends RuntimeException {
    public MessagingException(String message, Throwable cause) {
        super(message, cause);
    }
}