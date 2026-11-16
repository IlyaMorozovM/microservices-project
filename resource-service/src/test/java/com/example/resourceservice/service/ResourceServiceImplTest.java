package com.example.resourceservice.service;

import com.example.resourceservice.client.SongServiceClient;
import com.example.resourceservice.client.StorageServiceClient;
import com.example.resourceservice.client.dto.StorageResponseDto;
import com.example.resourceservice.entity.Resource;
import com.example.resourceservice.entity.StorageType;
import com.example.resourceservice.exception.InvalidMp3FileException;
import com.example.resourceservice.exception.ResourceNotFoundException;
import com.example.resourceservice.messaging.ResourceEventPublisher;
import com.example.resourceservice.repository.ResourceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResourceServiceImplTest {

    private static final String STAGING_BUCKET = "staging-bucket";

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private S3StorageService s3StorageService;

    @Mock
    private SongServiceClient songServiceClient;

    @Mock
    private StorageServiceClient storageServiceClient;

    @Mock
    private ResourceEventPublisher resourceEventPublisher;

    @InjectMocks
    private ResourceServiceImpl resourceService;

    private byte[] audioData;
    private StorageResponseDto stagingStorage;

    @BeforeEach
    void setUp() {
        audioData = new byte[]{1, 2, 3};
        stagingStorage = new StorageResponseDto(1L, "STAGING", STAGING_BUCKET, "/files");
    }

    @Test
    void uploadResource_shouldSaveResourceAndPublishEvent_whenContentTypeIsValid() {
        String storageKey = "resources/generated-key.mp3";
        when(storageServiceClient.getStorageByType(StorageType.STAGING.name())).thenReturn(stagingStorage);
        when(s3StorageService.upload(audioData, STAGING_BUCKET)).thenReturn(storageKey);

        Resource savedResource = new Resource();
        savedResource.setId(42L);
        savedResource.setStorageKey(storageKey);
        savedResource.setBucket(STAGING_BUCKET);
        savedResource.setStorageType(StorageType.STAGING);
        when(resourceRepository.save(any(Resource.class))).thenReturn(savedResource);

        Long resultId = resourceService.uploadResource(audioData, "audio/mpeg");

        assertThat(resultId).isEqualTo(42L);
        verify(resourceEventPublisher).publishResourceCreated(42L);
        verify(s3StorageService, never()).delete(anyString(), anyString());
    }

    @Test
    void uploadResource_shouldThrowInvalidMp3FileException_whenContentTypeIsNotMpeg() {
        assertThatThrownBy(() -> resourceService.uploadResource(audioData, "application/json"))
                .isInstanceOf(InvalidMp3FileException.class)
                .hasMessageContaining("Invalid file format");

        verify(s3StorageService, never()).upload(any(), anyString());
        verify(resourceRepository, never()).save(any());
    }

    @Test
    void uploadResource_shouldDeleteFromS3_whenDatabaseSaveFails() {
        String storageKey = "resources/generated-key.mp3";
        when(storageServiceClient.getStorageByType(StorageType.STAGING.name())).thenReturn(stagingStorage);
        when(s3StorageService.upload(audioData, STAGING_BUCKET)).thenReturn(storageKey);
        when(resourceRepository.save(any(Resource.class)))
                .thenThrow(new RuntimeException("DB connection lost"));

        assertThatThrownBy(() -> resourceService.uploadResource(audioData, "audio/mpeg"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("DB connection lost");

        verify(s3StorageService).delete(storageKey, STAGING_BUCKET);
        verify(resourceEventPublisher, never()).publishResourceCreated(any());
    }

    @Test
    void getResourceById_shouldThrowResourceNotFoundException_whenResourceDoesNotExist() {
        when(resourceRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> resourceService.getResourceById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    void deleteResourcesByIds_shouldDeleteFromS3AndDbAndCallSongService_whenResourcesExist() {
        Resource resource1 = new Resource();
        resource1.setId(1L);
        resource1.setStorageKey("resources/key1.mp3");
        resource1.setBucket(STAGING_BUCKET);

        Resource resource2 = new Resource();
        resource2.setId(2L);
        resource2.setStorageKey("resources/key2.mp3");
        resource2.setBucket(STAGING_BUCKET);

        when(resourceRepository.findAllByIdIn(java.util.List.of(1L, 2L)))
                .thenReturn(java.util.List.of(resource1, resource2));

        var deletedIds = resourceService.deleteResourcesByIds("1,2");

        assertThat(deletedIds).containsExactly(1L, 2L);
        verify(s3StorageService).delete("resources/key1.mp3", STAGING_BUCKET);
        verify(s3StorageService).delete("resources/key2.mp3", STAGING_BUCKET);
        verify(resourceRepository).deleteAll(java.util.List.of(resource1, resource2));
        verify(songServiceClient).deleteSongMetadata(1L);
        verify(songServiceClient).deleteSongMetadata(2L);
    }
}