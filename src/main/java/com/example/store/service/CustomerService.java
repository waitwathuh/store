package com.example.store.service;

import com.example.store.dto.CustomerDTO;
import com.example.store.entity.Customer;

import java.util.List;

public interface CustomerService {

    List<CustomerDTO> getAllCustomers();

    CustomerDTO getCustomerById(Long id);

    CustomerDTO createCustomer(Customer customer);

    List<CustomerDTO> getCustomersByNameQuery(String query);
}
