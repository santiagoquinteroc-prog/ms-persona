package com.reto.ms_persona.application.ports.output;

import com.reto.ms_persona.domain.Inscripcion;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface InscripcionRepositoryPort {
    Mono<Inscripcion> save(Inscripcion inscripcion);
    Mono<Boolean> existsByPersonaIdAndBootcampId(Long personaId, Long bootcampId);
    Flux<Inscripcion> findByPersonaId(Long personaId);
}

