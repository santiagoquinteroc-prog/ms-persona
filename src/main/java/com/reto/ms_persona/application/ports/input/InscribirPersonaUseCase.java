package com.reto.ms_persona.application.ports.input;

import com.reto.ms_persona.domain.Inscripcion;
import reactor.core.publisher.Mono;

public interface InscribirPersonaUseCase {
    Mono<Inscripcion> ejecutar(Long personaId, Long bootcampId);
}

