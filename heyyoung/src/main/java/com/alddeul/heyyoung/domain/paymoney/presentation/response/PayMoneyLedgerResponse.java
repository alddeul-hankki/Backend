package com.alddeul.heyyoung.domain.paymoney.presentation.response;

import com.alddeul.heyyoung.domain.paymoney.model.entity.PayMoneyLedger;

import java.time.OffsetDateTime;

public record PayMoneyLedgerResponse(
        Long id,
        String type,
        long amount,
        long balanceAfter,
        String description,
        OffsetDateTime createdAt
) {
    public static PayMoneyLedgerResponse toResponse(PayMoneyLedger l) {
        return new PayMoneyLedgerResponse(
                l.getId(),
                l.getType().name(),
                l.getAmount(),
                l.getBalanceAfter(),
                l.getDescription(),
                l.getCreatedAt()
        );
    }

}
