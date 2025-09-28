package com.tarrific.backend.controller;

import com.tarrific.backend.model.Tariff;
import com.tarrific.backend.repository.TariffRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tariffs")
@CrossOrigin(origins = "http://localhost:3000")
public class TariffController {

    private final TariffRepository tariffRepository;

    public TariffController(TariffRepository tariffRepository) {
        this.tariffRepository = tariffRepository;
    }

    @GetMapping
    public List<Tariff> getAllTariffs() {
        return tariffRepository.findAll();
    }
}
