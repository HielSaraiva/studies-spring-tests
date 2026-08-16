package org.example.studiesspringtests;

import static org.example.studiesspringtests.common.PlanetConstants.*;
import static org.assertj.core.api.Assertions.*;

import org.example.studiesspringtests.domain.Planet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.DynamicPropertyRegistry;


@ActiveProfiles("it")
@AutoConfigureRestTestClient
@Sql(scripts = "/import_planets.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/remove_planets.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class PlanetIT {
    @Autowired
    private RestTestClient restTestClient;

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0.33")
            .withDatabaseName("starwars")
            .withUsername("user")
            .withPassword("123456");

    @DynamicPropertySource
    static void registerDatasource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
    }

    @Test
    @DisplayName("Dado um planeta com dados válidos, quando criado, então deve retornar status 201")
    public void createPlanet_returnsCreated() {
        restTestClient.post()
                .uri("/planets")
                .body(PLANET)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Planet.class)
                .value(sut -> {
                    assertThat(sut.getId()).isNotNull();
                    assertThat(sut.getName()).isEqualTo(PLANET.getName());
                    assertThat(sut.getClimate()).isEqualTo(PLANET.getClimate());
                    assertThat(sut.getTerrain()).isEqualTo(PLANET.getTerrain());
                });
    }

    @Test
    @DisplayName("Dado um planeta existente, quando buscado pelo ID, então deve retornar status 200 com o planeta")
    public void getPlanet_returnsPlanet(){
        restTestClient.get()
                .uri("/planets/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Planet.class)
                .value(sut -> {
                    assertThat(sut.getId()).isNotNull();
                    assertThat(sut.getName()).isEqualTo(TATOOINE.getName());
                    assertThat(sut.getClimate()).isEqualTo(TATOOINE.getClimate());
                    assertThat(sut.getTerrain()).isEqualTo(TATOOINE.getTerrain());
                });
    }

    @Test
    @DisplayName("Dado um nome de planeta existente, quando buscado, então deve retornar status 200 com o planeta")
    public void getPlanetByName_returnsPlanet(){
        restTestClient.get()
                .uri("/planets/name/" + TATOOINE.getName())
                .exchange()
                .expectStatus().isOk()
                .expectBody(Planet.class)
                .value(sut -> {
                    assertThat(sut.getId()).isNotNull();
                    assertThat(sut.getName()).isEqualTo(TATOOINE.getName());
                    assertThat(sut.getClimate()).isEqualTo(TATOOINE.getClimate());
                    assertThat(sut.getTerrain()).isEqualTo(TATOOINE.getTerrain());
                });
    }

    @Test
    @DisplayName("Dado planetas cadastrados, quando consulto a listagem, então deve retornar todos os planetas")
    public void listPlanets_returnsAllPlanets(){
        restTestClient.get()
                .uri("/planets")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Planet[].class)
                .value(sut -> {
                    assert sut != null;
                    assertThat(sut.length).isEqualTo(3);
                    assertThat(sut[0].getName()).isEqualTo(TATOOINE.getName());
                    assertThat(sut[0].getClimate()).isEqualTo(TATOOINE.getClimate());
                    assertThat(sut[0].getTerrain()).isEqualTo(TATOOINE.getTerrain());
                });
    }

    @Test
    @DisplayName("Dado um clima existente, quando filtro planetas, então deve retornar apenas os planetas daquele clima")
    public void listPlanetsByClimate_returnsPlanet(){
        restTestClient.get()
                .uri("/planets?climate=" + TATOOINE.getClimate())
                .exchange()
                .expectStatus().isOk()
                .expectBody(Planet[].class)
                .value(sut -> {
                    assert sut != null;
                    assertThat(sut.length).isEqualTo(1);
                    assertThat(sut[0].getName()).isEqualTo(TATOOINE.getName());
                    assertThat(sut[0].getClimate()).isEqualTo(TATOOINE.getClimate());
                    assertThat(sut[0].getTerrain()).isEqualTo(TATOOINE.getTerrain());
                });
    }

    @Test
    @DisplayName("Dado um terreno existente, quando filtro planetas, então deve retornar apenas os planetas daquele terreno")
    public void listPlanetsByTerrain_returnsPlanet(){
        restTestClient.get()
                .uri("/planets?terrain=" + TATOOINE.getTerrain())
                .exchange()
                .expectStatus().isOk()
                .expectBody(Planet[].class)
                .value(sut -> {
                    assert sut != null;
                    assertThat(sut.length).isEqualTo(1);
                    assertThat(sut[0].getName()).isEqualTo(TATOOINE.getName());
                    assertThat(sut[0].getClimate()).isEqualTo(TATOOINE.getClimate());
                    assertThat(sut[0].getTerrain()).isEqualTo(TATOOINE.getTerrain());
                });
    }

    @Test
    @DisplayName("Dado um planeta existente, quando removo pelo ID, então deve retornar status 204")
    public void removePlanet_returnsNoContent(){
        restTestClient.delete()
                .uri("/planets/1")
                .exchange()
                .expectStatus().isNoContent()
                .expectBody();
    }
}
