package com.example.demo.usecases.e2e;

import com.example.demo.configuration.TestPostgresqlContainer;
import com.example.demo.order.domain.OrderInput;
import com.example.demo.order.domain.OrderRepository;
import com.example.demo.order.domain.OutboxRepository;
import com.example.demo.order.domain.file.FileService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class CreateOrderScenarioE2ETest {
    public static PostgreSQLContainer<TestPostgresqlContainer> postgreSQLContainer = TestPostgresqlContainer.getInstance();


    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OutboxRepository outboxRepository;

    @MockitoSpyBean
    private FileService fileService;

    @BeforeAll
    static void setup() {
        postgreSQLContainer.start();
    }

    @Test
    void testCreateOrderThroughController() {
        // Given
        UUID orderId = UUID.randomUUID();
        String productName = "Test Product";
        OrderInput orderInput = new OrderInput(orderId, productName, 1);
        // When
        given()
                .contentType(JSON)
                .body(orderInput)
                .post("/order")
                .then()
                .assertThat()
                .statusCode(200);
        // Then
        assertThat(orderRepository.findAll()).isNotEmpty();
        assertThat(outboxRepository.findAll()).isNotEmpty();
        assertThat(outboxRepository.findOutboxMessageByOrderId(orderId).getProductName()).isEqualTo(productName);
        verify(fileService, times(1)).writeFile(any());
    }

    @Test
    void testCreateOrderThroughControllerAndRollback() {
        // Given
        UUID orderId = UUID.randomUUID();
        String productName = "Test Product";
        OrderInput orderInput = new OrderInput(orderId, productName, 1);
        // When
        given()
                .contentType(JSON)
                .body(orderInput)
                .post("/orderFailed")
                .then()
                .assertThat()
                .statusCode(500);
        // Then
        assertThat(orderRepository.findAll()).isEmpty();
        assertThat(outboxRepository.findAll()).isEmpty();
        verify(fileService, never()).writeFile(any());

    }
}
