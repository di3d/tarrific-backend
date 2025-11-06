package com.tarrific.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "hs_code", schema = "tariff")
public class HsCode {
    @Id
    private String hsCode;
    private String description;
}
