package com.tarrific.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "tariff_origin", schema = "tariff")
public class TariffOrigin {

    @Embeddable
    @Getter
    @Setter
    public static class Key implements Serializable {
        private Integer tariffId;
        private Integer countryId;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Key that)) return false;
            return Objects.equals(tariffId, that.tariffId)
                    && Objects.equals(countryId, that.countryId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(tariffId, countryId);
        }
    }

    @EmbeddedId
    private Key id = new Key();

    @ManyToOne
    @MapsId("tariffId")
    @JoinColumn(name = "tariff_id", nullable = false)
    private Tariff tariff;

    @ManyToOne
    @MapsId("countryId")
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;
}
