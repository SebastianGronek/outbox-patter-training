package com.example.demo.order.domain;

import java.util.UUID;

public record OrderDto(UUID orderId, String productName, Integer quantity) {
    public static OrderDto fromOrder(Order order) {
        return new OrderDto(order.getOrderId(), order.getProductName(), order.getQuantity());
    }
}