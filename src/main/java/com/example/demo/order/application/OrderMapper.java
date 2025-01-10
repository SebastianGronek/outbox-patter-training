package com.example.demo.order.application;

import com.example.demo.order.domain.Order;
import com.example.demo.order.domain.OrderInput;

public interface OrderMapper {
        Order toOrder(OrderInput orderInput);
}
