package com.tarrific.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@Table(name = "tariff_destination", schema = "tariff")
public class TariffDestination {
    @EmbeddedId
    private TariffDestinationId id;

    @MapsId("tariffId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "tariff_id", nullable = false)
    private Tariff tariff;

    @MapsId("countryId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;

}