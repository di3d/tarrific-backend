package com.tarrific.backend.config;

import com.tarrific.backend.model.HsSection;
import com.tarrific.backend.repository.HsSectionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class HsSectionLoader {

    /**
     * Loads HS sections first.
     * Looks for /seed/hs_sections.csv with header: code,name
     * Falls back to a small built-in list if the file is missing.
     */
    @Bean(name = "hsSectionSeeder")
    @Order(1)
    @Transactional
    public CommandLineRunner hsSectionSeeder(HsSectionRepository sectionRepo) {
        return args -> {
            // With ddl-auto=create, table is empty. Still keep idempotent behavior.
            if (sectionRepo.count() > 0) {
                System.out.println("HS sections already present. Skipping.");
                return;
            }

            List<HsSection> toSave = new ArrayList<>();
            try {
                InputStream in = getClass().getResourceAsStream("/data/sections.csv");
                if (in != null) {
                    try (BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                        String line;
                        boolean header = true;
                        while ((line = br.readLine()) != null) {
                            if (header) { header = false; continue; }
                            String[] parts = line.split(",", 2);
                            if (parts.length < 2) continue;
                            String code = parts[0].trim().replace("\"", ""); // handle "section"
                            String name = parts[1].trim();
                            if (code.isEmpty() || name.isEmpty()) continue;
                            HsSection s = new HsSection();
                            s.setCode(code);
                            s.setName(name);
                            toSave.add(s);
                        }
                    }
                }

                // Fallback if no file or empty
                if (toSave.isEmpty()) {
                    HsSection s1 = new HsSection(); s1.setCode("XVI"); s1.setName("Machinery and Electrical Equipment");
                    HsSection s2 = new HsSection(); s2.setCode("XV");  s2.setName("Base Metals and Articles of Base Metal");
                    toSave.add(s1); toSave.add(s2);
                }

                sectionRepo.saveAll(toSave);
                sectionRepo.flush();
                System.out.println("✅ HS sections loaded: " + toSave.size());
            } catch (Exception e) {
                System.err.println("Failed to load HS sections: " + e.getMessage());
                // Do not rethrow; allow app to continue for dev convenience
            }
        };
    }
}
