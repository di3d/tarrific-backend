package com.tarrific.backend.service;

import com.tarrific.backend.dto.AgreementDTO;
import com.tarrific.backend.dto.CalculationRequest;
import com.tarrific.backend.dto.CalculationResponse;
import com.tarrific.backend.model.Country;
import com.tarrific.backend.repository.CountryRepository;
import com.tarrific.backend.repository.HsCodeRepository;
import com.tarrific.backend.repository.TariffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CalculationService {

    private final CountryRepository countryRepository;
    private final HsCodeRepository hsCodeRepository;
    private final TariffRepository tariffRepository;

    public CalculationResponse calculate(CalculationRequest request) {

        if (request.getOriginCountry() == null ||
                request.getImportingCountry() == null ||
                request.getHsCode() == null ||
                request.getShipmentValue() == null) {
            throw new IllegalArgumentException("Missing required parameters");
        }

        // Find countries
        Country originCountry = countryRepository.findByName(request.getOriginCountry());
        Country importingCountry = countryRepository.findByName(request.getImportingCountry());

        if (originCountry == null || importingCountry == null) {
            throw new IllegalArgumentException("Invalid country name");
        }

        // Find HS Code
        HsCode hsCode = hsCodeRepository.findByHsCode(request.getHsCode());
        if (hsCode == null) {
            throw new IllegalArgumentException("Invalid HS Code");
        }

        // Date handling
        LocalDate calculationDate = request.getDate() != null
                ? LocalDate.parse(request.getDate())
                : LocalDate.now();

        // Find applicable tariff by HS code and effective dates
        List<Tariff> allTariffs = tariffRepository.findByHsCode_HsCode(hsCode.getHsCode());
        Tariff applicableTariff = allTariffs.stream()
                .filter(t -> t.getEffectiveDate() != null &&
                        t.getExpiryDate() != null &&
                        !calculationDate.isBefore(t.getEffectiveDate()) &&
                        !calculationDate.isAfter(t.getExpiryDate()))
                .findFirst()
                .orElse(null);

        // Compute rate and duty
        double effectiveRate = applicableTariff != null ? applicableTariff.getBaseRate() : 0.0;
        double totalDuty = (request.getShipmentValue() * effectiveRate) / 100.0;
        double effectivePercentage = (totalDuty / request.getShipmentValue()) * 100.0;

        // Build response
        CalculationResponse response = new CalculationResponse();
        response.setBaseTariff(effectiveRate);
        response.setTotalDuty(totalDuty);
        response.setEffectiveRate(effectivePercentage);
        response.setDefaultRate(effectiveRate);
        response.setHsCode(hsCode.getHsCode());
        response.setCommodityDescription(hsCode.getDescription());
        response.setShipmentValue(request.getShipmentValue());
        response.setOriginCountry(request.getOriginCountry());
        response.setImportingCountry(request.getImportingCountry());
        response.setDate(calculationDate.toString());
        response.setCurrency(request.getCurrency());

        if (applicableTariff != null) {
            AgreementDTO dto = new AgreementDTO();
            dto.setId(applicableTariff.getTariffId());
            dto.setRate(applicableTariff.getBaseRate());
            dto.setTariffType(applicableTariff.getRateType());
            dto.setStartDate(applicableTariff.getEffectiveDate());
            dto.setEndDate(applicableTariff.getExpiryDate());
            response.setApplicableTariff(dto);
        }

        return response;
    }
}
