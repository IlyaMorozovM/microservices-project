package com.example.resourceprocessor.messaging;

import com.example.resourceprocessor.client.ResourceServiceClient;
import com.example.resourceprocessor.client.SongServiceClient;
import com.example.resourceprocessor.dto.SongMetadataDto;
import com.example.resourceprocessor.service.Mp3MetadataExtractor;
import com.example.resourceprocessor.tracing.TracingConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ResourceEventListener {

    private final ResourceServiceClient resourceServiceClient;
    private final Mp3MetadataExtractor metadataExtractor;
    private final SongServiceClient songServiceClient;
    private final ResourceProcessedEventPublisher resourceProcessedEventPublisher;

    @RabbitListener(queues = "${rabbitmq.queue}")
    public void handleResourceCreated(ResourceCreatedEvent event, Message message) {
        String traceId = extractTraceId(message);
        if (traceId != null) {
            MDC.put(TracingConstants.TRACE_ID_MDC_KEY, traceId);
        }

        try {
            Long resourceId = event.resourceId();
            log.info("Received resource processing event for resourceId={}", resourceId);

            byte[] resourceData = resourceServiceClient.getResourceData(resourceId);

            SongMetadataDto metadata = metadataExtractor.extract(resourceData);
            metadata.setId(resourceId);

            songServiceClient.createSongMetadata(metadata);

            resourceProcessedEventPublisher.publishResourceProcessed(resourceId);

            log.info("Successfully processed resourceId={}", resourceId);
        } finally {
            MDC.remove(TracingConstants.TRACE_ID_MDC_KEY);
        }
    }

    private String extractTraceId(Message message) {
        Object header = message.getMessageProperties().getHeaders().get(TracingConstants.TRACE_ID_HEADER);
        return header != null ? header.toString() : null;
    }
}