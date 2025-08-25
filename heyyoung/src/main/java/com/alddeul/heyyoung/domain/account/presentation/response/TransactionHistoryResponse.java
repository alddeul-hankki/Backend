package com.alddeul.heyyoung.domain.account.presentation.response;


import com.alddeul.heyyoung.domain.account.external.deposit.dto.InquireAccountHistoryListResponse;

public record TransactionHistoryResponse(
        String date,
        String time,
        String typeName,
        String accountNumber,
        Long balance,
        Long afterBalance,
        String summary,
        String memo

) {
    public static TransactionHistoryResponse from(InquireAccountHistoryListResponse.Rec.Detail response) {
        return new TransactionHistoryResponse(
                response.getTransactionDate(),
                response.getTransactionTime(),
                response.getTransactionTypeName(),
                response.getTransactionAccountNo(),
                response.getTransactionBalance(),
                response.getTransactionAfterBalance(),
                response.getTransactionSummary(),
                response.getTransactionMemo()
        );
    }
}
