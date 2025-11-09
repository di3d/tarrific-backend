package com.tarrific.backend.controller;

import com.tarrific.backend.dto.TradeAgreementDTO;
import com.tarrific.backend.dto.TradeAgreementViewDTO;
import com.tarrific.backend.model.Country;
import com.tarrific.backend.model.TradeAgreement;
import com.tarrific.backend.repository.TradeAgreementCountryRepository;
import com.tarrific.backend.repository.TradeAgreementRepository;
import com.tarrific.backend.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/trade-agreements")
@CrossOrigin(origins = "${CORS_ORIGIN}", allowCredentials = "true")
public class TradeAgreementController {
    private final TradeAgreementRepository repository;
    private final DashboardService dashboardService;
    private final TradeAgreementCountryRepository tacRepo;

    public TradeAgreementController(TradeAgreementRepository repository, DashboardService dashboardService, TradeAgreementCountryRepository tacRepo) {
        this.repository = repository;
        this.dashboardService = dashboardService;
        this.tacRepo = tacRepo;
    }

    /**
     * Get all raw trade agreements (for the TradeAgreementsCard component)
     * Returns basic agreement info without country/HS code details
     */
    @GetMapping
    public List<TradeAgreementDTO> getAll() { 
        return repository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get all active preferential tariffs grouped by trade agreements
     * This provides detailed tariff info with countries for the world map
     */
    @GetMapping("/tariffs")
    public List<TradeAgreementViewDTO> getAllTariffs() { 
        return dashboardService.getAllActiveTradeAgreements(); 
    }

    /**
     * Get raw trade agreement by ID
     */
    @GetMapping("/{id}")
    public TradeAgreement getById(@PathVariable Integer id) { 
        return repository.findById(id).orElse(null); 
    }

    @PostMapping
    public TradeAgreement create(@RequestBody TradeAgreement ta) { 
        return repository.save(ta); 
    }

    @PutMapping("/{id}")
    public TradeAgreement update(@PathVariable Integer id, @RequestBody TradeAgreement ta) {
        ta.setAgreementId(id);
        return repository.save(ta);
    }

    @GetMapping("/{id}/countries")
    public ResponseEntity<List<Country>> countries(@PathVariable Long id){
        return ResponseEntity.ok(tacRepo.findCountriesByAgreementId(id));
    }


    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) { 
        repository.deleteById(id); 
    }
    
    private TradeAgreementDTO toDTO(TradeAgreement agreement) {
        TradeAgreementDTO dto = new TradeAgreementDTO();
        dto.setAgreementId(agreement.getAgreementId());
        dto.setName(agreement.getName());
        dto.setDescription(agreement.getDescription());
        dto.setEffectiveDate(agreement.getEffectiveDate());
        dto.setExpiryDate(agreement.getExpiryDate());
        return dto;
    }
}
