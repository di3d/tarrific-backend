package com.tarrific.backend.config;

import com.tarrific.backend.model.HsCode;
import com.tarrific.backend.model.HsSection;
import com.tarrific.backend.repository.HsCodeRepository;
import com.tarrific.backend.repository.HsSectionRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Order(2)
@Configuration
public class HsCodeLoader {

    @Bean
    CommandLineRunner loadHsCodes(HsCodeRepository codeRepo, HsSectionRepository sectionRepo) {
        return args -> {
            if (codeRepo.count() > 0) {
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
                         .setHeader() // section,hscode,description,parent,level
                         .setSkipHeaderRecord(true)
                         .build())) {

                List<HsCode> batch = new ArrayList<>(5000);
                int skipped = 0;

                for (CSVRecord r : parser) {
                    String sectionCode = safe(r, "section");
                    String code = safe(r, "hscode");
                    String desc = safe(r, "description");
                    String parent = safe(r, "parent");
                    String level = safe(r, "level");

                    // === Filtering rules ===
                    if (code.isBlank() || desc.isBlank()) {
                        skipped++;
                        continue;
                    }
                    if ("TOTAL".equalsIgnoreCase(parent)) {
                        skipped++;
                        continue;
                    }
                    if (!code.chars().allMatch(Character::isDigit)) {
                        skipped++;
                        continue;
                    }
                    // Only import levels 2, 4, or 6
                    if (!List.of("2", "4", "6").contains(level)) {
                        skipped++;
                        continue;
                    }
                    // Uncomment this if you only want HS6-level codes:
                    // if (!"6".equals(level)) continue;

                    HsSection section = sectionRepo.findByCode(sectionCode).orElse(null);

                    HsCode hs = new HsCode();
                    hs.setHsCode(code);
                    hs.setDescription(desc);
                    hs.setParent(parent);
                    hs.setLevel(level);
                    hs.setSection(section);

                    batch.add(hs);

                    if (batch.size() == 2000) {
                        codeRepo.saveAll(batch);
                        batch.clear();
                    }
                }

                if (!batch.isEmpty()) codeRepo.saveAll(batch);
                System.out.printf("HS code import complete. Skipped %d summary rows.%n", skipped);

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
