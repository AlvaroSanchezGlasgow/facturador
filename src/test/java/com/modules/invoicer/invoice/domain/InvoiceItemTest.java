package com.modules.invoicer.invoice.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class InvoiceItemTest {

    @Test
    void calculatesSubtotalTaxAndTotal() {
        InvoiceItem item = InvoiceItem.builder()
                .description("Test")
                .quantity(new BigDecimal("2"))
                .unitPrice(new BigDecimal("5.00"))
                .taxRate(new BigDecimal("10.00"))
                .build();

        assertThat(item.getSubtotal()).isEqualByComparingTo("10.00");
        assertThat(item.getTaxAmount()).isEqualByComparingTo("1.00");
        assertThat(item.getTotal()).isEqualByComparingTo("11.00");
    }

    @Test
    void subtotalIsZeroWhenQuantityIsNull() {
        InvoiceItem item = InvoiceItem.builder()
                .description("Test")
                .unitPrice(new BigDecimal("5.00"))
                .taxRate(new BigDecimal("21.00"))
                .build();

        assertThat(item.getSubtotal()).isEqualByComparingTo("0.00");
        assertThat(item.getTaxAmount()).isEqualByComparingTo("0.00");
    }
}
