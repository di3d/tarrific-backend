package com.tarrific.backend.controller;

import com.tarrific.backend.model.Country;
import com.tarrific.backend.repository.CountryRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/countries")
public class CountryController {
    private final CountryRepository repository;

    public CountryController(CountryRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Country> getAll() { return repository.findAll(); }

    @GetMapping("/{id}")
    public Country getById(@PathVariable Integer id) { return repository.findById(id).orElse(null); }

    @PostMapping
    public Country create(@RequestBody Country c) { return repository.save(c); }

    @PutMapping("/{id}")
    public Country update(@PathVariable Integer id, @RequestBody Country c) {
        c.setCountryId(id);
        return repository.save(c);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) { repository.deleteById(id); }
}
