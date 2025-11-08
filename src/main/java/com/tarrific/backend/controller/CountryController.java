package com.tarrific.backend.controller;

import com.tarrific.backend.dto.CountryDTO;
import com.tarrific.backend.model.Country;
import com.tarrific.backend.model.TariffDestination;
import com.tarrific.backend.repository.CountryRepository;
import com.tarrific.backend.repository.TariffDestinationRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/countries")
@CrossOrigin(origins = "http://localhost:3000")
public class CountryController {
    private final CountryRepository repository;
    private final TariffDestinationRepository tariffDestinationRepository;

    public CountryController(CountryRepository repository, TariffDestinationRepository tariffDestinationRepository) {
        this.repository = repository;
        this.tariffDestinationRepository = tariffDestinationRepository;
    }

    @GetMapping
    public List<CountryDTO> getAll() { 
        return repository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

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
    public ResponseEntity<?> deleteCountry(@PathVariable Long id) {
        try {
            repository.deleteById(Math.toIntExact(id));
            return ResponseEntity.noContent().build();
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Cannot delete: Country is referenced by tariffs or agreements.");
        }
    }

    private CountryDTO toDTO(Country country) {
        CountryDTO dto = new CountryDTO();
        dto.setId(country.getCountryId());
        dto.setName(country.getName());
        
        // Calculate average tariff rate for this country as importing destination
        List<TariffDestination> destinations = tariffDestinationRepository.findByCountry(country);
        Double avgRate = destinations.stream()
                .filter(td -> td.getTariff() != null && td.getTariff().getBaseRate() != null)
                .mapToDouble(td -> td.getTariff().getBaseRate())
                .average()
                .orElse(0.0);
        
        dto.setTariffRate(avgRate);
        dto.setIsoCode(country.getIsoCode());
        dto.setRegion(country.getRegion());
        return dto;
    }
}
