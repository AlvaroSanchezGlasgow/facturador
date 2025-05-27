package com.modules.invoicer.invoice.application;

import com.modules.invoicer.invoice.domain.*;
import com.modules.invoicer.user.domain.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public List<Invoice> findInvoicesByUser(User user) {
        return invoiceRepository.findByUser(user);
    }

    public Optional<Invoice> findInvoiceByIdAndUser(Long id, User user) {
        return invoiceRepository.findByIdAndUser(id, user);
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

    @Transactional
    public void deleteInvoice(Long id, User user) {
        Invoice invoice = invoiceRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new IllegalArgumentException("Factura no encontrada o no pertenece al usuario."));
        invoiceRepository.delete(invoice);
    }

    @Transactional
    public Customer createOrUpdateCustomer(Customer customer, User user) {
        customer.setUser(user);
        return customerRepository.save(customer);
    }

    public List<Customer> findCustomersByUser(User user) {
        return customerRepository.findByUser(user);
    }

    public Optional<Customer> findCustomerByIdAndUser(Long id, User user) {
        return customerRepository.findByIdAndUser(id, user);
    }

    @Transactional
    public void deleteCustomer(Long id, User user) {
        Customer customer = customerRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado o no pertenece al usuario."));
        customerRepository.delete(customer);
    }

    public long countPendingInvoices(User user) {
        return invoiceRepository.countByUserAndStatus(user, InvoiceStatus.PENDING);
    }

    public long countPaidInvoices(User user) {
        return invoiceRepository.countByUserAndStatus(user, InvoiceStatus.PAID);
    }

    public long countCustomers(User user) {
        return customerRepository.countByUser(user);
    }

    public List<Invoice> findLatestInvoices(User user) {
        return invoiceRepository.findTop5ByUserOrderByInvoiceDateDesc(user);
    }
}