package com.example.demo.order.domain.file;

import com.example.demo.order.domain.OutboxMessage;

import java.util.UUID;

public interface FileService {
    String writeFile(OutboxMessage outboxMessage);

    boolean setWasWrittenToDiscToTrue(UUID orderId);
}
