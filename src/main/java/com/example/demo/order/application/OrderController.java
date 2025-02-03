package com.example.demo.order.application;

import com.example.demo.order.domain.OrderDto;
import com.example.demo.order.domain.OrderInput;
import com.example.demo.order.domain.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
class OrderController {
    private final OrderService orderService;


    @PostMapping("/order")
    void order(@RequestBody OrderInput orderInput) {
        orderService.handleOrder(orderInput);
    }

    @PostMapping("/orderFailed")
    void orderFailed(@RequestBody OrderInput orderInput) {
        orderService.handleOrderFailed(orderInput);
    }

    @GetMapping("/order/{id}")
    ResponseEntity<OrderDto> getOrderById(@PathVariable String id) {
        OrderDto orderByOrderId = orderService.getOrderByOrderId(UUID.fromString(id));
        return ResponseEntity.ok(orderByOrderId);
    }
}
