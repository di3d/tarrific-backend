package com.tarrific.backend.controller;

import com.tarrific.backend.dto.TariffDTO;
import com.tarrific.backend.model.Tariff;
import com.tarrific.backend.repository.TariffRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tariffs")
@CrossOrigin(origins = "http://localhost:3000")
public class TariffController {

    private final TariffRepository tariffRepository;

    public TariffController(TariffRepository tariffRepository) {
        this.tariffRepository = tariffRepository;
    }

    @GetMapping
    public List<TariffDTO> getAllTariffs() {
        List<Tariff> tariffs = tariffRepository.findAll();
        return tariffs.stream()
                .map(t -> new TariffDTO(
                        t.getId(),
                        t.getHsCode().getHsCode(),
                        t.getHsCode().getDescription(),
                        t.getBaseRate(),
                        t.getRateType(),
                        t.getEffectiveDate(),
                        t.getExpiryDate()
                ))
                .collect(Collectors.toList());
    }
}
