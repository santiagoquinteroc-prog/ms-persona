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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Testcontainers
class ListarPersonasIT {

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
    void listarPersonas_listaVacia_retorna200() {
        webTestClient.get()
                .uri("/personas")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$.length()").isEqualTo(0);
    }

    @Test
    void listarPersonas_conPersonas_retorna200() {
        PersonaEntity persona1 = PersonaEntity.builder()
                .nombre("Santiago")
                .correo("santiago@test.com")
                .build();
        PersonaEntity persona2 = PersonaEntity.builder()
                .nombre("Juan")
                .correo("juan@test.com")
                .build();
        personaR2dbcRepository.save(persona1).block();
        personaR2dbcRepository.save(persona2).block();

        webTestClient.get()
                .uri("/personas")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$.length()").isEqualTo(2)
                .jsonPath("$[0].nombre").exists()
                .jsonPath("$[0].correo").exists()
                .jsonPath("$[1].nombre").exists()
                .jsonPath("$[1].correo").exists();
    }
}

