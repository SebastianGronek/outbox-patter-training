package com.example.demo.order.application;

import com.example.demo.order.domain.Order;
import com.example.demo.order.domain.OrderInput;
import com.example.demo.order.domain.OutboxMessage;

public interface OrderMapper {
    Order toOrder(OrderInput orderInput);

    OutboxMessage toOutbox(OrderInput orderInput);
}
