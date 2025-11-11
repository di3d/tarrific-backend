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
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Configuration
public class HsCodeLoader {

    /**
     * Loads HS codes after sections.
     * Looks for /seed/hs_codes.csv with headers: hsCode,description,level,parent,sectionCode
     * Falls back to a small in-memory set if missing.
     */
    @Bean(name = "hsCodeSeeder")
    @Order(2)
    @DependsOn("hsSectionSeeder")
    @Transactional
    public CommandLineRunner hsCodeSeeder(HsCodeRepository codeRepo, HsSectionRepository sectionRepo) {
        return args -> {
            if (codeRepo.count() > 0) {
                System.out.println("HS codes already present. Skipping.");
                return;
            }

            List<HsCode> batch = new ArrayList<>();
            try {
                InputStream in = getClass().getResourceAsStream("/data/harmonized-system.csv");
                System.out.println("[DEBUG] Found harmonized-system.csv: " + (in != null));
                if (in != null) {
                    try (InputStreamReader r = new InputStreamReader(in, StandardCharsets.UTF_8);
                         CSVParser parser = CSVFormat.DEFAULT
                                 .withFirstRecordAsHeader()
                                 .withIgnoreEmptyLines()
                                 .parse(r)) {

                        for (CSVRecord rec : parser) {
                            String sectionCode = safe(rec, "section").trim().toUpperCase();
                            String hs = safe(rec, "hscode").trim();
                            String desc = safe(rec, "description").trim();
                            String parent = safe(rec, "parent").trim();
                            String level = safe(rec, "level").trim();

                            // Skip top-level rows (TOTAL or level 2)
                            if ("TOTAL".equalsIgnoreCase(parent) || "2".equals(level)) {
                                continue;
                            }

                            HsCode code = new HsCode();
                            code.setHsCode(hs);
                            code.setDescription(desc);
                            code.setLevel(level);
                            code.setParent(parent);

                            HsSection section = sectionRepo.findByCode(sectionCode).orElse(null);
                            code.setSection(section);

                            batch.add(code);
                            if (batch.size() >= 1000) {
                                codeRepo.saveAll(batch);
                                batch.clear();
                            }
                        }
                    }
                }

                // Fallback examples when file is missing
                if (batch.isEmpty()) {
                    Map<String, String> fallback = Map.of(
                            "851713", "Telephone sets; smartphones for cellular or other wireless networks",
                            "847130", "Portable automatic data processing machines (laptops)",
                            "852852", "Monitors and projectors, not incorporating television reception apparatus",
                            "850760", "Lithium-ion accumulators",
                            "851762", "Machines for the reception, conversion and transmission of data (routers)",
                            "854231", "Electronic integrated circuits; processors and controllers"
                    );
                    // Try to attach common electronics section if present
                    HsSection electronics = sectionRepo.findByCode("XVI").orElse(null);
                    for (Map.Entry<String, String> e : fallback.entrySet()) {
                        HsCode c = new HsCode();
                        c.setHsCode(e.getKey());
                        c.setDescription(e.getValue());
                        c.setLevel("6");
                        c.setParent(e.getKey().substring(0, Math.min(4, e.getKey().length())));
                        c.setSection(electronics);
                        batch.add(c);
                    }
                }

                if (!batch.isEmpty()) codeRepo.saveAll(batch);
                codeRepo.flush();
                System.out.println("✅ HS codes loaded: " + (batch.size()));
            } catch (Exception e) {
                System.err.println("Failed to load HS codes: " + e.getMessage());
            }
        };
    }

    private static String safe(CSVRecord r, String h) {
        String v = r.isMapped(h) ? r.get(h) : "";
        return v == null ? "" : v.trim();
    }
}
