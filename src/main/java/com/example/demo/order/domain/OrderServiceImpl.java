package com.example.demo.order.domain;

import com.example.demo.order.application.OrderMapper;
import com.example.demo.order.domain.file.FileServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final FileServiceImpl fileService;

    @Override
    public void handleOrder(OrderInput orderInput) {
        log.info("Handling order with orderId: {}, product name: {}, quantity: {}", orderInput.orderId(), orderInput.productName(), orderInput.quantity());
        orderRepository.save(orderMapper.toOrder(orderInput));
        log.info("Order saved");
        fileService.writeFile(orderInput);
    }

    @Override
    public OrderDto getOrderByOrderId(UUID orderId) {
        log.info("Getting order with orderId: {}", orderId);
        Order orderByOrderId = orderRepository.findOrderByOrderId(orderId);
        log.info("Found order: {}", orderByOrderId);
        return OrderDto.fromOrder(orderByOrderId);
    }

}
