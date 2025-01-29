package com.example.demo.order.domain.file;

import com.example.demo.order.domain.OrderInput;
import com.example.demo.order.domain.OutboxMessage;

import java.io.IOException;

public interface FileService {
    String writeFile(OutboxMessage outboxMessage);
}
