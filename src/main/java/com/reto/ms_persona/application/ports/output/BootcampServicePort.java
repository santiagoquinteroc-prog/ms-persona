package com.reto.ms_persona.application.ports.output;

import com.reto.ms_persona.domain.BootcampInfo;
import reactor.core.publisher.Mono;

public interface BootcampServicePort {
    Mono<BootcampInfo> findById(Long id);
}

