package com.example.resourceservice.exception;

import com.example.resourceservice.dto.ErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponseDto> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String message = "Invalid value '" + ex.getValue() + "' for ID. Must be a positive integer";
        ErrorResponseDto error = new ErrorResponseDto(
                message,
                String.valueOf(HttpStatus.BAD_REQUEST.value())
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponseDto> handleBadRequest(BadRequestException ex) {
        ErrorResponseDto error = new ErrorResponseDto(
                ex.getMessage(),
                String.valueOf(HttpStatus.BAD_REQUEST.value())
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(InvalidMp3FileException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidMp3(InvalidMp3FileException ex) {
        ErrorResponseDto error = new ErrorResponseDto(
                ex.getMessage(),
                String.valueOf(HttpStatus.BAD_REQUEST.value())
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleNotFound(ResourceNotFoundException ex) {
        ErrorResponseDto error = new ErrorResponseDto(
                ex.getMessage(),
                String.valueOf(HttpStatus.NOT_FOUND.value())
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(SongServiceIntegrationException.class)
    public ResponseEntity<ErrorResponseDto> handleSongServiceIntegration(SongServiceIntegrationException ex) {
        ErrorResponseDto error = new ErrorResponseDto(
                "Internal server error",
                String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value())
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGeneralException(Exception ex) {
        ErrorResponseDto error = new ErrorResponseDto(
                "Internal server error",
                String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value())
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}