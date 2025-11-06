package com.tarrific.backend.model;

import com.tarrific.backend.model.Country;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CountryTest {

    @Test
    void country_ShouldCreateValidObject_WhenInstantiated() {
        // Given: Create a new Country
        Country country = new Country();

        // When: Set properties
        country.setName("Singapore");
        country.setIsoCode("SGP");
        country.setRegion("Asia");

        // Then: Properties should be set correctly
        assertEquals("Singapore", country.getName());
        assertEquals("SGP", country.getIsoCode());
        assertEquals("Asia", country.getRegion());
    }

    @Test
    void country_ShouldHaveNullIdWhenNew() {
        // Given: Create new country
        Country country = new Country();

        // Then: ID should be null for new entity
        assertNull(country.getId());
    }

    @Test
    void country_ShouldAcceptValidName() {
        // Given: Create country
        Country country = new Country();

        // When: Set valid name
        country.setName("Malaysia");

        // Then: Should accept the name
        assertEquals("Malaysia", country.getName());
    }
}