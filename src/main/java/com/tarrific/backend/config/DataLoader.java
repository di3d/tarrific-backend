package com.tarrific.backend.config;

import com.tarrific.backend.model.*;
import com.tarrific.backend.repository.*;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Configuration
public class DataLoader {

    @Bean
    ApplicationListener<ApplicationReadyEvent> initDataListener(
            CountryRepository countryRepo,
            HsCodeRepository hsRepo,
            TariffRepository tariffRepo,
            TariffOriginRepository tariffOriginRepo,
            TariffDestinationRepository tariffDestinationRepo,
            TradeAgreementRepository tradeRepo,
            TradeAgreementCountryRepository tacRepo,
            PreferentialTariffRepository prefRepo
    ) {
        return event -> {
            // === COUNTRIES ===
            if (countryRepo.count() == 0) {
                Country sg = save(countryRepo, "Singapore","SG","Asia");
                Country my = save(countryRepo, "Malaysia","MY","Asia");
                Country jp = save(countryRepo, "Japan","JP","Asia");
                Country cn = save(countryRepo, "China","CN","Asia");
                Country kr = save(countryRepo, "South Korea","KR","Asia");
                Country th = save(countryRepo, "Thailand","TH","Asia");
                Country id = save(countryRepo, "Indonesia","ID","Asia");
                Country vn = save(countryRepo, "Vietnam","VN","Asia");
                Country ph = save(countryRepo, "Philippines","PH","Asia");
                Country au = save(countryRepo, "Australia","AU","Oceania");
                Country nz = save(countryRepo, "New Zealand","NZ","Oceania");
                Country in = save(countryRepo, "India","IN","Asia");
                Country us = save(countryRepo, "United States","US","North America");
                System.out.println("Countries seeded.");
            }

            // === Ensure HS Codes exist (from HsCodeLoader) ===
            if (hsRepo.count() == 0) {
                System.err.println("HS codes not loaded yet. HsCodeLoader should import CSV before DataLoader runs.");
                return;
            }

            // Example lookups (using real imported HS6 codes)
            HsCode phones     = findHs(hsRepo, "851713"); // Smartphones
            HsCode telephones = findHs(hsRepo, "851714"); // Non-smartphones
            HsCode laptops    = findHs(hsRepo, "847130");
            HsCode displays   = findHs(hsRepo, "852852");
            HsCode batteries  = findHs(hsRepo, "850760");
            HsCode routers    = findHs(hsRepo, "851762");
            HsCode processors = findHs(hsRepo, "854231");

            // If any key HS codes missing, skip tariff seeding
            if (phones == null || laptops == null || displays == null ||
                    batteries == null || routers == null || processors == null) {
                System.err.println("Some example HS codes missing from DB. Skipping tariff seeding.");
                return;
            }

            // === Base Tariffs ===
            if (tariffRepo.count() == 0) {
                Tariff tPhones = tariff(phones,10f,"Ad Valorem");
                Tariff tLaps  = tariff(laptops,5f,"Ad Valorem");
                Tariff tDisp  = tariff(displays,12f,"Ad Valorem");
                Tariff tBatt  = tariff(batteries,6f,"Ad Valorem");
                Tariff tRout  = tariff(routers,8f,"Ad Valorem");
                Tariff tProc  = tariff(processors,3f,"Ad Valorem");

                tariffRepo.saveAll(List.of(tPhones,tLaps,tDisp,tBatt,tRout,tProc));
                System.out.println("Base tariffs seeded.");

                // === Allowed Origins & Destinations ===
                List<Country> allCountries = countryRepo.findAll();
                List<Country> asean = allCountries.stream()
                        .filter(c -> List.of("SG","MY","TH","ID","VN","PH").contains(c.getIsoCode()))
                        .toList();

                List<Country> majorExporters = allCountries.stream()
                        .filter(c -> List.of("CN","JP","KR","US","IN").contains(c.getIsoCode()))
                        .toList();

                for (Tariff t : List.of(tPhones,tLaps,tDisp,tBatt,tRout,tProc)) {
                    for (Country c : majorExporters) {
                        TariffOrigin o = new TariffOrigin();
                        o.setTariff(t);
                        o.setCountry(c);
                        tariffOriginRepo.save(o);
                    }
                    for (Country c : asean) {
                        TariffDestination d = new TariffDestination();
                        d.setTariff(t);
                        d.setCountry(c);
                        tariffDestinationRepo.save(d);
                    }
                }
                System.out.println("Origins and destinations seeded.");
            }

            // === Trade Agreements ===
            if (tradeRepo.count() == 0) {
                TradeAgreement afta = agreement(tradeRepo, "ASEAN Free Trade Area",
                        "ASEAN intra-regional", 2030);
                TradeAgreement rcep = agreement(tradeRepo, "RCEP",
                        "ASEAN + CN, JP, KR, AU, NZ", 2035);
                TradeAgreement fta_us_sg = agreement(tradeRepo, "US-SG FTA",
                        "Bilateral SG-USA", 2032);

                // Memberships
                List<Country> asean = countryRepo.findAll().stream()
                        .filter(c -> List.of("SG","MY","TH","ID","VN","PH").contains(c.getIsoCode()))
                        .toList();

                for (Country c : asean) tacRepo.save(link(afta, c));
                for (Country c : countryRepo.findAll().stream()
                        .filter(c -> List.of("SG","MY","CN","JP","KR","AU","NZ").contains(c.getIsoCode()))
                        .toList()) {
                    tacRepo.save(link(rcep, c));
                }
                tacRepo.save(link(fta_us_sg, countryRepo.findByIsoCodeIgnoreCase("SG").orElseThrow()));
                tacRepo.save(link(fta_us_sg, countryRepo.findByIsoCodeIgnoreCase("US").orElseThrow()));
                System.out.println("Trade agreements seeded.");
            }

            // === Preferential Tariffs ===
            if (prefRepo.count() == 0) {
                Tariff tPhones = tariffRepo.findByHsCode(phones).stream().findFirst().orElseThrow();
                Tariff tLaps   = tariffRepo.findByHsCode(laptops).stream().findFirst().orElseThrow();
                Tariff tBatt   = tariffRepo.findByHsCode(batteries).stream().findFirst().orElseThrow();

                TradeAgreement rcep = tradeRepo.findByName("RCEP").orElseThrow();
                TradeAgreement afta = tradeRepo.findByName("ASEAN Free Trade Area").orElseThrow();
                TradeAgreement fta  = tradeRepo.findByName("US-SG FTA").orElseThrow();

                prefRepo.save(pref(tPhones, rcep, 0f));
                prefRepo.save(pref(tLaps,  rcep, 1f));
                prefRepo.save(pref(tBatt,  afta, 2f));
                prefRepo.save(pref(tPhones, fta, 0f));
                System.out.println("Preferential tariffs seeded.");
            }
        };
    }

