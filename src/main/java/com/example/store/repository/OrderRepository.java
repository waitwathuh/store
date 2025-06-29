package com.example.store.repository;

import com.example.store.dto.OrderDTO;
import com.example.store.entity.Order;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
    OrderDTO getOrderById(Long id);
}
