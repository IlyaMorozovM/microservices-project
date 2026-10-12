package com.example.resourceservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "s3")
@Getter
@Setter
public class S3Properties {
    private String endpoint;
    private String bucketName;
    private String region;
    private String accessKey;
    private String secretKey;
}