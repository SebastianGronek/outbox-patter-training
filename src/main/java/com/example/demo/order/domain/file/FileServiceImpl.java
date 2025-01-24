package com.example.demo.order.domain.file;

import com.example.demo.order.domain.OrderInput;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;

@Service
@Slf4j
public class FileServiceImpl implements FileService {

    public void writeFile(OrderInput orderInput) {
        String path = System.getProperty("user.dir") + "/src/main/resources/files/";
        String filename = path + orderInput.orderId() + ".txt";
        String content = "Order ID: " + orderInput.orderId() + "\nProduct Name: " + orderInput.productName() + "\nQuantity: " + orderInput.quantity();
        try (FileWriter fileWriter = new FileWriter(filename)) {
            fileWriter.write(content);
            log.info("File written successfully");
        } catch (IOException e) {
            log.info("Error writing file: " + e.getMessage());
        }
    }
}