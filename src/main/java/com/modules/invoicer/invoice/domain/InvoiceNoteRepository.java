package com.modules.invoicer.invoice.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceNoteRepository extends JpaRepository<InvoiceNote, Long> {
    List<InvoiceNote> findByInvoiceOrderByCreatedAtDesc(Invoice invoice);
}
