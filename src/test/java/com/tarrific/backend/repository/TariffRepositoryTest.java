package com.tarrific.backend.repository;

import com.tarrific.backend.model.Tariff;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class TariffRepositoryTest {

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        // Use embedded H2 with simple in-memory URL and force Hibernate to manage schema per test
        registry.add("spring.datasource.url", () -> "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;INIT=CREATE SCHEMA IF NOT EXISTS TARIFF\\;SET SCHEMA TARIFF");
        registry.add("spring.datasource.driver-class-name", () -> "org.h2.Driver");
        registry.add("spring.datasource.username", () -> "sa");
        registry.add("spring.datasource.password", () -> "");
        registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.H2Dialect");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        // Avoid picking up application data.sql during @DataJpaTest
        registry.add("spring.sql.init.mode", () -> "never");
    }

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TariffRepository tariffRepository;

    @Test
    void findById_ShouldReturnTariff_WhenExists() {
        // Given
        Tariff tariff = new Tariff();
        // Only set properties that exist on your Tariff model
        Tariff saved = entityManager.persistAndFlush(tariff);

        // When
        Optional<Tariff> found = tariffRepository.findById(saved.getTariffId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getTariffId()).isEqualTo(saved.getTariffId());
    }

    @Test
    void findById_ShouldReturnEmpty_WhenNotExists() {
        // When
        Optional<Tariff> found = tariffRepository.findById(999);

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    void findAll_ShouldReturnAllTariffs() {
        // Given
        Tariff tariff1 = new Tariff();
        Tariff tariff2 = new Tariff();

        entityManager.persist(tariff1);
        entityManager.persist(tariff2);
        entityManager.flush();

        // When
        List<Tariff> tariffs = tariffRepository.findAll();

        // Then
        assertThat(tariffs).hasSize(2);
    }

    @Test
    void save_ShouldPersistTariff() {
        // Given
        Tariff tariff = new Tariff();

        // When
        Tariff saved = tariffRepository.save(tariff);

        // Then
        assertThat(saved.getTariffId()).isNotNull();
        assertThat(entityManager.find(Tariff.class, saved.getTariffId())).isNotNull();
    }

    @Test
    void deleteById_ShouldRemoveTariff() {
        // Given
        Tariff tariff = new Tariff();
        Tariff saved = entityManager.persistAndFlush(tariff);

        // When
        tariffRepository.deleteById(saved.getTariffId());
        entityManager.flush();

        // Then
        assertThat(entityManager.find(Tariff.class, saved.getTariffId())).isNull();
    }

    @Test
    void count_ShouldReturnCorrectCount() {
        // Given
        Tariff tariff1 = new Tariff();
        Tariff tariff2 = new Tariff();

        entityManager.persist(tariff1);
        entityManager.persist(tariff2);
        entityManager.flush();

        // When
        long count = tariffRepository.count();

        // Then
        assertThat(count).isEqualTo(2);
    }
}