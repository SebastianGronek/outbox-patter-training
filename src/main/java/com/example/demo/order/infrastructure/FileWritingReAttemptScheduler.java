package com.example.demo.order.infrastructure;

import com.example.demo.order.domain.OutboxMessage;
import com.example.demo.order.domain.OutboxRepository;
import com.example.demo.order.domain.file.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

@RequiredArgsConstructor
@Component
@Slf4j
public class FileWritingReAttemptScheduler {
    private static final int BATCH_SIZE = 3;
    private final OutboxRepository outboxRepository;
    private final FileService fileService;
    private final TransactionTemplate requiredTx;


    @Scheduled(fixedRate = 3000)
    // This cron expression runs every 1 minute between 9 AM and 5 PM, Monday to Friday
    public void scheduleFileWritingReAttempt() {
        while (reattemptWritingToDisk()) {
            log.info("File writing reattempt done");
        }
    }

    public boolean reattemptWritingToDisk() {
        return requiredTx.execute((status) -> {
            List<OutboxMessage> messages = outboxRepository.findOutboxMessagesByWasWrittenToDisc(false, Pageable.ofSize(BATCH_SIZE));
            log.info("Found {} messages to reattempt writing to disk", messages.size());
            for (OutboxMessage message : messages) {
                reattemptWritingToDisk(message);
            }
            return messages.size() == BATCH_SIZE;
        });

    }

    private void reattemptWritingToDisk(OutboxMessage message) {
        try {
            fileService.writeFile(message);
            log.info("Reattempting to write message to disk: " + message);
            message.setWasWrittenToDisc(true);
            outboxRepository.save(message);
        } catch (Exception e) {
            log.error("Failed to write message to disk during reattempt: " + message);
        }
    }

}
