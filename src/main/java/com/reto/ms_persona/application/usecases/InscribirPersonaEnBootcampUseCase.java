package com.reto.ms_persona.application.usecases;

import com.reto.ms_persona.application.ports.input.InscribirPersonaUseCase;
import com.reto.ms_persona.application.ports.output.BootcampServicePort;
import com.reto.ms_persona.application.ports.output.InscripcionRepositoryPort;
import com.reto.ms_persona.application.ports.output.PersonaRepositoryPort;
import com.reto.ms_persona.application.ports.output.ReporteServicePort;
import com.reto.ms_persona.domain.BootcampNoEncontradoException;
import com.reto.ms_persona.domain.BootcampSolapadoException;
import com.reto.ms_persona.domain.Inscripcion;
import com.reto.ms_persona.domain.MaximoBootcampsException;
import com.reto.ms_persona.domain.PersonaNoEncontradaException;
import com.reto.ms_persona.domain.PersonaYaInscritaException;
import com.reto.ms_persona.domain.ReglasNegocio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class InscribirPersonaEnBootcampUseCase implements InscribirPersonaUseCase {

    private final PersonaRepositoryPort personaRepositoryPort;
    private final InscripcionRepositoryPort inscripcionRepositoryPort;
    private final BootcampServicePort bootcampServicePort;
    private final ReporteServicePort reporteServicePort;

    @Override
    public Mono<Inscripcion> ejecutar(Long personaId, Long bootcampId) {
        return personaRepositoryPort.findById(personaId)
                .switchIfEmpty(Mono.error(new PersonaNoEncontradaException("Persona con ID " + personaId + " no encontrada")))
                .flatMap(persona -> bootcampServicePort.findById(bootcampId)
                        .switchIfEmpty(Mono.error(new BootcampNoEncontradoException("Bootcamp con ID " + bootcampId + " no encontrado")))
                        .flatMap(bootcamp -> inscripcionRepositoryPort.existsByPersonaIdAndBootcampId(personaId, bootcampId)
                                .flatMap(existe -> {
                                    if (existe) {
                                        return Mono.error(new PersonaYaInscritaException("La persona ya está inscrita en este bootcamp"));
                                    }
                                    return inscripcionRepositoryPort.findByPersonaId(personaId)
                                            .collectList()
                                            .flatMap(inscripcionesExistentes -> {
                                                return Flux.fromIterable(inscripcionesExistentes)
                                                        .flatMap(inscripcion -> bootcampServicePort.findById(inscripcion.getBootcampId())
                                                                .map(bootcampInfo -> {
                                                                    LocalDate fechaInicio = bootcampInfo.getFechaLanzamiento();
                                                                    LocalDate fechaFin = fechaInicio.plusDays(bootcampInfo.getDuracionSemanas() * 7L);
                                                                    inscripcion.setFechaInicio(fechaInicio);
                                                                    inscripcion.setFechaFin(fechaFin);
                                                                    return inscripcion;
                                                                }))
                                                        .collectList()
                                                        .flatMap(inscripcionesConFechas -> {
                                                            try {
                                                                ReglasNegocio.validarMaximoBootcamps(inscripcionesConFechas, bootcamp);
                                                                ReglasNegocio.validarNoSolape(inscripcionesConFechas, bootcamp);
                                                            } catch (MaximoBootcampsException | BootcampSolapadoException e) {
                                                                return Mono.error(e);
                                                            }
                                                            LocalDate fechaInicio = bootcamp.getFechaLanzamiento();
                                                            LocalDate fechaFin = fechaInicio.plusDays(bootcamp.getDuracionSemanas() * 7L);
                                                            Inscripcion nuevaInscripcion = Inscripcion.builder()
                                                                    .personaId(personaId)
                                                                    .bootcampId(bootcampId)
                                                                    .fechaInscripcion(LocalDateTime.now())
                                                                    .fechaInicio(fechaInicio)
                                                                    .fechaFin(fechaFin)
                                                                    .build();
                                                            ReglasNegocio.validarInscripcion(nuevaInscripcion);
                                                            return inscripcionRepositoryPort.save(nuevaInscripcion)
                                                                    .doOnSuccess(inscripcionGuardada -> {
                                                                        reporteServicePort.notificarInscripcion(
                                                                                bootcampId,
                                                                                persona.getNombre(),
                                                                                persona.getCorreo()
                                                                        ).subscribe();
                                                                    });
                                                        });
                                            });
                                })));
    }
}

