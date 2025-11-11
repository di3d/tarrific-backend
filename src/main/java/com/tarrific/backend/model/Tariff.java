package com.tarrific.backend.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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

    // === Tariff values ===
    @Column(name = "base_rate")
    private Float baseRate;            // % value for ad valorem tariffs

    @Column(name = "rate_type")
    private String rateType;           // "Ad Valorem", "Specific", or "Mixed"

    @Column(name = "specific_rate")
    private Float specificRate;        // numeric rate for specific tariffs (e.g. 0.25)

    @Column(name = "unit")
    private String unit;               // unit of measure for specific tariffs (e.g. "kg", "litre", "piece")

    @Temporal(TemporalType.DATE)
    @Column(name = "effective_date")
    private Date effectiveDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "expiry_date")
    private Date expiryDate;

    // === Relationships ===
    @OneToMany(mappedBy = "tariff", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("tariff-origins")
    private List<TariffOrigin> tariffOrigins = new ArrayList<>();

    @OneToMany(mappedBy = "tariff", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("tariff-destinations")
    private List<TariffDestination> tariffDestinations = new ArrayList<>();
}
