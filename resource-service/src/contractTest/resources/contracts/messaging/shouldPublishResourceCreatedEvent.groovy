import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "should publish resource created event"
    label "resource_created"

    input {
        triggeredBy("publishResourceCreated()")
    }

    outputMessage {
        sentTo("resource.processing.exchange")
        headers {
            messagingContentType(applicationJson())
            header("amqp_receivedRoutingKey", "resource.processing.key")
        }
        body(resourceId: 1)
    }
}