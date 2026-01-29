package com.reto.ms_persona.application.usecases;

import com.reto.ms_persona.application.ports.input.InscribirPersonaUseCase;
import com.reto.ms_persona.application.ports.output.InscripcionRepositoryPort;
import com.reto.ms_persona.application.ports.output.PersonaRepositoryPort;
import com.reto.ms_persona.domain.Inscripcion;
import com.reto.ms_persona.domain.PersonaNoEncontradaException;
import com.reto.ms_persona.domain.PersonaYaInscritaException;
import com.reto.ms_persona.domain.ReglasNegocio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class InscribirPersonaEnBootcampUseCase implements InscribirPersonaUseCase {

    private final PersonaRepositoryPort personaRepositoryPort;
    private final InscripcionRepositoryPort inscripcionRepositoryPort;

    @Override
    public Mono<Inscripcion> ejecutar(Long personaId, Long bootcampId) {
        return personaRepositoryPort.findById(personaId)
                .switchIfEmpty(Mono.error(new PersonaNoEncontradaException("Persona con ID " + personaId + " no encontrada")))
                .flatMap(persona -> inscripcionRepositoryPort.existsByPersonaIdAndBootcampId(personaId, bootcampId))
                .flatMap(existe -> {
                    if (existe) {
                        return Mono.error(new PersonaYaInscritaException("La persona ya está inscrita en este bootcamp"));
                    }
                    Inscripcion inscripcion = Inscripcion.builder()
                            .personaId(personaId)
                            .bootcampId(bootcampId)
                            .fechaInscripcion(LocalDateTime.now())
                            .build();
                    ReglasNegocio.validarInscripcion(inscripcion);
                    return inscripcionRepositoryPort.save(inscripcion);
                });
    }
}

