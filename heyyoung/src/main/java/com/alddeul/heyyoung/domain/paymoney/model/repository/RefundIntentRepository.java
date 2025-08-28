package com.alddeul.heyyoung.domain.paymoney.model.repository;

import com.alddeul.heyyoung.domain.paymoney.model.entity.RefundIntent;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefundIntentRepository extends JpaRepository<RefundIntent, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select t from RefundIntent t where t.id = :id")
    Optional<RefundIntent> findByIdForUpdate(@Param("id") Long id);
}
