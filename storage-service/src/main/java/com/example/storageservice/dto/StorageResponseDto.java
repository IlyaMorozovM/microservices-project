package com.example.storageservice.dto;

import com.example.storageservice.entity.StorageType;

public record StorageResponseDto(Long id, StorageType storageType, String bucket, String path) {
}