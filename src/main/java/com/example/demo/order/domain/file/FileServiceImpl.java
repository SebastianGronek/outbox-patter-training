package com.example.demo.order.domain.file;

import com.example.demo.order.domain.OutboxMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;

@Service
@Slf4j
@Profile("!test")
public class FileServiceImpl implements FileService {

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
}