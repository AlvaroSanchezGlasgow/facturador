package com.modules.invoicer.invoice.application;

import com.modules.invoicer.invoice.domain.Invoice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class VerifactuService {

    private static final Logger logger = LoggerFactory.getLogger(VerifactuService.class);

    public boolean sendInvoiceToVerifactu(Invoice invoice) {
        logger.info("Sending invoice {} to Verifactu", invoice.getInvoiceNumber());
        // Lógica para generar el XML y enviar a la API de Verifactu
        // Esto implicaría:
        // 1. Generar el XML de la factura según el esquema Verifactu.
        // 2. Firmar digitalmente el XML (requiere configuración de certificados).
        // 3. Realizar la llamada HTTP a la API de Verifactu.
        // 4. Procesar la respuesta y actualizar el estado de la factura.

        System.out.println("Simulando envío de factura a Verifactu: " + invoice.getInvoiceNumber());
        // Aquí iría la implementación real
        invoice.setVerifactuSent(true);
        invoice.setVerifactuResponse("Simulated success");
        logger.info("Invoice {} sent to Verifactu", invoice.getInvoiceNumber());
        return true;
    }

    @Async
    public CompletableFuture<Boolean> sendInvoiceToVerifactuAsync(Invoice invoice) {
        logger.info("Asynchronously sending invoice {} to Verifactu", invoice.getInvoiceNumber());
        return CompletableFuture.completedFuture(sendInvoiceToVerifactu(invoice));
    }
}