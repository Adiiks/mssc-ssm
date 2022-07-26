package com.adrian.msscssm.config;

import com.adrian.msscssm.domain.PaymentEvent;
import com.adrian.msscssm.domain.PaymentState;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import reactor.core.publisher.Mono;

import java.util.UUID;

@SpringBootTest
class StateMachineConfigTest {

    @Autowired
    StateMachineFactory<PaymentState, PaymentEvent> factory;

    @Test
    void testNewMachineState() {
        StateMachine<PaymentState, PaymentEvent> stateMachine = factory.getStateMachine(UUID.randomUUID());

        stateMachine.startReactively().block();

        System.out.println(stateMachine.getState().toString());

        stateMachine.sendEvent(Mono.just(MessageBuilder.withPayload(PaymentEvent.PRE_AUTHORIZE).build())).blockFirst();

        System.out.println(stateMachine.getState().toString());

        stateMachine.sendEvent(Mono.just(MessageBuilder.withPayload(PaymentEvent.PRE_AUTH_APPROVED).build())).blockFirst();

        System.out.println(stateMachine.getState().toString());
    }
}