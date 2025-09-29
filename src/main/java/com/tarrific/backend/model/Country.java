package com.tarrific.backend.model;

import jakarta.persistence.*;


@Entity
public class Country {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;
    private Double tariffRate;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getTariffRate() {
        return tariffRate;
    }

    public void setTariffRate(Double tariffRate) {
        this.tariffRate = tariffRate;
    }
}
