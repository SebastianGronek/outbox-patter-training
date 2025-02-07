package com.example.demo.order.domain;

import org.springframework.context.ApplicationEvent;

public class OutboxMessageEvent extends ApplicationEvent {
    private final OutboxMessage outboxMessage;

    public OutboxMessageEvent(Object source, OutboxMessage outboxMessage) {
        super(source);
        this.outboxMessage = outboxMessage;
    }

    public OutboxMessage getOutboxMessage() {
        return outboxMessage;
    }
}