package com.modules.invoicer.invoice.application;

import com.modules.invoicer.invoice.domain.Invoice;
import com.modules.invoicer.invoice.domain.InvoiceNote;
import com.modules.invoicer.invoice.domain.InvoiceNoteRepository;
import com.modules.invoicer.invoice.domain.InvoiceRepository;
import com.modules.invoicer.user.domain.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InvoiceNoteService {

    private final InvoiceNoteRepository invoiceNoteRepository;
    private final InvoiceRepository invoiceRepository;

    public InvoiceNoteService(InvoiceNoteRepository invoiceNoteRepository,
                              InvoiceRepository invoiceRepository) {
        this.invoiceNoteRepository = invoiceNoteRepository;
        this.invoiceRepository = invoiceRepository;
    }

    @Transactional
    public InvoiceNote addNote(Long invoiceId, String content, User user) {
        Invoice invoice = invoiceRepository.findByIdAndUser(invoiceId, user)
                .orElseThrow(() -> new IllegalArgumentException("Factura no encontrada o no pertenece al usuario."));
        InvoiceNote note = InvoiceNote.builder()
                .invoice(invoice)
                .author(user)
                .content(content)
                .build();
        return invoiceNoteRepository.save(note);
    }

    public List<InvoiceNote> findNotesByInvoice(Invoice invoice) {
        return invoiceNoteRepository.findByInvoiceOrderByCreatedAtDesc(invoice);
    }
}
