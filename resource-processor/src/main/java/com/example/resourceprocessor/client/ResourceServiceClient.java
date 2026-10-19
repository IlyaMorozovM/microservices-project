package com.example.resourceprocessor.client;

import com.example.resourceprocessor.exception.ResourceServiceIntegrationException;
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
public class ResourceServiceClient {

    private static final String RESOURCE_SERVICE_BASE_URL = "http://resource-service";

    private final RestClient.Builder loadBalancedRestClientBuilder;

    @Retryable(
            retryFor = RestClientException.class,
            maxAttempts = 4,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public byte[] getResourceData(Long resourceId) {
        log.info("Fetching resource data for resourceId={}", resourceId);
        return loadBalancedRestClientBuilder.build()
                .get()
                .uri(RESOURCE_SERVICE_BASE_URL + "/resources/{id}", resourceId)
                .retrieve()
                .body(byte[].class);
    }

    @Recover
    public byte[] recoverGetResourceData(RestClientException e, Long resourceId) {
        log.error("Failed to retrieve resource data after retries for resourceId={}", resourceId, e);
        throw new ResourceServiceIntegrationException(
                "Failed to retrieve resource data for ID=" + resourceId, e);
    }
}