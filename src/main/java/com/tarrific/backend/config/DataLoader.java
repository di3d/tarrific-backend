package com.tarrific.backend.config;

import com.tarrific.backend.model.*;
import com.tarrific.backend.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Date;
import java.util.List;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initData(
            CountryRepository countryRepo,
            HsCodeRepository hsRepo,
            TariffRepository tariffRepo,
            TariffOriginRepository tariffOriginRepo,
            TariffDestinationRepository tariffDestinationRepo,
            TradeAgreementRepository tradeRepo,
            PreferentialTariffRepository prefRepo
    ) {
        return args -> {
            if (countryRepo.count() == 0) {

                // --- COUNTRIES ---
                Country sg = new Country(); sg.setName("Singapore"); sg.setIsoCode("SG"); sg.setRegion("Asia");
                Country jp = new Country(); jp.setName("Japan"); jp.setIsoCode("JP"); jp.setRegion("Asia");
                Country cn = new Country(); cn.setName("China"); cn.setIsoCode("CN"); cn.setRegion("Asia");
                Country kr = new Country(); kr.setName("South Korea"); kr.setIsoCode("KR"); kr.setRegion("Asia");
                Country my = new Country(); my.setName("Malaysia"); my.setIsoCode("MY"); my.setRegion("Asia");

                countryRepo.saveAll(List.of(sg, jp, cn, kr, my));

                // --- HS CODES (Electronics-focused) ---
                HsCode hsPhones = new HsCode(); hsPhones.setHsCode("851712"); hsPhones.setDescription("Mobile phones and smartphones");
                HsCode hsLaptops = new HsCode(); hsLaptops.setHsCode("847130"); hsLaptops.setDescription("Portable computers and laptops");
                HsCode hsChips = new HsCode(); hsChips.setHsCode("854231"); hsChips.setDescription("Electronic integrated circuits (processors, controllers)");
                HsCode hsDisplays = new HsCode(); hsDisplays.setHsCode("852872"); hsDisplays.setDescription("Monitors and projectors");
                HsCode hsBatteries = new HsCode(); hsBatteries.setHsCode("850760"); hsBatteries.setDescription("Lithium-ion batteries");
                hsRepo.saveAll(List.of(hsPhones, hsLaptops, hsChips, hsDisplays, hsBatteries));

                // --- TARIFFS (Base rates for electronics imports) ---
                Tariff tPhones = new Tariff();  tPhones.setHsCode(hsPhones);  tPhones.setBaseRate(10.0f); tPhones.setRateType("Ad Valorem"); tPhones.setEffectiveDate(new Date());
                Tariff tLaptops = new Tariff(); tLaptops.setHsCode(hsLaptops); tLaptops.setBaseRate(5.0f);  tLaptops.setRateType("Ad Valorem"); tLaptops.setEffectiveDate(new Date());
                Tariff tChips = new Tariff();   tChips.setHsCode(hsChips);   tChips.setBaseRate(2.0f);  tChips.setRateType("Ad Valorem"); tChips.setEffectiveDate(new Date());
                Tariff tDisplays = new Tariff();tDisplays.setHsCode(hsDisplays);tDisplays.setBaseRate(8.0f);  tDisplays.setRateType("Ad Valorem"); tDisplays.setEffectiveDate(new Date());
                Tariff tBatteries = new Tariff();tBatteries.setHsCode(hsBatteries);tBatteries.setBaseRate(12.0f); tBatteries.setRateType("Ad Valorem"); tBatteries.setEffectiveDate(new Date());

                tariffRepo.saveAll(List.of(tPhones, tLaptops, tChips, tDisplays, tBatteries));

                // --- ORIGINS / DESTINATIONS ---
                // Assume products originate from CN, KR, JP; main destinations are SG and MY.
                for (Tariff t : List.of(tPhones, tLaptops, tChips, tDisplays, tBatteries)) {
                    for (Country origin : List.of(cn, jp, kr)) {
                        TariffOrigin o = new TariffOrigin();
                        o.setTariff(t);
                        o.setCountry(origin);
                        tariffOriginRepo.save(o);
                    }
                    for (Country dest : List.of(sg, my)) {
                        TariffDestination d = new TariffDestination();
                        d.setTariff(t);
                        d.setCountry(dest);
                        tariffDestinationRepo.save(d);
                    }
                }

                // --- TRADE AGREEMENTS ---
                TradeAgreement afta = new TradeAgreement();
                afta.setName("ASEAN Free Trade Area");
                afta.setDescription("Tariff reduction among ASEAN member states");
                afta.setEffectiveDate(new Date());

                TradeAgreement rcep = new TradeAgreement();
                rcep.setName("Regional Comprehensive Economic Partnership");
                rcep.setDescription("Agreement among ASEAN, China, Japan, South Korea, Australia, and New Zealand");
                rcep.setEffectiveDate(new Date());

                tradeRepo.saveAll(List.of(afta, rcep));

                // --- PREFERENTIAL TARIFFS ---
                PreferentialTariff p1 = new PreferentialTariff();
                p1.setTariff(tPhones);
                p1.setAgreement(rcep);
                p1.setPreferentialRate(0.0f);
                p1.setRateType("Preferential");
                p1.setEffectiveDate(new Date());

                PreferentialTariff p2 = new PreferentialTariff();
                p2.setTariff(tLaptops);
                p2.setAgreement(rcep);
                p2.setPreferentialRate(1.0f);
                p2.setRateType("Preferential");
                p2.setEffectiveDate(new Date());

                PreferentialTariff p3 = new PreferentialTariff();
                p3.setTariff(tChips);
                p3.setAgreement(afta);
                p3.setPreferentialRate(0.0f);
                p3.setRateType("Preferential");
                p3.setEffectiveDate(new Date());

                PreferentialTariff p4 = new PreferentialTariff();
                p4.setTariff(tDisplays);
                p4.setAgreement(afta);
                p4.setPreferentialRate(2.0f);
                p4.setRateType("Preferential");
                p4.setEffectiveDate(new Date());

                PreferentialTariff p5 = new PreferentialTariff();
                p5.setTariff(tBatteries);
                p5.setAgreement(rcep);
                p5.setPreferentialRate(3.0f);
                p5.setRateType("Preferential");
                p5.setEffectiveDate(new Date());

                prefRepo.saveAll(List.of(p1, p2, p3, p4, p5));
            }
        };
    }
}
