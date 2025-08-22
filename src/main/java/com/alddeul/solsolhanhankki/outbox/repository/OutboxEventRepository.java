package com.alddeul.solsolhanhankki.outbox.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alddeul.solsolhanhankki.outbox.model.entity.OutboxEvent;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {
    List<OutboxEvent> findByProcessedFalse();
}

