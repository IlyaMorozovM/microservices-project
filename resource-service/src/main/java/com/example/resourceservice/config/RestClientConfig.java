package com.example.resourceservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@RequiredArgsConstructor
public class RestClientConfig {

    private final SongServiceProperties songServiceProperties;

    @Bean
    public RestClient songServiceRestClient() {
        return RestClient.builder()
                .baseUrl(songServiceProperties.getUrl())
                .build();
    }
}