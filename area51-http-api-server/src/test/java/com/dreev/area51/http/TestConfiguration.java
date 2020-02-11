package com.dreev.area51.http;

import java.util.Map;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Binding.DestinationType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * This Configuration class is in charge of declaring : Exchange, Queue and
 * Binding. This is a prerequisite that a system administrator would take care
 * of on a production RabbitMQ broker.
 */
@Configuration
class TestConfiguration {

    @Value("${dreev.area51.amqp.exchange}")
    String exchange;

    @Value("${dreev.area51.amqp.routing-key}")
    String routingKey;

    @Bean
    public Exchange exchange() {
        return new TopicExchange(exchange);
    }

    @Bean
    public Queue queue() {
        return new Queue(routingKey, false);
    }

    @Bean
    public Binding binding() {
        return new Binding(routingKey, DestinationType.QUEUE, exchange, "#", Map.of());
    }

}