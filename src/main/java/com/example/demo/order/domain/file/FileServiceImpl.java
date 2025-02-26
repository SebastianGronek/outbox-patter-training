package com.example.demo.order.domain.file;

import com.example.demo.order.domain.OutboxMessage;
import com.example.demo.order.domain.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
//@Profile("!test")
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    private final OutboxRepository outboxRepository;

    public String writeFile(OutboxMessage outboxMessage) {
        imitateErrorForRightProductName(outboxMessage);
        String path = System.getProperty("user.dir") + "/src/main/resources/files/";
        String filename = path + outboxMessage.getOrderId() + ".txt";
        String content = "Order ID: " + outboxMessage.getOrderId() + "\nProduct Name: " + outboxMessage.getProductName() + "\nQuantity: " + outboxMessage.getQuantity() + "\nTime of creation: " + LocalDateTime.now();
        try (FileWriter fileWriter = new FileWriter(filename)) {
            fileWriter.write(content);
            log.info("File written successfully: " + filename);
        } catch (IOException e) {
            log.info("Error writing file: " + e.getMessage());
        }
        return filename;
    }

    private void imitateErrorForRightProductName(OutboxMessage outboxMessage) {
        if (outboxMessage.getProductName().equals("Illegal product")) {
            outboxMessage.setProductName("Legal product");
            log.info("Saving product with name changed to:" + outboxMessage.getProductName());
            outboxRepository.save(outboxMessage);
            throw new RuntimeException("Failed to save illegal product");
        } else if (outboxMessage.getProductName().equals("Error product")) {
            throw new RuntimeException("Failed to save error product");
        }
    }

    @Override
    @Transactional
    public boolean setWasWrittenToDiscToTrue(UUID orderId) {
        log.info("Setting wasSend to true for production implementation");
        OutboxMessage outboxMessage = outboxRepository.findOutboxMessageByOrderId(orderId);
        if (outboxMessage != null) {
            outboxMessage.setWasWrittenToDisc(true);
            log.info("Setting wasSend to true for implementation: " + outboxMessage.getWasWrittenToDisc());
            outboxRepository.save(outboxMessage);
            return true;
        }
        return false;
    }
}
