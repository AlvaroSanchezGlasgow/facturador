package com.modules.invoicer.invoice.application;

import com.modules.invoicer.invoice.domain.Customer;
import com.modules.invoicer.invoice.domain.Invoice;
import com.modules.invoicer.invoice.domain.InvoiceItem;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class InvoicePdfServiceTest {

    private final InvoicePdfService pdfService = new InvoicePdfService();

    @Test
    void generatesPdfBytes() {
        Invoice invoice = Invoice.builder()
                .invoiceNumber("TEST-1")
                .invoiceDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(30))
                .customer(Customer.builder().name("Cliente").build())
                .items(Collections.singletonList(InvoiceItem.builder()
                        .description("Servicio")
                        .quantity(new BigDecimal("1"))
                        .unitPrice(new BigDecimal("100"))
                        .build()))
                .build();
        invoice.calculateTotals();

        byte[] data = pdfService.generateInvoicePdf(invoice);
        assertThat(data).isNotEmpty();
    }
}
