package com.tarrific.backend.repository;

import com.tarrific.backend.model.HsCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HsCodeRepository extends JpaRepository<HsCode, String> {
    Optional<HsCode> findByHsCode(String hsCode);
}
