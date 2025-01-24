package com.example.demo.order.domain;

import java.util.UUID;

public record OrderInput(UUID orderId, String productName, Integer quantity) {
}
