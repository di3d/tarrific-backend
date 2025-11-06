package com.tarrific.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgreementDTO {
    private Integer id;
    private Integer tariffId;
    private Integer agreementId;
    private String agreementName;
    private Double preferentialRate;
    private String rateType;
    private Date effectiveDate;
    private Date expiryDate;
}
