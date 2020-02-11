package com.dreev.area51.participant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;

@RunWith(MockitoJUnitRunner.class)
public class ParticipantServiceTest {

    private static final String EXCHANGE = "exchange";
    private static final String ROUTING_KEY = "key";

    @Mock
    ParticipantRepository repo;

    @Mock
    AmqpTemplate amqp;

    ParticipantService service;
    
    @Before
    public void setUp() {
        service = new ParticipantService(repo, amqp, EXCHANGE, ROUTING_KEY);
    }

    @Test
    public void register_should_save_participant_and_send_message() {
        String id = "greengoblin";
        Participant participant = new Participant(id, "Norman Osborn");
        Message expectedMessage = new Message(id.getBytes(), new MessageProperties());

        service.register(participant);

        verify(repo).save(participant);
        verify(amqp).send(EXCHANGE, ROUTING_KEY, expectedMessage);
    }

    @Test
    public void findAll_should_return_participants() {
        List<Participant> participants = List.of(new Participant("venom", "Eddie Brock"));
        when(repo.findAll()).thenReturn(participants);
        Participants expected = new Participants(participants);

        Participants result = service.findAll();

        assertThat(result).isEqualTo(expected);
    }

}
