package com.example.store.service;

import com.example.store.dto.OrderDTO;
import com.example.store.entity.Order;

import java.util.List;

public interface OrderService {

    OrderDTO getOrderById(Long orderId);

    List<OrderDTO> getAllOrders();

    OrderDTO createOrder(Order order);
}
