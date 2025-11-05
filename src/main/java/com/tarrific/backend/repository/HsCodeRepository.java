package com.tarrific.backend.repository;

import com.tarrific.backend.model.HsCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HsCodeRepository extends JpaRepository<HsCode, String> {

    // Look up by code value
    HsCode findByHsCode(String hsCode);
}
