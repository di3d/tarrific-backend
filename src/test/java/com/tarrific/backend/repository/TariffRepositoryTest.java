package com.tarrific.backend.repository;

import com.tarrific.backend.model.Tariff;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.show-sql=true"
})
class TariffRepositoryTest {

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
