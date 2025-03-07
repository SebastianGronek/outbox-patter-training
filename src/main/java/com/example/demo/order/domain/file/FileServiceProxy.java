package com.example.demo.order.domain.file;

import com.example.demo.order.domain.OutboxMessage;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.concurrent.ThreadLocalRandom;

@RequiredArgsConstructor
@Setter
public class FileServiceProxy implements FileService {
    private final FileService fileService;
    volatile float failRate = 0.0f;

    @Override
    public String writeFile(OutboxMessage outboxMessage) {
        if (ThreadLocalRandom.current().nextFloat() >= failRate) {
            return fileService.writeFile(outboxMessage);
        } else {
            throw new RuntimeException("FileServiceProxy: writeFile failed " + outboxMessage.getId());
        }
    }
}
