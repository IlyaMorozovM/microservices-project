package com.example.resourceservice.messaging;

import com.example.resourceservice.service.ResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ResourceProcessedEventListener {

    private final ResourceService resourceService;

    @RabbitListener(queues = "${rabbitmq.processed-queue}")
    public void handleResourceProcessed(ResourceProcessedEvent event) {
        Long resourceId = event.resourceId();
        log.info("Received resource processed event for resourceId={}", resourceId);

        resourceService.markResourceAsProcessed(resourceId);

        log.info("Successfully moved resourceId={} to permanent storage", resourceId);
    }
}