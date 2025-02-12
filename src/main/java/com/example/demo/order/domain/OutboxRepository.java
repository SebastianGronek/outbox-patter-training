package com.example.demo.order.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OutboxRepository extends JpaRepository<OutboxMessage, Long> {
    OutboxMessage findOutboxMessageByOrderId(UUID orderId);

    List<OutboxMessage> findOutboxMessagesByWasWrittenToDisc(boolean wasSend);

   default OutboxMessage saveAndThrowError(){
        throw new RuntimeException("Error");
    };
}
