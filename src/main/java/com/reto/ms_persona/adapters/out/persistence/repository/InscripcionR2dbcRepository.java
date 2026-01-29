package com.reto.ms_persona.adapters.out.persistence.repository;

import com.reto.ms_persona.adapters.out.persistence.entity.InscripcionEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface InscripcionR2dbcRepository extends ReactiveCrudRepository<InscripcionEntity, Long> {
    Mono<Boolean> existsByPersonaIdAndBootcampId(Long personaId, Long bootcampId);
}

