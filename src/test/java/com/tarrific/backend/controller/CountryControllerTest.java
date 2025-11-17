package com.tarrific.backend.controller;

import com.tarrific.backend.model.Country;
import com.tarrific.backend.repository.CountryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CountryController.class)
class CountryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CountryRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAll_ShouldReturnCountryList_WhenCountriesExist() throws Exception {
        // Given: Mock repository returns countries
        Country singapore = new Country();
        singapore.setCountryId(1);
        singapore.setName("Singapore");
        singapore.setIsoCode("SGP");
        singapore.setRegion("Asia");

        Country malaysia = new Country();
        malaysia.setCountryId(2);
        malaysia.setName("Malaysia");
        malaysia.setIsoCode("MYS");
        malaysia.setRegion("Asia");

        List<Country> countries = Arrays.asList(singapore, malaysia);
        when(repository.findAll()).thenReturn(countries);

        // When & Then: GET /api/countries
        mockMvc.perform(get("/api/countries"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Singapore"))
                .andExpect(jsonPath("$[1].name").value("Malaysia")); // FIXED: Changed from andExpected to andExpect

        verify(repository).findAll();
    }

    @Test
    void getById_ShouldReturnCountry_WhenCountryExists() throws Exception {
        // Given: Mock repository returns country
        Country singapore = new Country();
        singapore.setCountryId(1);
        singapore.setName("Singapore");
        singapore.setIsoCode("SGP");
        singapore.setRegion("Asia");

        when(repository.findById(1)).thenReturn(Optional.of(singapore));

        // When & Then: GET /api/countries/1
        mockMvc.perform(get("/api/countries/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Singapore"))
                .andExpect(jsonPath("$.isoCode").value("SGP"));

        verify(repository).findById(1);
    }

    @Test
    void getById_ShouldReturnNull_WhenCountryNotExists() throws Exception {
        // Given: Mock repository returns empty
        when(repository.findById(999)).thenReturn(Optional.empty());

        // When & Then: GET /api/countries/999
        mockMvc.perform(get("/api/countries/999"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(repository).findById(999);
    }

    @Test
    void create_ShouldReturnCreatedCountry_WhenValidCountry() throws Exception {
        // Given: New country data
        Country newCountry = new Country();
        newCountry.setName("Thailand");
        newCountry.setIsoCode("THA");
        newCountry.setRegion("Asia");

        Country savedCountry = new Country();
        savedCountry.setCountryId(3);
        savedCountry.setName("Thailand");
        savedCountry.setIsoCode("THA");
        savedCountry.setRegion("Asia");

        when(repository.save(any(Country.class))).thenReturn(savedCountry);

        // When & Then: POST /api/countries
        mockMvc.perform(post("/api/countries")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newCountry)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.countryId").value(3))
                .andExpect(jsonPath("$.name").value("Thailand"));

        verify(repository).save(any(Country.class));
    }

    @Test
    void update_ShouldReturnUpdatedCountry_WhenValidData() throws Exception {
        // Given: Updated country data
        Country updatedCountry = new Country();
        updatedCountry.setName("Updated Singapore");
        updatedCountry.setIsoCode("SGP");
        updatedCountry.setRegion("Asia");

        Country savedCountry = new Country();
        savedCountry.setCountryId(1);
        savedCountry.setName("Updated Singapore");
        savedCountry.setIsoCode("SGP");
        savedCountry.setRegion("Asia");

        when(repository.save(any(Country.class))).thenReturn(savedCountry);

        // When & Then: PUT /api/countries/1
        mockMvc.perform(put("/api/countries/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedCountry)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Singapore"));

        verify(repository).save(any(Country.class));
    }

    @Test
    void delete_ShouldCallRepository_WhenValidId() throws Exception {
        // Given: Valid country ID
        doNothing().when(repository).deleteById(1);

        // When & Then: DELETE /api/countries/1
        mockMvc.perform(delete("/api/countries/1"))
                .andExpect(status().isOk());

        verify(repository).deleteById(1);
    }
}