package com.example.demo.order.domain;

import java.util.UUID;

public record OrderInput(UUID orderID, String productName, Integer quantity) {
}
