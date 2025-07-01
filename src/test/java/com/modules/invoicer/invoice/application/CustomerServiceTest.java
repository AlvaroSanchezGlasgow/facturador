package com.modules.invoicer.invoice.application;

import com.modules.invoicer.invoice.domain.Customer;
import com.modules.invoicer.invoice.domain.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;
    @InjectMocks
    private CustomerService customerService;

    @Test
    void findAllDelegatesToRepository() {
        List<Customer> customers = List.of(new Customer());
        when(customerRepository.findAll()).thenReturn(customers);
        assertThat(customerService.findAll()).isEqualTo(customers);
    }

    @Test
    void findAllAsyncDelegatesToRepository() {
        when(customerRepository.findAll()).thenReturn(Collections.emptyList());
        CompletableFuture<List<Customer>> future = customerService.findAllAsync();
        assertThat(future.join()).isEmpty();
    }

    @Test
    void savePersistsCustomer() {
        Customer customer = new Customer();
        when(customerRepository.save(any(Customer.class))).thenAnswer(inv -> inv.getArgument(0));
        assertThat(customerService.save(customer)).isEqualTo(customer);
    }

    @Test
    void saveAsyncPersistsCustomer() {
        Customer customer = new Customer();
        when(customerRepository.save(any(Customer.class))).thenAnswer(inv -> inv.getArgument(0));
        CompletableFuture<Customer> future = customerService.saveAsync(customer);
        assertThat(future.join()).isEqualTo(customer);
    }
}
