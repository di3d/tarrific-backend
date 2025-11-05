package com.tarrific.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "country", schema = "tariff")
public class Country {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "country_id", nullable = false)
    private Integer id;

    @Size(max = 100)
    @NotNull
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Size(max = 3)
    @NotNull
    @Column(name = "iso_code", columnDefinition = "CHAR(3)")
    private String isoCode;

    @Size(max = 100)
    @Column(name = "region", length = 100)
    private String region;

}