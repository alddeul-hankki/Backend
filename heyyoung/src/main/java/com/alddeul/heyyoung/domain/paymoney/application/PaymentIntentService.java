package com.alddeul.heyyoung.domain.paymoney.application;

import com.alddeul.heyyoung.domain.paymoney.model.entity.PaymentIntent;
import com.alddeul.heyyoung.domain.paymoney.model.repository.PaymentIntentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentIntentService {
    private final PaymentIntentRepository paymentIntentRepository;

    @Transactional
    public PaymentIntent save(PaymentIntent paymentIntent) {
        return paymentIntentRepository.save(paymentIntent);
    }

    @Transactional(readOnly = true)
    public PaymentIntent getPaymentIntent(String token) {
        return paymentIntentRepository.findByIdempotencyKey(token)
                .orElseThrow(() -> new RuntimeException("해당 결제요청을 처리할 수 없습니다."));
    }
}
