package com.tarrific.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "trade_agreement", schema = "TARIFF")
public class TradeAgreement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer agreementId;

    private String name;
    private String description;
    private Date effectiveDate;
    private Date expiryDate;
}
