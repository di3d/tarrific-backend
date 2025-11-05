package com.tarrific.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "trade_agreement", schema = "tariff")
public class TradeAgreement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "agreement_id", nullable = false)
    private Integer id;

    @Size(max = 150)
    @NotNull
    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Lob
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "effective_date")
    private LocalDate effectiveDate;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

}