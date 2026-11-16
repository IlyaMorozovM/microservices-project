package com.example.storageservice.service;

import com.example.storageservice.dto.StorageRequestDto;
import com.example.storageservice.dto.StorageResponseDto;
import com.example.storageservice.entity.Storage;
import com.example.storageservice.exception.BadRequestException;
import com.example.storageservice.mapper.StorageMapper;
import com.example.storageservice.repository.StorageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StorageServiceImpl implements StorageService {

    private static final int MAX_CSV_LENGTH = 200;

    private final StorageRepository storageRepository;
    private final StorageMapper storageMapper;

    @Override
    @Transactional
    public Long createStorage(StorageRequestDto dto) {
        if (storageRepository.existsByBucketAndPath(dto.getBucket(), dto.getPath())) {
            throw new BadRequestException(
                    "Storage with bucket '" + dto.getBucket() + "' and path '" + dto.getPath() + "' already exists");
        }
        return storageRepository.save(storageMapper.toEntity(dto)).getId();
    }

    @Override
    @Transactional(readOnly = true)
    public List<StorageResponseDto> getAllStorages() {
        return storageRepository.findAll().stream()
                .map(storageMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional
    public List<Long> deleteStoragesByIds(String csv) {
        List<Long> ids = parseAndValidateCsv(csv);
        List<Storage> existing = storageRepository.findAllByIdIn(ids);
        List<Long> existingIds = existing.stream().map(Storage::getId).toList();
        storageRepository.deleteAll(existing);
        return existingIds;
    }

    private List<Long> parseAndValidateCsv(String csv) {
        if (csv == null || csv.isBlank()) {
            throw new BadRequestException("Id list must not be empty");
        }
        if (csv.length() > MAX_CSV_LENGTH) {
            throw new BadRequestException(
                    "CSV string is too long: received " + csv.length()
                            + " characters, maximum allowed is " + MAX_CSV_LENGTH);
        }
        List<Long> ids = new ArrayList<>();
        for (String part : csv.split(",")) {
            String trimmed = part.trim();
            if (!trimmed.matches("\\d+")) {
                throw new BadRequestException("Id list contains invalid value: '" + trimmed + "'");
            }
            ids.add(Long.parseLong(trimmed));
        }
        return ids;
    }
}