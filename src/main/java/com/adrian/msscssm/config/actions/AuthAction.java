package com.adrian.msscssm.config.actions;

import com.adrian.msscssm.domain.PaymentEvent;
import com.adrian.msscssm.domain.PaymentState;
import com.adrian.msscssm.services.PaymentServiceImpl;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Random;

@Component
public class AuthAction implements Action<PaymentState, PaymentEvent> {

    @Override
    public void execute(StateContext<PaymentState, PaymentEvent> context) {
        System.out.println("Auth was called");

        if (new Random().nextInt(10) < 8) {
            System.out.println("Approved !!!");
            context.getStateMachine()
                    .sendEvent(Mono.just(MessageBuilder.withPayload(PaymentEvent.AUTH_APPROVED)
                            .setHeader(PaymentServiceImpl.PAYMENT_ID_HEADER, context.getMessageHeader(PaymentServiceImpl.PAYMENT_ID_HEADER))
                            .build()))
                    .blockFirst();
        } else {
            System.out.println("Declined !!!");
            context.getStateMachine()
                    .sendEvent(Mono.just(MessageBuilder.withPayload(PaymentEvent.AUTH_DECLINED)
                            .setHeader(PaymentServiceImpl.PAYMENT_ID_HEADER, context.getMessageHeader(PaymentServiceImpl.PAYMENT_ID_HEADER))
                            .build()))
                    .blockFirst();
        }
    }
}
