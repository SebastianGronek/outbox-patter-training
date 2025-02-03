package com.example.demo.order.domain.file;

import com.example.demo.order.domain.OutboxMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@Profile("test")
class FileServiceTestImplementation implements FileService {
    @Override
    public String writeFile(OutboxMessage outboxMessage) {
        log.info("Writing file for failed implementation");
        throw new RuntimeException("Error in fileService");
    }
}
