package com.example.demo.order.domain;

import java.util.UUID;

public interface OrderService {
    void handleOrder(OrderInput order);

    public Order getOrderByOrderId(UUID id);
}
