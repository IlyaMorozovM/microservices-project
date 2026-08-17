package com.example.resourceservice.service;

import com.example.resourceservice.client.SongServiceClient;
import com.example.resourceservice.dto.SongMetadataDto;
import com.example.resourceservice.entity.Resource;
import com.example.resourceservice.exception.BadRequestException;
import com.example.resourceservice.exception.InvalidMp3FileException;
import com.example.resourceservice.exception.ResourceNotFoundException;
import com.example.resourceservice.exception.SongServiceIntegrationException;
import com.example.resourceservice.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResourceServiceImpl implements ResourceService {

    private static final int MAX_CSV_LENGTH = 200;
    private static final String MP3_CONTENT_TYPE = "audio/mpeg";

    private final ResourceRepository resourceRepository;
    private final Mp3MetadataExtractor metadataExtractor;
    private final SongServiceClient songServiceClient;

    @Override
    @Transactional
    public Long uploadResource(byte[] audioData, String contentType) {
        validateContentType(contentType);

        SongMetadataDto metadata = metadataExtractor.extract(audioData);

        Resource resource = new Resource();
        resource.setData(audioData);
        Resource saved = resourceRepository.save(resource);

        metadata.setId(saved.getId());

        try {
            songServiceClient.createSongMetadata(metadata);
        } catch (SongServiceIntegrationException e) {
            resourceRepository.delete(saved);
            throw e;
        }

        return saved.getId();
    }

    @Override
    public byte[] getResourceById(Long id) {
        validateId(id);
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource with ID=" + id + " not found"));
        return resource.getData();
    }

    @Override
    @Transactional
    public List<Long> deleteResourcesByIds(String csv) {
        List<Long> ids = parseAndValidateCsv(csv);
        List<Resource> existingResources = resourceRepository.findAllByIdIn(ids);
        List<Long> existingIds = existingResources.stream().map(Resource::getId).toList();

        resourceRepository.deleteAll(existingResources);

        for (Long id : existingIds) {
            songServiceClient.deleteSongMetadata(id);
        }

        return existingIds;
    }

    private void validateContentType(String contentType) {
        if (contentType == null || !contentType.equalsIgnoreCase(MP3_CONTENT_TYPE)) {
            throw new InvalidMp3FileException(
                    "Invalid file format: " + contentType + ". Only MP3 files are allowed");
        }
    }

    private void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Invalid value '" + id + "' for ID. Must be a positive integer");
        }
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

        String[] parts = csv.split(",");
        List<Long> ids = new ArrayList<>();
        for (String part : parts) {
            String trimmed = part.trim();
            if (!trimmed.matches("\\d+")) {
                throw new BadRequestException("Invalid ID format: '" + trimmed + "'. Only positive integers are allowed");
            }
            ids.add(Long.parseLong(trimmed));
        }
        return ids;
    }
}