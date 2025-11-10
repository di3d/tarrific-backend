package com.tarrific.backend.config;

import com.tarrific.backend.model.HsSection;
import com.tarrific.backend.repository.HsSectionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Order(1)
@Configuration
public class HsSectionLoader {

    @Bean
    CommandLineRunner loadHsSections(HsSectionRepository repo) {
        return args -> {
            if (repo.count() > 0) {
                System.out.println("HS sections already populated. Skipping CSV import.");
                return;
            }

            var in = getClass().getResourceAsStream("/data/sections.csv");
            if (in == null) {
                System.err.println("CSV not found at /data/sections.csv");
                return;
            }

            try (var reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                reader.readLine(); // skip header: section,name
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] cols = line.split(",", 2);
                    if (cols.length < 2) continue;
                    String code = cols[0].trim();
                    String name = cols[1].trim();

                    if (code.isEmpty() || name.isEmpty()) continue;

                    HsSection s = new HsSection();
                    s.setCode(code);
                    s.setName(name);
                    repo.save(s);
                }
                System.out.println("HS sections imported.");
            }
        };
    }
}
