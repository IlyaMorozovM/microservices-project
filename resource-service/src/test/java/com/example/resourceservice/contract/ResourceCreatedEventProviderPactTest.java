package com.example.resourceservice.contract;

import au.com.dius.pact.provider.PactVerifyProvider;
import au.com.dius.pact.provider.junit5.MessageTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.loader.PactFolder;
import com.example.resourceservice.messaging.ResourceCreatedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

@Provider("resource-service")
@PactFolder("../resource-processor/build/pacts")
@ExtendWith(PactVerificationInvocationContextProvider.class)
public class ResourceCreatedEventProviderPactTest {

    @BeforeEach
    void before(PactVerificationContext context) {
        context.setTarget(new MessageTestTarget(
                java.util.List.of("com.example.resourceservice.contract"),
                null));
    }

    @TestTemplate
    void verifyPact(PactVerificationContext context) {
        context.verifyInteraction();
    }

    @PactVerifyProvider("a resource created event")
    public String verifyResourceCreatedEvent() throws Exception {
        ResourceCreatedEvent event = new ResourceCreatedEvent(1L);
        return new ObjectMapper().writeValueAsString(event);
    }
}