package com.example.demo.order.domain.file;

import com.example.demo.order.domain.OutboxMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
//@Profile("!test")
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    public String writeFile(OutboxMessage outboxMessage) {
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
}
