package com.tarrific.backend.service;

import com.tarrific.backend.dto.TradeAgreementViewDTO;
import com.tarrific.backend.model.*;
import com.tarrific.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final PreferentialTariffRepository preferentialTariffRepository;
    private final TariffOriginRepository tariffOriginRepository;
    private final TariffDestinationRepository tariffDestinationRepository;

    /**
     * Get all active trade agreements with their associated countries and HS codes
     * This endpoint is backward-compatible with the old /tariffs endpoint format
     */
    public List<TradeAgreementViewDTO> getAllActiveTradeAgreements() {
        List<TradeAgreementViewDTO> results = new ArrayList<>();
        Date now = new Date();

        // Get all preferential tariffs
        List<PreferentialTariff> preferentialTariffs = preferentialTariffRepository.findAll();

        for (PreferentialTariff prefTariff : preferentialTariffs) {
            // Check if preferential tariff is active
            if (prefTariff.getEffectiveDate() != null && prefTariff.getEffectiveDate().after(now)) {
                continue; // Not yet effective
            }
            if (prefTariff.getExpiryDate() != null && prefTariff.getExpiryDate().before(now)) {
                continue; // Already expired
            }

            Tariff tariff = prefTariff.getTariff();
            if (tariff == null) continue;

            // Get origins and destinations for this tariff
            List<TariffOrigin> origins = tariffOriginRepository.findByTariff(tariff);
            List<TariffDestination> destinations = tariffDestinationRepository.findByTariff(tariff);

            // Create a view DTO for each origin-destination combination
            for (TariffOrigin origin : origins) {
                for (TariffDestination destination : destinations) {
                    TradeAgreementViewDTO dto = new TradeAgreementViewDTO();
                    dto.setId(prefTariff.getPrefTariffId());
                    dto.setCountryA(origin.getCountry().getName());
                    dto.setCountryB(destination.getCountry().getName());
                    dto.setHsCode(tariff.getHsCode().getHsCode());
                    dto.setRate(prefTariff.getPreferentialRate() != null ? 
                            prefTariff.getPreferentialRate().doubleValue() : 0.0);
                    dto.setTariffType(prefTariff.getRateType() != null ? 
                            prefTariff.getRateType() : "PREFERENTIAL");
                    dto.setStartDate(prefTariff.getEffectiveDate());
                    dto.setEndDate(prefTariff.getExpiryDate());
                    dto.setAgreementName(prefTariff.getAgreement() != null ? 
                            prefTariff.getAgreement().getName() : "Unknown");
                    
                    results.add(dto);
                }
            }
        }

        return results;
    }
}
