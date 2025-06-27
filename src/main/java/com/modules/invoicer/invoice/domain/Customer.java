package com.modules.invoicer.invoice.domain;

import com.modules.invoicer.shared.domain.BaseEntity;
import com.modules.invoicer.user.domain.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

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

    @Column(name = "surname")
    private String surname;


    @Column(name = "company_name")
    private String company_name;

    @NotBlank(message = "El NIF/CIF es obligatorio")
    @Column(name = "nif", nullable = false)
    private String nif;

    @Email(message = "El formato del email no es válido")
    @NotBlank(message = "El email del cliente es obligatorio")
    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "address")
    private String address;

    @Column(name = "city")
    private String city;

    @Column(name = "postal_code")
    private String postalCode;

    @Column(name = "province")
    private String province;

    @Column(name = "country")
    private String country;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}
