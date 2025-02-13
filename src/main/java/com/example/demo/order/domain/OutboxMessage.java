package com.example.demo.order.domain;

import com.example.demo.order.infrastructure.OutboxMessageListener;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.UUID;

@Entity
@Table(name = "outbox")
@NoArgsConstructor
@ToString
@Data
@EntityListeners(OutboxMessageListener.class)
public class OutboxMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(unique = true)
    UUID orderId;
    String productName;
    Integer quantity;
    Boolean wasWrittenToDisc = false;

    public OutboxMessage(UUID orderId, String productName, Integer quantity) {
        this.orderId = orderId;
        this.productName = productName;
        this.quantity = quantity;
    }
}
