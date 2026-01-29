package com.reto.ms_persona.adapters.out.persistence.repository;

import com.reto.ms_persona.adapters.out.persistence.entity.PersonaEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface PersonaR2dbcRepository extends ReactiveCrudRepository<PersonaEntity, Long> {
    Mono<PersonaEntity> findById(Long id);
}

