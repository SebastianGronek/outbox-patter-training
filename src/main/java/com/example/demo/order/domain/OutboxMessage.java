package com.example.demo.order.domain;

import com.example.demo.order.infrastrucure.OutboxMessageListener;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.UUID;

@Entity
@Table(name = "outbox")
@NoArgsConstructor
@ToString
@Getter
@EntityListeners(OutboxMessageListener.class)
public class OutboxMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    UUID orderId;
    String productName;
    Integer quantity;

    public OutboxMessage(UUID orderId, String productName, Integer quantity) {
        this.orderId = orderId;
        this.productName = productName;
        this.quantity = quantity;
    }
}
