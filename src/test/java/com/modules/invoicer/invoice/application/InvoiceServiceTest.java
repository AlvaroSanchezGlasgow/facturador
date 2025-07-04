package com.modules.invoicer.invoice.application;

import com.modules.invoicer.invoice.domain.Customer;
import com.modules.invoicer.invoice.domain.CustomerRepository;
import com.modules.invoicer.invoice.domain.Invoice;
import com.modules.invoicer.invoice.domain.InvoiceItem;
import com.modules.invoicer.invoice.domain.InvoiceRepository;
import com.modules.invoicer.invoice.domain.InvoiceStatusHistoryRepository;
import com.modules.invoicer.invoice.domain.InvoiceStatusHistory;
import com.modules.invoicer.invoice.application.VerifactuService;
import com.modules.invoicer.user.domain.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.atLeastOnce;

@ExtendWith(MockitoExtension.class)
class InvoiceServiceTest {

    @Mock
    private InvoiceRepository invoiceRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private VerifactuService verifactuService;
    @Mock
    private InvoiceStatusHistoryRepository statusHistoryRepository;
    @InjectMocks
    private InvoiceService invoiceService;

    @Test
    void createInvoiceAssociatesUserAndSavesEntities() {
        User user = new User();
        Customer customer = new Customer();
        Invoice invoice = Invoice.builder()
                .customer(customer)
                .items(new ArrayList<>())
                .build();
        invoice.addItem(InvoiceItem.builder()
                .description("item")
                .quantity(new BigDecimal("1"))
                .unitPrice(new BigDecimal("10.00"))
                .build());

        when(customerRepository.save(any(Customer.class))).thenAnswer(inv -> inv.getArgument(0));
        when(invoiceRepository.save(any(Invoice.class))).thenAnswer(inv -> inv.getArgument(0));

        Invoice result = invoiceService.createInvoice(invoice, user);

        assertThat(result.getUser()).isEqualTo(user);
        assertThat(result.getCustomer().getUser()).isEqualTo(user);
        assertThat(result.getSubtotal()).isEqualByComparingTo("10.00");
        verify(customerRepository).save(customer);
        verify(invoiceRepository).save(invoice);
        verify(statusHistoryRepository).save(any(InvoiceStatusHistory.class));
    }

    @Test
    void createInvoiceIgnoresEmptyItems() {
        User user = new User();
        Customer customer = new Customer();
        Invoice invoice = Invoice.builder()
                .customer(customer)
                .items(new ArrayList<>())
                .build();
        invoice.addItem(new InvoiceItem()); // empty item should be ignored
        invoice.addItem(InvoiceItem.builder()
                .description("item")
                .quantity(new BigDecimal("2"))
                .unitPrice(new BigDecimal("5.00"))
                .build());

        when(customerRepository.save(any(Customer.class))).thenAnswer(inv -> inv.getArgument(0));
        when(invoiceRepository.save(any(Invoice.class))).thenAnswer(inv -> inv.getArgument(0));

        Invoice result = invoiceService.createInvoice(invoice, user);

        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getSubtotal()).isEqualByComparingTo("10.00");
        verify(statusHistoryRepository).save(any(InvoiceStatusHistory.class));
    }

    @Test
    void sendInvoiceUpdatesStatusAndCallsService() {
        User user = new User();
        Invoice invoice = new Invoice();
        invoice.setId(1L);

        when(invoiceRepository.findByIdAndUser(1L, user)).thenReturn(java.util.Optional.of(invoice));
        when(invoiceRepository.save(any(Invoice.class))).thenAnswer(inv -> inv.getArgument(0));
        when(verifactuService.sendInvoiceToVerifactu(invoice)).thenReturn(true);

        Invoice result = invoiceService.sendInvoiceToVerifactu(1L, user);

        assertThat(result.isVerifactuSent()).isTrue();
        assertThat(result.getStatus()).isEqualTo(com.modules.invoicer.invoice.domain.InvoiceStatus.SENT_VERIFACTU);
        verify(verifactuService).sendInvoiceToVerifactu(invoice);
        verify(statusHistoryRepository, org.mockito.Mockito.atLeastOnce()).save(any(InvoiceStatusHistory.class));
    }

    @Test
    void updateInvoiceStatusPersistsChange() {
        User user = new User();
        Invoice invoice = new Invoice();
        invoice.setId(2L);
        invoice.setStatus(com.modules.invoicer.invoice.domain.InvoiceStatus.DRAFT);

        when(invoiceRepository.findByIdAndUser(2L, user)).thenReturn(java.util.Optional.of(invoice));
        when(invoiceRepository.save(any(Invoice.class))).thenAnswer(inv -> inv.getArgument(0));

        Invoice result = invoiceService.updateInvoiceStatus(2L, com.modules.invoicer.invoice.domain.InvoiceStatus.PAID, user);

        assertThat(result.getStatus()).isEqualTo(com.modules.invoicer.invoice.domain.InvoiceStatus.PAID);
        verify(invoiceRepository).save(invoice);
        verify(statusHistoryRepository).save(any(InvoiceStatusHistory.class));
    }

    @Test
    void getInvoiceStatusHistoryDelegatesToRepository() {
        User user = new User();
        Invoice invoice = new Invoice();
        java.util.List<InvoiceStatusHistory> history = java.util.List.of(new InvoiceStatusHistory());
        when(invoiceRepository.findByIdAndUser(5L, user)).thenReturn(java.util.Optional.of(invoice));
        when(statusHistoryRepository.findByInvoiceOrderByCreatedAtAsc(invoice)).thenReturn(history);
        assertThat(invoiceService.getInvoiceStatusHistory(5L, user)).isEqualTo(history);
    }

    @Test
    void asyncWrappersDelegateToSyncMethods() {
        User user = new User();
        when(invoiceRepository.findByUser(user)).thenReturn(java.util.List.of());
        assertThat(invoiceService.findInvoicesByUserAsync(user).join()).isEmpty();

        when(invoiceRepository.findByIdAndUser(3L, user)).thenReturn(java.util.Optional.empty());
        assertThat(invoiceService.findInvoiceByIdAndUserAsync(3L, user).join()).isEmpty();

        when(invoiceRepository.countByUserAndStatus(user, com.modules.invoicer.invoice.domain.InvoiceStatus.PENDING)).thenReturn(2L);
        assertThat(invoiceService.countPendingInvoicesAsync(user).join()).isEqualTo(2L);

        Invoice invoice = new Invoice();
        java.util.List<InvoiceStatusHistory> history = java.util.List.of();
        when(invoiceRepository.findByIdAndUser(4L, user)).thenReturn(java.util.Optional.of(invoice));
        when(statusHistoryRepository.findByInvoiceOrderByCreatedAtAsc(invoice)).thenReturn(history);
        assertThat(invoiceService.getInvoiceStatusHistoryAsync(4L, user).join()).isEqualTo(history);
    }
}
