package com.tarrific.backend.repository;

import com.tarrific.backend.model.PreferentialTariff;
import com.tarrific.backend.model.Tariff;
import com.tarrific.backend.model.TradeAgreement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface PreferentialTariffRepository extends JpaRepository<PreferentialTariff, Integer> {
    
    List<PreferentialTariff> findByTariff(Tariff tariff);
    List<PreferentialTariff> findByAgreement(TradeAgreement agreement);
    
    @Query("SELECT pt FROM PreferentialTariff pt WHERE pt.tariff = :tariff " +
           "AND pt.effectiveDate <= :date " +
           "AND (pt.expiryDate IS NULL OR pt.expiryDate >= :date)")
    List<PreferentialTariff> findActiveByTariffAndDate(@Param("tariff") Tariff tariff, 
                                                        @Param("date") Date date);
}
