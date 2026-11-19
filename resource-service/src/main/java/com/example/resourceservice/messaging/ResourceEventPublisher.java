package com.example.resourceservice.messaging;

import com.example.resourceservice.config.RabbitMqProperties;
import com.example.resourceservice.exception.MessagingException;
import com.example.resourceservice.tracing.TracingConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.MessagePostProcessor;
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

        String traceId = MDC.get(TracingConstants.TRACE_ID_MDC_KEY);

        MessagePostProcessor processor = message -> {
            if (traceId != null) {
                message.getMessageProperties().setHeader(TracingConstants.TRACE_ID_HEADER, traceId);
            }
            return message;
        };

        rabbitTemplate.convertAndSend(
                rabbitMqProperties.getExchange(),
                rabbitMqProperties.getRoutingKey(),
                new ResourceCreatedEvent(resourceId),
                processor);
    }

    @Recover
    public void recoverPublish(AmqpException e, Long resourceId) {
        log.error("Failed to publish resource created event after retries for resourceId={}", resourceId, e);
        throw new MessagingException("Failed to publish resource created event after retries", e);
    }
}