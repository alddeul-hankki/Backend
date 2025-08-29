package com.alddeul.heyyoung.domain.account.presentation.response;


import com.alddeul.heyyoung.domain.account.external.deposit.dto.InquireAccountHistoryListResponse;

public record TransactionHistoryResponse(
        String date,
        String time,
        String typeName,
        String accountNumber,
        Long balance,
        Long afterBalance,
        String summary
) {
    public static TransactionHistoryResponse from(InquireAccountHistoryListResponse.Rec.Detail response) {
        return new TransactionHistoryResponse(
                response.transactionDate(),
                response.transactionTime(),
                response.transactionTypeName(),
                response.transactionAccountNo(),
                response.transactionBalance(),
                response.transactionAfterBalance(),
                response.transactionSummary()
        );
    }
}
