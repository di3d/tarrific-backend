package com.tarrific.backend.repository;

import com.tarrific.backend.model.Tariff;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TariffRepository extends JpaRepository<Tariff, Integer> {}
