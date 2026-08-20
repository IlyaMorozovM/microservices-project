package com.example.resourceservice.client;

import com.example.resourceservice.dto.SongMetadataDto;
import com.example.resourceservice.exception.SongServiceIntegrationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
@RequiredArgsConstructor
public class SongServiceClient {

    private static final String SONG_SERVICE_BASE_URL = "http://song-service";

    private final RestClient.Builder loadBalancedRestClientBuilder;

    public void createSongMetadata(SongMetadataDto metadata) {
        try {
            loadBalancedRestClientBuilder.build()
                    .post()
                    .uri(SONG_SERVICE_BASE_URL + "/songs")
                    .body(metadata)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException e) {
            throw new SongServiceIntegrationException(
                    "Failed to save song metadata in Song Service", e);
        }
    }

    public void deleteSongMetadata(Long id) {
        try {
            loadBalancedRestClientBuilder.build()
                    .delete()
                    .uri(SONG_SERVICE_BASE_URL + "/songs?id={id}", id)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException e) {
            throw new SongServiceIntegrationException(
                    "Failed to delete song metadata in Song Service", e);
        }
    }
}