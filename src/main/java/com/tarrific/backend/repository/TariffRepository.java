package com.tarrific.backend.repository;

import com.tarrific.backend.model.HsCode;
import com.tarrific.backend.model.Tariff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface TariffRepository extends JpaRepository<Tariff, Integer> {

    // === Existing Methods ===
    List<Tariff> findByHsCode(HsCode hsCode);

    @Query("""
        SELECT t FROM Tariff t
        WHERE t.hsCode = :hsCode
          AND t.effectiveDate <= :date
          AND (t.expiryDate IS NULL OR t.expiryDate >= :date)
    """)
    List<Tariff> findActiveByHsCodeAndDate(
            @Param("hsCode") HsCode hsCode,
            @Param("date") Date date
    );

    // === New Section Breakdown Query ===
    @Query("""
        SELECT h.section, AVG(t.baseRate), COUNT(t.id)
        FROM Tariff t
        JOIN t.hsCode h
        WHERE h.section IS NOT NULL
        GROUP BY h.section
        ORDER BY h.section.code
    """)
    List<Object[]> findAverageBySection();
}
