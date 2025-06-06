package com.modules.invoicer.invoice.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class InvoiceTest {

    @Test
    void calculateTotalsAggregatesItems() {
        Invoice invoice = new Invoice();
        invoice.addItem(InvoiceItem.builder()
                .description("A")
                .quantity(new BigDecimal("1"))
                .unitPrice(new BigDecimal("10.00"))
                .taxRate(new BigDecimal("10.00"))
                .build());
        invoice.addItem(InvoiceItem.builder()
                .description("B")
                .quantity(new BigDecimal("2"))
                .unitPrice(new BigDecimal("20.00"))
                .taxRate(new BigDecimal("10.00"))
                .build());

        invoice.calculateTotals();

        assertThat(invoice.getSubtotal()).isEqualByComparingTo("50.00");
        assertThat(invoice.getTaxAmount()).isEqualByComparingTo("5.00");
        assertThat(invoice.getTotalAmount()).isEqualByComparingTo("55.00");
    }
}
