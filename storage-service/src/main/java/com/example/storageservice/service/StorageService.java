package com.example.storageservice.service;

import com.example.storageservice.dto.StorageRequestDto;
import com.example.storageservice.dto.StorageResponseDto;

import java.util.List;

public interface StorageService {

    Long createStorage(StorageRequestDto dto);

    List<StorageResponseDto> getAllStorages();

    List<Long> deleteStoragesByIds(String csv);
}