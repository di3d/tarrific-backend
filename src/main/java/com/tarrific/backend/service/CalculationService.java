package com.tarrific.backend.service;

import com.tarrific.backend.dto.AgreementDTO;
import com.tarrific.backend.dto.CalculationRequest;
import com.tarrific.backend.dto.CalculationResponse;
import com.tarrific.backend.model.*;
import com.tarrific.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CalculationService {

    private final CountryRepository countryRepository;
    private final HsCodeRepository hsCodeRepository;
    private final TariffRepository tariffRepository;
    private final TariffOriginRepository tariffOriginRepository;
    private final TariffDestinationRepository tariffDestinationRepository;
    private final PreferentialTariffRepository preferentialTariffRepository;

    public CalculationResponse calculate(CalculationRequest request) {
        // Validate input
        if (request.getOriginCountry() == null || request.getImportingCountry() == null ||
                request.getHsCode() == null || request.getShipmentValue() == null) {
            throw new IllegalArgumentException("Missing required parameters");
        }

        // Find countries
        Country originCountry = countryRepository.findByName(request.getOriginCountry())
                .orElseThrow(
                        () -> new IllegalArgumentException("Origin country not found: " + request.getOriginCountry()));

        Country importingCountry = countryRepository.findByName(request.getImportingCountry())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Importing country not found: " + request.getImportingCountry()));

        // Find HS Code
        HsCode hsCode = hsCodeRepository.findByHsCode(request.getHsCode())
                .orElseThrow(() -> new IllegalArgumentException("Invalid HS Code: " + request.getHsCode()));

        // Parse the calculation date
        Date calculationDate;
        try {
            if (request.getDate() != null && !request.getDate().isEmpty()) {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                formatter.setLenient(false);
                calculationDate = formatter.parse(request.getDate());

                // Set to end of day to ensure we capture tariffs effective anytime during this
                // date
                java.util.Calendar cal = java.util.Calendar.getInstance();
                cal.setTime(calculationDate);
                cal.set(java.util.Calendar.HOUR_OF_DAY, 23);
                cal.set(java.util.Calendar.MINUTE, 59);
                cal.set(java.util.Calendar.SECOND, 59);
                cal.set(java.util.Calendar.MILLISECOND, 999);
                calculationDate = cal.getTime();
            } else {
                calculationDate = new Date();
            }
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid date format. Use yyyy-MM-dd");
        }

        // Find all active tariffs for this HS code
        List<Tariff> activeTariffs = tariffRepository.findActiveByHsCodeAndDate(hsCode, calculationDate);

        // Filter tariffs by origin and destination countries
        Tariff applicableTariff = null;
        for (Tariff tariff : activeTariffs) {
            // Get origins and destinations for this tariff
            List<TariffOrigin> origins = tariffOriginRepository.findByTariff(tariff);
            List<TariffDestination> destinations = tariffDestinationRepository.findByTariff(tariff);

            // Check if this tariff applies to our origin and destination
            boolean hasOrigin = origins.stream()
                    .anyMatch(to -> to.getCountry().getCountryId().equals(originCountry.getCountryId()));
            boolean hasDestination = destinations.stream()
                    .anyMatch(td -> td.getCountry().getCountryId().equals(importingCountry.getCountryId()));

            if (hasOrigin && hasDestination) {
                applicableTariff = tariff;
                break;
            }
        }

        // Default rate: use base rate from tariff or 0 if no tariff found
        Double defaultRate = applicableTariff != null
                ? (applicableTariff.getBaseRate() != null ? applicableTariff.getBaseRate().doubleValue() : 0.0)
                : 0.0;

        // Check for preferential tariff (trade agreement)
        PreferentialTariff preferentialTariff = null;
        if (applicableTariff != null) {
            List<PreferentialTariff> preferentialTariffs = preferentialTariffRepository
                    .findActiveByTariffAndDate(applicableTariff, calculationDate);

            // Use the first preferential tariff found (you could add logic to pick the best
            // one)
            if (!preferentialTariffs.isEmpty()) {
                preferentialTariff = preferentialTariffs.get(0);
            }
        }

        // Calculate effective rate
        Double effectiveRate;
        if (preferentialTariff != null && preferentialTariff.getPreferentialRate() != null) {
            effectiveRate = preferentialTariff.getPreferentialRate().doubleValue();
        } else {
            effectiveRate = defaultRate;
        }

        // Calculate total duty
        Double totalDuty = (request.getShipmentValue() * effectiveRate) / 100.0;
        Double effectivePercentage = effectiveRate;

        // Build response
        CalculationResponse response = new CalculationResponse();
        response.setBaseTariff(effectiveRate);
        response.setTotalDuty(totalDuty);
        response.setEffectiveRate(effectivePercentage);
        response.setDefaultRate(defaultRate);
        response.setHsCode(request.getHsCode());
        response.setCommodityDescription(hsCode.getDescription());
        response.setShipmentValue(request.getShipmentValue());
        response.setOriginCountry(request.getOriginCountry());
        response.setImportingCountry(request.getImportingCountry());
        response.setDate(request.getDate());
        response.setCurrency(request.getCurrency());

        // Set applicable preferential tariff if found
        if (preferentialTariff != null) {
            AgreementDTO agreementDTO = new AgreementDTO();
            agreementDTO.setId(preferentialTariff.getPrefTariffId());
            agreementDTO.setTariffId(preferentialTariff.getTariff().getTariffId());
            agreementDTO.setAgreementId(preferentialTariff.getAgreement().getAgreementId());
            agreementDTO.setAgreementName(preferentialTariff.getAgreement().getName());
            agreementDTO.setPreferentialRate(preferentialTariff.getPreferentialRate().doubleValue());
            agreementDTO.setRateType(preferentialTariff.getRateType());
            agreementDTO.setEffectiveDate(preferentialTariff.getEffectiveDate());
            agreementDTO.setExpiryDate(preferentialTariff.getExpiryDate());
            response.setApplicableTariff(agreementDTO);
        }

        System.out.println(response);
        return response;
    }
}
