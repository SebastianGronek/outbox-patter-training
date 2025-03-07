package com.example.demo.order.domain.file;

import com.example.demo.order.domain.OutboxMessage;
import com.example.demo.order.domain.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

//@Service
@Slf4j
//@Profile("test")
@RequiredArgsConstructor
class FileServiceTestImplementation implements FileService {

    private final OutboxRepository outboxRepository;

    @Override
    public String writeFile(OutboxMessage outboxMessage) {
        imitateErrorForRightProductName(outboxMessage);
        if (outboxMessage.getProductName().equals("Legal product")) {
            log.info("Writing file for successful implementation");
            return "file.txt";
        }
        log.info("Writing file for failed implementation");
        throw new RuntimeException("Error in fileService");
    }

    private void imitateErrorForRightProductName(OutboxMessage outboxMessage) {
        if (outboxMessage.getProductName().equals("Illegal product")) {
            outboxMessage.setProductName("Legal product");
            log.info("Saving product with name changed to:" + outboxMessage.getProductName());
            outboxRepository.save(outboxMessage);
            throw new RuntimeException("Failed to save illegal product");
        }
    }
}
