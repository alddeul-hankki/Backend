package com.alddeul.heyyoung.domain.paymoney.model.repository;

import com.alddeul.heyyoung.domain.paymoney.model.entity.TopUpIntent;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TopUpIntentRepository extends JpaRepository<TopUpIntent, Long> {
    // 동시성 제어용: 행 잠금 (지갑 크레딧 직전 등)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select t from TopUpIntent t where t.id = :id")
    Optional<TopUpIntent> findByIdForUpdate(@Param("id") Long id);
}
