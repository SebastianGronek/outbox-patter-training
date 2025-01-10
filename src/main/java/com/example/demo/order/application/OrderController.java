package com.example.demo.order.application;

import com.example.demo.order.domain.Order;
import com.example.demo.order.domain.OrderInput;
import com.example.demo.order.domain.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
class OrderController {
    @Autowired
    OrderService orderService;


    @PostMapping("/order")
    void order(OrderInput orderInput) {
        orderService.handleOrder(orderInput);
    }

    @GetMapping("/order/{id}")
    Order getOrderById(@PathVariable String id) {
        return orderService.getOrderByOrderId(UUID.fromString(id));
    }
}
