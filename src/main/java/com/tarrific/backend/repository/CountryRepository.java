package com.tarrific.backend.repository;

import com.tarrific.backend.model.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CountryRepository extends JpaRepository<Country, Integer> {

    Optional<Country> findByNameIgnoreCase(String name);

    Optional<Country> findByIsoCodeIgnoreCase(String isoCode);
}
