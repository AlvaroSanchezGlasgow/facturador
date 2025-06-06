package com.modules.invoicer.invoice.domain;

import com.modules.invoicer.shared.domain.BaseEntity;
import com.modules.invoicer.user.domain.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "invoices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invoice extends BaseEntity {

    @Column(name = "invoice_number", unique = true, nullable = false)
    private String invoiceNumber;

    @NotNull(message = "La fecha de la factura es obligatoria")
    @Column(name = "invoice_date", nullable = false)
    private LocalDate invoiceDate;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<InvoiceItem> items = new ArrayList<>();

    @Column(name = "subtotal", precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "tax_amount", precision = 10, scale = 2)
    private BigDecimal taxAmount;

    @Column(name = "total_amount", precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(name = "status")
    private InvoiceStatus status = InvoiceStatus.DRAFT;

    @Column(name = "verifactu_sent")
    @Builder.Default
    private boolean verifactuSent = false;

    @Column(name = "verifactu_response")
    private String verifactuResponse;

    @Column(name = "notes")
    private String notes;

    public void addItem(InvoiceItem item) {
        items.add(item);
        item.setInvoice(this);
        calculateTotals();
    }

    public void removeItem(InvoiceItem item) {
        items.remove(item);
        item.setInvoice(null);
        calculateTotals();
    }

    public void calculateTotals() {
        subtotal = items.stream()
                .filter(item -> item.getQuantity() != null && item.getUnitPrice() != null)
                .map(InvoiceItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        taxAmount = items.stream()
                .filter(item -> item.getQuantity() != null && item.getUnitPrice() != null)
                .map(InvoiceItem::getTaxAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        totalAmount = subtotal.add(taxAmount);
    }
}
