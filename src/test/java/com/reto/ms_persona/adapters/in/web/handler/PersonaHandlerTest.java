package com.reto.ms_persona.adapters.in.web.handler;

import com.reto.ms_persona.adapters.in.web.dto.CreatePersonaRequest;
import com.reto.ms_persona.adapters.in.web.dto.PersonaResponse;
import com.reto.ms_persona.adapters.in.web.mapper.PersonaMapper;
import com.reto.ms_persona.application.ports.input.CrearPersonaUseCase;
import com.reto.ms_persona.application.ports.output.PersonaRepositoryPort;
import com.reto.ms_persona.domain.CorreoDuplicadoException;
import com.reto.ms_persona.domain.Persona;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PersonaHandlerTest {

    @Mock
    private CrearPersonaUseCase crearPersonaUseCase;

    @Mock
    private PersonaRepositoryPort personaRepositoryPort;

    @Mock
    private PersonaMapper personaMapper;

    @Mock
    private Validator validator;

    @InjectMocks
    private PersonaHandler personaHandler;

    @Test
    void crearPersona_valido_retorna201() {
        CreatePersonaRequest request = new CreatePersonaRequest("Santiago", "santiago@test.com");
        Persona persona = Persona.builder()
                .id(1L)
                .nombre("Santiago")
                .correo("santiago@test.com")
                .build();
        PersonaResponse response = PersonaResponse.builder()
                .id(1L)
                .nombre("Santiago")
                .correo("santiago@test.com")
                .build();

        when(validator.validate(any())).thenReturn(Collections.emptySet());
        when(crearPersonaUseCase.ejecutar("Santiago", "santiago@test.com"))
                .thenReturn(Mono.just(persona));
        when(personaMapper.toResponse(persona)).thenReturn(response);

        MockServerRequest serverRequest = MockServerRequest.builder()
                .body(Mono.just(request));

        Mono<ServerResponse> result = personaHandler.crearPersona(serverRequest);

        StepVerifier.create(result)
                .assertNext(serverResponse -> {
                    assertThat(serverResponse.statusCode()).isEqualTo(HttpStatus.CREATED);
                })
                .verifyComplete();
    }

    @Test
    void crearPersona_correoDuplicado_retorna409() {
        CreatePersonaRequest request = new CreatePersonaRequest("Santiago", "santiago@test.com");

        when(validator.validate(any())).thenReturn(Collections.emptySet());
        when(crearPersonaUseCase.ejecutar("Santiago", "santiago@test.com"))
                .thenReturn(Mono.error(new CorreoDuplicadoException("El correo santiago@test.com ya está registrado")));

        MockServerRequest serverRequest = MockServerRequest.builder()
                .body(Mono.just(request));

        Mono<ServerResponse> result = personaHandler.crearPersona(serverRequest);

        StepVerifier.create(result)
                .assertNext(serverResponse -> {
                    assertThat(serverResponse.statusCode()).isEqualTo(HttpStatus.CONFLICT);
                })
                .verifyComplete();
    }
}

