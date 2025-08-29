package com.alddeul.heyyoung.domain.account.application;

import com.alddeul.heyyoung.domain.account.model.entity.Account;
import com.alddeul.heyyoung.domain.account.model.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountFacade {
    private final AccountRepository accountRepository;

    public Account getAccount(Long accountId){
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("계좌 ID로 계좌를 찾을 수 없습니다."));
    }

    public String getPrimaryAccountByUser(Long userId) {
        List<Account> accountList = accountRepository.findBySolUserId(userId);
        for (Account account : accountList) {
            if (account.getPrimaryAccount()) {
                return account.getAccountNumber();
            }
        }
        return null;
    }

    public Account getAccountByAccountNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber);
    }
}
