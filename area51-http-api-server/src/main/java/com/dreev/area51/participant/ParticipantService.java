package com.dreev.area51.participant;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ParticipantService {

    private final ParticipantRepository repo;
    private final AmqpTemplate amqp;

    private final String exchange;
    private final String routingKey;

    public ParticipantService(ParticipantRepository repo, AmqpTemplate amqp,
            @Value("${dreev.area51.amqp.exchange}") String exchange,
            @Value("${dreev.area51.amqp.routing-key}") String routingKey) {
        this.repo = repo;
        this.amqp = amqp;
        this.exchange = exchange;
        this.routingKey = routingKey;
    }

    /**
     * Register a participant and send it to RabbitMQ
     * 
     * @param deviceId
     */
    @Transactional
    public void register(Participant participant) {
        send(participant);
        repo.save(participant);
    }

    private void send(Participant participant) {
        MessageProperties props = new MessageProperties();
        Message message = new Message(participant.getId().getBytes(), props);
        log.info("Sending message to exchange : {} - keys : {} - size : {}B", exchange, routingKey,
                message.getBody().length);
        amqp.send(exchange, routingKey, message);
    }

    /**
     * @return All participants
     */
    public Participants findAll() {
        return new Participants(repo.findAll());
    }

}
