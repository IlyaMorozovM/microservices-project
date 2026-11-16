package com.example.storageservice.mapper;

import com.example.storageservice.dto.StorageRequestDto;
import com.example.storageservice.dto.StorageResponseDto;
import com.example.storageservice.entity.Storage;
import org.springframework.stereotype.Component;

@Component
public class StorageMapper {

    public Storage toEntity(StorageRequestDto dto) {
        Storage storage = new Storage();
        storage.setStorageType(dto.getStorageType());
        storage.setBucket(dto.getBucket());
        storage.setPath(dto.getPath());
        return storage;
    }

    public StorageResponseDto toResponseDto(Storage storage) {
        return new StorageResponseDto(
                storage.getId(), storage.getStorageType(), storage.getBucket(), storage.getPath());
    }
}