package com.reto.ms_persona.application.ports.output;

import reactor.core.publisher.Mono;

public interface ReporteServicePort {
    Mono<Void> notificarInscripcion(Long bootcampId, String nombre, String correo);
}

