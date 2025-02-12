package com.example.demo.usecases.it;

import com.example.demo.configuration.TestPostgresqlContainer;
import com.example.demo.order.domain.OrderInput;
import com.example.demo.order.domain.OrderRepository;
import com.example.demo.order.domain.OrderService;
import com.example.demo.order.domain.OutboxRepository;
import com.example.demo.order.domain.file.FileService;
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

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class CreateOrderScenarioITTest {
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
    private FileService fileService;

    @AfterEach
    void clearDatabase() {
        orderRepository.deleteAll();
        outboxRepository.deleteAll();
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
        verify(fileService).setWasWrittenToDiscToTrue(any());
        assertThat(outboxRepository.findOutboxMessageByOrderId(orderId).getWasWrittenToDisc()).isEqualTo(true);
    }

    @Test
    void testCreateOrderWasCommittedButNotWrittenDown() {
        // Given
        UUID orderId = UUID.randomUUID();
        String productName = "improper";
        OrderInput orderInput = new OrderInput(orderId, productName, 1);
        // When
        orderService.handleOrder(orderInput);
        // Then
        assertThat(orderRepository.findAll()).isNotEmpty();
        assertThat(outboxRepository.findAll()).isNotEmpty();
        assertThat(outboxRepository.findOutboxMessageByOrderId(orderId).getWasWrittenToDisc()).isEqualTo(false);
        verify(fileService).writeFile(any());
        verify(fileService, never()).setWasWrittenToDiscToTrue(any());
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
        verify(fileService, never()).setWasWrittenToDiscToTrue(any());
    }
}
