package com.alddeul.heyyoung.domain.paymoney.model.repository;

import com.alddeul.heyyoung.domain.paymoney.model.entity.PayMoney;
import com.alddeul.heyyoung.domain.user.model.entity.SolUser;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PayMoneyRepository extends JpaRepository<PayMoney, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<PayMoney> findByUserId(Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from PayMoney p join p.user u where u.email = :email")
    Optional<PayMoney> findByUserEmailForUpdate(String email);

    @Lock(LockModeType.NONE)
    Optional<PayMoney> findByUserEmail(@Param("email") String email);

    boolean existsByUser(SolUser user);
}