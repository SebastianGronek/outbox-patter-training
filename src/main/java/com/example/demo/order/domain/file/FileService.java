package com.example.demo.order.domain.file;

import com.example.demo.order.domain.OutboxMessage;

public interface FileService {
    String writeFile(OutboxMessage outboxMessage);
}
