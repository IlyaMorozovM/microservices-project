package com.example.storageservice.dto;

import com.example.storageservice.entity.StorageType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StorageRequestDto {

    @NotNull(message = "Storage type is required")
    private StorageType storageType;

    @NotBlank(message = "Bucket is required")
    @Size(max = 100, message = "Bucket must be at most 100 characters")
    private String bucket;

    @NotBlank(message = "Path is required")
    @Size(max = 200, message = "Path must be at most 200 characters")
    private String path;
}