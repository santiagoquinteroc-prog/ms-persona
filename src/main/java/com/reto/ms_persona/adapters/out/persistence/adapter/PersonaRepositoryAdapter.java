package com.reto.ms_persona.adapters.out.persistence.adapter;

import com.reto.ms_persona.adapters.out.persistence.entity.PersonaEntity;
import com.reto.ms_persona.adapters.out.persistence.repository.PersonaR2dbcRepository;
import com.reto.ms_persona.application.ports.output.PersonaRepositoryPort;
import com.reto.ms_persona.domain.Persona;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class PersonaRepositoryAdapter implements PersonaRepositoryPort {

    private final PersonaR2dbcRepository personaR2dbcRepository;

    @Override
    public Mono<Persona> findById(Long id) {
        return personaR2dbcRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public Mono<Persona> save(Persona persona) {
        PersonaEntity entity = toEntity(persona);
        return personaR2dbcRepository.save(entity)
                .map(this::toDomain);
    }

    @Override
    public Mono<Persona> findByCorreo(String correo) {
        return personaR2dbcRepository.findByCorreo(correo)
                .map(this::toDomain);
    }

    private PersonaEntity toEntity(Persona persona) {
        return PersonaEntity.builder()
                .id(persona.getId())
                .nombre(persona.getNombre())
                .correo(persona.getCorreo())
                .build();
    }

    private Persona toDomain(PersonaEntity entity) {
        return Persona.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .correo(entity.getCorreo())
                .build();
    }
}

