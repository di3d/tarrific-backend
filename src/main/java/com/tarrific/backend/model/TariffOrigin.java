package com.tarrific.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "tariff_origin", schema = "tariff")
public class TariffOrigin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "tariff_id")
    private Tariff tariff;

    @ManyToOne
    @JoinColumn(name = "country_id")
    private Country country;
}
