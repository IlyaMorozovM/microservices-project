package com.example.resourceservice.messaging;

import com.example.resourceservice.service.ResourceService;
import com.example.resourceservice.tracing.TracingConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ResourceProcessedEventListener {

    private final ResourceService resourceService;

    @RabbitListener(queues = "${rabbitmq.processed-queue}")
    public void handleResourceProcessed(ResourceProcessedEvent event, Message message) {
        String traceId = extractTraceId(message);
        if (traceId != null) {
            MDC.put(TracingConstants.TRACE_ID_MDC_KEY, traceId);
        }

        try {
            Long resourceId = event.resourceId();
            log.info("Received resource processed event for resourceId={}", resourceId);

            resourceService.markResourceAsProcessed(resourceId);

            log.info("Successfully moved resourceId={} to permanent storage", resourceId);
        } finally {
            MDC.remove(TracingConstants.TRACE_ID_MDC_KEY);
        }
    }

    private String extractTraceId(Message message) {
        Object header = message.getMessageProperties().getHeaders().get(TracingConstants.TRACE_ID_HEADER);
        return header != null ? header.toString() : null;
    }
}