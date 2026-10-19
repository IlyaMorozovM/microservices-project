package com.example.resourceservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
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
        return org.springframework.amqp.core.QueueBuilder
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