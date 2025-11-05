package com.tarrific.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CalculationResponse {
    private Double baseTariff;
    private Double totalDuty;
    private Double effectiveRate;
    private AgreementDTO applicableTariff;
    private Double defaultRate;
    private String hsCode;
    private String commodityDescription;
    private Double shipmentValue;
    private String originCountry;
    private String importingCountry;
    private String date;
    private String currency;
}
