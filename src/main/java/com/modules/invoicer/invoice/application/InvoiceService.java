package com.modules.invoicer.invoice.application;

import com.modules.invoicer.invoice.domain.*;
import com.modules.invoicer.user.domain.User;
import com.modules.invoicer.invoice.application.VerifactuService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final CustomerRepository customerRepository;
    private final VerifactuService verifactuService;

    private static final Logger logger = LoggerFactory.getLogger(InvoiceService.class);

    public InvoiceService(InvoiceRepository invoiceRepository, CustomerRepository customerRepository,
                          VerifactuService verifactuService) {
        this.invoiceRepository = invoiceRepository;
        this.customerRepository = customerRepository;
        this.verifactuService = verifactuService;
    }

    @Transactional
    public Invoice createInvoice(Invoice invoice, User user) {
        logger.info("Creating invoice for user: {}", user.getUsername());
        invoice.setUser(user);

        Customer customer = invoice.getCustomer();
        Customer persistentCustomer;
        if (customer.getId() != null) {
            persistentCustomer = customerRepository.findByIdAndUser(customer.getId(), user)
                    .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado o no pertenece al usuario."));
        } else {
            customer.setUser(user);
            persistentCustomer = customerRepository.save(customer);
        }
        invoice.setCustomer(persistentCustomer);

        // Remove items without required information before persisting
        invoice.getItems().removeIf(InvoiceItem::isEmpty);

        // Ensure all remaining items are linked to the invoice instance
        if (!invoice.getItems().isEmpty()) {
            List<InvoiceItem> items = new ArrayList<>(invoice.getItems());
            invoice.getItems().clear();
            items.forEach(invoice::addItem);
        }

        invoice.calculateTotals();
        Invoice saved = invoiceRepository.save(invoice);
        logger.info("Invoice {} created", saved.getId());
        return saved;
    }

    @Async
    public CompletableFuture<Invoice> createInvoiceAsync(Invoice invoice, User user) {
        logger.info("Asynchronously creating invoice");
        return CompletableFuture.completedFuture(createInvoice(invoice, user));
    }

    public List<Invoice> findInvoicesByUser(User user) {
        logger.info("Fetching invoices for user: {}", user.getUsername());
        return invoiceRepository.findByUser(user);
    }

    @Async
    public CompletableFuture<List<Invoice>> findInvoicesByUserAsync(User user) {
        logger.info("Asynchronously fetching invoices for user: {}", user.getUsername());
        return CompletableFuture.completedFuture(findInvoicesByUser(user));
    }

    public Optional<Invoice> findInvoiceByIdAndUser(Long id, User user) {
        logger.info("Finding invoice {} for user: {}", id, user.getUsername());
        return invoiceRepository.findByIdAndUser(id, user);
    }

    @Async
    public CompletableFuture<Optional<Invoice>> findInvoiceByIdAndUserAsync(Long id, User user) {
        logger.info("Asynchronously finding invoice {} for user: {}", id, user.getUsername());
        return CompletableFuture.completedFuture(findInvoiceByIdAndUser(id, user));
    }

    @Transactional
    public Invoice updateInvoice(Invoice updatedInvoice, User user) {
        logger.info("Updating invoice {} for user: {}", updatedInvoice.getId(), user.getUsername());
        return invoiceRepository.findByIdAndUser(updatedInvoice.getId(), user)
                .map(existingInvoice -> {
                    existingInvoice.setInvoiceNumber(updatedInvoice.getInvoiceNumber());
                    existingInvoice.setInvoiceDate(updatedInvoice.getInvoiceDate());
                    existingInvoice.setDueDate(updatedInvoice.getDueDate());
                    // Actualizar cliente si es necesario
                    Customer updatedCustomer = updatedInvoice.getCustomer();
                    updatedCustomer.setUser(user);
                    existingInvoice.setCustomer(customerRepository.save(updatedCustomer));

                    // Actualizar items (lógica para añadir/eliminar/modificar items)
                    existingInvoice.getItems().clear();
                    updatedInvoice.getItems().stream()
                            .filter(item -> !item.isEmpty())
                            .forEach(existingInvoice::addItem);

                    existingInvoice.calculateTotals();
                    Invoice saved = invoiceRepository.save(existingInvoice);
                    logger.info("Invoice {} updated", saved.getId());
                    return saved;
                })
                .orElseThrow(() -> new IllegalArgumentException("Factura no encontrada o no pertenece al usuario."));
    }

    @Async
    public CompletableFuture<Invoice> updateInvoiceAsync(Invoice updatedInvoice, User user) {
        logger.info("Asynchronously updating invoice {}", updatedInvoice.getId());
        return CompletableFuture.completedFuture(updateInvoice(updatedInvoice, user));
    }

    @Transactional
    public void deleteInvoice(Long id, User user) {
        logger.info("Deleting invoice {} for user: {}", id, user.getUsername());
        Invoice invoice = invoiceRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new IllegalArgumentException("Factura no encontrada o no pertenece al usuario."));
        invoiceRepository.delete(invoice);
    }

    @Async
    public CompletableFuture<Void> deleteInvoiceAsync(Long id, User user) {
        logger.info("Asynchronously deleting invoice {}", id);
        deleteInvoice(id, user);
        return CompletableFuture.completedFuture(null);
    }

    @Transactional
    public Customer createOrUpdateCustomer(Customer customer, User user) {
        logger.info("Creating or updating customer {} for user {}", customer.getNif(), user.getUsername());
        customer.setUser(user);
        return customerRepository.save(customer);
    }

    @Async
    public CompletableFuture<Customer> createOrUpdateCustomerAsync(Customer customer, User user) {
        logger.info("Asynchronously creating or updating customer {}", customer.getNif());
        return CompletableFuture.completedFuture(createOrUpdateCustomer(customer, user));
    }

    public List<Customer> findCustomersByUser(User user) {
        logger.info("Fetching customers for user {}", user.getUsername());
        return customerRepository.findByUser(user);
    }

    @Async
    public CompletableFuture<List<Customer>> findCustomersByUserAsync(User user) {
        logger.info("Asynchronously fetching customers for user {}", user.getUsername());
        return CompletableFuture.completedFuture(findCustomersByUser(user));
    }

    public Optional<Customer> findCustomerByIdAndUser(Long id, User user) {
        logger.info("Finding customer {} for user {}", id, user.getUsername());
        return customerRepository.findByIdAndUser(id, user);
    }

    @Async
    public CompletableFuture<Optional<Customer>> findCustomerByIdAndUserAsync(Long id, User user) {
        logger.info("Asynchronously finding customer {} for user {}", id, user.getUsername());
        return CompletableFuture.completedFuture(findCustomerByIdAndUser(id, user));
    }

    @Transactional
    public void deleteCustomer(Long id, User user) {
        logger.info("Deleting customer {} for user {}", id, user.getUsername());
        Customer customer = customerRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado o no pertenece al usuario."));
        customerRepository.delete(customer);
    }

    @Async
    public CompletableFuture<Void> deleteCustomerAsync(Long id, User user) {
        logger.info("Asynchronously deleting customer {}", id);
        deleteCustomer(id, user);
        return CompletableFuture.completedFuture(null);
    }

    public long countPendingInvoices(User user) {
        logger.info("Counting pending invoices for user {}", user.getUsername());
        return invoiceRepository.countByUserAndStatus(user, InvoiceStatus.PENDING);
    }

    @Async
    public CompletableFuture<Long> countPendingInvoicesAsync(User user) {
        logger.info("Asynchronously counting pending invoices for user {}", user.getUsername());
        return CompletableFuture.completedFuture(countPendingInvoices(user));
    }

    public long countPaidInvoices(User user) {
        logger.info("Counting paid invoices for user {}", user.getUsername());
        return invoiceRepository.countByUserAndStatus(user, InvoiceStatus.PAID);
    }

    @Async
    public CompletableFuture<Long> countPaidInvoicesAsync(User user) {
        logger.info("Asynchronously counting paid invoices for user {}", user.getUsername());
        return CompletableFuture.completedFuture(countPaidInvoices(user));
    }

    public java.util.Map<InvoiceStatus, Long> countInvoicesByStatus(User user) {
        logger.info("Counting invoices by status for user {}", user.getUsername());
        java.util.Map<InvoiceStatus, Long> counts = new java.util.EnumMap<>(InvoiceStatus.class);
        for (InvoiceStatus status : InvoiceStatus.values()) {
            counts.put(status, invoiceRepository.countByUserAndStatus(user, status));
        }
        return counts;
    }

    @Async
    public CompletableFuture<java.util.Map<InvoiceStatus, Long>> countInvoicesByStatusAsync(User user) {
        logger.info("Asynchronously counting invoices by status for user {}", user.getUsername());
        return CompletableFuture.completedFuture(countInvoicesByStatus(user));
    }

    public long countCustomers(User user) {
        logger.info("Counting customers for user {}", user.getUsername());
        return customerRepository.countByUser(user);
    }

    @Async
    public CompletableFuture<Long> countCustomersAsync(User user) {
        logger.info("Asynchronously counting customers for user {}", user.getUsername());
        return CompletableFuture.completedFuture(countCustomers(user));
    }

    public List<Invoice> findLatestInvoices(User user) {
        logger.info("Fetching latest invoices for user {}", user.getUsername());
        return invoiceRepository.findTop5ByUserOrderByInvoiceDateDesc(user);
    }

    @Async
    public CompletableFuture<List<Invoice>> findLatestInvoicesAsync(User user) {
        logger.info("Asynchronously fetching latest invoices for user {}", user.getUsername());
        return CompletableFuture.completedFuture(findLatestInvoices(user));
    }

    @Transactional
    public Invoice sendInvoiceToVerifactu(Long id, User user) {
        logger.info("Sending invoice {} to Verifactu for user {}", id, user.getUsername());
        Invoice invoice = invoiceRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new IllegalArgumentException("Factura no encontrada o no pertenece al usuario."));

        if (invoice.isVerifactuSent()) {
            return invoice;
        }

        invoice.setStatus(InvoiceStatus.PENDING_VERIFACTU);
        invoiceRepository.save(invoice);

        boolean sent = verifactuService.sendInvoiceToVerifactu(invoice);
        if (sent) {
            invoice.setStatus(InvoiceStatus.SENT_VERIFACTU);
        }
        return invoiceRepository.save(invoice);
    }

    /**
     * Update the status of an existing invoice.
     *
     * @param id     the invoice id
     * @param status the new status
     * @param user   the authenticated user
     * @return the updated invoice
     */
    @Transactional
    public Invoice updateInvoiceStatus(Long id, InvoiceStatus status, User user) {
        logger.info("Updating status of invoice {} to {} for user {}", id, status, user.getUsername());
        Invoice invoice = invoiceRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new IllegalArgumentException("Factura no encontrada o no pertenece al usuario."));
        invoice.setStatus(status);
        return invoiceRepository.save(invoice);
    }
}