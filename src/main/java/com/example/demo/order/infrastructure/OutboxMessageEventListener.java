package com.example.demo.order.infrastructure;

import com.example.demo.order.domain.OutboxMessageEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
@Slf4j
public class OutboxMessageEventListener {
    private final OutboxHandler outboxHandler;
    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleOutboxMessageEvent(OutboxMessageEvent event) {
        log.info("Handling OutboxMessageEvent for OutboxMessage: {}", event.getOutboxMessage());
        try {
            outboxHandler.handleOutboxMessageEvent(event.getOutboxMessage().getId());
            log.info("FileService.writeFile called successfully");
        } catch (Exception e) {
            log.error("Error writing file in OutboxMessageEventListener: " + e.getMessage(), e);
        }
    }
}