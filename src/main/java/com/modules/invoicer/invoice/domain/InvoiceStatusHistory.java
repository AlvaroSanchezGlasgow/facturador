package com.modules.invoicer.invoice.domain;

import com.modules.invoicer.shared.domain.BaseEntity;
import com.modules.invoicer.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "invoice_status_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceStatusHistory extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private InvoiceStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User changedBy;
}
