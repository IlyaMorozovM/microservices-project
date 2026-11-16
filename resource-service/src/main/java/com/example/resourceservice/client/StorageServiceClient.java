package com.example.resourceservice.client;

import com.example.resourceservice.client.dto.StorageResponseDto;
import com.example.resourceservice.entity.StorageType;
import com.example.resourceservice.exception.StorageServiceIntegrationException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class StorageServiceClient {

    private static final String STORAGE_SERVICE_BASE_URL = "http://storage-service";
    private static final String STAGING_FALLBACK_BUCKET = "staging-bucket";
    private static final String PERMANENT_FALLBACK_BUCKET = "permanent-bucket";
    private static final String FALLBACK_PATH = "/files";

    private final RestClient.Builder loadBalancedRestClientBuilder;

    @CircuitBreaker(name = "storageService", fallbackMethod = "getStorageByTypeFallback")
    public StorageResponseDto getStorageByType(String storageType) {
        try {
            List<StorageResponseDto> storages = loadBalancedRestClientBuilder.build()
                    .get()
                    .uri(STORAGE_SERVICE_BASE_URL + "/storages")
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<StorageResponseDto>>() {});

            if (storages == null) {
                throw new StorageServiceIntegrationException(
                        "Storage Service returned an empty response", null);
            }

            return storages.stream()
                    .filter(s -> s.storageType().equalsIgnoreCase(storageType))
                    .findFirst()
                    .orElseThrow(() -> new StorageServiceIntegrationException(
                            "No storage found for type: " + storageType, null));
        } catch (RestClientException e) {
            throw new StorageServiceIntegrationException(
                    "Failed to retrieve storage details from Storage Service", e);
        }
    }

    private StorageResponseDto getStorageByTypeFallback(String storageType, Throwable t) {
        log.warn("Storage Service unavailable, returning stub data for storageType={}. Reason: {}",
                storageType, t.getMessage());

        String bucket = StorageType.PERMANENT.name().equalsIgnoreCase(storageType)
                ? PERMANENT_FALLBACK_BUCKET
                : STAGING_FALLBACK_BUCKET;

        return new StorageResponseDto(-1L, storageType, bucket, FALLBACK_PATH);
    }
}