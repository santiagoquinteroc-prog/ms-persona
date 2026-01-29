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
class CrearPersonaIT {

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
    void crearPersona_exitoso() {
        String requestBody = """
                {
                    "nombre": "Santiago",
                    "correo": "santiago@test.com"
                }
                """;

        webTestClient.post()
                .uri("/personas")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").exists()
                .jsonPath("$.nombre").isEqualTo("Santiago")
                .jsonPath("$.correo").isEqualTo("santiago@test.com");
    }

    @Test
    void crearPersona_correoDuplicado_retorna409() {
        PersonaEntity personaExistente = PersonaEntity.builder()
                .nombre("Juan")
                .correo("santiago@test.com")
                .build();
        personaR2dbcRepository.save(personaExistente).block();

        String requestBody = """
                {
                    "nombre": "Santiago",
                    "correo": "santiago@test.com"
                }
                """;

        webTestClient.post()
                .uri("/personas")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange()
                .expectStatus().isEqualTo(org.springframework.http.HttpStatus.CONFLICT)
                .expectBody(String.class)
                .value(body -> assertThat(body).contains("ya está registrado"));
    }

    @Test
    void crearPersona_sinNombre_retorna400() {
        String requestBody = """
                {
                    "correo": "santiago@test.com"
                }
                """;

        webTestClient.post()
                .uri("/personas")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void crearPersona_sinCorreo_retorna400() {
        String requestBody = """
                {
                    "nombre": "Santiago"
                }
                """;

        webTestClient.post()
                .uri("/personas")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void crearPersona_correoInvalido_retorna400() {
        String requestBody = """
                {
                    "nombre": "Santiago",
                    "correo": "correo-invalido"
                }
                """;

        webTestClient.post()
                .uri("/personas")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange()
                .expectStatus().isBadRequest();
    }
}

