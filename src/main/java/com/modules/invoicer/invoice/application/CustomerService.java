package com.modules.invoicer.invoice.application;

import com.modules.invoicer.invoice.domain.Customer;
import com.modules.invoicer.invoice.domain.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.List;

@Service
public class CustomerService {

    final CustomerRepository customerRepository;

    @Autowired
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    @Async
    public CompletableFuture<List<Customer>> findAllAsync() {
        return CompletableFuture.completedFuture(findAll());
    }

    public Customer save(Customer customer) {
        return customerRepository.save(customer);
    }

    @Async
    public CompletableFuture<Customer> saveAsync(Customer customer) {
        return CompletableFuture.completedFuture(save(customer));
    }

}
