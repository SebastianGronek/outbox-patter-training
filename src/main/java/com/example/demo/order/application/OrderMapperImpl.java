package com.example.demo.order.application;

import com.example.demo.order.domain.Order;
import com.example.demo.order.domain.OrderInput;
import com.example.demo.order.domain.OutboxMessage;
import org.springframework.stereotype.Service;

@Service
class OrderMapperImpl implements OrderMapper {
    @Override
    public Order toOrder(OrderInput orderInput) {
        return new Order(orderInput.orderId(), orderInput.productName(), orderInput.quantity());
    }

    @Override
    public OutboxMessage toOutbox(OrderInput orderInput) {
        return new OutboxMessage(orderInput.orderId(), orderInput.productName(), orderInput.quantity());
    }
}
