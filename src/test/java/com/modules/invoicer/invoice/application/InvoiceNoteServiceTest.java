package com.modules.invoicer.invoice.application;

import com.modules.invoicer.invoice.domain.*;
import com.modules.invoicer.user.domain.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InvoiceNoteServiceTest {

    @Mock
    private InvoiceNoteRepository noteRepository;
    @Mock
    private InvoiceRepository invoiceRepository;
    @InjectMocks
    private InvoiceNoteService noteService;

    @Test
    void addNoteCreatesAndPersistsNote() {
        User user = new User();
        Invoice invoice = new Invoice();
        InvoiceNote saved = new InvoiceNote();
        when(invoiceRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(invoice));
        when(noteRepository.save(any(InvoiceNote.class))).thenReturn(saved);

        InvoiceNote result = noteService.addNote(1L, "hello", user);
        assertThat(result).isEqualTo(saved);
    }

    @Test
    void findNotesByInvoiceDelegatesToRepository() {
        Invoice invoice = new Invoice();
        List<InvoiceNote> notes = List.of(new InvoiceNote());
        when(noteRepository.findByInvoiceOrderByCreatedAtDesc(invoice)).thenReturn(notes);
        assertThat(noteService.findNotesByInvoice(invoice)).isEqualTo(notes);
    }
}
