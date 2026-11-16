package com.example.storageservice.controller;

import com.example.storageservice.dto.IdResponseDto;
import com.example.storageservice.dto.IdsResponseDto;
import com.example.storageservice.dto.StorageRequestDto;
import com.example.storageservice.dto.StorageResponseDto;
import com.example.storageservice.service.StorageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class StorageController {

    private final StorageService storageService;

    @PostMapping("/storages")
    public ResponseEntity<IdResponseDto> createStorage(@Valid @RequestBody StorageRequestDto dto) {
        return ResponseEntity.ok(new IdResponseDto(storageService.createStorage(dto)));
    }

    @GetMapping("/storages")
    public ResponseEntity<List<StorageResponseDto>> getAllStorages() {
        return ResponseEntity.ok(storageService.getAllStorages());
    }

    @DeleteMapping("/storages")
    public ResponseEntity<IdsResponseDto> deleteStorages(@RequestParam("id") String ids) {
        return ResponseEntity.ok(new IdsResponseDto(storageService.deleteStoragesByIds(ids)));
    }
}