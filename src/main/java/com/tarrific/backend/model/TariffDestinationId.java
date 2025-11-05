package com.tarrific.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class TariffDestinationId implements Serializable {
    private static final long serialVersionUID = -8573887968550459360L;
    @NotNull
    @Column(name = "tariff_id", nullable = false)
    private Integer tariffId;

    @NotNull
    @Column(name = "country_id", nullable = false)
    private Integer countryId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        TariffDestinationId entity = (TariffDestinationId) o;
        return Objects.equals(this.tariffId, entity.tariffId) &&
                Objects.equals(this.countryId, entity.countryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tariffId, countryId);
    }

}