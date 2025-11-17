package com.tarrific.backend.repository;

import com.tarrific.backend.model.HsCode;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class HsCodeRepositoryTest {

    @DynamicPropertySource
    static void registerProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;INIT=CREATE SCHEMA IF NOT EXISTS TARIFF\\;SET SCHEMA TARIFF");
        registry.add("spring.datasource.driver-class-name", () -> "org.h2.Driver");
        registry.add("spring.datasource.username", () -> "sa");
        registry.add("spring.datasource.password", () -> "");
        registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.H2Dialect");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.sql.init.mode", () -> "never");
    }

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private HsCodeRepository hsCodeRepository;

    @Test
    void saveAndFind_shouldPersistAndLookup() {
        HsCode hs = new HsCode();
        hs.setHsCode("0101.21");
        hs.setDescription("Purebred breeding animals");

        hsCodeRepository.save(hs);
        entityManager.flush();

        assertThat(hsCodeRepository.findById("0101.21")).isPresent();
        assertThat(hsCodeRepository.findByHsCode("0101.21")).isPresent();
    }

    @Test
    void findAllAndDelete_shouldReflectChanges() {
        HsCode hs1 = new HsCode();
        hs1.setHsCode("0202.30");
        hs1.setDescription("Frozen meat of bovine animals");

        HsCode hs2 = new HsCode();
        hs2.setHsCode("0303.45");
        hs2.setDescription("Frozen fish fillets");

        hsCodeRepository.save(hs1);
        hsCodeRepository.save(hs2);
        entityManager.flush();

        List<HsCode> all = hsCodeRepository.findAll();
        assertThat(all).hasSize(2);

        hsCodeRepository.deleteById("0202.30");
        entityManager.flush();

        assertThat(hsCodeRepository.findById("0202.30")).isEmpty();
        assertThat(hsCodeRepository.findAll()).hasSize(1);
    }
}
