package com.example.demo.order.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Order findOrderByOrderId(UUID orderId);
}
