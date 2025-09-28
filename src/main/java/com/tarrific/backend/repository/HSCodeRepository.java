package com.tarrific.backend.repository;

import com.tarrific.backend.model.HSCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HSCodeRepository extends JpaRepository<HSCode, Long> {
    HSCode findByCode(String code);
}
