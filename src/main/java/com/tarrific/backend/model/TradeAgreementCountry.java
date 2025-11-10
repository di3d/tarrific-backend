package com.tarrific.backend.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "trade_agreement_country")
public class TradeAgreementCountry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // changed from Long → Integer

    @ManyToOne(optional = false)
    @JoinColumn(name = "agreement_id", referencedColumnName = "agreementId")
    private TradeAgreement agreement;

    @ManyToOne(optional = false)
    @JoinColumn(name = "country_id", referencedColumnName = "countryId")
    private Country country;
}

