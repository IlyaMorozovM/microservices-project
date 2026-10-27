package com.example.resourceprocessor.messaging;

import com.example.resourceprocessor.client.ResourceServiceClient;
import com.example.resourceprocessor.client.SongServiceClient;
import com.example.resourceprocessor.dto.SongMetadataDto;
import com.example.resourceprocessor.service.Mp3MetadataExtractor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ResourceEventListener {

    private final ResourceServiceClient resourceServiceClient;
    private final Mp3MetadataExtractor metadataExtractor;
    private final SongServiceClient songServiceClient;

    @RabbitListener(queues = "${rabbitmq.queue}")
    public void handleResourceCreated(ResourceCreatedEvent event) {
        Long resourceId = event.resourceId();
        log.info("Received resource processing event for resourceId={}", resourceId);

        byte[] resourceData = resourceServiceClient.getResourceData(resourceId);

        SongMetadataDto metadata = metadataExtractor.extract(resourceData);
        metadata.setId(resourceId);

        songServiceClient.createSongMetadata(metadata);

        log.info("Successfully processed resourceId={}", resourceId);
    }
}