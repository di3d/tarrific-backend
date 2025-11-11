package com.tarrific.backend.service;

import com.tarrific.backend.dto.TradeAgreementViewDTO;
import com.tarrific.backend.model.*;
import com.tarrific.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final TariffRepository tariffRepository;
    private final TradeAgreementRepository agreementRepository;
    private final TradeAgreementCountryRepository tacRepository;
    private final CountryRepository countryRepository;

    /**
     * Builds a flattened tariff + agreement view for frontend visualizations.
     * Compatible with MapPage expecting fields like:
     * countryA, countryB, hsCode, rate, tariffType, agreementName
     */
    public List<TradeAgreementViewDTO> getAllActiveTradeAgreements() {
        List<TradeAgreementViewDTO> results = new ArrayList<>();
        Date now = new Date();

        // Fetch all trade agreements
        List<TradeAgreement> agreements = agreementRepository.findAll();
        // Fetch all tariffs
        List<Tariff> tariffs = tariffRepository.findAll();

        // Iterate through each trade agreement
        for (TradeAgreement agreement : agreements) {
            // Get countries under this agreement
            List<Country> memberCountries = tacRepository.findCountriesByAgreementId(agreement.getAgreementId());
            if (memberCountries.isEmpty()) continue;

            // For each tariff, create country pairs within the same agreement
            for (Tariff tariff : tariffs) {
                if (tariff.getEffectiveDate() != null && tariff.getEffectiveDate().after(now)) {
                    continue; // Not yet effective
                }
                if (tariff.getExpiryDate() != null && tariff.getExpiryDate().before(now)) {
                    continue; // Expired
                }

                for (Country origin : memberCountries) {
                    for (Country destination : memberCountries) {
                        if (origin.getCountryId().equals(destination.getCountryId())) continue;

                        TradeAgreementViewDTO dto = new TradeAgreementViewDTO();
                        dto.setId(tariff.getTariffId());
                        dto.setCountryA(origin.getName());
                        dto.setCountryB(destination.getName());
                        dto.setHsCode(tariff.getHsCode() != null ? tariff.getHsCode().getHsCode() : "Unknown");
                        dto.setRate(tariff.getBaseRate() != null ? tariff.getBaseRate().doubleValue() : 0.0);
                        dto.setTariffType(tariff.getRateType() != null ? tariff.getRateType() : "Ad Valorem");
                        dto.setStartDate(tariff.getEffectiveDate());
                        dto.setEndDate(tariff.getExpiryDate());
                        dto.setAgreementName(agreement.getName());

                        results.add(dto);
                    }
                }
            }
        }

        return results;
    }
}
