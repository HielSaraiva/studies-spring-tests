package org.example.studiesspringtests.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Example;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;

import static org.example.studiesspringtests.common.PlanetConstants.PLANET;
import static org.example.studiesspringtests.common.PlanetConstants.TATOOINE;

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
    @DisplayName("Dado um planeta existente, quando buscado pelo ID, então deve retornar o planeta")
    public void getPlanet_byExistingId_returnsPlanet() throws Exception{
        Planet planet = testEntityManager.persistFlushFind(PLANET);

        Optional<Planet> planetOpt = planetRepository.findById(planet.getId());

        assertThat(planetOpt).isNotEmpty();
        assertThat(planetOpt.get()).isEqualTo(planet);
    }

    @Test
    @DisplayName("Dado um ID inexistente, quando buscado pelo ID, então deve retornar vazio")
    public void getPlanet_byUnexistingId_returnsEmpty() throws Exception{
        Optional<Planet> planetOpt = planetRepository.findById(1L);

        assertThat(planetOpt).isEmpty();
    }

    @Test
    @DisplayName("Dado um planeta existente, quando buscado pelo nome, então deve retornar o planeta")
    public void getPlanet_byExistingName_returnsPlanet() throws Exception{
        Planet planet = testEntityManager.persistFlushFind(PLANET);

        Optional<Planet> planetOpt = planetRepository.findByName(planet.getName());

        assertThat(planetOpt).isNotEmpty();
        assertThat(planetOpt.get()).isEqualTo(planet);
    }

    @Test
    @DisplayName("Dado um nome inexistente, quando buscado pelo nome, então deve retornar vazio")
    public void getPlanet_byUnexistingName_returnsNotFound() throws Exception{
        Optional<Planet> planetOpt = planetRepository.findByName("name");

        assertThat(planetOpt).isEmpty();
    }

    @Sql(scripts = "/import_planets.sql")
    @Test
    @DisplayName("Dado planetas cadastrados, quando consulto com filtros, então deve retornar apenas os planetas que atendem ao critério")
    public void listPlanets_returnsFilteredPlanets() throws Exception{
        Example<Planet> queryWithoutFilters = QueryBuilder.makeQuery(new Planet());
        Example<Planet> queryWithFilters = QueryBuilder.makeQuery(new Planet(TATOOINE.getClimate(), TATOOINE.getTerrain()));

        List<Planet> planetsWithoutFilters = planetRepository.findAll(queryWithoutFilters);
        List<Planet> planetsWithFilters = planetRepository.findAll(queryWithFilters);

        assertThat(planetsWithoutFilters).isNotEmpty();
        assertThat(planetsWithoutFilters).hasSize(3);
        assertThat(planetsWithFilters).isNotEmpty();
        assertThat(planetsWithFilters).hasSize(1);
        assertThat(planetsWithFilters.getFirst().getName()).isEqualTo(TATOOINE.getName());
        assertThat(planetsWithFilters.getFirst().getClimate()).isEqualTo(TATOOINE.getClimate());
        assertThat(planetsWithFilters.getFirst().getTerrain()).isEqualTo(TATOOINE.getTerrain());
    }

    @Test
    @DisplayName("Dado que não existem planetas cadastrados, quando consulto todos, então deve retornar lista vazia")
    public void listPlanets_returnsNoPlanets() throws Exception{
        Example<Planet> query =  QueryBuilder.makeQuery(new Planet());

        List<Planet> response = planetRepository.findAll(query);

        assertThat(response).isEmpty();
    }

    @Test
    @DisplayName("Dado um planeta existente, quando removo pelo ID, então deve remover do banco")
    public void removePlanets_withExistingId_removesPlanetFromDatabase(){
        Planet planet = testEntityManager.persistFlushFind(PLANET);

        planetRepository.deleteById(planet.getId());

        Planet removedPlanet = testEntityManager.find(Planet.class, planet.getId());
        assertThat(removedPlanet).isNull();
    }

    @Test
    @DisplayName("Dado um ID inexistente, quando tentar remover, então não deve lançar exceção")
    public void removePlanets_withUnexistingId_doesNotThrowException() {
        assertThatCode(() -> planetRepository.deleteById(1L))
                .doesNotThrowAnyException();
    }
}
