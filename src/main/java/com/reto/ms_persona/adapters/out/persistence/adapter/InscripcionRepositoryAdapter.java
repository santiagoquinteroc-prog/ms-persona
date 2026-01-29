package com.reto.ms_persona.adapters.out.persistence.adapter;

import com.reto.ms_persona.adapters.out.persistence.entity.InscripcionEntity;
import com.reto.ms_persona.adapters.out.persistence.repository.InscripcionR2dbcRepository;
import com.reto.ms_persona.application.ports.output.InscripcionRepositoryPort;
import com.reto.ms_persona.domain.Inscripcion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class InscripcionRepositoryAdapter implements InscripcionRepositoryPort {

    private final InscripcionR2dbcRepository inscripcionR2dbcRepository;

    @Override
    public Mono<Inscripcion> save(Inscripcion inscripcion) {
        InscripcionEntity entity = toEntity(inscripcion);
        return inscripcionR2dbcRepository.save(entity)
                .map(this::toDomain);
    }

    @Override
    public Mono<Boolean> existsByPersonaIdAndBootcampId(Long personaId, Long bootcampId) {
        return inscripcionR2dbcRepository.existsByPersonaIdAndBootcampId(personaId, bootcampId);
    }

    private InscripcionEntity toEntity(Inscripcion inscripcion) {
        return InscripcionEntity.builder()
                .id(inscripcion.getId())
                .personaId(inscripcion.getPersonaId())
                .bootcampId(inscripcion.getBootcampId())
                .fechaInscripcion(inscripcion.getFechaInscripcion())
                .build();
    }

    private Inscripcion toDomain(InscripcionEntity entity) {
        return Inscripcion.builder()
                .id(entity.getId())
                .personaId(entity.getPersonaId())
                .bootcampId(entity.getBootcampId())
                .fechaInscripcion(entity.getFechaInscripcion())
                .build();
    }
}

