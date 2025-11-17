package com.tarrific.backend.service;

import com.tarrific.backend.dto.CalculationRequest;
import com.tarrific.backend.dto.CalculationResponse;
import com.tarrific.backend.model.*;
import com.tarrific.backend.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CalculationServiceTest {

    @Mock
    private CountryRepository countryRepository;
    @Mock
    private HsCodeRepository hsCodeRepository;
    @Mock
    private TariffRepository tariffRepository;
    @Mock
    private TariffOriginRepository tariffOriginRepository;
    @Mock
    private TariffDestinationRepository tariffDestinationRepository;
    @Mock
    private PreferentialTariffRepository preferentialTariffRepository;

    private CalculationService calculationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        calculationService = new CalculationService(countryRepository, hsCodeRepository, tariffRepository,
                tariffOriginRepository, tariffDestinationRepository, preferentialTariffRepository);
    }

    @Test
    void calculate_shouldApplyPreferentialRateWhenAvailable() {
        // Arrange request
        CalculationRequest req = new CalculationRequest();
        req.setOriginCountry("OriginLand");
        req.setImportingCountry("Importia");
        req.setHsCode("0101.21");
        req.setShipmentValue(1000.0);
        req.setDate("2025-01-01");
        req.setCurrency("USD");

        Country origin = new Country();
        origin.setCountryId(1);
        origin.setName("OriginLand");

        Country dest = new Country();
        dest.setCountryId(2);
        dest.setName("Importia");

        HsCode hs = new HsCode();
        hs.setHsCode("0101.21");
        hs.setDescription("Purebred breeding animals");

        Tariff tariff = new Tariff();
        tariff.setTariffId(10);
    tariff.setBaseRate(5.0f); // 5%

        TariffOrigin to = new TariffOrigin();
        to.setTariff(tariff);
        to.setCountry(origin);

        TariffDestination td = new TariffDestination();
        td.setTariff(tariff);
        td.setCountry(dest);

        PreferentialTariff pref = new PreferentialTariff();
        pref.setPrefTariffId(50);
        pref.setTariff(tariff);
    pref.setPreferentialRate(2.0f); // 2%
        pref.setRateType("PERCENTAGE");
        TradeAgreement agreement = new TradeAgreement();
        agreement.setAgreementId(77);
        agreement.setName("Test Agreement");
        pref.setAgreement(agreement);
        pref.setEffectiveDate(new Date());
        pref.setExpiryDate(new Date(System.currentTimeMillis() + 86400000));

        when(countryRepository.findByNameIgnoreCase("OriginLand")).thenReturn(Optional.of(origin));
        when(countryRepository.findByNameIgnoreCase("Importia")).thenReturn(Optional.of(dest));
        when(hsCodeRepository.findByHsCode("0101.21")).thenReturn(Optional.of(hs));
        when(tariffRepository.findActiveByHsCodeAndDate(eq(hs), any(Date.class))).thenReturn(List.of(tariff));
        when(tariffOriginRepository.findByTariff(tariff)).thenReturn(List.of(to));
        when(tariffDestinationRepository.findByTariff(tariff)).thenReturn(List.of(td));
        when(preferentialTariffRepository.findActiveByTariffAndDate(eq(tariff), any(Date.class))).thenReturn(List.of(pref));

        // Act
        CalculationResponse resp = calculationService.calculate(req);

        // Assert
        assertThat(resp.getBaseTariff()).isEqualTo(2.0); // Effective rate should be preferential
        assertThat(resp.getDefaultRate()).isEqualTo(5.0); // Default/base rate from tariff
        assertThat(resp.getTotalDuty()).isEqualTo((1000.0 * 2.0) / 100.0);
        assertThat(resp.getApplicableTariff()).isNotNull();
        assertThat(resp.getApplicableTariff().getAgreementName()).isEqualTo("Test Agreement");
    }

    @Test
    void calculate_shouldUseDefaultRateWhenNoPreferentialTariff() {
        CalculationRequest req = new CalculationRequest();
        req.setOriginCountry("OriginLand");
        req.setImportingCountry("Importia");
        req.setHsCode("0101.21");
        req.setShipmentValue(500.0);
        req.setCurrency("USD");

        Country origin = new Country();
        origin.setCountryId(1);
        origin.setName("OriginLand");

        Country dest = new Country();
        dest.setCountryId(2);
        dest.setName("Importia");

        HsCode hs = new HsCode();
        hs.setHsCode("0101.21");
        hs.setDescription("Purebred breeding animals");

        Tariff tariff = new Tariff();
        tariff.setTariffId(10);
    tariff.setBaseRate(7.0f); // 7%

        TariffOrigin to = new TariffOrigin();
        to.setTariff(tariff);
        to.setCountry(origin);

        TariffDestination td = new TariffDestination();
        td.setTariff(tariff);
        td.setCountry(dest);

        when(countryRepository.findByNameIgnoreCase("OriginLand")).thenReturn(Optional.of(origin));
        when(countryRepository.findByNameIgnoreCase("Importia")).thenReturn(Optional.of(dest));
        when(hsCodeRepository.findByHsCode("0101.21")).thenReturn(Optional.of(hs));
        when(tariffRepository.findActiveByHsCodeAndDate(eq(hs), any(Date.class))).thenReturn(List.of(tariff));
        when(tariffOriginRepository.findByTariff(tariff)).thenReturn(List.of(to));
        when(tariffDestinationRepository.findByTariff(tariff)).thenReturn(List.of(td));
        when(preferentialTariffRepository.findActiveByTariffAndDate(eq(tariff), any(Date.class))).thenReturn(List.of());

        CalculationResponse resp = calculationService.calculate(req);

        assertThat(resp.getBaseTariff()).isEqualTo(7.0);
        assertThat(resp.getDefaultRate()).isEqualTo(7.0);
        assertThat(resp.getTotalDuty()).isEqualTo((500.0 * 7.0) / 100.0);
        assertThat(resp.getApplicableTariff()).isNull();
    }

    @Test
    void calculate_shouldThrowOnMissingParams() {
        CalculationRequest req = new CalculationRequest();
        // Missing required fields
        try {
            calculationService.calculate(req);
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage()).contains("Missing required parameters");
        }
    }

    @Test
    void calculate_shouldReturnZeroDuty_WhenShipmentValueZero() {
        CalculationRequest req = new CalculationRequest();
        req.setOriginCountry("OriginLand");
        req.setImportingCountry("Importia");
        req.setHsCode("0101.21");
        req.setShipmentValue(0.0); // zero shipment
        req.setCurrency("USD");

        Country origin = new Country();
        origin.setCountryId(1);
        origin.setName("OriginLand");

        Country dest = new Country();
        dest.setCountryId(2);
        dest.setName("Importia");

        HsCode hs = new HsCode();
        hs.setHsCode("0101.21");
        hs.setDescription("Purebred breeding animals");

        Tariff tariff = new Tariff();
        tariff.setTariffId(10);
        tariff.setBaseRate(5.0f);

        TariffOrigin to = new TariffOrigin();
        to.setTariff(tariff);
        to.setCountry(origin);

        TariffDestination td = new TariffDestination();
        td.setTariff(tariff);
        td.setCountry(dest);

        when(countryRepository.findByNameIgnoreCase("OriginLand")).thenReturn(Optional.of(origin));
        when(countryRepository.findByNameIgnoreCase("Importia")).thenReturn(Optional.of(dest));
        when(hsCodeRepository.findByHsCode("0101.21")).thenReturn(Optional.of(hs));
        when(tariffRepository.findActiveByHsCodeAndDate(eq(hs), any(Date.class))).thenReturn(List.of(tariff));
        when(tariffOriginRepository.findByTariff(tariff)).thenReturn(List.of(to));
        when(tariffDestinationRepository.findByTariff(tariff)).thenReturn(List.of(td));
        when(preferentialTariffRepository.findActiveByTariffAndDate(eq(tariff), any(Date.class))).thenReturn(List.of());

        CalculationResponse resp = calculationService.calculate(req);
        assertThat(resp.getTotalDuty()).isEqualTo(0.0);
        assertThat(resp.getBaseTariff()).isEqualTo(5.0);
    }

    @Test
    void calculate_shouldSelectCorrectTariff_WhenMultipleActiveTariffs() {
        CalculationRequest req = new CalculationRequest();
        req.setOriginCountry("OriginLand");
        req.setImportingCountry("Importia");
        req.setHsCode("0101.21");
        req.setShipmentValue(200.0);
        req.setCurrency("USD");

        Country origin = new Country();
        origin.setCountryId(1);
        origin.setName("OriginLand");

        Country dest = new Country();
        dest.setCountryId(2);
        dest.setName("Importia");

        HsCode hs = new HsCode();
        hs.setHsCode("0101.21");
        hs.setDescription("Purebred breeding animals");

        // Tariff A (wrong destination)
        Tariff tariffA = new Tariff();
        tariffA.setTariffId(11);
        tariffA.setBaseRate(9.0f);
        TariffOrigin toA = new TariffOrigin();
        toA.setTariff(tariffA);
        toA.setCountry(origin);
        Country otherDest = new Country();
        otherDest.setCountryId(99);
        otherDest.setName("Elsewhere");
        TariffDestination tdA = new TariffDestination();
        tdA.setTariff(tariffA);
        tdA.setCountry(otherDest);

        // Tariff B (correct origin & destination)
        Tariff tariffB = new Tariff();
        tariffB.setTariffId(12);
        tariffB.setBaseRate(4.0f);
        TariffOrigin toB = new TariffOrigin();
        toB.setTariff(tariffB);
        toB.setCountry(origin);
        TariffDestination tdB = new TariffDestination();
        tdB.setTariff(tariffB);
        tdB.setCountry(dest);

        when(countryRepository.findByNameIgnoreCase("OriginLand")).thenReturn(Optional.of(origin));
        when(countryRepository.findByNameIgnoreCase("Importia")).thenReturn(Optional.of(dest));
        when(hsCodeRepository.findByHsCode("0101.21")).thenReturn(Optional.of(hs));
        when(tariffRepository.findActiveByHsCodeAndDate(eq(hs), any(Date.class))).thenReturn(List.of(tariffA, tariffB));
        when(tariffOriginRepository.findByTariff(tariffA)).thenReturn(List.of(toA));
        when(tariffDestinationRepository.findByTariff(tariffA)).thenReturn(List.of(tdA));
        when(tariffOriginRepository.findByTariff(tariffB)).thenReturn(List.of(toB));
        when(tariffDestinationRepository.findByTariff(tariffB)).thenReturn(List.of(tdB));
        when(preferentialTariffRepository.findActiveByTariffAndDate(any(Tariff.class), any(Date.class))).thenReturn(List.of());

        CalculationResponse resp = calculationService.calculate(req);

        // Should pick tariffB (rate 4%)
        assertThat(resp.getBaseTariff()).isEqualTo(4.0);
        assertThat(resp.getDefaultRate()).isEqualTo(4.0);
        assertThat(resp.getTotalDuty()).isEqualTo((200.0 * 4.0) / 100.0);
    }
}
