package com.modules.invoicer.invoice.domain;

import com.modules.invoicer.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findByUser(User user);
    Optional<Invoice> findByInvoiceNumberAndUser(String invoiceNumber, User user);
    Optional<Invoice> findByIdAndUser(Long id, User user);
    long countByUserAndStatus(User user, InvoiceStatus status);
    List<Invoice> findTop5ByUserOrderByInvoiceDateDesc(User user);
}
