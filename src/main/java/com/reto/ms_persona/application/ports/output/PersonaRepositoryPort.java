package com.reto.ms_persona.application.ports.output;

import com.reto.ms_persona.domain.Persona;
import reactor.core.publisher.Mono;

public interface PersonaRepositoryPort {
    Mono<Persona> findById(Long id);
}

