package com.example.demo.order.infrastrucure;

import com.example.demo.order.domain.OutboxMessage;
import com.example.demo.order.domain.file.FileService;
import jakarta.persistence.PostPersist;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class OutboxMessageListener {
    private final FileService fileService;
    static {
        log.info("OutboxMessageListener loaded");
    }
    @PostPersist
    public void postPersist(OutboxMessage outboxMessage) {
        log.info("OutboxMessageListener triggered for OutboxMessage: {}", outboxMessage);
        try {
            fileService.writeFile(outboxMessage);
            log.info("FileService.writeFile called successfully");

        } catch (Exception e) {
            log.error("Error writing file: " + e.getMessage());
        }

    }
}
