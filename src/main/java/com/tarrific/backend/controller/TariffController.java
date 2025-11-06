package com.tarrific.backend.controller;

import com.tarrific.backend.model.Tariff;
import com.tarrific.backend.repository.TariffRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tariffs")
public class TariffController {
    private final TariffRepository repository;

    public TariffController(TariffRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Tariff> getAll() { return repository.findAll(); }

    @GetMapping("/{id}")
    public Tariff getById(@PathVariable Integer id) { return repository.findById(id).orElse(null); }

    @PostMapping
    public Tariff create(@RequestBody Tariff t) { return repository.save(t); }

    @PutMapping("/{id}")
    public Tariff update(@PathVariable Integer id, @RequestBody Tariff t) {
        t.setTariffId(id);
        return repository.save(t);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) { repository.deleteById(id); }
}
