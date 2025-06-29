package com.example.store.service;

import com.example.store.dto.OrderCustomerDTO;
import com.example.store.dto.OrderDTO;
import com.example.store.entity.Customer;
import com.example.store.entity.Order;
import com.example.store.exception.NotFoundException;
import com.example.store.mapper.OrderMapper;
import com.example.store.repository.OrderRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTests {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderServiceImpl orderServiceImpl;

    private Order orderA;
    private Order orderB;

    private OrderDTO orderDTOA;
    private OrderDTO orderDTOB;

    @BeforeEach
    void setUp() {
        Customer customerA = new Customer();
        customerA.setName("John Doe");
        customerA.setId(1L);

        Customer customerB = new Customer();
        customerB.setName("Jane Doe");
        customerB.setId(2L);

        orderA = new Order();
        orderA.setId(1L);
        orderA.setDescription("Order A");
        orderA.setCustomer(customerA);

        orderB = new Order();
        orderB.setId(2L);
        orderB.setDescription("Order B");
        orderB.setCustomer(customerB);

        OrderCustomerDTO orderCustomerDTOA = new OrderCustomerDTO();
        orderCustomerDTOA.setId(customerA.getId());
        orderCustomerDTOA.setName(customerA.getName());

        OrderCustomerDTO orderCustomerDTOB = new OrderCustomerDTO();
        orderCustomerDTOB.setId(customerB.getId());
        orderCustomerDTOB.setName(customerB.getName());

        orderDTOA = new OrderDTO();
        orderDTOA.setCustomer(orderCustomerDTOA);
        orderDTOA.setId(orderA.getId());
        orderDTOA.setDescription("Order A");

        orderDTOB = new OrderDTO();
        orderDTOB.setCustomer(orderCustomerDTOB);
        orderDTOB.setId(orderB.getId());
        orderDTOB.setDescription("Order B");
    }

    @Test
    void testGetOrderById() {
        when(orderRepository.findById(orderA.getId())).thenReturn(Optional.ofNullable(orderA));
        when(orderMapper.orderToOrderDTO(orderA)).thenReturn(orderDTOA);

        OrderDTO actualOrdersDTO = orderServiceImpl.getOrderById(orderA.getId());

        assertEquals(orderDTOA, actualOrdersDTO);
        verify(orderRepository).findById(orderA.getId());
    }

    @Test
    void testGetOrderByIdNotFound() {
        Long orderId = orderA.getId();
        String message = String.format("Order with orderId %s not found", orderId);

        when(orderRepository.findById(orderId)).thenThrow(new NotFoundException(message));

        NotFoundException exception =
                assertThrows(NotFoundException.class, () -> orderServiceImpl.getOrderById(orderId));
        assertEquals(message, exception.getMessage());
    }

    @Test
    void testGetAllOrders() {
        List<Order> orders = List.of(orderA, orderB);
        List<OrderDTO> expectedOrderDTOs = List.of(orderDTOA, orderDTOB);

        when(orderRepository.findAll()).thenReturn(orders);
        when(orderMapper.ordersToOrderDTOs(orders)).thenReturn(expectedOrderDTOs);

        List<OrderDTO> actualOrderDTOs = orderServiceImpl.getAllOrders();

        assertEquals(expectedOrderDTOs.size(), actualOrderDTOs.size());
        assertEquals(expectedOrderDTOs, actualOrderDTOs);
        verify(orderRepository).findAll();
    }

    @Test
    void testCreateOrder() {
        when(orderRepository.save(orderA)).thenReturn(orderA);
        when(orderMapper.orderToOrderDTO(orderA)).thenReturn(orderDTOA);

        OrderDTO actualOrdersDTO = orderServiceImpl.createOrder(orderA);

        assertEquals(orderDTOA, actualOrdersDTO);
        verify(orderRepository).save(orderA);
    }
}