    // --- Helpers ---
    private Country save(CountryRepository repo, String name, String iso, String region){
        Country c = new Country();
        c.setName(name);
        c.setIsoCode(iso);
        c.setRegion(region);
        return repo.save(c);
    }

    private HsCode findHs(HsCodeRepository repo, String code) {
        Optional<HsCode> opt = repo.findByHsCode(code);
        return opt.orElse(null);
    }

    private Tariff tariff(HsCode hs, float rate, String type) {
        Tariff t = new Tariff();
        t.setHsCode(hs);
        t.setBaseRate(rate);
        t.setRateType(type);

        Date now = new Date();
        t.setEffectiveDate(now);

        // expiry = 5 years from now
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.add(Calendar.YEAR, 5);
        t.setExpiryDate(cal.getTime());

        return t;
    }


    private TradeAgreement agreement(TradeAgreementRepository r, String n, String d, int expiryYear){
        TradeAgreement a = new TradeAgreement();
        a.setName(n);
        a.setDescription(d);
        Date now = new Date();
        a.setEffectiveDate(now);

        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.set(Calendar.YEAR, expiryYear);
        a.setExpiryDate(cal.getTime());

        return r.save(a);
    }

    private TradeAgreementCountry link(TradeAgreement a, Country c){
        TradeAgreementCountry x = new TradeAgreementCountry();
        x.setAgreement(a);
        x.setCountry(c);
        return x;
    }

    private PreferentialTariff pref(Tariff t, TradeAgreement a, float rate){
        PreferentialTariff p = new PreferentialTariff();
        p.setTariff(t);
        p.setAgreement(a);
        p.setPreferentialRate(rate);
        p.setRateType("Preferential");
        p.setEffectiveDate(new Date());
        return p;
    }
}
