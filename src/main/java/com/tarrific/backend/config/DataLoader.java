package com.tarrific.backend.config;

import com.tarrific.backend.model.*;
import com.tarrific.backend.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.*;
import java.util.*;

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
            TradeAgreementCountryRepository tacRepo,
            PreferentialTariffRepository prefRepo
    ) {
        return args -> {
            if (countryRepo.count() > 0) return;   // already seeded

            // Countries
            Country sg = save(countryRepo, "Singapore","SG","Asia");
            Country my = save(countryRepo, "Malaysia","MY","Asia");
            Country jp = save(countryRepo, "Japan","JP","Asia");
            Country cn = save(countryRepo, "China","CN","Asia");
            Country kr = save(countryRepo, "South Korea","KR","Asia");

            // HS codes
            HsCode phones = hs("851712","Mobile phones and smartphones");
            HsCode laptops= hs("847130","Portable computers and laptops");
            hsRepo.saveAll(List.of(phones,laptops));

            // Tariffs
            Tariff tPhones = tariff(phones,10f,"Ad Valorem");
            Tariff tLaps  = tariff(laptops,5f,"Ad Valorem");
            tariffRepo.saveAll(List.of(tPhones,tLaps));

            // Origins/Destinations
            for (Country o : List.of(cn,jp,kr)) {
                TariffOrigin to = new TariffOrigin(); to.setTariff(tPhones); to.setCountry(o); tariffOriginRepo.save(to);
                TariffOrigin to2= new TariffOrigin(); to2.setTariff(tLaps);  to2.setCountry(o); tariffOriginRepo.save(to2);
            }
            for (Country d : List.of(sg,my)) {
                TariffDestination td = new TariffDestination(); td.setTariff(tPhones); td.setCountry(d); tariffDestinationRepo.save(td);
                TariffDestination td2= new TariffDestination(); td2.setTariff(tLaps);  td2.setCountry(d); tariffDestinationRepo.save(td2);
            }

            // Agreements
            TradeAgreement afta = new TradeAgreement();
            afta.setName("ASEAN Free Trade Area"); afta.setDescription("ASEAN members"); afta.setEffectiveDate(new Date());
            tradeRepo.save(afta);

            TradeAgreement rcep = new TradeAgreement();
            rcep.setName("RCEP"); rcep.setDescription("ASEAN + CN, JP, KR, AU, NZ"); rcep.setEffectiveDate(new Date());
            tradeRepo.save(rcep);

            // Agreement membership (simple)
            for (Country c : List.of(sg,my)) {
                tacRepo.save(link(afta, c));
            }
            for (Country c : List.of(sg,my,cn,jp,kr)) {
                tacRepo.save(link(rcep, c));
            }

            // Preferential rates
            prefRepo.save(pref(tPhones, rcep, 0f));
            prefRepo.save(pref(tLaps,  rcep, 1f));
        };
    }

    private Country save(CountryRepository repo, String name, String iso, String region){
        Country c=new Country(); c.setName(name); c.setIsoCode(iso); c.setRegion(region); return repo.save(c);
    }
    private HsCode hs(String code,String desc){ HsCode h=new HsCode(); h.setHsCode(code); h.setDescription(desc); return h; }
    private Tariff tariff(HsCode hs, float rate, String type){
        Tariff t = new Tariff(); t.setHsCode(hs); t.setBaseRate(rate); t.setRateType(type); t.setEffectiveDate(new Date()); return t;
    }
    private TradeAgreementCountry link(TradeAgreement a, Country c){
        TradeAgreementCountry x=new TradeAgreementCountry(); x.setAgreement(a); x.setCountry(c); return x;
    }
    private PreferentialTariff pref(Tariff t, TradeAgreement a, float rate){
        PreferentialTariff p=new PreferentialTariff(); p.setTariff(t); p.setAgreement(a); p.setPreferentialRate(rate);
        p.setRateType("Preferential"); p.setEffectiveDate(new Date()); return p;
    }
}
