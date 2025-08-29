package com.alddeul.heyyoung.domain.paymoney.model.repository;

import com.alddeul.heyyoung.domain.paymoney.model.entity.PayMoney;
import com.alddeul.heyyoung.domain.user.model.entity.SolUser;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PayMoneyRepository extends JpaRepository<PayMoney, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<PayMoney> findByUserId(Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<PayMoney> findByUserEmail(String email);

    boolean existsByUser(SolUser user);
}