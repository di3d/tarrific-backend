package com.tarrific.backend.config;

import com.tarrific.backend.model.*;
import com.tarrific.backend.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Configuration
public class DataLoader {

    /**
     * Seeds core Tarrific data after HS sections and codes are available.
     * Populates countries, trade agreements, tariffs, origins/destinations, and preferential rates.
     */
    @Bean(name = "appDataSeeder")
    @Order(3)
    @DependsOn({"hsSectionSeeder", "hsCodeSeeder"})
    @Transactional
    public CommandLineRunner appDataSeeder(
            CountryRepository countryRepo,
            TradeAgreementRepository agreementRepo,
            TradeAgreementCountryRepository tacRepo,
            TariffRepository tariffRepo,
            TariffOriginRepository originRepo,
            TariffDestinationRepository destRepo,
            PreferentialTariffRepository prefRepo,
            HsCodeRepository hsRepo
    ) {
        return args -> {
            seedCountries(countryRepo);
            Map<String, TradeAgreement> agreements = seedTradeAgreements(agreementRepo);
            seedAgreementCountries(tacRepo, agreements, countryRepo);
            seedTariffs(tariffRepo, originRepo, destRepo, prefRepo, agreements, countryRepo, hsRepo);
        };
    }

    // ==========================================================
    // ================ 1. COUNTRY SEEDING =======================
    // ==========================================================

    private void seedCountries(CountryRepository repo) {
        if (repo.count() > 0) {
            System.out.println("Countries already exist. Skipping.");
            return;
        }

        repo.saveAll(List.of(
                country("Singapore", "SG", "Asia"),
                country("Malaysia", "MY", "Asia"),
                country("Japan", "JP", "Asia"),
                country("China", "CN", "Asia"),
                country("South Korea", "KR", "Asia"),
                country("Thailand", "TH", "Asia"),
                country("Indonesia", "ID", "Asia"),
                country("Vietnam", "VN", "Asia"),
                country("Philippines", "PH", "Asia"),
                country("Australia", "AU", "Oceania"),
                country("New Zealand", "NZ", "Oceania"),
                country("India", "IN", "Asia"),
                country("United States", "US", "North America")
        ));
        repo.flush();
        System.out.println("✅ Countries seeded.");
    }

    private Country country(String name, String iso, String region) {
        Country c = new Country();
        c.setName(name);
        c.setIsoCode(iso);
        c.setRegion(region);
        return c;
    }

    // ==========================================================
    // ================ 2. TRADE AGREEMENTS ======================
    // ==========================================================

    private Map<String, TradeAgreement> seedTradeAgreements(TradeAgreementRepository repo) {
        if (repo.count() == 0) {
            repo.saveAll(List.of(
                    agreement("AFTA", "ASEAN Free Trade Area"),
                    agreement("RCEP", "Regional Comprehensive Economic Partnership")
            ));
            repo.flush();
        }

        Map<String, TradeAgreement> map = new HashMap<>();
        for (TradeAgreement a : repo.findAll()) map.put(a.getName(), a);
        System.out.println("✅ Trade agreements ensured.");
        return map;
    }

