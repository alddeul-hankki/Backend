package com.alddeul.heyyoung.domain.paymoney.model.repository;

import com.alddeul.heyyoung.domain.paymoney.model.entity.PayMoneyLedger;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PayMoneyLedgerRepository extends JpaRepository<PayMoneyLedger, Long> {
    Optional<PayMoneyLedger> findByPaymoneyIdAndIdempotencyKey(Long paymoneyId, String idempotencyKey);

    List<PayMoneyLedger> findAllByPaymoney_User_EmailOrderByCreatedAtDesc(String email);
}