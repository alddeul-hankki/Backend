package com.alddeul.heyyoung.domain.account.external.deposit.adapter;

import com.alddeul.heyyoung.common.api.external.dto.FinanceApiResponse;
import com.alddeul.heyyoung.domain.account.external.deposit.FinanceAccountClient;
import com.alddeul.heyyoung.domain.account.external.deposit.dto.DepositAccountResponse;
import com.alddeul.heyyoung.domain.account.external.deposit.dto.WithdrawalAccountResponse;
import com.alddeul.heyyoung.domain.account.external.deposit.port.AccountDepositPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountDepositAdapter implements AccountDepositPort {
    private final FinanceAccountClient accountClient;

    @Override
    public FinanceApiResponse<WithdrawalAccountResponse> withdraw(String userKey, String accountNo, Long amount, String summary, String instTxnNo) {
        return accountClient.withdrawDemandDepositAccount(userKey, accountNo, amount, summary, instTxnNo);
    }

    @Override
    public FinanceApiResponse<DepositAccountResponse> deposit(String userKey, String accountNo, Long amount, String summary, String instTxnNo) {
        return accountClient.depositDemandDepositAccount(userKey, accountNo, amount, summary, instTxnNo);
    }
}