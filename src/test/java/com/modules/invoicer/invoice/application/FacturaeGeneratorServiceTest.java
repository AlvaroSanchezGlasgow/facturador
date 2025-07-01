package com.modules.invoicer.invoice.application;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;

class FacturaeGeneratorServiceTest {

    private final FacturaeGeneratorService service = new FacturaeGeneratorService();

    @Test
    void generateFacturaeXmlReturnsContent() {
        String xml = service.generateFacturaeXml(new Object());
        assertThat(xml).contains("<Facturae>");
    }

    @Test
    void generateFacturaeXmlAsyncDelegates() {
        CompletableFuture<String> future = service.generateFacturaeXmlAsync(new Object());
        assertThat(future.join()).contains("<Facturae>");
    }
}
