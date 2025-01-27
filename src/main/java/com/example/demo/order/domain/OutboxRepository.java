package com.example.demo.order.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OutboxRepository extends JpaRepository<OutboxMessage, Long> {
}
