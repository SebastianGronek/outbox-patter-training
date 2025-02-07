package com.example.demo.order.infrastrucure;

import com.example.demo.order.domain.OutboxMessage;
import com.example.demo.order.domain.OutboxMessageEvent;
import jakarta.persistence.PostPersist;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class OutboxMessageListener {

    private final ApplicationEventPublisher eventPublisher;

    static {
        log.info("OutboxMessageListener loaded");
    }

    @PostPersist
    public void postPersist(OutboxMessage outboxMessage) {
        log.info("OutboxMessageListener triggered for OutboxMessage: {}", outboxMessage);
        eventPublisher.publishEvent(new OutboxMessageEvent(this, outboxMessage));
    }
}
