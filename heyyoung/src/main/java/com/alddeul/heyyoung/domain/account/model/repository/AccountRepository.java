package com.alddeul.heyyoung.domain.account.model.repository;

import com.alddeul.heyyoung.domain.account.model.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByAccountNumberIn(List<String> accountNumbers);
}
