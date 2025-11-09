package com.tarrific.backend.config;

import com.tarrific.backend.model.*;
import com.tarrific.backend.repository.*;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;

import java.util.Date;
import java.util.List;

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
            if (countryRepo.count() > 0) return; // already seeded

            // === Countries ===
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

            // === HS Codes ===
            HsCode phones     = hs("8517.12","Mobile phones and smartphones");
            HsCode laptops    = hs("8471.30","Portable laptops and notebook PCs");
            HsCode displays   = hs("8528.52","LCD / LED Displays");
            HsCode batteries  = hs("8507.60","Lithium-ion batteries");
            HsCode routers    = hs("8517.62","Wireless routers and access points");
            HsCode processors = hs("8542.31","Microprocessors");

            hsRepo.saveAll(List.of(phones, laptops, displays, batteries, routers, processors));

            // === Base Tariffs ===
            Tariff tPhones = tariff(phones,10f,"Ad Valorem");
            Tariff tLaps  = tariff(laptops,5f,"Ad Valorem");
            Tariff tDisp  = tariff(displays,12f,"Ad Valorem");
            Tariff tBatt  = tariff(batteries,6f,"Ad Valorem");
            Tariff tRout  = tariff(routers,8f,"Ad Valorem");
            Tariff tProc  = tariff(processors,3f,"Ad Valorem");

            tariffRepo.saveAll(List.of(tPhones,tLaps,tDisp,tBatt,tRout,tProc));

            // === Allowed Origins ===
            List<Country> majorExporters = List.of(cn,jp,kr,us,tw(in));
            for (Tariff t : List.of(tPhones,tLaps,tDisp,tBatt,tRout,tProc)) {
                for (Country c : majorExporters) {
                    TariffOrigin o = new TariffOrigin();
                    o.setTariff(t);
                    o.setCountry(c);
                    tariffOriginRepo.save(o);
                }
            }

            // === Allowed Destinations ===
            List<Country> asean = List.of(sg,my,th,id,vn,ph);
            for (Tariff t : List.of(tPhones,tLaps,tDisp,tBatt,tRout,tProc)) {
                for (Country c : asean) {
                    TariffDestination d = new TariffDestination();
                    d.setTariff(t);
                    d.setCountry(c);
                    tariffDestinationRepo.save(d);
                }
            }

            // === Trade Agreements ===
            TradeAgreement afta = agreement(tradeRepo, "ASEAN Free Trade Area", "ASEAN intra-regional");
            TradeAgreement rcep = agreement(tradeRepo, "RCEP", "ASEAN + CN, JP, KR, AU, NZ");
            TradeAgreement fta_us_sg = agreement(tradeRepo, "US-SG FTA", "Bilateral SG-USA");

            // Memberships
            for (Country c : asean) tacRepo.save(link(afta, c));
            for (Country c : List.of(sg,my,cn,jp,kr,au,nz)) tacRepo.save(link(rcep, c));
            tacRepo.save(link(fta_us_sg, sg));
            tacRepo.save(link(fta_us_sg, us));

            // === Preferential Tariffs ===
            prefRepo.save(pref(tPhones, rcep, 0f));
            prefRepo.save(pref(tLaps,  rcep, 1f));
            prefRepo.save(pref(tBatt,  afta, 2f));
            prefRepo.save(pref(tPhones, fta_us_sg, 0f));
        };
    }

    // Helpers
    private Country save(CountryRepository repo, String name, String iso, String region){
        Country c = new Country();
        c.setName(name);
        c.setIsoCode(iso);
        c.setRegion(region);
        return repo.save(c);
    }

    private HsCode hs(String code,String desc){
        HsCode h = new HsCode();
        h.setHsCode(code);
        h.setDescription(desc);
        return h;
    }

    private Tariff tariff(HsCode hs, float rate, String type){
        Tariff t = new Tariff();
        t.setHsCode(hs);
        t.setBaseRate(rate);
        t.setRateType(type);
        t.setEffectiveDate(new Date());
        return t;
    }

    private TradeAgreement agreement(TradeAgreementRepository r, String n, String d){
        TradeAgreement a = new TradeAgreement();
        a.setName(n);
        a.setDescription(d);
        a.setEffectiveDate(new Date());
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

    // India helper
    private Country tw(Country c) { return c; }
}
