package com.tarrific.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "tariff", schema = "tariff")
public class Tariff {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer tariffId;

    @ManyToOne
    @JoinColumn(name = "hs_code", referencedColumnName = "hsCode")
    private HsCode hsCode;

    private Float baseRate;
    private String rateType;
    private Date effectiveDate;
    private Date expiryDate;

    @OneToMany(mappedBy = "tariff", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TariffOrigin> tariffOrigins = new ArrayList<>();

    @OneToMany(mappedBy = "tariff", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TariffDestination> tariffDestinations = new ArrayList<>();

}
