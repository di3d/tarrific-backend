package com.tarrific.backend.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "tariff")
public class Tariff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // foreign keys
    @Column(name = "country_a_id")
    private Long countryAId;

    @Column(name = "country_b_id")
    private Long countryBId;

    @Column(name = "hscode_id")
    private Long hscodeId;

    private Double rate;

    @Enumerated(EnumType.STRING)
    @Column(name = "tariff_type")
    private TariffType tariffType;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    // getters and setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Long getCountryAId() {
        return countryAId;
    }
    public void setCountryAId(Long countryAId) {
        this.countryAId = countryAId;
    }

    public Long getCountryBId() {
        return countryBId;
    }
    public void setCountryBId(Long countryBId) {
        this.countryBId = countryBId;
    }

    public Long getHscodeId() {
        return hscodeId;
    }
    public void setHscodeId(Long hscodeId) {
        this.hscodeId = hscodeId;
    }

    public Double getRate() {
        return rate;
    }
    public void setRate(Double rate) {
        this.rate = rate;
    }

    public TariffType getTariffType() {
        return tariffType;
    }
    public void setTariffType(TariffType tariffType) {
        this.tariffType = tariffType;
    }

    public LocalDate getStartDate() {
        return startDate;
    }
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}
