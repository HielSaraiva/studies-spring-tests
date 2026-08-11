package org.example.studiesspringtests.domain;

import static org.assertj.core.api.Assertions.*;
import static org.example.studiesspringtests.common.PlanetConstants.PLANET;
import static org.example.studiesspringtests.common.PlanetConstants.INVALID_PLANET;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;


@ExtendWith(MockitoExtension.class)
public class PlanetServiceTest {

    @InjectMocks
    private PlanetService planetService;

    @Mock
    private PlanetRepository planetRepository;

    @Test
    @DisplayName("Dado um planeta válido, quando criado, então deve ser retornado")
    public void createPlanet_withValidData_returnsPlanet() {
        when(planetRepository.save(PLANET)).thenReturn(PLANET);

        Planet sut = planetService.create(PLANET);

        assertThat(sut).isEqualTo(PLANET);
    }

    @Test
    @DisplayName("Dado um planeta inválido, quando criado, então deve ser lançada uma excessão")
    public void createPlanet_withInvalidData_throwsException() {
        when(planetRepository.save(INVALID_PLANET)).thenThrow(RuntimeException.class);

        assertThatThrownBy(() -> planetService.create(INVALID_PLANET)).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Dado um planeta que existe no BD, quando pesquisado por ID, então deve retornar o planeta")
    public void getPlanet_byExistingId_returnsPlanet() {
        when(planetRepository.findById(1L)).thenReturn(Optional.of(PLANET));

        Optional<Planet> sut = planetService.getById(1L);

        assertThat(sut).isNotEmpty();
        assertThat(sut.get()).isEqualTo(PLANET);
    }

    @Test
    @DisplayName("Dado um planeta que não existe no BD, quando pesquisado por ID, então deve retornar vazio")
    public void getPlanet_byUnexistingId_returnsEmpty() {
        when(planetRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Planet> sut = planetService.getById(1L);

        assertThat(sut).isEmpty();
    }

    @Test
    @DisplayName("Dado um planeta que existe no BD, quando pesquisado por nome, então deve retornar o planeta")
    public void getPlanet_byExistingName_returnsPlanet() {
        when(planetRepository.findByName("name")).thenReturn(Optional.of(PLANET));

        Optional<Planet> sut = planetService.getByName("name");

        assertThat(sut).isNotEmpty();
        assertThat(sut.get()).isEqualTo(PLANET);
    }

    @Test
    @DisplayName("Dado um planeta que não existe no BD, quando pesquisado por nome, então deve retornar vazio")
    public void getPlanet_byUnexistingName_returnsEmpty() {
        when(planetRepository.findByName("name")).thenReturn(Optional.empty());

        Optional<Planet> sut = planetService.getByName("name");

        assertThat(sut).isEmpty();
    }

    @Test
    @DisplayName("Dado uma lista de planetas, quando pesquisado por terreno e clima, então deve retornar uma lista correspondente")
    public void listPlanets_returnsPlanets() {
        List<Planet> planets = List.of(PLANET);
        when(planetRepository.findAll(any())).thenReturn(planets);

        List<Planet> sut = planetService.list(PLANET.getTerrain(), PLANET.getClimate());

        assertThat(sut)
                .isNotEmpty()
                .hasSize(1)
                .containsExactly(PLANET);
    }

    @Test
    @DisplayName("Dado uma lista de planetas, quando pesquisado por terreno e clima, deve retornar uma lista vazia")
    public void listPlanets_returnsNoPlanets() {
        when(planetRepository.findAll(any())).thenReturn(Collections.emptyList());

        List<Planet> sut = planetService.list(PLANET.getTerrain(), PLANET.getClimate());

        assertThat(sut).isEmpty();
    }

    @Test
    @DisplayName("Dado um planeta que existe no BD, quando removido, então não deve lançar exceção")
    public void removePlanet_withExistingId_doesNotThrowAnyException() {
        assertThatCode(() -> planetService.remove(1L)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Dado um planeta que não existe no BD, quando removido, então deve lançar exceção")
    public void removePlanet_withUnexistingId_throwException() {
        doThrow(new RuntimeException()).when(planetRepository).deleteById(99L);

        assertThatThrownBy(() -> planetService.remove(99L)).isInstanceOf(RuntimeException.class);
    }
}
