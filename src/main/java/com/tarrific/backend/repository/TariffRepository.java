package com.tarrific.backend.repository;

import com.tarrific.backend.model.Tariff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TariffRepository extends JpaRepository<Tariff, Integer> {

    // Find tariffs by HS code string value
    List<Tariff> findByHsCode_HsCode(String hsCode);

    // Optional: find tariffs active within a date range
    List<Tariff> findByEffectiveDateBeforeAndExpiryDateAfter(
            java.time.LocalDate start, java.time.LocalDate end
    );
}
