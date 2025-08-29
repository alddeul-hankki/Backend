package com.alddeul.solsolhanhankki.order.model.repository;

import com.alddeul.solsolhanhankki.order.model.entity.Orders;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Orders, Long> {

    Optional<Orders> findById(Long orderId);

    List<Orders> findAllByGroupId(Long groupId);

    Optional<Orders> findByIdAndUserId(Long orderId, Long userId);

    Optional<Orders> findByPaymentToken(String paymentToken);

    List<Orders> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
}