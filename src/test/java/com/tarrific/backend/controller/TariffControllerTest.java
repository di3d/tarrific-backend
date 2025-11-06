package com.tarrific.backend.controller;

import com.tarrific.backend.model.Tariff;
import com.tarrific.backend.repository.TariffRepository;
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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TariffController.class)
class TariffControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TariffRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAll_ShouldReturnTariffList_WhenTariffsExist() throws Exception {
        // Given: Mock repository returns tariffs
        Tariff tariff1 = new Tariff();
        tariff1.setTariffId(1);
        // Set other properties based on your Tariff model

        Tariff tariff2 = new Tariff();
        tariff2.setTariffId(2);
        // Set other properties based on your Tariff model

        List<Tariff> tariffs = Arrays.asList(tariff1, tariff2);
        when(repository.findAll()).thenReturn(tariffs);

        // When & Then: GET /api/tariffs
        mockMvc.perform(get("/api/tariffs"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));

        verify(repository).findAll();
    }

    @Test
    void getById_ShouldReturnTariff_WhenTariffExists() throws Exception {
        // Given: Mock repository returns tariff
        Tariff tariff = new Tariff();
        tariff.setTariffId(1);
        // Set other properties based on your Tariff model

        when(repository.findById(1)).thenReturn(Optional.of(tariff));

        // When & Then: GET /api/tariffs/1
        mockMvc.perform(get("/api/tariffs/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.tariffId").value(1));

        verify(repository).findById(1);
    }

    @Test
    void getById_ShouldReturnNull_WhenTariffNotExists() throws Exception {
        // Given: Mock repository returns empty
        when(repository.findById(999)).thenReturn(Optional.empty());

        // When & Then: GET /api/tariffs/999
        mockMvc.perform(get("/api/tariffs/999"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(repository).findById(999);
    }

    @Test
    void create_ShouldReturnCreatedTariff_WhenValidTariff() throws Exception {
        // Given: New tariff data
        Tariff newTariff = new Tariff();
        // Set properties based on your Tariff model

        Tariff savedTariff = new Tariff();
        savedTariff.setTariffId(3);
        // Set other properties to match newTariff

        when(repository.save(any(Tariff.class))).thenReturn(savedTariff);

        // When & Then: POST /api/tariffs
        mockMvc.perform(post("/api/tariffs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newTariff)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tariffId").value(3));

        verify(repository).save(any(Tariff.class));
    }

    @Test
    void update_ShouldReturnUpdatedTariff_WhenValidData() throws Exception {
        // Given: Updated tariff data
        Tariff updatedTariff = new Tariff();
        // Set properties for update

        Tariff savedTariff = new Tariff();
        savedTariff.setTariffId(1);
        // Set updated properties

        when(repository.save(any(Tariff.class))).thenReturn(savedTariff);

        // When & Then: PUT /api/tariffs/1
        mockMvc.perform(put("/api/tariffs/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedTariff)))
                .andExpect(status().isOk());

        verify(repository).save(any(Tariff.class));
    }

    @Test
    void delete_ShouldCallRepository_WhenValidId() throws Exception {
        // Given: Valid tariff ID
        doNothing().when(repository).deleteById(1);

        // When & Then: DELETE /api/tariffs/1
        mockMvc.perform(delete("/api/tariffs/1"))
                .andExpect(status().isOk());

        verify(repository).deleteById(1);
    }
}