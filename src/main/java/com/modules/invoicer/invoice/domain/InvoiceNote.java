package com.modules.invoicer.invoice.domain;

import com.modules.invoicer.shared.domain.BaseEntity;
import com.modules.invoicer.user.domain.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "invoice_notes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceNote extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User author;

    @NotBlank(message = "El contenido de la nota es obligatorio")
    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;
}
