package com.alddeul.heyyoung.domain.account.external.deposit.port;

import com.alddeul.heyyoung.common.api.external.dto.FinanceApiResponse;
import com.alddeul.heyyoung.domain.account.external.deposit.dto.DepositAccountResponse;
import com.alddeul.heyyoung.domain.account.external.deposit.dto.WithdrawalAccountResponse;


public interface AccountDepositPort {
    FinanceApiResponse<WithdrawalAccountResponse> withdraw(String userKey, String accountNo, Long amount, String summary, String instTxnNo);

    FinanceApiResponse<DepositAccountResponse> deposit(String userKey, String accountNo, Long amount, String summary, String instTxnNo);
}
