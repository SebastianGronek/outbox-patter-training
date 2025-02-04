package com.example.demo.order.domain.file;

import com.example.demo.order.domain.OutboxMessage;
import com.example.demo.order.domain.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

@Service
@Slf4j
@Profile("!test")
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    private final OutboxRepository outboxRepository;

    public String writeFile(OutboxMessage outboxMessage) {
        String path = System.getProperty("user.dir") + "/src/main/resources/files/";
        String filename = path + outboxMessage.getOrderId() + ".txt";
        String content = "Order ID: " + outboxMessage.getOrderId() + "\nProduct Name: " + outboxMessage.getProductName() + "\nQuantity: " + outboxMessage.getQuantity();
        try (FileWriter fileWriter = new FileWriter(filename)) {
            fileWriter.write(content);
            log.info("File written successfully");
        } catch (IOException e) {
            log.info("Error writing file: " + e.getMessage());
        }
        return filename;
    }

    @Override
    public boolean setWasSendToTrue(UUID orderId) {
        log.info("Setting wasSend to true for production implementation");
        OutboxMessage outboxMessage = outboxRepository.findOutboxMessageByOrderId(orderId);
        if (outboxMessage != null) {
            outboxMessage.setWasSend(true);
            outboxRepository.save(outboxMessage);
            return true;
        }
        return false;
    }
}