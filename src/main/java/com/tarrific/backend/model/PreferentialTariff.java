package com.tarrific.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "preferential_tariff", schema = "tariff")
public class PreferentialTariff {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pref_tariff_id", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "tariff_id", nullable = false)
    private Tariff tariff;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "agreement_id", nullable = false)
    private TradeAgreement agreement;

    @NotNull
    @Column(name = "preferential_rate", nullable = false)
    private Float preferentialRate;

    @Size(max = 50)
    @Column(name = "rate_type", length = 50)
    private String rateType;

    @Column(name = "effective_date")
    private LocalDate effectiveDate;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

}