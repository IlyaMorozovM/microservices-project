package com.example.resourceservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "song-service")
@Getter
@Setter
public class SongServiceProperties {
    private String url;
}