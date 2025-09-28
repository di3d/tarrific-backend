package com.tarrific.backend.controller;

import com.tarrific.backend.repository.TariffRepository;
import com.tarrific.backend.dto.TariffDTO;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/tariffs")
@CrossOrigin(origins = "http://localhost:3000")
public class TariffController {

    private final TariffRepository tariffRepository;

    public TariffController(TariffRepository tariffRepository) {
        this.tariffRepository = tariffRepository;
    }

    @GetMapping
    public List<TariffDTO> getAllTariffs() {
        return tariffRepository.findAll().stream().map(t ->
                new TariffDTO(
                        t.getCountryA().getName(),
                        t.getCountryB().getName(),
                        t.getHsCode().getCode(),
                        t.getHsCode().getDescription(),
                        t.getRate(),
                        t.getTariffType().name(),
                        t.getStartDate(),
                        t.getEndDate()
                )
        ).collect(Collectors.toList());
    }
}
