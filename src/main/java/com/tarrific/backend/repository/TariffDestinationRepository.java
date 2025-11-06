package com.tarrific.backend.repository;

import com.tarrific.backend.model.TariffDestination;
import com.tarrific.backend.model.Country;
import com.tarrific.backend.model.Tariff;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TariffDestinationRepository extends JpaRepository<TariffDestination, TariffDestination.Key> {
    List<TariffDestination> findByTariff(Tariff tariff);
    List<TariffDestination> findByCountry(Country country);
    List<TariffDestination> findByTariffIn(List<Tariff> tariffs);
}
