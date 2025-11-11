package com.tarrific.backend.repository;

import com.tarrific.backend.model.HsSection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HsSectionRepository extends JpaRepository<HsSection, Long> {
    Optional<HsSection> findByCode(String code);
}

