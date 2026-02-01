package com.reto.ms_persona.application.ports.output;

import com.reto.ms_persona.domain.Persona;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PersonaRepositoryPort {
    Mono<Persona> findById(Long id);
    Mono<Persona> save(Persona persona);
    Mono<Persona> findByCorreo(String correo);
    Flux<Persona> findAll();
}

