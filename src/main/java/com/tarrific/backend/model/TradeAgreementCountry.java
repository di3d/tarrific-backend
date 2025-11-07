package com.tarrific.backend.model;

import jakarta.persistence.*;
import lombok.*;

@Getter @Setter
@Entity @Table(name = "trade_agreement_country")
public class TradeAgreementCountry {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional=false) @JoinColumn(name="agreement_id")
    private TradeAgreement agreement;

    @ManyToOne(optional=false) @JoinColumn(name="country_id")
    private Country country;
}
