package com.modules.invoicer.invoice.domain;

import com.modules.invoicer.shared.domain.BaseEntity;
import com.modules.invoicer.user.domain.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "customers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer extends BaseEntity {

    @NotBlank(message = "El nombre del cliente es obligatorio")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "El NIF/CIF es obligatorio")
    @Column(name = "tax_id", nullable = false)
    private String taxId;

    @Email(message = "El formato del email no es válido")
    private String email;

    private String phone;

    private String address;

    private String city;

    @Column(name = "postal_code")
    private String postalCode;

    private String province;

    private String country;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
