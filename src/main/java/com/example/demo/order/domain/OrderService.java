package com.example.demo.order.domain;

import java.util.UUID;

public interface OrderService {
    void handleOrder(OrderInput order);
    void handleOrderFailed(OrderInput order);

    OrderDto getOrderByOrderId(UUID id);
}
