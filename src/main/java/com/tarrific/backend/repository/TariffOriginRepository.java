package com.tarrific.backend.repository;

import com.tarrific.backend.model.TariffOrigin;
import com.tarrific.backend.model.Country;
import com.tarrific.backend.model.Tariff;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TariffOriginRepository extends JpaRepository<TariffOrigin, TariffOrigin.Key> {
    List<TariffOrigin> findByTariff(Tariff tariff);
    List<TariffOrigin> findByCountry(Country country);
    List<TariffOrigin> findByTariffIn(List<Tariff> tariffs);
}
