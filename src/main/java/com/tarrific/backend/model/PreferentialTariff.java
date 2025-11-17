package com.tarrific.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "preferential_tariff", schema = "TARIFF")
public class PreferentialTariff {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer prefTariffId;

    @ManyToOne
    @JoinColumn(name = "tariff_id")
    private Tariff tariff;

    @ManyToOne
    @JoinColumn(name = "agreement_id")
    private TradeAgreement agreement;

    private Float preferentialRate;
    private String rateType;
    private Date effectiveDate;
    private Date expiryDate;
}
