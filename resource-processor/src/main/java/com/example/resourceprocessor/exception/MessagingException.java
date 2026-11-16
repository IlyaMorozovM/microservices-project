package com.example.resourceprocessor.exception;

public class MessagingException extends RuntimeException {
    public MessagingException(String message, Throwable cause) {
        super(message, cause);
    }
}