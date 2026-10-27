package com.example.resourceservice.contract;

import com.example.resourceservice.config.RabbitMqConfig;
import com.example.resourceservice.config.RabbitMqProperties;
import com.example.resourceservice.messaging.ResourceEventPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.verifier.messaging.boot.AutoConfigureMessageVerifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@SpringBootTest(
        classes = BaseMessagingContractTest.TestConfig.class,
        properties = {
                "stubrunner.amqp.enabled=true",
                "stubrunner.amqp.mockConnection=true"
        })
@AutoConfigureMessageVerifier
public abstract class BaseMessagingContractTest {

    @Configuration
    @ImportAutoConfiguration(RabbitAutoConfiguration.class)
    @EnableConfigurationProperties(RabbitMqProperties.class)
    @Import({RabbitMqConfig.class, ResourceEventPublisher.class})
    static class TestConfig {
    }

    @Autowired
    private ResourceEventPublisher resourceEventPublisher;

    public void publishResourceCreated() {
        resourceEventPublisher.publishResourceCreated(1L);
    }
}