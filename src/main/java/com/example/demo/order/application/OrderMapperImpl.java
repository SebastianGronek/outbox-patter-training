package com.example.demo.order.application;

import com.example.demo.order.domain.Order;
import com.example.demo.order.domain.OrderInput;
import org.springframework.stereotype.Service;

@Service
class OrderMapperImpl implements OrderMapper {
    @Override
    public Order toOrder(OrderInput orderInput) {
        return new Order(orderInput.orderID(), orderInput.productName(), orderInput.quantity());
    }
}
