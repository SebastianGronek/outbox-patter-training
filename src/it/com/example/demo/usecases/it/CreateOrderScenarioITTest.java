package com.example.demo.usecases.it;

import com.example.demo.configuration.TestPostgresqlContainer;
import com.example.demo.order.domain.OrderInput;
import com.example.demo.order.domain.OrderRepository;
import com.example.demo.order.domain.OrderService;
import com.example.demo.order.domain.OutboxMessage;
import com.example.demo.order.domain.OutboxRepository;
import com.example.demo.order.domain.file.FileService;
import com.example.demo.order.domain.file.FileServiceProxy;
import com.example.demo.order.infrastructure.OutboxHandler;
import com.example.demo.utils.FileCreationTestHandler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.shaded.org.awaitility.Awaitility;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.example.demo.utils.FileCreationTestHandler.verifyFileCreation;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class CreateOrderScenarioITTest {

    private static final String FILE_PATH = System.getProperty("user.dir") + "/src/main/resources/files/";
    public static PostgreSQLContainer<TestPostgresqlContainer> postgreSQLContainer = TestPostgresqlContainer.getInstance();

    @BeforeAll
    static void setup() {
        postgreSQLContainer.start();
    }

    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OutboxRepository outboxRepository;
    @MockitoSpyBean
    private OutboxHandler outboxHandler;
    @MockitoSpyBean
    private FileService fileService;

    @AfterEach
    void clearDatabase() {
        orderRepository.deleteAll();
        outboxRepository.deleteAll();
        FileCreationTestHandler.deleteTestFiles(FILE_PATH);
    }

    @Test
    void testCreateOrder() {
        // Given
        UUID orderId = UUID.randomUUID();
        String productName = "proper";
        OrderInput orderInput = new OrderInput(orderId, productName, 1);
        // When
        orderService.handleOrder(orderInput);
        // Then
        assertThat(orderRepository.findAll()).isNotEmpty();
        assertThat(outboxRepository.findAll()).isNotEmpty();
        verify(fileService).writeFile(any());
        verify(outboxHandler).handleOutboxMessageEvent(anyLong());
        assertThat(outboxRepository.findOutboxMessageByOrderId(orderId).getWasWrittenToDisc()).isEqualTo(true);
        verifyFileCreation(FILE_PATH + orderId + ".txt");
    }

    @Test
    void testCreateOrderWasCommittedButNotWrittenDown() {
        // Given
        ((FileServiceProxy) fileService).setFailRate(1.0f);
        UUID orderId = UUID.randomUUID();
        String productName = "Error product";
        OrderInput orderInput = new OrderInput(orderId, productName, 1);
        // When
        orderService.handleOrder(orderInput);
        // Then
        assertThat(orderRepository.findAll()).isNotEmpty();
        assertThat(outboxRepository.findAll()).isNotEmpty();
        assertThat(outboxRepository.findOutboxMessageByOrderId(orderId).getWasWrittenToDisc()).isEqualTo(false);
        verify(fileService).writeFile(any());
        ((FileServiceProxy) fileService).setFailRate(0.0f);
    }

    @Test
    void testCreateOrderAndRollback() {
        // Given
        UUID orderId = UUID.randomUUID();
        String productName = "proper";
        OrderInput orderInput = new OrderInput(orderId, productName, 1);
        // When
        assertThrows(RuntimeException.class, () -> orderService.handleOrderFailed(orderInput));
        // Then
        assertThat(orderRepository.findAll()).isEmpty();
        assertThat(outboxRepository.findAll()).isEmpty();
        verify(fileService, never()).writeFile(any());
    }

    @Test
    void testCreateOrdersAndSchedulerWriteThemDownToDisc() throws InterruptedException {
        // Given
        outboxRepository.deleteAll();
        ((FileServiceProxy) fileService).setFailRate(1.0f);
        String productName = "Illegal product";
        int numberOfOrders = 9;
        // When
        for (int i = 0; i < numberOfOrders; i++) {
            orderService.handleOrder(new OrderInput(UUID.randomUUID(), productName, i));
        }
        Awaitility.await().until(() -> outboxRepository.findAll().size() == numberOfOrders);
        // Then
        assertThat(orderRepository.findAll().size()).isEqualTo(numberOfOrders);
        assertThat(outboxRepository.findAll().size()).isEqualTo(numberOfOrders);
        // And
        assertThat(outboxRepository.findIdsByWasWrittenToDisc(false, null)).hasSize(numberOfOrders);
        ((FileServiceProxy) fileService).setFailRate(0.0f);
        Awaitility.await().until(() -> outboxRepository.findIdsByWasWrittenToDisc(true, null).size() == numberOfOrders);
        Set<UUID> ids = outboxRepository.findAll().stream().map(OutboxMessage::getOrderId).collect(Collectors.toSet());
        for (UUID id : ids) {
            verifyFileCreation(FILE_PATH + id + ".txt");
        }
    }
}
