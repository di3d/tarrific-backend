package com.tarrific.backend.controller;

import com.tarrific.backend.model.Country;
import com.tarrific.backend.repository.CountryRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/countries")
public class CountryController {

    private final CountryRepository repository;

    public CountryController(CountryRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Country> all() {
        return repository.findAll();
    }

    @PostMapping
    public Country create(@RequestBody Country country) {
        return repository.save(country);
    }
}
