package com.example.store.service;

import com.example.store.dto.CustomerDTO;
import com.example.store.entity.Customer;
import com.example.store.exception.NotFoundException;
import com.example.store.mapper.CustomerMapper;
import com.example.store.repository.CustomerRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Override
    public List<CustomerDTO> getAllCustomers() {
        return customerMapper.customersToCustomerDTOs(customerRepository.findAll());
    }

    @Override
    public CustomerDTO getCustomerById(Long customerId) {
        Optional<Customer> customerDTO = customerRepository.findById(customerId);

        if (customerDTO.isEmpty()) {
            String message = String.format("No customer with id '%s' was found", customerId);
            log.warn(message);
            throw new NotFoundException(message);
        } else {
            return customerMapper.customerToCustomerDTO(customerDTO.get());
        }
    }

    @Override
    public CustomerDTO createCustomer(Customer customer) {
        return customerMapper.customerToCustomerDTO(customerRepository.save(customer));
    }

    @Override
    public List<CustomerDTO> getCustomersByNameQuery(String query) {
        List<Customer> customerList = customerRepository.findByNameContainsIgnoreCase(query);

        if (customerList.isEmpty()) {
            String message = String.format("No customer name with query '%s' was found", query);
            log.warn(message);
            throw new NotFoundException(message);
        } else {
            return customerMapper.customersToCustomerDTOs(customerList);
        }
    }
}
