package com.tarrific.backend.repository;

import com.tarrific.backend.model.TradeAgreement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TradeAgreementRepository extends JpaRepository<TradeAgreement, Integer> {
    Optional<TradeAgreement> findByName(String name);
}
