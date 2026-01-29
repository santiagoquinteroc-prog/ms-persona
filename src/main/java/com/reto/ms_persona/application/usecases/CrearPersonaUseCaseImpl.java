package com.reto.ms_persona.application.usecases;

import com.reto.ms_persona.application.ports.input.CrearPersonaUseCase;
import com.reto.ms_persona.application.ports.output.PersonaRepositoryPort;
import com.reto.ms_persona.domain.CorreoDuplicadoException;
import com.reto.ms_persona.domain.Persona;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CrearPersonaUseCaseImpl implements CrearPersonaUseCase {

    private final PersonaRepositoryPort personaRepositoryPort;

    @Override
    public Mono<Persona> ejecutar(String nombre, String correo) {
        return personaRepositoryPort.findByCorreo(correo)
                .flatMap(existente -> Mono.error(new CorreoDuplicadoException("El correo " + correo + " ya está registrado")))
                .cast(Persona.class)
                .switchIfEmpty(
                        Mono.defer(() -> {
                            Persona nuevaPersona = Persona.builder()
                                    .nombre(nombre)
                                    .correo(correo)
                                    .build();
                            return personaRepositoryPort.save(nuevaPersona);
                        })
                );
    }
}

