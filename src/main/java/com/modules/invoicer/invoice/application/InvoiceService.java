package com.modules.invoicer.invoice.application;

import com.modules.invoicer.invoice.domain.*;
import com.modules.invoicer.user.domain.User;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;

import java.util.List;
import java.util.Optional;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final CustomerRepository customerRepository;

    public InvoiceService(InvoiceRepository invoiceRepository, CustomerRepository customerRepository) {
        this.invoiceRepository = invoiceRepository;
        this.customerRepository = customerRepository;
    }

    @Transactional
    public Invoice createInvoice(Invoice invoice, User user) {
        invoice.setUser(user);
        invoice.getCustomer().setUser(user); // Asegura que el cliente también esté asociado al usuario
        Customer savedCustomer = customerRepository.save(invoice.getCustomer());
        invoice.setCustomer(savedCustomer);
        invoice.calculateTotals();
        return invoiceRepository.save(invoice);
    }

    @Async
    public CompletableFuture<Invoice> createInvoiceAsync(Invoice invoice, User user) {
        return CompletableFuture.completedFuture(createInvoice(invoice, user));
    }

    public List<Invoice> findInvoicesByUser(User user) {
        return invoiceRepository.findByUser(user);
    }

    @Async
    public CompletableFuture<List<Invoice>> findInvoicesByUserAsync(User user) {
        return CompletableFuture.completedFuture(findInvoicesByUser(user));
    }

    public Optional<Invoice> findInvoiceByIdAndUser(Long id, User user) {
        return invoiceRepository.findByIdAndUser(id, user);
    }

    @Async
    public CompletableFuture<Optional<Invoice>> findInvoiceByIdAndUserAsync(Long id, User user) {
        return CompletableFuture.completedFuture(findInvoiceByIdAndUser(id, user));
    }

    @Transactional
    public Invoice updateInvoice(Invoice updatedInvoice, User user) {
        return invoiceRepository.findByIdAndUser(updatedInvoice.getId(), user)
                .map(existingInvoice -> {
                    existingInvoice.setInvoiceNumber(updatedInvoice.getInvoiceNumber());
                    existingInvoice.setInvoiceDate(updatedInvoice.getInvoiceDate());
                    existingInvoice.setDueDate(updatedInvoice.getDueDate());
                    // Actualizar cliente si es necesario
                    Customer updatedCustomer = updatedInvoice.getCustomer();
                    updatedCustomer.setUser(user);
                    existingInvoice.setCustomer(customerRepository.save(updatedCustomer));

                    // Actualizar items (lógica más compleja para añadir/eliminar/modificar items)
                    existingInvoice.getItems().clear();
                    updatedInvoice.getItems().forEach(existingInvoice::addItem);

                    existingInvoice.calculateTotals();
                    return invoiceRepository.save(existingInvoice);
                })
                .orElseThrow(() -> new IllegalArgumentException("Factura no encontrada o no pertenece al usuario."));
    }

    @Async
    public CompletableFuture<Invoice> updateInvoiceAsync(Invoice updatedInvoice, User user) {
        return CompletableFuture.completedFuture(updateInvoice(updatedInvoice, user));
    }

    @Transactional
    public void deleteInvoice(Long id, User user) {
        Invoice invoice = invoiceRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new IllegalArgumentException("Factura no encontrada o no pertenece al usuario."));
        invoiceRepository.delete(invoice);
    }

    @Async
    public CompletableFuture<Void> deleteInvoiceAsync(Long id, User user) {
        deleteInvoice(id, user);
        return CompletableFuture.completedFuture(null);
    }

    @Transactional
    public Customer createOrUpdateCustomer(Customer customer, User user) {
        customer.setUser(user);
        return customerRepository.save(customer);
    }

    @Async
    public CompletableFuture<Customer> createOrUpdateCustomerAsync(Customer customer, User user) {
        return CompletableFuture.completedFuture(createOrUpdateCustomer(customer, user));
    }

    public List<Customer> findCustomersByUser(User user) {
        return customerRepository.findByUser(user);
    }

    @Async
    public CompletableFuture<List<Customer>> findCustomersByUserAsync(User user) {
        return CompletableFuture.completedFuture(findCustomersByUser(user));
    }

    public Optional<Customer> findCustomerByIdAndUser(Long id, User user) {
        return customerRepository.findByIdAndUser(id, user);
    }

    @Async
    public CompletableFuture<Optional<Customer>> findCustomerByIdAndUserAsync(Long id, User user) {
        return CompletableFuture.completedFuture(findCustomerByIdAndUser(id, user));
    }

    @Transactional
    public void deleteCustomer(Long id, User user) {
        Customer customer = customerRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado o no pertenece al usuario."));
        customerRepository.delete(customer);
    }

    @Async
    public CompletableFuture<Void> deleteCustomerAsync(Long id, User user) {
        deleteCustomer(id, user);
        return CompletableFuture.completedFuture(null);
    }

    public long countPendingInvoices(User user) {
        return invoiceRepository.countByUserAndStatus(user, InvoiceStatus.PENDING);
    }

    @Async
    public CompletableFuture<Long> countPendingInvoicesAsync(User user) {
        return CompletableFuture.completedFuture(countPendingInvoices(user));
    }

    public long countPaidInvoices(User user) {
        return invoiceRepository.countByUserAndStatus(user, InvoiceStatus.PAID);
    }

    @Async
    public CompletableFuture<Long> countPaidInvoicesAsync(User user) {
        return CompletableFuture.completedFuture(countPaidInvoices(user));
    }

    public long countCustomers(User user) {
        return customerRepository.countByUser(user);
    }

    @Async
    public CompletableFuture<Long> countCustomersAsync(User user) {
        return CompletableFuture.completedFuture(countCustomers(user));
    }

    public List<Invoice> findLatestInvoices(User user) {
        return invoiceRepository.findTop5ByUserOrderByInvoiceDateDesc(user);
    }

    @Async
    public CompletableFuture<List<Invoice>> findLatestInvoicesAsync(User user) {
        return CompletableFuture.completedFuture(findLatestInvoices(user));
    }
}