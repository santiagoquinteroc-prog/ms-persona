package com.reto.ms_persona.adapters.in.web;

import com.reto.ms_persona.adapters.out.persistence.entity.PersonaEntity;
import com.reto.ms_persona.adapters.out.persistence.repository.PersonaR2dbcRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Testcontainers
class ObtenerPersonaIT {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>(DockerImageName.parse("mysql:8.0"))
            .withDatabaseName("persona_db_test")
            .withUsername("test")
            .withPassword("test")
            .withInitScript("init.sql");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.r2dbc.url", () -> "r2dbc:mysql://localhost:" + mysql.getMappedPort(3306) + "/persona_db_test");
        registry.add("spring.r2dbc.username", () -> "test");
        registry.add("spring.r2dbc.password", () -> "test");
    }

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private PersonaR2dbcRepository personaR2dbcRepository;

    @BeforeEach
    void setUp() {
        personaR2dbcRepository.deleteAll().block();
    }

    @Test
    void obtenerPersona_existente_retorna200() {
        PersonaEntity persona = PersonaEntity.builder()
                .nombre("Santiago")
                .correo("santiago@test.com")
                .build();
        PersonaEntity saved = personaR2dbcRepository.save(persona).block();

        webTestClient.get()
                .uri("/personas/{id}", saved.getId())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isEqualTo(saved.getId())
                .jsonPath("$.nombre").isEqualTo("Santiago")
                .jsonPath("$.correo").isEqualTo("santiago@test.com");
    }

    @Test
    void obtenerPersona_noExistente_retorna404() {
        webTestClient.get()
                .uri("/personas/999")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(String.class)
                .value(body -> assertThat(body).contains("no encontrada"));
    }

    @Test
    void obtenerPersona_idInvalido_retorna400() {
        webTestClient.get()
                .uri("/personas/abc")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(String.class)
                .value(body -> assertThat(body).contains("inválido"));
    }
}

