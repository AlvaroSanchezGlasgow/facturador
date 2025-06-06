package com.modules.invoicer.invoice.application;

import com.modules.invoicer.invoice.domain.Customer;
import com.modules.invoicer.invoice.domain.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.List;

@Service
public class CustomerService {

    final CustomerRepository customerRepository;

    private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);

    @Autowired
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<Customer> findAll() {
        logger.info("Fetching all customers");
        return customerRepository.findAll();
    }

    @Async
    public CompletableFuture<List<Customer>> findAllAsync() {
        logger.info("Fetching all customers asynchronously");
        return CompletableFuture.completedFuture(findAll());
    }

    public Customer save(Customer customer) {
        logger.info("Saving customer with NIF: {}", customer.getNif());
        return customerRepository.save(customer);
    }

    @Async
    public CompletableFuture<Customer> saveAsync(Customer customer) {
        logger.info("Asynchronously saving customer with NIF: {}", customer.getNif());
        return CompletableFuture.completedFuture(save(customer));
    }

}
