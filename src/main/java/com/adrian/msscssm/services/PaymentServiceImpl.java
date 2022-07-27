package com.adrian.msscssm.services;

import com.adrian.msscssm.domain.Payment;
import com.adrian.msscssm.domain.PaymentEvent;
import com.adrian.msscssm.domain.PaymentState;
import com.adrian.msscssm.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    public static final String PAYMENT_ID_HEADER = "payment_id";

    private final PaymentRepository paymentRepository;
    private final StateMachineFactory<PaymentState, PaymentEvent> stateMachineFactory;
    private final PaymentStateChangeInterceptor paymentStateChangeInterceptor;

    @Override
    public Payment createNewPayment(Payment payment) {
        payment.setState(PaymentState.NEW);

        return paymentRepository.save(payment);
    }

    @Transactional
    @Override
    public StateMachine<PaymentState, PaymentEvent> preAuth(Long paymentId) {
        StateMachine<PaymentState, PaymentEvent> stateMachine = build(paymentId);

        sentEvent(paymentId, stateMachine, PaymentEvent.PRE_AUTHORIZE);

        return stateMachine;
    }

    @Transactional
    @Override
    public StateMachine<PaymentState, PaymentEvent> authorizePayment(Long paymentId) {
        StateMachine<PaymentState, PaymentEvent> stateMachine = build(paymentId);

        sentEvent(paymentId, stateMachine, PaymentEvent.AUTHORIZE);

        return stateMachine;
    }

    private void sentEvent(Long paymentId, StateMachine<PaymentState, PaymentEvent> stateMachine, PaymentEvent event) {
        Message message = MessageBuilder.withPayload(event)
                .setHeader(PAYMENT_ID_HEADER, paymentId)
                .build();

        stateMachine.sendEvent(Mono.just((message))).blockFirst();
    }

    private StateMachine<PaymentState, PaymentEvent> build(Long paymentId) {
        Payment payment = paymentRepository.getReferenceById(paymentId);

        StateMachine<PaymentState, PaymentEvent> stateMachine =
                stateMachineFactory.getStateMachine(Long.toString(payment.getId()));

        stateMachine.stopReactively().block();

        stateMachine.getStateMachineAccessor()
                .doWithAllRegions(sma -> {
                    sma.addStateMachineInterceptor(paymentStateChangeInterceptor);
                    sma.resetStateMachineReactively(new DefaultStateMachineContext<>(payment.getState(), null,
                            null, null)).block();
                });

        stateMachine.startReactively().block();

        return stateMachine;
    }
}
