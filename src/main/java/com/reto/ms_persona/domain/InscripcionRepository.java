package com.reto.ms_persona.domain;

import reactor.core.publisher.Mono;

public interface InscripcionRepository {
    Mono<Inscripcion> save(Inscripcion inscripcion);
    Mono<Boolean> existsByPersonaIdAndBootcampId(Long personaId, Long bootcampId);
}

