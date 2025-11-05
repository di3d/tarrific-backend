package com.tarrific.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgreementDTO {
    private Long id;
    private Long countryAId;
    private Long countryBId;
    private Long hscodeId;
    private Double rate;
    private String tariffType;
    private LocalDate startDate;
    private LocalDate endDate;
}
