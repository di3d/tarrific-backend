package com.tarrific.backend.controller;

import com.tarrific.backend.model.TradeAgreement;
import com.tarrific.backend.repository.TradeAgreementRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trade-agreements")
public class TradeAgreementController {
    private final TradeAgreementRepository repository;

    public TradeAgreementController(TradeAgreementRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<TradeAgreement> getAll() { return repository.findAll(); }

    @GetMapping("/{id}")
    public TradeAgreement getById(@PathVariable Integer id) { return repository.findById(id).orElse(null); }

    @PostMapping
    public TradeAgreement create(@RequestBody TradeAgreement ta) { return repository.save(ta); }

    @PutMapping("/{id}")
    public TradeAgreement update(@PathVariable Integer id, @RequestBody TradeAgreement ta) {
        ta.setAgreementId(id);
        return repository.save(ta);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) { repository.deleteById(id); }
}
