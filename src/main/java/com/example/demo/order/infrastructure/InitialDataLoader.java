package com.example.demo.order.infrastructure;

import com.example.demo.order.domain.OutboxMessage;
import com.example.demo.order.domain.OutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.UUID;

@RequiredArgsConstructor
@Component
@Profile("!test")
public class InitialDataLoader implements CommandLineRunner {

    private final OutboxRepository outboxRepository;

    @Override
    public void run(String... args) throws Exception {
        // Create and save some initial data
        OutboxMessage message1 = new OutboxMessage();
        message1.setProductName("Illegal product");
        message1.setQuantity(1);
        message1.setOrderId(UUID.randomUUID());
        message1.setWasWrittenToDisc(false);
        outboxRepository.save(message1);

        OutboxMessage message2 = new OutboxMessage();
        message2.setProductName("Illegal product");
        message2.setQuantity(2);
        message2.setOrderId(UUID.randomUUID());
        message2.setWasWrittenToDisc(false);
        outboxRepository.save(message2);

        OutboxMessage message3 = new OutboxMessage();
        message3.setProductName("Product 3");
        message3.setQuantity(3);
        message3.setOrderId(UUID.randomUUID());
        message3.setWasWrittenToDisc(true);
        outboxRepository.save(message3);

        OutboxMessage message4 = new OutboxMessage();
        message4.setProductName("Illegal product");
        message4.setQuantity(4);
        message4.setOrderId(UUID.randomUUID());
        message4.setWasWrittenToDisc(false);
        outboxRepository.save(message4);

        OutboxMessage message5 = new OutboxMessage();
        message5.setProductName("Product 5");
        message5.setQuantity(5);
        message5.setOrderId(UUID.randomUUID());
        message5.setWasWrittenToDisc(true);
        outboxRepository.save(message5);

        OutboxMessage message6 = new OutboxMessage();
        message6.setProductName("Illegal product");
        message6.setQuantity(6);
        message6.setOrderId(UUID.randomUUID());
        message6.setWasWrittenToDisc(false);
        outboxRepository.save(message6);

        OutboxMessage message7 = new OutboxMessage();
        message7.setProductName("Illegal product");
        message7.setQuantity(7);
        message7.setOrderId(UUID.randomUUID());
        message7.setWasWrittenToDisc(false);
        outboxRepository.save(message7);

        System.out.println("Initial data loaded into the database");
    }
}