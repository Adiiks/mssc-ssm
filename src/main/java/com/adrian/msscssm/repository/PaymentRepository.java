package com.adrian.msscssm.repository;

import com.adrian.msscssm.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
