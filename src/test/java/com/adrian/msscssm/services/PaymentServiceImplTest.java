package com.adrian.msscssm.services;

import com.adrian.msscssm.domain.Payment;
import com.adrian.msscssm.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@SpringBootTest
class PaymentServiceImplTest {

    @Autowired
    PaymentService paymentService;

    @Autowired
    PaymentRepository paymentRepository;

    Payment payment;

    @BeforeEach
    void setUp() {
        payment = Payment.builder()
                .amount(new BigDecimal("12.99"))
                .build();
    }

    @Transactional
    @Test
    void preAuth() {
        Payment savedPayment = paymentService.createNewPayment(payment);

        paymentService.preAuth(savedPayment.getId());

        Payment preAuthPayment = paymentRepository.getReferenceById(savedPayment.getId());

        System.out.println(preAuthPayment);
    }

    @Transactional
    @Test
    void authorize() {
        Payment savedPayment = paymentService.createNewPayment(payment);

        paymentService.preAuth(savedPayment.getId());
        paymentService.authorizePayment(savedPayment.getId());

        Payment authPayment = paymentRepository.getReferenceById(savedPayment.getId());

        System.out.println(authPayment);
    }
}