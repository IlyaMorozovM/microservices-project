package com.example.storageservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponseDto(String errorMessage, Map<String, String> details, String errorCode) {

    public ErrorResponseDto(String errorMessage, String errorCode) {
        this(errorMessage, null, errorCode);
    }
}