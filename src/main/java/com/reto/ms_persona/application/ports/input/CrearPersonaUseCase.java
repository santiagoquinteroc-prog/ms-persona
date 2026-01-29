package com.reto.ms_persona.application.ports.input;

import com.reto.ms_persona.domain.Persona;
import reactor.core.publisher.Mono;

public interface CrearPersonaUseCase {
    Mono<Persona> ejecutar(String nombre, String correo);
}

