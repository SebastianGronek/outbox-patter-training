package com.example.demo.usecases;

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
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class CreateOrderScenarioIT {
    public static PostgreSQLContainer<TestPostgresqlContainer> postgreSQLContainer = TestPostgresqlContainer.getInstance();


    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OutboxRepository outboxRepository;

    @Autowired
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
    }
}
