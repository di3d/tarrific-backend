package com.tarrific.backend.controller;

import com.tarrific.backend.model.PreferentialTariff;
import com.tarrific.backend.repository.PreferentialTariffRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/preferential-tariffs")
public class PreferentialTariffController {
    private final PreferentialTariffRepository repository;

    public PreferentialTariffController(PreferentialTariffRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<PreferentialTariff> getAll() { return repository.findAll(); }

    @GetMapping("/{id}")
    public PreferentialTariff getById(@PathVariable Integer id) { return repository.findById(id).orElse(null); }

    @PostMapping
    public PreferentialTariff create(@RequestBody PreferentialTariff pt) { return repository.save(pt); }

    @PutMapping("/{id}")
    public PreferentialTariff update(@PathVariable Integer id, @RequestBody PreferentialTariff pt) {
        pt.setPrefTariffId(id);
        return repository.save(pt);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) { repository.deleteById(id); }
}
