package com.example.resourceservice.client.dto;

public record StorageResponseDto(Long id, String storageType, String bucket, String path) {
}