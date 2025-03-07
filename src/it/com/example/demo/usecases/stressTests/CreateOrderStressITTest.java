package com.example.demo.usecases.stressTests;

import com.example.demo.configuration.TestPostgresqlContainer;
import com.example.demo.order.domain.OrderInput;
import com.example.demo.order.domain.OrderRepository;
import com.example.demo.order.domain.OrderService;
import com.example.demo.order.domain.OutboxMessage;
import com.example.demo.order.domain.OutboxRepository;
import com.example.demo.order.domain.file.FileService;
import com.example.demo.order.domain.file.FileServiceProxy;
import com.example.demo.utils.FileCreationTestHandler;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.shaded.org.awaitility.Awaitility;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.example.demo.utils.FileCreationTestHandler.verifyFileCreation;

@Slf4j
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class CreateOrderStressITTest {

    private static final String FILE_PATH = System.getProperty("user.dir") + "/src/main/resources/files/";
    public static PostgreSQLContainer<TestPostgresqlContainer> postgreSQLContainer = TestPostgresqlContainer.getInstance();
    @Autowired
    OrderService orderService;
    @Autowired
    FileService fileService;
    @Autowired
    OutboxRepository outboxRepository;
    @Autowired
    OrderRepository orderRepository;

    @BeforeAll
    static void setup() {
        postgreSQLContainer.start();
    }

    @AfterEach
    void clearDatabase() {
        orderRepository.deleteAll();
        outboxRepository.deleteAll();
        FileCreationTestHandler.deleteTestFiles(FILE_PATH);
    }

    @Test
    void shouldWriteDownGivenNumberOfFiles() throws InterruptedException {
        //given
        long inititalRepositorySize = outboxRepository.count();
        int numberOfThreads = 10;
        int numberOfOrdersPerThread = 5;
        int numberOfOrders = numberOfThreads * numberOfOrdersPerThread;
        AtomicInteger atomicInteger = new AtomicInteger(0);
        Supplier<OrderInput> orderInputSupplier = () -> OrderInput.builder()
                .orderId(UUID.randomUUID())
                .productName("product" + atomicInteger.incrementAndGet())
                .quantity(1)
                .build();
        ((FileServiceProxy) fileService).setFailRate(0.25f);
        //when
        createOrders(orderInputSupplier, numberOfThreads, numberOfOrdersPerThread);
        //then
        Awaitility.await().until(() -> outboxRepository.countByWasWrittenToDisc(true) - inititalRepositorySize == numberOfOrders);
        Set<UUID> ids = outboxRepository.findAll().stream().map(OutboxMessage::getOrderId).collect(Collectors.toSet());
        for (UUID id : ids) {
            verifyFileCreation(FILE_PATH + id + ".txt");
        }
    }

    private void createOrders(Supplier<OrderInput> orderInputSupplier, int numberOfThreads, int numberOfOrdersPerThread) throws InterruptedException {
        final CyclicBarrier threadGate = new CyclicBarrier(numberOfThreads);
        final CountDownLatch countDownLatch = new CountDownLatch(numberOfThreads * numberOfOrdersPerThread);

        for (int i = 0; i < numberOfThreads; i++) {
            new Thread(() -> {
                try {
                    threadGate.await();
                    for (int task = 0; task < numberOfOrdersPerThread; task++) {
                        orderService.handleOrder(orderInputSupplier.get());
                    }
                    countDownLatch.countDown();
                } catch (Exception e) {
                    log.error("Error in thread", e);
                }
            }
            ).start();
        }
        countDownLatch.await(60, TimeUnit.SECONDS);
    }


}
