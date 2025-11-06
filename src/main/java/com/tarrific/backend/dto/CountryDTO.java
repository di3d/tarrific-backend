package com.tarrific.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CountryDTO {
    private Integer id;
    private String name;
    private Double tariffRate;
    private String isoCode;
    private String region;
}
