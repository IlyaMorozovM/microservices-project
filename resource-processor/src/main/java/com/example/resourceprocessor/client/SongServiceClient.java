package com.example.resourceprocessor.client;

import com.example.resourceprocessor.dto.SongMetadataDto;
import com.example.resourceprocessor.exception.SongServiceIntegrationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Slf4j
@Component
@RequiredArgsConstructor
public class SongServiceClient {

    private static final String SONG_SERVICE_BASE_URL = "http://song-service";

    private final RestClient.Builder loadBalancedRestClientBuilder;

    @Retryable(
            retryFor = RestClientException.class,
            maxAttempts = 4,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public void createSongMetadata(SongMetadataDto metadata) {
        log.info("Saving song metadata for resourceId={}", metadata.getId());
        loadBalancedRestClientBuilder.build()
                .post()
                .uri(SONG_SERVICE_BASE_URL + "/songs")
                .body(metadata)
                .retrieve()
                .toBodilessEntity();
    }

    @Recover
    public void recoverCreateSongMetadata(RestClientException e, SongMetadataDto metadata) {
        log.error("Failed to save song metadata after retries for resourceId={}", metadata.getId(), e);
        throw new SongServiceIntegrationException("Failed to save song metadata", e);
    }
}