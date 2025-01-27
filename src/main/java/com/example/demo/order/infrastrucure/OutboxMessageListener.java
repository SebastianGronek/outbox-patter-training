package com.example.demo.order.infrastrucure;

import com.example.demo.order.domain.OutboxMessage;
import com.example.demo.order.domain.file.FileService;
import jakarta.persistence.PostPersist;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class OutboxMessageListener {
    private final FileService fileService;

    @PostPersist
    public void postPersist(OutboxMessage outboxMessage) {
        fileService.writeFile(outboxMessage);
    }
}
