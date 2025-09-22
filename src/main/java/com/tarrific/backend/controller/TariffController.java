package com.tarrific.backend.controller;

import com.tarrific.backend.model.Tariff;
import com.tarrific.backend.repository.TariffRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tariffs")
public class TariffController {

    private final TariffRepository repository;

    public TariffController(TariffRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Tariff> all() {
        return repository.findAll();
    }

    @PostMapping
    public Tariff create(@RequestBody Tariff tariff) {
        return repository.save(tariff);
    }
}
