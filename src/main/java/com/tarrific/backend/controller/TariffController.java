package com.tarrific.backend.controller;

import com.tarrific.backend.model.*;
import com.tarrific.backend.repository.*;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tariffs")
@CrossOrigin(origins = "${CORS_ORIGIN}", allowCredentials = "true")
public class TariffController {

    private final TariffRepository tariffRepository;
    private final HsCodeRepository hsCodeRepository;
    private final CountryRepository countryRepository;
    private final TariffOriginRepository tariffOriginRepository;
    private final TariffDestinationRepository tariffDestinationRepository;

    public TariffController(
            TariffRepository tariffRepository,
            HsCodeRepository hsCodeRepository,
            CountryRepository countryRepository,
            TariffOriginRepository tariffOriginRepository,
            TariffDestinationRepository tariffDestinationRepository
    ) {
        this.tariffRepository = tariffRepository;
        this.hsCodeRepository = hsCodeRepository;
        this.countryRepository = countryRepository;
        this.tariffOriginRepository = tariffOriginRepository;
        this.tariffDestinationRepository = tariffDestinationRepository;
    }

    // === Retrieve all tariffs ===
    @GetMapping
    public List<Tariff> getAll() {
        return tariffRepository.findAll();
    }

    // === Retrieve a single tariff by ID ===
    @GetMapping("/{id}")
    public Tariff getById(@PathVariable Integer id) {
        return tariffRepository.findById(id).orElse(null);
    }

    // === Create new tariff ===
    @PostMapping
    public Tariff create(@RequestBody Tariff t) {
        attachExistingHsCode(t);
        normalizeCountries(t);
        Tariff saved = tariffRepository.save(t);
        saveOriginsAndDestinations(saved);
        return saved;
    }

    // === Update tariff ===
    @PutMapping("/{id}")
    public Tariff update(@PathVariable Integer id, @RequestBody Tariff t) {
        t.setTariffId(id);
        attachExistingHsCode(t);
        normalizeCountries(t);
        Tariff updated = tariffRepository.save(t);
        saveOriginsAndDestinations(updated);
        return updated;
    }

    // === Delete tariff ===
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        tariffRepository.deleteById(id);
    }

    // === Retrieve tariffs with country info ===
    @GetMapping("/with-countries")
    public List<Map<String, Object>> getTariffsWithCountries() {
        return tariffRepository.findAll().stream().map(t -> {
            Map<String, Object> dto = new LinkedHashMap<>();
            dto.put("tariffId", t.getTariffId());
            dto.put("hsCode", t.getHsCode());
            dto.put("baseRate", t.getBaseRate());
            dto.put("rateType", t.getRateType());
            dto.put("effectiveDate", t.getEffectiveDate());
            dto.put("expiryDate", t.getExpiryDate());
            dto.put("origins", t.getTariffOrigins().stream().map(o -> Map.of(
                    "name", o.getCountry().getName(),
                    "isoCode", o.getCountry().getIsoCode()
            )).toList());
            dto.put("destinations", t.getTariffDestinations().stream().map(d -> Map.of(
                    "name", d.getCountry().getName(),
                    "isoCode", d.getCountry().getIsoCode()
            )).toList());
            return dto;
        }).toList();
    }

    // === Helpers ===

    /** Ensures we attach an existing HS code entity */
    private void attachExistingHsCode(Tariff t) {
        if (t.getHsCode() != null && t.getHsCode().getHsCode() != null) {
            HsCode existing = hsCodeRepository.findById(t.getHsCode().getHsCode())
                    .orElseThrow(() -> new RuntimeException("HS Code not found: " + t.getHsCode().getHsCode()));
            t.setHsCode(existing);
        }
    }

    /** Reuses existing countries to prevent duplicates */
    private void normalizeCountries(Tariff t) {
        if (t.getTariffOrigins() != null) {
            for (TariffOrigin o : t.getTariffOrigins()) {
                Country c = o.getCountry();
                if (c != null && c.getName() != null) {
                    Country existing = countryRepository
                            .findByNameIgnoreCase(c.getName())
                            .orElseGet(() -> countryRepository.save(c));
                    o.setCountry(existing);
                }
                o.setTariff(t);
            }
        }

        if (t.getTariffDestinations() != null) {
            for (TariffDestination d : t.getTariffDestinations()) {
                Country c = d.getCountry();
                if (c != null && c.getName() != null) {
                    Country existing = countryRepository
                            .findByNameIgnoreCase(c.getName())
                            .orElseGet(() -> countryRepository.save(c));
                    d.setCountry(existing);
                }
                d.setTariff(t);
            }
        }
    }


    /** Saves child entities (origins & destinations) linked to tariff */
    private void saveOriginsAndDestinations(Tariff t) {
        if (t.getTariffOrigins() != null)
            tariffOriginRepository.saveAll(t.getTariffOrigins());
        if (t.getTariffDestinations() != null)
            tariffDestinationRepository.saveAll(t.getTariffDestinations());
    }
}
