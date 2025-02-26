package com.example.demo.order.infrastructure;

import com.example.demo.order.domain.OutboxMessage;
import com.example.demo.order.domain.OutboxRepository;
import com.example.demo.order.domain.file.FileService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxHandler {
    private final FileService fileService;
    private final OutboxRepository outboxRepository;

    @Transactional
    public void handleOutboxMessageEvent(long id) {
        log.info("Handling OutboxMessageEvent for OutboxMessage with id: {}", id);
        OutboxMessage outboxMessage = outboxRepository.findOutboxMessagesById(id);
        if (outboxMessage == null) {
            throw new EntityNotFoundException("OutboxMessage with id " + id + " not found");
        }
        if (Boolean.FALSE.equals(outboxMessage.getWasWrittenToDisc())) {
            fileService.writeFile(outboxMessage);
            outboxMessage.setWasWrittenToDisc(true);
            log.info("Setting wasSend to true for implementation: " + outboxMessage.getWasWrittenToDisc());
            outboxRepository.save(outboxMessage);
            log.info("FileService.writeFile called successfully");
        } else {
            log.info("Message already written to disk: " + outboxMessage);
        }
    }

}
