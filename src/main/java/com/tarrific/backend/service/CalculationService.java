package com.tarrific.backend.service;

import com.tarrific.backend.dto.AgreementDTO;
import com.tarrific.backend.dto.CalculationRequest;
import com.tarrific.backend.dto.CalculationResponse;
import com.tarrific.backend.model.Country;
import com.tarrific.backend.model.HSCode;
import com.tarrific.backend.model.Tariff;
import com.tarrific.backend.repository.CountryRepository;
import com.tarrific.backend.repository.HSCodeRepository;
import com.tarrific.backend.repository.TariffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CalculationService {

    private final CountryRepository countryRepository;
    private final HSCodeRepository hsCodeRepository;
    private final TariffRepository tariffRepository;

    public CalculationResponse calculate(CalculationRequest request) {
        // Validate input
        if (request.getOriginCountry() == null || request.getImportingCountry() == null ||
                request.getHsCode() == null || request.getShipmentValue() == null) {
            throw new IllegalArgumentException("Missing required parameters");
        }

        // Find countries
        Country originCountry = countryRepository.findByName(request.getOriginCountry());
        Country importingCountry = countryRepository.findByName(request.getImportingCountry());

        if (originCountry == null || importingCountry == null) {
            throw new IllegalArgumentException("Invalid country name");
        }

        // Find HS Code
        HSCode hsCode = hsCodeRepository.findByCode(request.getHsCode());
        if (hsCode == null) {
            throw new IllegalArgumentException("Invalid HS Code");
        }

        // Default rate is the importing country's tariff rate
        Double defaultRate = importingCountry.getTariffRate() != null ? importingCountry.getTariffRate() : 0.0;

        // Parse the date for comparison
        LocalDate calculationDate = request.getDate() != null ? 
                LocalDate.parse(request.getDate()) : LocalDate.now();

        // Find all tariffs and check for applicable specific agreement
        List<Tariff> allTariffs = tariffRepository.findAll();
        Tariff applicableTariff = allTariffs.stream()
                .filter(t -> t.getCountryA() != null && 
                           t.getCountryB() != null && 
                           t.getHsCode() != null)
                .filter(t -> t.getCountryA().getName().equals(request.getOriginCountry()))
                .filter(t -> t.getCountryB().getName().equals(request.getImportingCountry()))
                .filter(t -> t.getHsCode().getCode().equals(request.getHsCode()))
                .filter(t -> t.getStartDate() != null && 
                           t.getEndDate() != null &&
                           !calculationDate.isBefore(t.getStartDate()) && 
                           !calculationDate.isAfter(t.getEndDate()))
                .findFirst()
                .orElse(null);

        // Use specific tariff rate if exists, otherwise use default country rate
        Double effectiveRate = applicableTariff != null ? applicableTariff.getRate() : defaultRate;
        Double totalDuty = (request.getShipmentValue() * effectiveRate) / 100.0;
        Double effectivePercentage = (totalDuty / request.getShipmentValue()) * 100.0;

        // Build response
        CalculationResponse response = new CalculationResponse();
        response.setBaseTariff(effectiveRate);
        response.setTotalDuty(totalDuty);
        response.setEffectiveRate(effectivePercentage);
        response.setDefaultRate(defaultRate);
        response.setHsCode(request.getHsCode());
        response.setCommodityDescription(request.getCommodityDescription());
        response.setShipmentValue(request.getShipmentValue());
        response.setOriginCountry(request.getOriginCountry());
        response.setImportingCountry(request.getImportingCountry());
        response.setDate(request.getDate());
        response.setCurrency(request.getCurrency());

        // Set applicable tariff if found
        if (applicableTariff != null) {
            AgreementDTO agreementDTO = new AgreementDTO();
            agreementDTO.setId(applicableTariff.getId());
            agreementDTO.setCountryAId(applicableTariff.getCountryA().getId());
            agreementDTO.setCountryBId(applicableTariff.getCountryB().getId());
            agreementDTO.setHscodeId(applicableTariff.getHsCode().getId());
            agreementDTO.setRate(applicableTariff.getRate());
            agreementDTO.setTariffType(applicableTariff.getTariffType().name());
            agreementDTO.setStartDate(applicableTariff.getStartDate());
            agreementDTO.setEndDate(applicableTariff.getEndDate());
            response.setApplicableTariff(agreementDTO);
        }

        return response;
    }
}
