package com.example.demo.order.domain;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.List;
import java.util.UUID;

public interface OutboxRepository extends JpaRepository<OutboxMessage, Long> {
    OutboxMessage findOutboxMessageByOrderId(UUID orderId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    OutboxMessage findOutboxMessagesById(long id);

    //TODO: change return value to list<Long>
    List<OutboxMessage> findIdsByWasWrittenToDisc(boolean wasSend, Pageable limit);

    int countByWasWrittenToDisc(boolean wasSend);

    default OutboxMessage saveAndThrowError() {
        throw new RuntimeException("Error");
    }
}
