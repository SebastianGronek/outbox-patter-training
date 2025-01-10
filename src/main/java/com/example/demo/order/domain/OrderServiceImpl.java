package com.example.demo.order.domain;

import com.example.demo.order.application.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
class OrderServiceImpl implements OrderService {
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    OrderMapper orderMapper;

    @Override
    public void handleOrder(OrderInput orderInput) {
        log.info("Handling order with orderId: {}, product name: {}, quantity: {}", orderInput.orderID(), orderInput.productName(), orderInput.quantity());
        orderRepository.save(orderMapper.toOrder(orderInput));
    }

    @Override
    public Order getOrderByOrderId(UUID orderId) {
        log.info("Getting order with orderId: {}", orderId);
        return orderRepository.findOrderByOrderId(orderId);
    }

}
