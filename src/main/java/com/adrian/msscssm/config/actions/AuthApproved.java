package com.adrian.msscssm.config.actions;

import com.adrian.msscssm.domain.PaymentEvent;
import com.adrian.msscssm.domain.PaymentState;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

@Component
public class AuthApproved implements Action<PaymentState, PaymentEvent> {

    @Override
    public void execute(StateContext<PaymentState, PaymentEvent> context) {
        System.out.println("Payment authorization approved");
    }
}
