package com.modules.invoicer.invoice.application;

import com.modules.invoicer.invoice.domain.Invoice;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;

class VerifactuServiceTest {

    private final VerifactuService service = new VerifactuService();

    @Test
    void sendInvoiceToVerifactuUpdatesInvoice() {
        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber("1");
        boolean result = service.sendInvoiceToVerifactu(invoice);
        assertThat(result).isTrue();
        assertThat(invoice.isVerifactuSent()).isTrue();
        assertThat(invoice.getVerifactuResponse()).isEqualTo("Simulated success");
    }

    @Test
    void sendInvoiceToVerifactuAsyncDelegates() {
        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber("2");
        CompletableFuture<Boolean> future = service.sendInvoiceToVerifactuAsync(invoice);
        assertThat(future.join()).isTrue();
    }
}
