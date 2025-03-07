package com.example.demo.order.domain;

import lombok.Builder;

import java.util.UUID;

@Builder
public record OrderInput(UUID orderId, String productName, Integer quantity) {
}
