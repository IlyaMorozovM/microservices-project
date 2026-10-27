package com.example.resourceservice.messaging;

import com.example.resourceservice.config.RabbitMqProperties;
import com.example.resourceservice.exception.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ResourceEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final RabbitMqProperties rabbitMqProperties;

    @Retryable(
            retryFor = AmqpException.class,
            maxAttempts = 4,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public void publishResourceCreated(Long resourceId) {
        log.info("Publishing resource created event for resourceId={}", resourceId);
        rabbitTemplate.convertAndSend(
                rabbitMqProperties.getExchange(),
                rabbitMqProperties.getRoutingKey(),
                new ResourceCreatedEvent(resourceId));
    }

    @Recover
    public void recoverPublish(AmqpException e, Long resourceId) {
        log.error("Failed to publish resource created event after retries for resourceId={}", resourceId, e);
        throw new MessagingException("Failed to publish resource created event after retries", e);
    }
}