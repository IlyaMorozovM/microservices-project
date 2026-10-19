package com.example.resourceprocessor.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "rabbitmq")
@Getter
@Setter
public class RabbitMqProperties {
    private String exchange;
    private String queue;
    private String routingKey;
}
