package com.example.demo.order.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OutboxRepository extends JpaRepository<OutboxMessage, Long> {
    OutboxMessage findOutboxMessageByOrderId(UUID orderId);
}