    private TradeAgreement agreement(String name, String desc) {
        TradeAgreement ta = new TradeAgreement();
        ta.setName(name);
        ta.setDescription(desc);
        ta.setEffectiveDate(new Date());
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, 5);
        ta.setExpiryDate(cal.getTime());
        return ta;
    }

    // ==========================================================
    // ================ 3. AGREEMENT COUNTRIES ==================
    // ==========================================================

    private void seedAgreementCountries(TradeAgreementCountryRepository tacRepo,
                                        Map<String, TradeAgreement> agreements,
                                        CountryRepository countryRepo) {
        if (tacRepo.count() > 0) {
            System.out.println("Trade agreement countries already exist. Skipping.");
            return;
        }

        Map<String, Country> byIso = countryRepo.findAll().stream()
                .collect(Collectors.toMap(Country::getIsoCode, c -> c));

        List<String> aftaIso = List.of("SG", "MY", "TH", "ID", "VN", "PH");
        List<String> rcepIso = List.of("SG", "MY", "TH", "ID", "VN", "PH", "JP", "CN", "KR", "AU", "NZ");

        TradeAgreement afta = agreements.get("AFTA");
        TradeAgreement rcep = agreements.get("RCEP");

        if (afta != null)
            aftaIso.forEach(iso -> saveAgreementCountry(tacRepo, afta, byIso.get(iso)));
        if (rcep != null)
            rcepIso.forEach(iso -> saveAgreementCountry(tacRepo, rcep, byIso.get(iso)));

        tacRepo.flush();
        System.out.println("✅ Trade agreement countries seeded.");
    }

    private void saveAgreementCountry(TradeAgreementCountryRepository repo, TradeAgreement ta, Country c) {
        if (c == null) return;
        TradeAgreementCountry row = new TradeAgreementCountry();
        row.setAgreement(ta);
        row.setCountry(c);
        repo.save(row);
    }

    // ==========================================================
    // ================ 4. TARIFFS + LINKS ======================
    // ==========================================================

    private void seedTariffs(
            TariffRepository tariffRepo,
            TariffOriginRepository originRepo,
            TariffDestinationRepository destRepo,
            PreferentialTariffRepository prefRepo,
            Map<String, TradeAgreement> agreements,
            CountryRepository countryRepo,
            HsCodeRepository hsRepo
    ) {
        if (tariffRepo.count() > 0) {
            System.out.println("Tariffs already exist. Skipping.");
            return;
        }

        HsCode phones = hsRepo.findById("851713").orElse(null);
        HsCode laptops = hsRepo.findById("847130").orElse(null);
        HsCode monitors = hsRepo.findById("852852").orElse(null);
        HsCode batteries = hsRepo.findById("850760").orElse(null);
        HsCode routers = hsRepo.findById("851762").orElse(null);
        HsCode processors = hsRepo.findById("854231").orElse(null);

        List<Tariff> tariffs = List.of(
                tariff(phones, 10f, null, null, "Ad Valorem"),
                tariff(laptops, 5f, null, null, "Ad Valorem"),
                tariff(monitors, null, 0.25f, "kg", "Specific"),
                tariff(batteries, null, 1.5f, "piece", "Specific"),
                tariff(routers, 8f, null, null, "Ad Valorem"),
                tariff(processors, 3f, 0.15f, "piece", "Mixed")
        );

        tariffRepo.saveAll(tariffs);
        tariffRepo.flush();
        System.out.println("✅ Base tariffs seeded.");

        assignOriginsAndDestinations(tariffs, originRepo, destRepo, countryRepo);
        seedPreferentialTariffs(tariffs, prefRepo, agreements);
    }

    private Tariff tariff(HsCode code, Float baseRate, Float specificRate, String unit, String rateType) {
        Tariff t = new Tariff();
        t.setHsCode(code);
        t.setBaseRate(baseRate);
        t.setSpecificRate(specificRate);
        t.setUnit(unit);
        t.setRateType(rateType);
        t.setEffectiveDate(new Date());
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, 5);
        t.setExpiryDate(cal.getTime());
        return t;
    }

    // ==========================================================
    // ================ 5. RANDOMIZED LINKS =====================
    // ==========================================================

    private void assignOriginsAndDestinations(
            List<Tariff> tariffs,
            TariffOriginRepository originRepo,
            TariffDestinationRepository destRepo,
            CountryRepository countryRepo
    ) {
        Map<String, Country> byIso = countryRepo.findAll().stream()
                .collect(Collectors.toMap(Country::getIsoCode, c -> c));
        List<Country> asean = new ArrayList<>(List.of("SG", "MY", "TH", "ID", "VN", "PH")
                .stream().map(byIso::get).filter(Objects::nonNull).toList());

        Random rand = new Random();

        for (Tariff t : tariffs) {
            Collections.shuffle(asean);

            // choose 2–4 origins
            int originCount = 2 + rand.nextInt(Math.max(1, asean.size() - 2));
            List<Country> origins = asean.subList(0, Math.min(originCount, asean.size()));

            for (Country origin : origins) {
                TariffOrigin o = new TariffOrigin();
                o.getId().setTariffId(t.getTariffId());
                o.getId().setCountryId(origin.getCountryId());
                o.setTariff(t);
                o.setCountry(origin);
                originRepo.save(o);
            }

            // choose 1–2 destinations excluding origins
            List<Country> validDestinations = asean.stream()
                    .filter(c -> !origins.contains(c))
                    .collect(Collectors.toList());

            if (!validDestinations.isEmpty()) {
                int destCount = 1 + rand.nextInt(Math.max(1, validDestinations.size() - 1));
                Collections.shuffle(validDestinations);

                List<Country> destinations = validDestinations.subList(0, destCount);
                for (Country dest : destinations) {
                    TariffDestination d = new TariffDestination();
                    d.getId().setTariffId(t.getTariffId());
                    d.getId().setCountryId(dest.getCountryId());
                    d.setTariff(t);
                    d.setCountry(dest);
                    destRepo.save(d);
                }
            }
        }

        originRepo.flush();
        destRepo.flush();
        System.out.println("✅ Tariff origins/destinations seeded (randomized, no self-links).");
    }

    // ==========================================================
    // ================ 6. PREFERENTIAL RATES ===================
    // ==========================================================

    private void seedPreferentialTariffs(
            List<Tariff> tariffs,
            PreferentialTariffRepository prefRepo,
            Map<String, TradeAgreement> agreements
    ) {
        Random rand = new Random();
        TradeAgreement afta = agreements.get("AFTA");
        TradeAgreement rcep = agreements.get("RCEP");

        for (Tariff t : tariffs) {
            if (t.getBaseRate() == null) continue;

            // AFTA 40–60% of base
            if (afta != null) {
                float aftaRate = (float) (t.getBaseRate() * (0.4 + rand.nextFloat() * 0.2));
                prefRepo.save(pref(t, afta, aftaRate));
            }

            // RCEP 60–80% of base
            if (rcep != null) {
                float rcepRate = (float) (t.getBaseRate() * (0.6 + rand.nextFloat() * 0.2));
                prefRepo.save(pref(t, rcep, rcepRate));
            }
        }

        prefRepo.flush();
        System.out.println("✅ Preferential tariffs seeded (randomized).");
    }

    private PreferentialTariff pref(Tariff t, TradeAgreement a, float rate) {
        PreferentialTariff p = new PreferentialTariff();
        p.setTariff(t);
        p.setAgreement(a);
        p.setPreferentialRate(rate);
        p.setRateType("Preferential");
        p.setEffectiveDate(new Date());
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, 5);
        p.setExpiryDate(cal.getTime());
        return p;
    }
}
