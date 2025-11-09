package com.tarrific.backend.config;

import com.tarrific.backend.model.HsCode;
import com.tarrific.backend.repository.HsCodeRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class HsCodeLoader {

    @Bean
    CommandLineRunner loadHsCodes(HsCodeRepository repo) {
        return args -> {
            if (repo.count() > 0) {
                System.out.println("HS code table already populated. Skipping CSV import.");
                return;
            }

            var in = getClass().getResourceAsStream("/data/harmonized-system.csv");
            if (in == null) {
                System.err.println("CSV not found at /data/harmonized-system.csv");
                return;
            }

            try (var reader = new InputStreamReader(in, StandardCharsets.UTF_8);
                 var parser = new CSVParser(reader, CSVFormat.DEFAULT
                         .builder()
                         .setHeader() // uses header row: section,hscode,description,parent,level
                         .setSkipHeaderRecord(true)
                         .build())) {

                List<HsCode> batch = new ArrayList<>(5000);

                for (CSVRecord r : parser) {
                    String level = safe(r, "level");         // "2", "4", "6"
                    String code  = safe(r, "hscode");        // keep leading zeros
                    String desc  = safe(r, "description");

                    // import only leaf subheadings (HS6)
                    if (!"6".equals(level)) continue;
                    if (code.isBlank()) continue;
                    if (!code.chars().allMatch(Character::isDigit)) continue; // skip TOTAL or odd rows

                    HsCode hs = new HsCode();
                    hs.setHsCode(code);        // String PK
                    hs.setDescription(desc);
                    batch.add(hs);

                    if (batch.size() == 2000) {
                        repo.saveAll(batch);
                        batch.clear();
                    }
                }

                if (!batch.isEmpty()) repo.saveAll(batch);
                System.out.println("HS6 import complete.");
            } catch (Exception e) {
                System.err.println("Failed to load HS codes: " + e.getMessage());
            }
        };
    }

    private static String safe(CSVRecord r, String h) {
        String v = r.isMapped(h) ? r.get(h) : "";
        return v == null ? "" : v.trim().replaceAll("^\"|\"$", "");
    }
}
