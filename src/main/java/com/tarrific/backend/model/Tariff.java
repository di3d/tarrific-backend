package com.tarrific.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Tariff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String hsCode;
    private String description;
    private Double rate;

    public Long getId() {
        return id;
    }

    public String getHsCode() {
        return hsCode;
    }

    public String getDescription() {
        return description;
    }

    public Double getRate() {
        return rate;
    }
}
