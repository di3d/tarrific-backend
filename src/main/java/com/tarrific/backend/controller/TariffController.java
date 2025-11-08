package com.tarrific.backend.controller;

import com.tarrific.backend.model.Tariff;
import com.tarrific.backend.repository.TariffRepository;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
    @GetMapping("/with-countries")
    public List<Map<String,Object>> getTariffsWithCountries() {
        return repository.findAll().stream().map(t -> {
            Map<String,Object> dto = new LinkedHashMap<>();
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


    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) { repository.deleteById(id); }
}
