package org.example.studiesspringtests.web;

import static org.example.studiesspringtests.common.PlanetConstants.PLANET;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import org.example.studiesspringtests.domain.Planet;
import org.example.studiesspringtests.domain.PlanetService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.Optional;

@WebMvcTest(PlanetController.class)
public class PlanetControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PlanetService planetService;

    @Test
    @DisplayName("Dado um planeta com dados válidos, quando criado, então deve retornar status 201")
    public void createPlanet_withValidData_returnsCreated() throws Exception {
        when(planetService.create(PLANET)).thenReturn(PLANET);

        mockMvc.perform(post("/planets").content(objectMapper.writeValueAsString(PLANET)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").value(PLANET));
    }

    @Test
    @DisplayName("Dado um planeta com dados inválidos, quando criado, então deve retornar status 422")
    public void createPlanet_withInvalidData_returnsBadRequest() throws Exception {
        Planet emptyPlanet = new Planet();
        Planet invalidPlanet = new Planet("", "", "");

        mockMvc.perform(post("/planets").content(objectMapper.writeValueAsString(emptyPlanet))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableContent());
        mockMvc.perform(post("/planets").content(objectMapper.writeValueAsString(invalidPlanet))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnprocessableContent());
    }

    @Test
    @DisplayName("Dado um planeta com nome existente, quando criado, então deve retornar status 409")
    public void createPlanet_withExistingName_returnsConflict() throws Exception {
        when(planetService.create(any())).thenThrow(DataIntegrityViolationException.class);

        mockMvc.perform(post("/planets").content(objectMapper.writeValueAsString(PLANET))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Dado um ID de planeta existente, quando buscado, então deve retornar status 200 com o planeta")
    public void getPlanet_byExistingId_returnsPlanet() throws Exception{
        when(planetService.getById(1L)).thenReturn(Optional.of(PLANET));

        mockMvc.perform(get("/planets/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(PLANET));
    }

    @Test
    @DisplayName("Dado um ID de planeta inexistente, quando buscado, então deve retornar status 404")
    public void getPlanet_byUnexistingId_returnsNotFound() throws Exception{
        mockMvc.perform(get("/planets/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Dado um nome de planeta existente, quando buscado, então deve retornar status 200 com o planeta")
    public void getPlanet_byExistingName_returnsPlanet() throws Exception{
        when(planetService.getByName(PLANET.getName())).thenReturn(Optional.of(PLANET));

        mockMvc.perform(get("/planets/name/" + PLANET.getName()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(PLANET));
    }

    @Test
    @DisplayName("Dado um nome de planeta inexistente, quando buscado, então deve retornar status 404")
    public void getPlanet_byUnexistingName_returnsNotFound() throws Exception{
        mockMvc.perform(get("/planets/name/1"))
                .andExpect(status().isNotFound());
    }
}
