package com.modules.invoicer.invoice.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceStatusHistoryRepository extends JpaRepository<InvoiceStatusHistory, Long> {
    List<InvoiceStatusHistory> findByInvoiceOrderByCreatedAtAsc(Invoice invoice);
}
