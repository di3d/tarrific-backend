package com.tarrific.backend.repository;

import com.tarrific.backend.model.Country;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HsCodeRepository extends JpaRepository<Country, Integer> {}
