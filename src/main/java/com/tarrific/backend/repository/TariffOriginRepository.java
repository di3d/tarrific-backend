package com.tarrific.backend.repository;

import com.tarrific.backend.model.TariffDestination;
import com.tarrific.backend.model.TariffOrigin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TariffOriginRepository extends JpaRepository<TariffOrigin, Integer> {}
