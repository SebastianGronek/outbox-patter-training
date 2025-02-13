package com.example.demo.order.infrastructure;

import com.example.demo.order.domain.OutboxMessage;
import com.example.demo.order.domain.OutboxRepository;
import com.example.demo.order.domain.file.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
@Slf4j
class FileWritingReAttemptScheduler {
    private final OutboxRepository outboxRepository;
    private final FileService fileService;


    @Scheduled(fixedRate = 3000)
    // This cron expression runs every 1 minute between 9 AM and 5 PM, Monday to Friday
    public void scheduleFileWritingReAttempt() {
        log.info("File writing reattempt scheduled");
        List<OutboxMessage> messages = outboxRepository.findOutboxMessagesByWasWrittenToDisc(false, Pageable.ofSize(2));
        log.info("Found {} messages to reattempt writing to disk", messages.size());
        for (OutboxMessage message : messages) {
            reattemptWritingToDisk(message);
        }
    }

    private void reattemptWritingToDisk(OutboxMessage message) {
        try {
            fileService.writeFile(message);
            System.out.println("Reattempting to write message to disk: " + message);
            message.setWasWrittenToDisc(true);
            outboxRepository.save(message);
        } catch (Exception e) {
            System.err.println("Failed to write message to disk during reattempt: " + message);
        }
    }

}
