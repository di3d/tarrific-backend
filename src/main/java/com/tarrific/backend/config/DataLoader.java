package com.tarrific.backend.config;

import com.tarrific.backend.model.Country;
import com.tarrific.backend.model.HsCode;
import com.tarrific.backend.model.Tariff;
import com.tarrific.backend.repository.CountryRepository;
import com.tarrific.backend.repository.HsCodeRepository;
import com.tarrific.backend.repository.TariffRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner loadSampleData(
            CountryRepository countryRepo,
            HsCodeRepository hsRepo,
            TariffRepository tariffRepo
    ) {
        return args -> {
            if (countryRepo.count() == 0) {
                Country sg = new Country();
                sg.setName("Singapore");
                sg.setIsoCode("SGP");
                sg.setRegion("Asia");
                countryRepo.save(sg);

                Country jp = new Country();
                jp.setName("Japan");
                jp.setIsoCode("JPN");
                jp.setRegion("Asia");
                countryRepo.save(jp);
            }

            if (hsRepo.count() == 0) {
                HsCode code1 = new HsCode();
                code1.setHsCode("851712");
                code1.setDescription("Telephones for cellular networks (smartphones)");
                code1.setChapter("85");
                code1.setHeading("17");
                code1.setSubheading("12");
                hsRepo.save(code1);
            }

            if (tariffRepo.count() == 0) {
                Tariff t = new Tariff();
                t.setHsCode(hsRepo.findByHsCode("851712"));
                t.setBaseRate(5.0f);
                t.setRateType("ad-valorem");
                t.setEffectiveDate(LocalDate.of(2025, 1, 1));
                t.setExpiryDate(LocalDate.of(2030, 12, 31));
                tariffRepo.save(t);
            }
        };
    }
}
