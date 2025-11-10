package com.tarrific.backend.controller;

import com.tarrific.backend.model.HsSection;
import com.tarrific.backend.repository.TariffRepository;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/hs/sections")
@CrossOrigin(origins = "${CORS_ORIGIN}", allowCredentials = "true")
public class HsSectionController {
    private final TariffRepository tariffRepo;

    public HsSectionController(TariffRepository tariffRepo) {
        this.tariffRepo = tariffRepo;
    }

    @GetMapping("/breakdown")
    public List<Map<String, Object>> getSectionBreakdown() {
        List<Object[]> raw = tariffRepo.findAverageBySection();
        List<Map<String, Object>> result = new ArrayList<>();

        for (Object[] row : raw) {
            HsSection section = (HsSection) row[0];
            Double avgRate = ((Number) row[1]).doubleValue();
            Long productCount = ((Number) row[2]).longValue();

            Map<String, Object> map = new HashMap<>();
            map.put("section", section.getCode());
            map.put("name", section.getName());
            map.put("avgRate", avgRate);
            map.put("productCount", productCount);
            result.add(map);
        }
        return result;
    }
}
