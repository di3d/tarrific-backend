package com.tarrific.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class TariffDTO {
    private Integer id;
    private String hsCode;
    private String description;
    private float baseRate;
    private String rateType;
    private LocalDate effectiveDate;
    private LocalDate expiryDate;
}

