package com.example.store.service;

import com.example.store.dto.CustomerDTO;
import com.example.store.entity.Customer;
import com.example.store.exception.NotFoundException;
import com.example.store.mapper.CustomerMapper;
import com.example.store.repository.CustomerRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceImplTests {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private CustomerServiceImpl customerServiceImpl;

    private Customer customerA;
    private Customer customerB;

    private CustomerDTO customerDTOA;
    private CustomerDTO customerDTOB;

    @BeforeEach
    void setUp() {
        customerA = new Customer();
        customerA.setName("John Doe");
        customerA.setId(1L);

        customerB = new Customer();
        customerB.setName("Jane Doe");
        customerB.setId(2L);

        customerDTOA = new CustomerDTO();
        customerDTOA.setId(customerA.getId());
        customerDTOA.setName(customerA.getName());

        customerDTOB = new CustomerDTO();
        customerDTOB.setId(customerB.getId());
        customerDTOB.setName(customerB.getName());
    }

    @Test
    void testGetAllCustomers() {
        List<Customer> customers = Arrays.asList(customerA, customerB);
        List<CustomerDTO> expectedCustomerDTOs = Arrays.asList(customerDTOA, customerDTOB);

        when(customerRepository.findAll()).thenReturn(customers);
        when(customerMapper.customersToCustomerDTOs(customers)).thenReturn(expectedCustomerDTOs);

        List<CustomerDTO> actualCustomerDTOs = customerServiceImpl.getAllCustomers();

        assertEquals(expectedCustomerDTOs.size(), actualCustomerDTOs.size());
        assertEquals(expectedCustomerDTOs, actualCustomerDTOs);
        verify(customerRepository).findAll();
    }

    @Test
    void testGetCustomerById() {
        when(customerRepository.findById(customerA.getId())).thenReturn(Optional.ofNullable(customerA));
        when(customerMapper.customerToCustomerDTO(customerA)).thenReturn(customerDTOA);

        CustomerDTO actualCustomerDTO = customerServiceImpl.getCustomerById(customerA.getId());

        assertEquals(customerDTOA, actualCustomerDTO);
        verify(customerRepository).findById(customerA.getId());
    }

    @Test
    void testGetCustomerByIdNotFound() {
        Long customerId = customerA.getId();
        String message = String.format("Order with orderId %s not found", customerId);

        when(customerRepository.findById(customerA.getId())).thenThrow(new NotFoundException(message));

        NotFoundException exception =
                assertThrows(NotFoundException.class, () -> customerServiceImpl.getCustomerById(customerId));
        assertEquals(message, exception.getMessage());
    }

    @Test
    void testGetCustomersByNameQuery() {
        String query = "Doe";

        when(customerRepository.findByNameContainsIgnoreCase(query)).thenReturn(List.of(customerA, customerB));
        when(customerMapper.customersToCustomerDTOs(List.of(customerA, customerB)))
                .thenReturn(List.of(customerDTOA, customerDTOB));

        List<CustomerDTO> actualCustomerDTOs = customerServiceImpl.getCustomersByNameQuery(query);

        assertEquals(2, actualCustomerDTOs.size());
        verify(customerRepository).findByNameContainsIgnoreCase(query);
    }

    @Test
    void testGetCustomersByNameQueryNotFound() {
        String query = "Doe";
        String message = String.format("Order with orderId %s not found", query);

        when(customerRepository.findByNameContainsIgnoreCase(query)).thenThrow(new NotFoundException(message));

        NotFoundException exception =
                assertThrows(NotFoundException.class, () -> customerServiceImpl.getCustomersByNameQuery(query));
        assertEquals(message, exception.getMessage());
    }

    @Test
    void testCreateCustomer() {
        when(customerRepository.save(customerA)).thenReturn(customerA);
        when(customerMapper.customerToCustomerDTO(any(Customer.class))).thenReturn(customerDTOA);

        CustomerDTO actualCustomerDTO = customerServiceImpl.createCustomer(customerA);

        assertEquals(customerDTOA, actualCustomerDTO);
        verify(customerRepository).save(customerA);
    }
}
