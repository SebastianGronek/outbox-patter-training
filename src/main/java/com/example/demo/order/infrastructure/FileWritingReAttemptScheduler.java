package com.example.demo.order.infrastructure;

import com.example.demo.order.domain.OutboxMessage;
import com.example.demo.order.domain.OutboxRepository;
import com.example.demo.order.domain.file.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
class FileWritingReAttemptScheduler {
    private final OutboxRepository outboxRepository;
    private final FileService fileService;


    @Scheduled(fixedRate = 1000)
    // This cron expression runs every 1 minute between 9 AM and 5 PM, Monday to Friday
    public void scheduleFileWritingReAttempt() {
        System.out.println("File writing reattempt scheduled");
        List<OutboxMessage> messages = outboxRepository.findOutboxMessagesByWasWrittenToDisc(false);
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
