package org.example.studiesspringtests.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.Optional;

import static org.example.studiesspringtests.common.PlanetConstants.PLANET;

@DataJpaTest
public class PlanetRepositoryTest {
    @Autowired
    private PlanetRepository planetRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @AfterEach
    public void afterEach() {
        PLANET.setId(null);
    }

    @Test
    @DisplayName("Dado um planeta com dados válidos, quando salvo no BD, então deve retornar o planeta salvo")
    public void createPlanet_withValidData_returnsPlanet() {
        Planet planet = planetRepository.save(PLANET);

        Planet sut = testEntityManager.find(Planet.class, planet.getId());

        assertThat(sut).isNotNull();
        assertThat(sut.getName()).isEqualTo(PLANET.getName());
        assertThat(sut.getTerrain()).isEqualTo(PLANET.getTerrain());
        assertThat(sut.getClimate()).isEqualTo(PLANET.getClimate());
    }

    @Test
    @DisplayName("Dado um planeta com dados inválidos, quando salvo no BD, então deve lançar exceção")
    public void createPlanet_withInvalidData_throwsException() {
        Planet emptyPlanet = new Planet();
        Planet invalidPlanet = new Planet("", "", "");

        assertThatThrownBy(() -> planetRepository.save(emptyPlanet)).isInstanceOf(RuntimeException.class);
        assertThatThrownBy(() -> planetRepository.save(invalidPlanet)).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Dado um planeta com nome já existente, quando salvo no BD, então deve lançar exceção")
    public void createPlanet_withExistingName_throwsException() {
        Planet planet = testEntityManager.persistFlushFind(PLANET);
        testEntityManager.detach(planet);
        planet.setId(null);

        assertThatThrownBy(() -> planetRepository.save(planet)).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("")
    public void getPlanet_byExistingId_returnsPlanet() throws Exception{
        Planet planet = testEntityManager.persistFlushFind(PLANET);

        Optional<Planet> planetOpt = planetRepository.findById(planet.getId());

        assertThat(planetOpt).isNotEmpty();
        assertThat(planetOpt.get()).isEqualTo(planet);
    }

    @Test
    @DisplayName("")
    public void getPlanet_byUnexistingId_returnsEmpty() throws Exception{
        Optional<Planet> planetOpt = planetRepository.findById(1L);

        assertThat(planetOpt).isEmpty();
    }

    @Test
    @DisplayName("")
    public void getPlanet_byExistingName_returnsPlanet() throws Exception{
        Planet planet = testEntityManager.persistFlushFind(PLANET);

        Optional<Planet> planetOpt = planetRepository.findByName(planet.getName());

        assertThat(planetOpt).isNotEmpty();
        assertThat(planetOpt.get()).isEqualTo(planet);
    }

    @Test
    @DisplayName("")
    public void getPlanet_byUnexistingName_returnsNotFound() throws Exception{
        Optional<Planet> planetOpt = planetRepository.findByName("name");

        assertThat(planetOpt).isEmpty();
    }
}
