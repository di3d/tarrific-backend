package com.tarrific.backend.repository;

import com.tarrific.backend.model.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface TradeAgreementCountryRepository extends JpaRepository<TradeAgreementCountry, Integer> {
    @Query("SELECT tac.country FROM TradeAgreementCountry tac WHERE tac.agreement.agreementId = :id")
    List<Country> findCountriesByAgreementId(@Param("id") Integer agreementId);
}
