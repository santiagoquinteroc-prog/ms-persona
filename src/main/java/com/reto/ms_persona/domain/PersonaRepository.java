package com.reto.ms_persona.domain;

import reactor.core.publisher.Mono;

public interface PersonaRepository {
    Mono<Persona> findById(Long id);
}

