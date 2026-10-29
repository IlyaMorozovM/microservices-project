package com.example.resourceprocessor.contract;

import au.com.dius.pact.consumer.MessagePactBuilder;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.consumer.junit5.ProviderType;
import au.com.dius.pact.core.model.PactSpecVersion;
import au.com.dius.pact.core.model.annotations.Pact;
import au.com.dius.pact.core.model.messaging.Message;
import au.com.dius.pact.core.model.messaging.MessagePact;
import com.example.resourceprocessor.messaging.ResourceCreatedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "resource-service",
        providerType = ProviderType.ASYNCH,
        pactVersion = PactSpecVersion.V3)
class ResourceCreatedEventConsumerPactTest {

    @Pact(consumer = "resource-processor")
    public MessagePact resourceCreatedEventPact(MessagePactBuilder builder) {
        PactDslJsonBody body = new PactDslJsonBody()
                .integerType("resourceId", 1L);

        return builder
                .expectsToReceive("a resource created event")
                .withContent(body)
                .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "resourceCreatedEventPact")
    void listenerCanReadResourceCreatedEvent(List<Message> messages) throws Exception {
        byte[] payload = messages.get(0).contentsAsBytes();

        ResourceCreatedEvent event = new ObjectMapper().readValue(payload, ResourceCreatedEvent.class);

        assertThat(event.resourceId()).isEqualTo(1L);
    }
}