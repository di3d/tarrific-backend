package com.tarrific.backend.controller;

import com.tarrific.backend.model.HsCode;
import com.tarrific.backend.model.Tariff;
import com.tarrific.backend.repository.HsCodeRepository;
import com.tarrific.backend.repository.TariffRepository;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tariffs")
public class TariffController {

    private final TariffRepository tariffRepository;
    private final HsCodeRepository hsCodeRepository;

    public TariffController(TariffRepository tariffRepository, HsCodeRepository hsCodeRepository) {
        this.tariffRepository = tariffRepository;
        this.hsCodeRepository = hsCodeRepository;
    }

    @GetMapping
    public List<Tariff> getAll() {
        return tariffRepository.findAll();
    }

    @GetMapping("/{id}")
    public Tariff getById(@PathVariable Integer id) {
        return tariffRepository.findById(id).orElse(null);
    }

    @PostMapping
    public Tariff create(@RequestBody Tariff t) {
        // Attach existing HS code if found
        if (t.getHsCode() != null && t.getHsCode().getHsCode() != null) {
            HsCode existing = hsCodeRepository.findById(t.getHsCode().getHsCode())
                    .orElseThrow(() -> new RuntimeException("HS Code not found: " + t.getHsCode().getHsCode()));
            t.setHsCode(existing);
        }
        return tariffRepository.save(t);
    }

    @PutMapping("/{id}")
    public Tariff update(@PathVariable Integer id, @RequestBody Tariff t) {
        t.setTariffId(id);
        if (t.getHsCode() != null && t.getHsCode().getHsCode() != null) {
            HsCode existing = hsCodeRepository.findById(t.getHsCode().getHsCode())
                    .orElseThrow(() -> new RuntimeException("HS Code not found: " + t.getHsCode().getHsCode()));
            t.setHsCode(existing);
        }
        return tariffRepository.save(t);
    }

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

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        tariffRepository.deleteById(id);
    }
}
