package com.tarrific.backend.repository;

import com.tarrific.backend.model.Country;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CountryRepositoryTest {

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
    private CountryRepository countryRepository;

    @Test
    void saveAndFindById_shouldPersistAndRetrieve() {
        Country c = new Country();
        c.setName("Singapore");
        c.setIsoCode("SG");
        c.setRegion("ASIA");

        Country saved = countryRepository.save(c);
        Integer id = java.util.Objects.requireNonNull(saved.getCountryId());

        Optional<Country> found = countryRepository.findById(id);
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Singapore");
    }

    @Test
    void derivedQueries_shouldWorkIgnoreCase() {
        Country c = new Country();
        c.setName("Malaysia");
        c.setIsoCode("MY");
        c.setRegion("ASIA");
        entityManager.persistAndFlush(c);

        assertThat(countryRepository.findByNameIgnoreCase("malaysia")).isPresent();
        assertThat(countryRepository.findByIsoCodeIgnoreCase("my")).isPresent();
    }

    @Test
    void findAllAndDelete_shouldReflectChanges() {
        Country c1 = new Country();
        c1.setName("Thailand");
        c1.setIsoCode("TH");
        c1.setRegion("ASIA");

        Country c2 = new Country();
        c2.setName("Indonesia");
        c2.setIsoCode("ID");
        c2.setRegion("ASIA");

        Country saved1 = countryRepository.save(c1);
    countryRepository.save(c2);
        entityManager.flush();

        List<Country> list = countryRepository.findAll();
        assertThat(list).hasSize(2);

        countryRepository.delete(saved1);
        entityManager.flush();

        List<Country> afterDelete = countryRepository.findAll();
        assertThat(afterDelete).hasSize(1);
        assertThat(afterDelete.getFirst().getIsoCode()).isEqualTo("ID");
    }
}
