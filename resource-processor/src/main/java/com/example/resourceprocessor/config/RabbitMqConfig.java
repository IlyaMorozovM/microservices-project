package com.example.resourceprocessor.config;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RabbitMqConfig {

    private final RabbitMqProperties rabbitMqProperties;

    @Bean
    public DirectExchange resourceProcessingExchange() {
        return new DirectExchange(rabbitMqProperties.getExchange());
    }

    @Bean
    public Queue resourceProcessingQueue() {
        return QueueBuilder
                .durable(rabbitMqProperties.getQueue())
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", rabbitMqProperties.getQueue() + ".dlq")
                .build();
    }

    @Bean
    public Queue deadLetterQueue() {
        return new Queue(rabbitMqProperties.getQueue() + ".dlq", true);
    }

    @Bean
    public Binding resourceProcessingBinding() {
        return BindingBuilder
                .bind(resourceProcessingQueue())
                .to(resourceProcessingExchange())
                .with(rabbitMqProperties.getRoutingKey());
    }

    @Bean
    public DirectExchange resourceProcessedExchange() {
        return new DirectExchange(rabbitMqProperties.getProcessedExchange());
    }

    @Bean
    public Queue resourceProcessedQueue() {
        return QueueBuilder
                .durable(rabbitMqProperties.getProcessedQueue())
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", rabbitMqProperties.getProcessedQueue() + ".dlq")
                .build();
    }

    @Bean
    public Queue resourceProcessedDeadLetterQueue() {
        return new Queue(rabbitMqProperties.getProcessedQueue() + ".dlq", true);
    }

    @Bean
    public Binding resourceProcessedBinding() {
        return BindingBuilder
                .bind(resourceProcessedQueue())
                .to(resourceProcessedExchange())
                .with(rabbitMqProperties.getProcessedRoutingKey());
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         MessageConverter jsonMessageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter);
        return template;
    }
}