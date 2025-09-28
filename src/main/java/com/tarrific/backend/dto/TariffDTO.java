package com.tarrific.backend.dto;

import java.time.LocalDate;

public class TariffDTO {
    private String countryA;
    private String countryB;
    private String hsCode;
    private String description;
    private Double rate;
    private String tariffType;
    private LocalDate startDate;
    private LocalDate endDate;

    // constructor
    public TariffDTO(String countryA, String countryB, String hsCode, String description,
                     Double rate, String tariffType, LocalDate startDate, LocalDate endDate) {
        this.countryA = countryA;
        this.countryB = countryB;
        this.hsCode = hsCode;
        this.description = description;
        this.rate = rate;
        this.tariffType = tariffType;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // getters
    public String getCountryA() { return countryA; }
    public String getCountryB() { return countryB; }
    public String getHsCode() { return hsCode; }
    public String getDescription() { return description; }
    public Double getRate() { return rate; }
    public String getTariffType() { return tariffType; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
}
