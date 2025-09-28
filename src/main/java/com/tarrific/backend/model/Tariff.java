package com.tarrific.backend.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "tariff")
public class Tariff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ✅ Country A
    @ManyToOne
    @JoinColumn(name = "country_a_id")
    private Country countryA;

    // ✅ Country B
    @ManyToOne
    @JoinColumn(name = "country_b_id")
    private Country countryB;

    // ✅ HS Code
    @ManyToOne
    @JoinColumn(name = "hscode_id")
    private HSCode hsCode;

    private Double rate;

    @Enumerated(EnumType.STRING)
    private TariffType tariffType;

    private LocalDate startDate;
    private LocalDate endDate;

    // getters and setters
    public Long getId() { return id; }

    public Country getCountryA() { return countryA; }
    public void setCountryA(Country countryA) { this.countryA = countryA; }

    public Country getCountryB() { return countryB; }
    public void setCountryB(Country countryB) { this.countryB = countryB; }

    public HSCode getHsCode() { return hsCode; }
    public void setHsCode(HSCode hsCode) { this.hsCode = hsCode; }

    public Double getRate() { return rate; }
    public void setRate(Double rate) { this.rate = rate; }

    public TariffType getTariffType() { return tariffType; }
    public void setTariffType(TariffType tariffType) { this.tariffType = tariffType; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
}
