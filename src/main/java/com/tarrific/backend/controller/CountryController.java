package com.tarrific.backend.controller;

import com.tarrific.backend.model.Country;
import com.tarrific.backend.repository.CountryRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/countries")
public class CountryController {

    private final CountryRepository repository;

    public CountryController(CountryRepository repository) {
        this.repository = repository;
    }

    // READ all countries
    @GetMapping
    public List<Country> all() {
        return repository.findAll();
    }

    // READ one country
    @GetMapping("/{id}")
    public ResponseEntity<Country> getById(@PathVariable Integer id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // CREATE a new country
    @PostMapping
    public Country create(@RequestBody Country country) {
        return repository.save(country);
    }

    // UPDATE an existing country
    @PutMapping("/{id}")
    public ResponseEntity<Country> update(@PathVariable Integer id, @RequestBody Country updatedCountry) {
        Optional<Country> optionalCountry = repository.findById(id);
        if (optionalCountry.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Country country = optionalCountry.get();
        country.setName(updatedCountry.getName());
        country.setIsoCode(updatedCountry.getIsoCode());
        country.setRegion(updatedCountry.getRegion());

        repository.save(country);
        return ResponseEntity.ok(country);
    }

    // DELETE a country
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
