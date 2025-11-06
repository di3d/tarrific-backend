package com.tarrific.backend.repository;

import com.tarrific.backend.model.Country;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TradeAgreementRepository extends JpaRepository<Country, Integer> {}
