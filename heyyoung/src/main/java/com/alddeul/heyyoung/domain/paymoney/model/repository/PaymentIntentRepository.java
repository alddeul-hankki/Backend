package com.alddeul.heyyoung.domain.paymoney.model.repository;

import com.alddeul.heyyoung.domain.paymoney.model.entity.PaymentIntent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentIntentRepository extends JpaRepository<PaymentIntent, Integer> {
    Optional<PaymentIntent> findByIdempotencyKey(String idempotencyKey);
}
