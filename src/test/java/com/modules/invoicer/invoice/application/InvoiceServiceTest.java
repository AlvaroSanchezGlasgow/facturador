package com.modules.invoicer.invoice.application;

import com.modules.invoicer.invoice.domain.Customer;
import com.modules.invoicer.invoice.domain.CustomerRepository;
import com.modules.invoicer.invoice.domain.Invoice;
import com.modules.invoicer.invoice.domain.InvoiceItem;
import com.modules.invoicer.invoice.domain.InvoiceRepository;
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

@ExtendWith(MockitoExtension.class)
class InvoiceServiceTest {

    @Mock
    private InvoiceRepository invoiceRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private VerifactuService verifactuService;
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
    }
}
