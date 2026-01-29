package com.reto.ms_persona.application.usecases;

import com.reto.ms_persona.application.ports.output.PersonaRepositoryPort;
import com.reto.ms_persona.domain.CorreoDuplicadoException;
import com.reto.ms_persona.domain.Persona;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CrearPersonaUseCaseImplTest {

    @Mock
    private PersonaRepositoryPort personaRepositoryPort;

    @InjectMocks
    private CrearPersonaUseCaseImpl crearPersonaUseCase;

    @Test
    void ejecutar_personaNueva_retornaPersonaCreada() {
        Persona personaCreada = Persona.builder()
                .id(1L)
                .nombre("Santiago")
                .correo("santiago@test.com")
                .build();

        when(personaRepositoryPort.findByCorreo("santiago@test.com"))
                .thenReturn(Mono.empty());
        when(personaRepositoryPort.save(any(Persona.class)))
                .thenReturn(Mono.just(personaCreada));

        StepVerifier.create(crearPersonaUseCase.ejecutar("Santiago", "santiago@test.com"))
                .expectNextMatches(persona -> 
                    persona.getId().equals(1L) &&
                    persona.getNombre().equals("Santiago") &&
                    persona.getCorreo().equals("santiago@test.com"))
                .verifyComplete();
    }

    @Test
    void ejecutar_correoDuplicado_lanzaExcepcion() {
        Persona personaExistente = Persona.builder()
                .id(1L)
                .nombre("Juan")
                .correo("santiago@test.com")
                .build();

        when(personaRepositoryPort.findByCorreo("santiago@test.com"))
                .thenReturn(Mono.just(personaExistente));

        StepVerifier.create(crearPersonaUseCase.ejecutar("Santiago", "santiago@test.com"))
                .expectErrorMatches(error -> 
                    error instanceof CorreoDuplicadoException &&
                    error.getMessage().contains("ya está registrado"))
                .verify();
    }
}

